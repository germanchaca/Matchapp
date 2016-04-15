package fiuba.matchapp.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fiuba.matchapp.R;
import fiuba.matchapp.helper.RippleAnimation;

public class Connect extends Fragment {
    RippleAnimation rippleBackground1;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_connect, container, false);

        init(view);// to initialize widgets

        doRippleBackground(); //start ripple background work..
        return view;
    }

    private void init(View view) {
        rippleBackground1 = (RippleAnimation) view.findViewById(R.id.content);
        rippleBackground1.setVisibility(View.VISIBLE);
    }

    public void doRippleBackground() {

        //start ripple background animations
        startAnimation();

        //handler created to handle cardStack as well as timer...
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //termina el efecto
            }
        }, 3000);

    }

    private void startAnimation() {
        //if it's not running
        if (!rippleBackground1.isRippleAnimationRunning()) {
            rippleBackground1.startRippleAnimation();
        }
    }

    //this method will stop background ripple animation. if it's running.
    private void stopAnimation() {
        if (rippleBackground1.isRippleAnimationRunning()) {
            rippleBackground1.stopRippleAnimation();
        }
    }


}
