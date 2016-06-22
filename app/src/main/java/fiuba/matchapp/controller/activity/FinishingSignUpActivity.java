package fiuba.matchapp.controller.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;

import com.github.paolorotolo.appintro.AppIntro2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fiuba.matchapp.R;
import fiuba.matchapp.app.MyApplication;
import fiuba.matchapp.controller.fragment.InterestsRecyclerViewFragment;
import fiuba.matchapp.controller.fragment.UploadProfilePhotoFragment;
import fiuba.matchapp.model.Interest;
import fiuba.matchapp.model.User;
import fiuba.matchapp.model.UserInterest;
import fiuba.matchapp.networking.httpRequests.okhttp.PutInterestsOkHttp;
import fiuba.matchapp.networking.httpRequests.okhttp.PutPhotoProfileOkHttp;
import fiuba.matchapp.utils.InterestsUtils;

/**
 * Created by german on 4/26/2016.
 */
public class FinishingSignUpActivity extends AppIntro2 implements UploadProfilePhotoFragment.OnProfilePhotoDataPass {
    // Please DO NOT override onCreate. Use init.
    private static final String TAG = "FinishingSignUpActivity";
    private ProgressDialog progressDialog;
    private String profilePhoto;

    @Override
    public void init(Bundle savedInstanceState) {

        this.profilePhoto = "";

        initProgressDialog();

        addInterestsSlides();

        if (getIntent().hasExtra("fbProfileUrl")) {
            String photoProfile = getIntent().getStringExtra("fbProfileUrl");
            Bundle bundle = new Bundle();
            bundle.putString("fbProfileUrl", photoProfile );
            UploadProfilePhotoFragment frag = new UploadProfilePhotoFragment();
            frag.setArguments(bundle);
            addSlide(frag);
        }else {
            addSlide(new UploadProfilePhotoFragment());
        }

        setFadeAnimation();
    }

    private void initProgressDialog() {
        progressDialog = new ProgressDialog(FinishingSignUpActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getResources().getString(R.string.refresh_account_details));
    }

    private void addInterestsSlides() {
        Intent intent = getIntent();
        List<Interest> interests = intent.getParcelableArrayListExtra("interests");

        Map<String,List<Interest>> mapInterestsByCategory = InterestsUtils.getStringListMap(interests);

        for (Map.Entry<String, List<Interest>> entry : mapInterestsByCategory.entrySet())
        {
            addSlide(InterestsRecyclerViewFragment.newInstance(entry.getKey(), (ArrayList<Interest>) entry.getValue()));
        }
    }

    @Override
    public void onDonePressed() {
        if(TextUtils.isEmpty(this.profilePhoto)){
            showSnackBarError(getResources().getString(R.string.intro_error_empty_photo));
        }else{
            sendPhotoToAppServer(this.profilePhoto);
        }
    }

    private void launchMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onNextPressed() {

        int index = pager.getCurrentItem();
        List<Fragment> slides = getSlides();
        InterestsRecyclerViewFragment fragment = (InterestsRecyclerViewFragment) slides.get(index-1);

        if (index < slides.size()){ //fragments de intereses
            if( InterestsUtils.interestsIsEmpty(fragment.mInterestsList) ) {
                showInterestError(getResources().getString(R.string.intro_error_empty_interest));
            }else{
                Log.d(TAG, "Siguiente categoria");
                sendInterestsToAppServer(fragment.mInterestsList);
            }
        }
    }

    private void sendPhotoToAppServer(final String profilePhoto) {
        progressDialog.show();

        PutPhotoProfileOkHttp request = new PutPhotoProfileOkHttp(MyApplication.getInstance().getPrefManager().getUser(), profilePhoto) {
            @Override
            protected void logout() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        progressDialog.dismiss();
                        MyApplication.getInstance().logout();
                    }
                });
            }

            @Override
            protected void onSuccess() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        User user = MyApplication.getInstance().getPrefManager().getUser();
                        user.setPhotoProfile(profilePhoto);
                        MyApplication.getInstance().getPrefManager().storeUser(user);
                        progressDialog.dismiss();
                        launchMainActivity();
                    }
                });
            }

            @Override
            protected void onConnectionError() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        showSnackBarError(getApplicationContext().getString(R.string.internet_problem));
                    }
                });
            }
        };
        request.makeRequest();



        //Log.d(TAG, "ProfilePhoto to send: " + profilePhoto);
    }

    private void showSnackBarError(String message) {
        Snackbar.make(backgroundFrame,message , Snackbar.LENGTH_LONG).show();
        progressDialog.dismiss();
    }

    private void showInterestError(String message) {
        pager.setCurrentItem(pager.getCurrentItem() - 1);
        showSnackBarError(message);
    }

    private void sendInterestsToAppServer(List<Interest> data) {

        final User user = MyApplication.getInstance().getPrefManager().getUser();

        Log.d(TAG,user.getName());
        final List<UserInterest> selectedInterests = user.getInterests();

        for(Interest interest : data){
            if (interest.isSelected()) {
                selectedInterests.add(interest.getUserInterest());
            }
        }

        progressDialog.show();


        PutInterestsOkHttp request = new PutInterestsOkHttp(MyApplication.getInstance().getPrefManager().getUser(),selectedInterests) {
            @Override
            protected void onAppServerConnectionError() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        showSnackBarError(getApplicationContext().getString(R.string.internet_problem));
                        progressDialog.dismiss();
                    }
                });
            }

            @Override
            protected void onUpdateDataSuccess() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        user.setInterests(selectedInterests);
                        MyApplication.getInstance().getPrefManager().storeUser(user);
                        progressDialog.dismiss();
                    }
                });
            }

            @Override
            protected void logout() {
                runOnUiThread(new Runnable() {
                    public void run() {
                progressDialog.dismiss();
                MyApplication.getInstance().logout();
                    }
                });
            }
        };
        request.makeRequest();


    }

    @Override
    public void onSlideChanged() {
    }



    @Override
    public void onProfilePhotoDataPass(String data) {
        this.profilePhoto = data;
    }


}
