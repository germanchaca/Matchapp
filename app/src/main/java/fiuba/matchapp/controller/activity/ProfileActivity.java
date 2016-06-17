package fiuba.matchapp.controller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        ImageView imageView = (ImageView) findViewById(R.id.user_image);

        EditText txtName = (EditText) findViewById(R.id.txtName);
        EditText txtAlias = (EditText) findViewById(R.id.txtAlias);
        EditText txtAge = (EditText) findViewById(R.id.txtAge);
        TextView txtEditAddress = (TextView) findViewById(R.id.txtLocation);

        setSupportActionBar(toolbar);

        Intent i = getIntent();
        User user = (User) i.getSerializableExtra("user");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarLayout.setTitle(user.getAlias());

        Map<String, List<UserInterest>> commonInterests = InterestsUtils.getCommonInterests(user.getInterests(), MyApplication.getInstance().getPrefManager().getUser().getInterests());

        List<UserInterest> commonInterestsLst = commonInterests.get(InterestsUtils.COMMON_INTERESTS);
        List<UserInterest> moreInterestsLst = commonInterests.get(InterestsUtils.MORE_INTERESTS);

        String[] commonTagGroup  = commonInterestsLst.toArray(new String[0]);
        String[] moreTagGroup  = moreInterestsLst.toArray(new String[0]);

        TagGroup mTagGroup = (TagGroup) findViewById(R.id.tag_group);
        mTagGroup.setTags(commonTagGroup);

        TagGroup mTagGroupMore = (TagGroup) findViewById(R.id.tag_group_more);
        mTagGroupMore.setTags(moreTagGroup);

        if(!TextUtils.isEmpty(user.getPhotoProfile())){
            imageView.setImageBitmap(ImageBase64.Base64ToBitmap(user.getPhotoProfile()));
        }
        txtName.setText(user.getName());
        txtAlias.setText(user.getAlias());
        txtAge.setText(user.getAge());

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
