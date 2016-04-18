package fiuba.matchapp.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

import fiuba.cardstack.SwipeDeck;
import fiuba.matchapp.R;
import fiuba.matchapp.adapter.SwipeDeckAdapter;
import fiuba.matchapp.helper.RippleAnimation;

public class Connect extends Fragment {
    private static final String TAG = "Connect_fragment";
    RippleAnimation rippleBackground1;
    private SwipeDeck cardStack;
    private SwipeDeckAdapter adapter;
    private ArrayList<String> testData;

    FloatingActionButton btnSwipeLeft;
    FloatingActionButton btnSwipeRight;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_connect, container, false);

        init(view);

        startAnimation();

        initCardstack();
        stopAnimation();
        //stopAnimation();//deberia estar cuando recibo el http request answer con los candidatos
        return view;
    }

    private void agregarCandidatosDePrueba(){
        testData.add("0");
        testData.add("1");
        testData.add("2");
        testData.add("3");
        testData.add("4");

    }
    private void initCardstack(){
        testData = new ArrayList<>();
        agregarCandidatosDePrueba();

        adapter = new SwipeDeckAdapter(testData, getActivity());
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
                startAnimation();
                //TODO aca va de nuevo la petición al http server, ojo que si supera el max hay que mostrar otra pantalla
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

        cardStack = (SwipeDeck) view.findViewById(R.id.swipe_deck);
        cardStack.setHardwareAccelerationEnabled(true);

        cardStack.setLeftImage(R.id.left_image);
        cardStack.setRightImage(R.id.right_image);

        btnSwipeLeft = (FloatingActionButton) view.findViewById(R.id.button);
        btnSwipeLeft.setVisibility(View.GONE);
        btnSwipeLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardStack.swipeTopCardLeft(180);

            }
        });

        btnSwipeRight= (FloatingActionButton) view.findViewById(R.id.button2);

        btnSwipeRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardStack.swipeTopCardRight(180);
            }
        });
    }

    private void addCandidate(String candidate){
        testData.add(candidate);
        adapter.notifyDataSetChanged();
    }
    private void startAnimation() {
        //if it's not running
        if (!rippleBackground1.isRippleAnimationRunning()) {
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
        }

    }


}
