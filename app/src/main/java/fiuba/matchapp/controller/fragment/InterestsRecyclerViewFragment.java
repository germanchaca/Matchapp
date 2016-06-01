package fiuba.matchapp.controller.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dpizarro.autolabel.library.AutoLabelUI;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fiuba.matchapp.R;
import fiuba.matchapp.adapter.InterestsRecyclerAdapter;
import fiuba.matchapp.model.Interest;

public class InterestsRecyclerViewFragment extends Fragment {

    private final String KEY_INSTANCE_STATE = "stateInterests";

    private AutoLabelUI mAutoLabel;
    public List<Interest> mInterestsList;
    private InterestsRecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private String category_name;
    private List<String> interest_items;
    private String category_array_name;
    private TextView txtSubtitle;
    private TextView txtTitle;


    public static InterestsRecyclerViewFragment newInstance(String category, ArrayList<Interest> interests) {
        InterestsRecyclerViewFragment f = new InterestsRecyclerViewFragment();

        Bundle args = new Bundle();
        args.putString("category", category);
        args.putParcelableArrayList("values", interests);
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        Bundle args = getArguments();
        String category = args.getString("category");
        ArrayList<Interest> values = args.getParcelableArrayList("values");

        initInterestItemsString(category, values);

        View view = inflater.inflate(R.layout.fragment_interest, container, false);
        findViews(view);
        txtTitle.setText(category_name);
        setListeners();
        setRecyclerView();
        return view;
    }

    private void initInterestItemsString(String category, List<Interest> values) {

        category_name = category;

        interest_items = new ArrayList<>();

        for(Interest i: values){
            interest_items.add(i.getDescription());
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            List<Interest> interests = savedInstanceState.getParcelableArrayList(KEY_INSTANCE_STATE);
            if (interests != null) {
                mInterestsList = interests;

                adapter.setInterests(interests);
                recyclerView.setAdapter(adapter);
            }
        }
    }

    private void itemListClicked(int position) {
        Interest interest = mInterestsList.get(position);
        boolean isSelected = interest.isSelected();
        boolean success;
        if (isSelected) {
            success = mAutoLabel.removeLabel(position);
        } else {
            success = mAutoLabel.addLabel(interest.getDescription(), position);
        }
        if (success) {
            adapter.setItemSelected(position, !isSelected);
        }
    }

    private void setListeners() {

        mAutoLabel.setOnRemoveLabelListener(new AutoLabelUI.OnRemoveLabelListener() {
            @Override
            public void onRemoveLabel(View view, int position) {
                adapter.setItemSelected(position, false);
            }

        });

        mAutoLabel.setOnLabelsEmptyListener(new AutoLabelUI.OnLabelsEmptyListener() {
            @Override
            public void onLabelsEmpty() {
                //isEmpty=true;
            }
        });

    }

    private void findViews(View view) {

        mAutoLabel = (AutoLabelUI) view.findViewById(R.id.label_view);
        mAutoLabel.setBackgroundResource(R.drawable.round_corner_background);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        txtTitle = (TextView) view.findViewById(R.id.title_category);
        txtSubtitle = (TextView) view.findViewById(R.id.subtitle_category);

        String sourceString = getResources().getString(R.string.intro_interests_subtitle);


        final SpannableStringBuilder str = new SpannableStringBuilder(sourceString);
        str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 20, 23, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        str.setSpan(new RelativeSizeSpan(1.3f), 20, 23, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        txtSubtitle.setText(str);
    }

    private void setRecyclerView() {
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(llm);

        mInterestsList = new ArrayList<>();

        //Populate list

        for (int i = 0; i < interest_items.size(); i++) {
            mInterestsList.add(new Interest(Integer.toString(i),category_array_name, interest_items.get(i)));
        }

        adapter = new InterestsRecyclerAdapter(mInterestsList);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new InterestsRecyclerAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View v, int position) {
                //isEmpty = false;
                itemListClicked(position);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(KEY_INSTANCE_STATE,
                (ArrayList<? extends Parcelable>) adapter.getInterests());

    }


}
