package fiuba.matchapp.controller.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;

import com.github.paolorotolo.appintro.AppIntro2;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
import fiuba.matchapp.model.UserInterest;
import fiuba.matchapp.networking.httpRequests.GetInterestsRequest;
import fiuba.matchapp.networking.httpRequests.PutUpdatePhothoProfileUser;
import fiuba.matchapp.networking.httpRequests.PutUpdateUserData;

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

        progressDialog = new ProgressDialog(FinishingSignUpActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getResources().getString(R.string.searching_for_interests));
        progressDialog.show();

        getAllInterestFromAppServer();


    }

    private void getAllInterestFromAppServer() {
        GetInterestsRequest request = new GetInterestsRequest() {
            @Override
            protected void onGetInterestsSuccess(List<Interest> interests) {

                Map<String,List<Interest> > mapInterestsByCategory = new HashMap<>();
                for (Interest i : interests) {
                    if(!mapInterestsByCategory.containsKey(i.getCategory())){
                        List<Interest> list = new ArrayList<>();
                        list.add(i);
                        mapInterestsByCategory.put(i.getCategory(),list);
                    }else {
                        List<Interest> list = mapInterestsByCategory.get(i.getCategory());
                        list.add(i);
                        mapInterestsByCategory.put(i.getCategory(),list);
                    }
                }
                for (Map.Entry<String, List<Interest>> entry : mapInterestsByCategory.entrySet())
                {
                    addSlide(InterestsRecyclerViewFragment.newInstance(entry.getKey(), (ArrayList<Interest>) entry.getValue()));
                }
                addSlide(new UploadProfilePhotoFragment());

                progressDialog.dismiss();
                progressDialog.setMessage(getResources().getString(R.string.refresh_account_details));
            }

            @Override
            protected void onGetInterestsDefaultError() {
                showSnackBarError(getResources().getString(R.string.internet_problem));
            }

            @Override
            protected void onGetInterestsConnectionError() {
                showSnackBarError(getResources().getString(R.string.internet_problem));
            }
        };
        request.make();
    }

    @Override
    public void onDonePressed() {
        if(TextUtils.isEmpty(this.profilePhoto)){
            showSnackBarError(getResources().getString(R.string.intro_error_empty_photo));
        }else{
            //sendPhotoToAppServer(this.profilePhoto);
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
            if( interestsIsEmpty(fragment.mInterestsList) ) {
                showInterestError(getResources().getString(R.string.intro_error_empty_interest));
            }else{
                //sendInterestsToAppServer(fragment.mInterestsList);
            }
        }
    }

    private void sendPhotoToAppServer(String profilePhoto) {
        progressDialog.show();

        PutUpdatePhothoProfileUser request = new PutUpdatePhothoProfileUser(MyApplication.getInstance().getPrefManager().getUser(), profilePhoto) {
            @Override
            protected void onUpdatePhotoProfileSuccess() {
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
        };
        request.make();

        Log.d(TAG, "ProfilePhoto to send: " + profilePhoto);
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
        List<UserInterest> selectedInterests = new ArrayList<UserInterest>();
        for(Interest interest : data){
            if (interest.isSelected()) {
                selectedInterests.add(interest.getUserInterest());
            }
        }

        progressDialog.show();
        PutUpdateUserData request = new PutUpdateUserData(MyApplication.getInstance().getPrefManager().getUser()) {
            @Override
            protected void onUpdateDataSuccess() {
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
        };
        request.changeInterests(selectedInterests);
        request.make();

    }

    @Override
    public void onSlideChanged() {
    }

    public boolean interestsIsEmpty(List<Interest> data){
        if(data == null) return true;
        for(Interest interest : data){
            if (interest.isSelected()) return false;
        }
        return true;
    }


    @Override
    public void onProfilePhotoDataPass(String data) {
        this.profilePhoto = data;
    }
}
