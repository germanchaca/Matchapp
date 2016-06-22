package fiuba.matchapp.controller.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.wx.goodview.GoodView;

import java.util.List;

import fiuba.cardstack.SwipeDeck;
import fiuba.matchapp.R;
import fiuba.matchapp.adapter.SwipeDeckAdapter;
import fiuba.matchapp.app.MyApplication;
import fiuba.matchapp.controller.activity.ProfileActivity;
import fiuba.matchapp.model.User;
import fiuba.matchapp.networking.httpRequests.okhttp.GetCandidatesOkHttp;
import fiuba.matchapp.networking.httpRequests.okhttp.PostMatchOkHttp;
import fiuba.matchapp.view.RippleAnimation;
import okhttp3.Call;

public class fragmentPlayMatching extends Fragment {
    private static final String TAG = "Connect_fragment";
    RippleAnimation rippleBackground1;
    private SwipeDeck cardStack;
    private SwipeDeckAdapter adapter;

    RelativeLayout btnSwipeLeft;
    RelativeLayout btnSwipeRight;
    private FrameLayout container;
    private FloatingActionButton buttonInfo;
    private RelativeLayout containerRetry;
    private ImageView retryImage;
    private RelativeLayout containerNoMoreCandidates;
    private GoodView mGoodView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGoodView = new GoodView(getActivity());
    }

    public void good(View view){
        mGoodView.setImage(R.drawable.ic_clear_24dp_checked);
        mGoodView.show(view);
    }

    public void good2(View view){
        mGoodView.setImage(R.drawable.ic_favorite_border_checked);
        mGoodView.show(view);
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
                showRetryButtonEnabled();
            }
        });

        builder.show();
    }
    private void initCardstack(){

        GetCandidatesOkHttp request = new GetCandidatesOkHttp() {
            @Override
            protected void onSuccess(final List<User> candidates) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        cardStack.setVisibility(View.VISIBLE);

                        if(candidates.size() == 0){
                            stopAnimation();
                            showNoMoreCandidatesView();
                        }else {

                            adapter = new SwipeDeckAdapter(candidates, getActivity());
                            fillCardStack();
                        }
                        stopAnimation();
                    }
                });
            }

            @Override
            protected void onConnectionError() {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        stopAnimation();
                        showConnectionError();
                    }
                });
            }

            @Override
            protected void onLimitDayReached() {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        showLimitDayErrorDialog();
                    }
                });
            }

            @Override
            protected void logout() {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        stopAnimation();
                        MyApplication.getInstance().logout();
                    }
                });
            }
        };
        startAnimation();
        request.makeRequest();
    }

    private void showLimitDayErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getResources().getString(R.string.swipe_no_more));
        builder.setPositiveButton(getResources().getString(R.string.swipe_no_more_ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                showRetryButtonEnabled();
            }
        });

        builder.show();
    }
    private void showNoMoreCandidatesView() {
        container.setVisibility(View.GONE);
        rippleBackground1.setVisibility(View.GONE);
        containerNoMoreCandidates.setVisibility(View.VISIBLE);
        btnSwipeLeft.setVisibility(View.GONE);
        btnSwipeRight.setVisibility(View.GONE);
        cardStack.setVisibility(View.GONE);
        containerRetry.setVisibility(View.GONE);
    }


    private void showRetryButtonEnabled() {
        container.setVisibility(View.GONE);
        rippleBackground1.setVisibility(View.GONE);
        containerNoMoreCandidates.setVisibility(View.GONE);
        btnSwipeLeft.setVisibility(View.GONE);
        btnSwipeRight.setVisibility(View.GONE);
        cardStack.setVisibility(View.GONE);
        containerRetry.setVisibility(View.VISIBLE);
    }
    private void startProfileActivity(View v,int position) {
        Intent i = new Intent(v.getContext(), ProfileActivity.class);
        User user = (User) adapter.getItem(position);
        i.putExtra("user", (Parcelable) user);
        v.getContext().startActivity(i);
    }
    private void fillCardStack() {
        cardStack.setAdapter(adapter);

        buttonInfo.setEnabled(true);

        buttonInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startProfileActivity(v,0);
            }
        });
        cardStack.setEventCallback(new SwipeDeck.SwipeEventCallback() {
            @Override
            public void cardSwipedLeft(final int position) {
                Log.i("MainActivity", "card was swiped left, position in adapter: " + position);
                if(position < adapter.getCount()){
                    buttonInfo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startProfileActivity(v,position+1);
                        }
                    });
                }
                mGoodView.reset();
            }

            @Override
            public void cardSwipedRight(final int position) {
                Log.i("MainActivity", "card was swiped right, position in adapter: " + position);
                if(position < adapter.getCount()){
                    buttonInfo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startProfileActivity(v,position+1);
                        }
                    });
                }
                User user = (User) adapter.getItem(position);
                Log.d("Swiped right:",user.getEmail());

                PostMatchOkHttp request = new PostMatchOkHttp(user.getEmail()) {
                    @Override
                    protected void onPostMatchRequestFailedUserConnectionError() {
                        if (getActivity() == null) return;
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                        showConnectionError();
                            }
                        });
                    }

                    @Override
                    protected void onPostMatchRequestSuccess() {

                    }

                    @Override
                    protected void logout() {
                        if (getActivity() == null) return;
                        MyApplication.getInstance().logout();
                    }
                };
                request.makeRequest();

                mGoodView.reset();

            }

            @Override
            public void cardsDepleted() {
                Log.i("MainActivity", "no more cards");
                buttonInfo.setEnabled(false);
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
        buttonInfo.setEnabled(false);

        cardStack = (SwipeDeck) view.findViewById(R.id.swipe_deck);
        cardStack.setHardwareAccelerationEnabled(true);

        cardStack.setLeftImage(R.id.left_image);
        cardStack.setRightImage(R.id.right_image);

        btnSwipeLeft = (RelativeLayout) view.findViewById(R.id.button);
        btnSwipeLeft.setVisibility(View.GONE);
        btnSwipeLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                good(v);
                cardStack.swipeTopCardLeft(180);
            }
        });

        btnSwipeRight= (RelativeLayout) view.findViewById(R.id.button2);

        btnSwipeRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                good2(v);
                cardStack.swipeTopCardRight(180);
            }
        });

        containerRetry = (RelativeLayout) view.findViewById(R.id.contentRetry);
        containerRetry.setVisibility(View.GONE);
        retryImage = (ImageView) view.findViewById(R.id.retryImage);
        retryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initCardstack();
            }
        });

        containerNoMoreCandidates = (RelativeLayout) view.findViewById(R.id.contentNoMoreCandidates);
        containerNoMoreCandidates.setVisibility(View.GONE);
    }

    private void startAnimation() {
        //if it's not running
        if (!rippleBackground1.isRippleAnimationRunning()) {
            containerRetry.setVisibility(View.GONE);
            containerNoMoreCandidates.setVisibility(View.GONE);
            container.setVisibility(View.GONE);
            rippleBackground1.setVisibility(View.VISIBLE);
            btnSwipeLeft.setVisibility(View.GONE);
            btnSwipeRight.setVisibility(View.GONE);
            rippleBackground1.startRippleAnimation();
            buttonInfo.setEnabled(false);
        }
    }

    private void stopAnimation() {
        if (rippleBackground1.isRippleAnimationRunning()) {
            containerRetry.setVisibility(View.GONE);
            containerNoMoreCandidates.setVisibility(View.GONE);
            rippleBackground1.stopRippleAnimation();
            rippleBackground1.setVisibility(View.GONE);
            btnSwipeLeft.setVisibility(View.VISIBLE);
            btnSwipeRight.setVisibility(View.VISIBLE);
            container.setVisibility(View.VISIBLE);
        }

    }


}
