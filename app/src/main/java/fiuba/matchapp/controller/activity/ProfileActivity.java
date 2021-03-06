package fiuba.matchapp.controller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fiuba.matchapp.R;
import fiuba.matchapp.app.MyApplication;
import fiuba.matchapp.model.Interest;
import fiuba.matchapp.model.User;
import fiuba.matchapp.model.UserInterest;
import fiuba.matchapp.utils.AdressUtils;
import fiuba.matchapp.utils.ImageBase64;
import fiuba.matchapp.utils.InterestsUtils;
import me.gujun.android.taggroup.TagGroup;

/**
 * Created by german on 4/19/2016.
 */
public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        ImageView imageView = (ImageView) findViewById(R.id.user_image);

        TextView txtName = (TextView) findViewById(R.id.txtName);
        TextView txtAge = (TextView) findViewById(R.id.txtAge);
        TextView txtEditAddress = (TextView) findViewById(R.id.txtLocation);

        setSupportActionBar(toolbar);

        Intent i = getIntent();
        User user = i.getParcelableExtra("user");
        Log.d(TAG, user.getAlias());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarLayout.setTitle(user.getAlias());

        Map<String, List<UserInterest>> commonInterests = InterestsUtils.getCommonInterests(user.getInterests(), MyApplication.getInstance().getPrefManager().getUser().getInterests());

        List<UserInterest> commonInterestsLst = commonInterests.get(InterestsUtils.COMMON_INTERESTS);
        List<UserInterest> moreInterestsLst;
        if(commonInterests.containsKey(InterestsUtils.MORE_INTERESTS)){
            moreInterestsLst = commonInterests.get(InterestsUtils.MORE_INTERESTS);
        }else{
            moreInterestsLst = new ArrayList<>();
        }

        for(UserInterest c:commonInterestsLst){
            Log.d("COMMON",c.getCategory() + " " + c.getDescription());
        }
        for(UserInterest c:moreInterestsLst){
            Log.d("UNCOMMON",c.getCategory() + " " + c.getDescription());
        }

        String[] commonTagGroup = new String[commonInterestsLst.size()];
        for (int j=0; j < commonInterestsLst.size(); j++) {
            commonTagGroup[j] = commonInterestsLst.get(j).getDescription().toString();
        }
        String[] moreTagGroup = new String[moreInterestsLst.size()];
        for (int j=0; j < moreInterestsLst.size(); j++) {
            moreTagGroup[j] = moreInterestsLst.get(j).getDescription().toString();
        }

        TagGroup mTagGroup = (TagGroup) findViewById(R.id.tag_group);
        mTagGroup.setTags(commonTagGroup);

        TagGroup mTagGroupMore = (TagGroup) findViewById(R.id.tag_group_more);
        mTagGroupMore.setTags(moreTagGroup);

        if(!TextUtils.isEmpty(user.getPhotoProfile())){
            imageView.setImageBitmap(ImageBase64.Base64ToBitmap(user.getPhotoProfile()));
        }
        txtName.setText(user.getName());
        txtAge.setText( Integer.toString(user.getAge()));

        try {
            txtEditAddress.setText(AdressUtils.getParsedAddress(user.getLatitude(),user.getLongitude()));
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
            break;
        }
        return super.onOptionsItemSelected(item);
    }
}
