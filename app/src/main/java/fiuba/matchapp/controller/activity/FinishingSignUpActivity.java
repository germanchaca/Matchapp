package fiuba.matchapp.controller.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;

import com.github.paolorotolo.appintro.AppIntro2;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import fiuba.matchapp.R;
import fiuba.matchapp.controller.fragment.InterestsRecyclerViewFragment;
import fiuba.matchapp.controller.fragment.UploadProfilePhotoFragment;
import fiuba.matchapp.model.Interest;

/**
 * Created by german on 4/26/2016.
 */
public class FinishingSignUpActivity extends AppIntro2 implements InterestsRecyclerViewFragment.OnInterestsDataPass, UploadProfilePhotoFragment.OnProfilePhotoDataPass {
    // Please DO NOT override onCreate. Use init.
    private static final String TAG = "FinishingSignUpActivity";
    private List<Interest> interests;
    private ProgressDialog progressDialog;
    private String profilePhoto;

    @Override
    public void init(Bundle savedInstanceState) {

        this.profilePhoto = "";

        progressDialog = new ProgressDialog(FinishingSignUpActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getResources().getString(R.string.refresh_account_details));

        addSlide(InterestsRecyclerViewFragment.newInstance(1));
        addSlide(InterestsRecyclerViewFragment.newInstance(2));
        addSlide(InterestsRecyclerViewFragment.newInstance(3));
        addSlide(InterestsRecyclerViewFragment.newInstance(4));
        addSlide(InterestsRecyclerViewFragment.newInstance(5));
        addSlide(new UploadProfilePhotoFragment());
    }

    @Override
    public void onDonePressed() {
        if(TextUtils.isEmpty(this.profilePhoto)){
            showEmptyPhotoError(getResources().getString(R.string.intro_error_empty_photo));
        }else{
            sendPhotoToAppServer(this.profilePhoto);
            launchMainActivity();
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

        if (index < slides.size()){ //fragments de intereses
            if( interestsIsEmpty(this.interests) ) {
                showInterestError(getResources().getString(R.string.intro_error_empty_interest));
            }else{
                sendInterestsToAppServer(this.interests);
            }
        }
    }

    private void sendPhotoToAppServer(String profilePhoto) {

        Log.d(TAG, "ProfilePhoto to send: " + profilePhoto);
    }

    private void showEmptyPhotoError(String message) {
        pager.setCurrentItem(pager.getCurrentItem() - 1);
        Snackbar.make(backgroundFrame,message , Snackbar.LENGTH_LONG).show();
        progressDialog.dismiss();
    }

    private void showInterestError(String message) {
        pager.setCurrentItem(pager.getCurrentItem() - 1);
        Snackbar.make(backgroundFrame,message, Snackbar.LENGTH_LONG).show();
        progressDialog.dismiss();
    }

    private void sendInterestsToAppServer(List<Interest> data) {
        List<Interest> selectedInterests = new ArrayList<Interest>();
        for(Interest interest : data){
            if (interest.isSelected()) {
                selectedInterests.add(interest);
            };
        }
        JSONArray jsonInterestsArray = new JSONArray(selectedInterests);
        Log.d(TAG, "Interests to send: " + jsonInterestsArray);
        //TODO hacer put request al server
    }

    @Override
    public void onSlideChanged() {
    }

    @Override
    public void onInterestsDataPass(List<Interest> data) {
        this.interests = data;
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
