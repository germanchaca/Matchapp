package fiuba.matchapp.controller.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import fiuba.matchapp.R;
import fiuba.matchapp.app.MyApplication;
import fiuba.matchapp.model.User;

/**
 * Created by german on 5/23/2016.
 */
public class NewMatchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        User userMatched = (User) intent.getSerializableExtra("new_match_user");
        User user = MyApplication.getInstance().getPrefManager().getUser();

        setContentView(R.layout.activity_new_match);

        TextView subtitle = (TextView) findViewById(R.id.subtitle);

        String sourceString = userMatched.getName() + " " + getResources().getString(R.string.activity_new_match_subtitle);

        Spannable spannable = new SpannableString(sourceString);

        spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.primary)), 0,userMatched.getName().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        subtitle.setText(spannable, TextView.BufferType.SPANNABLE);


    }
}
