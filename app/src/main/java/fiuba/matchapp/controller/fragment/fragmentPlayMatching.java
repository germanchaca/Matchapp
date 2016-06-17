package fiuba.matchapp.controller.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import fiuba.cardstack.SwipeDeck;
import fiuba.matchapp.R;
import fiuba.matchapp.adapter.SwipeDeckAdapter;
import fiuba.matchapp.app.MyApplication;
import fiuba.matchapp.model.User;
import fiuba.matchapp.networking.httpRequests.GetMatchCandidatesRequest;
import fiuba.matchapp.view.RippleAnimation;

public class fragmentPlayMatching extends Fragment {
    private static final String TAG = "Connect_fragment";
    RippleAnimation rippleBackground1;
    private SwipeDeck cardStack;
    private SwipeDeckAdapter adapter;

    RelativeLayout btnSwipeLeft;
    RelativeLayout btnSwipeRight;
    private FrameLayout container;
    private FloatingActionButton buttonInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_connect, container, false);

        init(view);
        initCardstack();
        return view;
    }

    private void showConnectionError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getResources().getString(R.string.connection_problem));
        builder.setPositiveButton(getResources().getString(R.string.connection_problem_candidates), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                initCardstack();
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.connection_problem_candidates_later), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //TODO show retryButton
            }
        });

        builder.show();
    }
    private void initCardstack(){

        //agregarCandidatosDePrueba();

        GetMatchCandidatesRequest request = new GetMatchCandidatesRequest() {
            @Override
            protected void onGetMatchCandidatesRequestFailedDefaultError() {
                stopAnimation();
                showConnectionError();
            }

            @Override
            protected void onGetMatchCandidatesRequestFailedUserConnectionError() {
                stopAnimation();
                showConnectionError();
            }

            @Override
            protected void onGetMatchCandidatesRequestSuccess(List<User> user) {
                if(user.size() == 0){
                    //TODO mostrar no hay candidatos
                }else {
                    ArrayList<User> users = (ArrayList<User>) user;
                    adapter = new SwipeDeckAdapter(users, getActivity(),buttonInfo);
                    fillCardStack();
                }
                stopAnimation();

            }

            @Override
            protected void logout() {
                stopAnimation();
                MyApplication.getInstance().logout();
            }
        };
        startAnimation();
        request.make();

    }

    private void fillCardStack() {
        cardStack.setAdapter(adapter);
        cardStack.setEventCallback(new SwipeDeck.SwipeEventCallback() {
            @Override
            public void cardSwipedLeft(int position) {
                Log.i("MainActivity", "card was swiped left, position in adapter: " + position);
            }

            @Override
            public void cardSwipedRight(int position) {
                Log.i("MainActivity", "card was swiped right, position in adapter: " + position);
            }

            @Override
            public void cardsDepleted() {
                Log.i("MainActivity", "no more cards");
                initCardstack();
            }

            @Override
            public void cardActionDown() {
                Log.i(TAG, "cardActionDown");
            }

            @Override
            public void cardActionUp() {
                Log.i(TAG, "cardActionUp");
            }

        });
    }

    private void init(View view) {
        rippleBackground1 = (RippleAnimation) view.findViewById(R.id.content);
        rippleBackground1.setVisibility(View.VISIBLE);
        container = (FrameLayout) view.findViewById(R.id.container);
        buttonInfo = (FloatingActionButton) view.findViewById(R.id.fb_info);

        cardStack = (SwipeDeck) view.findViewById(R.id.swipe_deck);
        cardStack.setHardwareAccelerationEnabled(true);

        cardStack.setLeftImage(R.id.left_image);
        cardStack.setRightImage(R.id.right_image);

        btnSwipeLeft = (RelativeLayout) view.findViewById(R.id.button);
        btnSwipeLeft.setVisibility(View.GONE);
        btnSwipeLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardStack.swipeTopCardLeft(180);
            }
        });

        btnSwipeRight= (RelativeLayout) view.findViewById(R.id.button2);

        btnSwipeRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardStack.swipeTopCardRight(180);
            }
        });
    }

    private void startAnimation() {
        //if it's not running
        if (!rippleBackground1.isRippleAnimationRunning()) {
            container.setVisibility(View.GONE);
            rippleBackground1.setVisibility(View.VISIBLE);
            btnSwipeLeft.setVisibility(View.GONE);
            btnSwipeRight.setVisibility(View.GONE);
            rippleBackground1.startRippleAnimation();
        }
    }

    private void stopAnimation() {
        if (rippleBackground1.isRippleAnimationRunning()) {
            rippleBackground1.stopRippleAnimation();
            rippleBackground1.setVisibility(View.GONE);
            btnSwipeLeft.setVisibility(View.VISIBLE);
            btnSwipeRight.setVisibility(View.VISIBLE);
            container.setVisibility(View.VISIBLE);
        }

    }


}
