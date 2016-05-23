package fiuba.matchapp.controller.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import fiuba.matchapp.R;
import fiuba.matchapp.app.MyApplication;
import fiuba.matchapp.model.User;

/**
 * Created by german on 5/23/2016.
 */
public class NewMatchActivity extends AppCompatActivity {

    private Button btnInitChat;
    private Button btnLater;
    private String chatRoomId;
    private User userMatched;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        userMatched = (User) intent.getSerializableExtra("new_match_user");
        chatRoomId = intent.getStringExtra("chat_room_id");

        //setear la foto en base 64 guardada
        User user = MyApplication.getInstance().getPrefManager().getUser();

        setContentView(R.layout.activity_new_match);

        initButtonStartChatConversation();

        initButtonLater();

        initSubtitleTextView(userMatched);

    }

    private void initButtonStartChatConversation() {
        btnInitChat = (Button) findViewById(R.id.btn_chat);
        btnInitChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startChatting();
            }
        });
    }

    private void startChatting() {
        Intent intent = new Intent(getApplicationContext(), ChatRoomActivity.class);
        intent.putExtra("chat_room_id", chatRoomId);
        intent.putExtra("name", userMatched.getName());
        startActivity(intent);
        finish();
    }

    private void initButtonLater() {
        btnLater = (Button) findViewById(R.id.btn_later);
        btnLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initSubtitleTextView(User userMatched) {
        TextView subtitle = (TextView) findViewById(R.id.subtitle);

        String sourceString = userMatched.getName() + " " + getResources().getString(R.string.activity_new_match_subtitle);

        Spannable spannable = new SpannableString(sourceString);

        spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.primary)), 0,userMatched.getName().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        subtitle.setText(spannable, TextView.BufferType.SPANNABLE);
    }
}
