package fiuba.matchapp.controller.activity;

import android.Manifest;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;

import java.util.List;

import fiuba.matchapp.R;
import fiuba.matchapp.app.MyApplication;
import fiuba.matchapp.controller.fragment.InterestsRecyclerViewFragment;
import fiuba.matchapp.controller.fragment.UploadProfilePhotoFragment;
import fiuba.matchapp.model.User;

/**
 * Created by german on 4/26/2016.
 */
public class Intro extends AppIntro2 {
    // Please DO NOT override onCreate. Use init.

    private static String[] PERMISSIONS_LOCATION = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};

    @Override
    public void init(Bundle savedInstanceState) {
        Resources res = getResources();

        addSlide(AppIntroFragment.newInstance(res.getString(R.string.intro_location_title), res.getString(R.string.intro_location_description), R.drawable.ic_place_black_24dp, Color.parseColor("#00bcd4")));
        askForPermissions(PERMISSIONS_LOCATION, 1);

        addSlide(InterestsRecyclerViewFragment.newInstance(1));
        addSlide(InterestsRecyclerViewFragment.newInstance(2));
        addSlide(InterestsRecyclerViewFragment.newInstance(3));
        addSlide(InterestsRecyclerViewFragment.newInstance(4));
        addSlide(InterestsRecyclerViewFragment.newInstance(5));

        User user = MyApplication.getInstance().getPrefManager().getUser();
        if( user.hasFbId() == false ){
            addSlide(new UploadProfilePhotoFragment());
        }

    }

    @Override
    public void onDonePressed() {
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

        if( (index > 0) && fragment.isEmpty ) {
            pager.setCurrentItem(pager.getCurrentItem() - 1);
            if (index == 6){
                Snackbar snackbar = Snackbar.make(backgroundFrame, getResources().getString(R.string.intro_error_empty_photo), Snackbar.LENGTH_LONG);
                snackbar.show();
            }else {
                Snackbar snackbar = Snackbar.make(backgroundFrame, getResources().getString(R.string.intro_error_empty_interest), Snackbar.LENGTH_LONG);
                snackbar.show();
            }

        }
    }

    @Override
    public void onSlideChanged() {
        int index = pager.getCurrentItem();
        if(index == 6){

        }
    }

}
