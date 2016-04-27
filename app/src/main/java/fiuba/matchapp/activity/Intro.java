package fiuba.matchapp.activity;

import android.Manifest;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;

import fiuba.matchapp.R;

/**
 * Created by german on 4/26/2016.
 */
public class Intro extends AppIntro2 {
    // Please DO NOT override onCreate. Use init.

    private static String[] PERMISSIONS_LOCATION = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};


    @Override
    public void init(Bundle savedInstanceState) {

        // Add your slide's fragments here.
        // AppIntro will automatically generate the dots indicator and buttons.
        //addSlide(first_fragment);
        //addSlide(second_fragment);
        //addSlide(third_fragment);
        //addSlide(fourth_fragment);
        // addSlide(SampleSlide.newInstance(R.layout.your_slide_here));

        Resources res = getResources();

        addSlide(AppIntroFragment.newInstance(res.getString(R.string.intro_location_title), res.getString(R.string.intro_location_description), R.drawable.ic_place_black_24dp, Color.parseColor("#00bcd4")));
        askForPermissions(PERMISSIONS_LOCATION, 1);

        addSlide(AppIntroFragment.newInstance(res.getString(R.string.intro_location_title), res.getString(R.string.intro_location_description), R.drawable.ic_place_black_24dp, Color.parseColor("#00bcd4")));

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
    }

    @Override
    public void onSlideChanged() {

    }

}
