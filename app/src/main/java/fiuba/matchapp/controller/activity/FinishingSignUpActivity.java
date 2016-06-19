package fiuba.matchapp.controller.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fiuba.matchapp.R;
import fiuba.matchapp.app.MyApplication;
import fiuba.matchapp.controller.fragment.InterestsRecyclerViewFragment;
import fiuba.matchapp.controller.fragment.UploadProfilePhotoFragment;
import fiuba.matchapp.model.Interest;
import fiuba.matchapp.model.User;
import fiuba.matchapp.model.UserInterest;
import fiuba.matchapp.networking.httpRequests.GetInterestsRequest;
import fiuba.matchapp.networking.httpRequests.PostAppServerTokenRequest;
import fiuba.matchapp.networking.httpRequests.PutUpdatePhothoProfileUser;
import fiuba.matchapp.networking.httpRequests.PutUpdateUserData;
import fiuba.matchapp.networking.httpRequests.okhttp.PutInterestsOkHttp;
import fiuba.matchapp.networking.httpRequests.okhttp.PutPhotoProfileOkHttp;
import fiuba.matchapp.utils.InterestsUtils;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

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

        addSlide(new UploadProfilePhotoFragment());
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
            protected void onSuccess() {
                User user = MyApplication.getInstance().getPrefManager().getUser();
                user.setPhotoProfile(profilePhoto);
                MyApplication.getInstance().getPrefManager().storeUser(user);
                progressDialog.dismiss();
                launchMainActivity();
            }

            @Override
            protected void onAuthError() {
                progressDialog.dismiss();
                MyApplication.getInstance().logout();
            }

            @Override
            protected void onConnectionError() {
                showSnackBarError(getApplicationContext().getString(R.string.internet_problem));
            }
        };
        request.makeRequest();

        /*PutUpdatePhothoProfileUser request = new PutUpdatePhothoProfileUser(MyApplication.getInstance().getPrefManager().getUser(), profilePhoto) {
            @Override
            protected void onUpdatePhotoProfileSuccess() {
                User user = MyApplication.getInstance().getPrefManager().getUser();
                user.setPhotoProfile(profilePhoto);
                MyApplication.getInstance().getPrefManager().storeUser(user);
                progressDialog.dismiss();
                launchMainActivity();
            }

            @Override
            protected void onAppServerUpdatePhotoProfileDefaultError() {
                showSnackBarError(getApplicationContext().getString(R.string.upload_photo_default_error));
            }

            @Override
            protected void onAppServerConnectionError() {
                showSnackBarError(getApplicationContext().getString(R.string.internet_problem));
            }

            @Override
            protected void logOut() {
                progressDialog.dismiss();
                MyApplication.getInstance().logout();
            }
        };
        request.make();*/

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

        /*
        PutUpdateUserData request = new PutUpdateUserData(MyApplication.getInstance().getPrefManager().getUser()) {
            @Override
            protected void onUpdateDataSuccess() {
                user.setInterests(selectedInterests);
                MyApplication.getInstance().getPrefManager().storeUser(user);
                progressDialog.dismiss();
            }

            @Override
            protected void onAppServerDefaultError() {
                showSnackBarError(getApplicationContext().getString(R.string.internet_problem));
                progressDialog.dismiss();
            }

            @Override
            protected void onAppServerConnectionError() {
                showSnackBarError(getApplicationContext().getString(R.string.internet_problem));
                progressDialog.dismiss();
            }

            @Override
            protected void logout() {
                progressDialog.dismiss();
                MyApplication.getInstance().logout();
            }
        };
        request.changeInterests(selectedInterests);
        request.make();*/
        PutInterestsOkHttp request = new PutInterestsOkHttp(MyApplication.getInstance().getPrefManager().getUser(),selectedInterests) {
            @Override
            protected void onAppServerConnectionError() {
                showSnackBarError(getApplicationContext().getString(R.string.internet_problem));
                progressDialog.dismiss();
            }

            @Override
            protected void onUpdateDataSuccess() {
                user.setInterests(selectedInterests);
                MyApplication.getInstance().getPrefManager().storeUser(user);
                progressDialog.dismiss();
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
