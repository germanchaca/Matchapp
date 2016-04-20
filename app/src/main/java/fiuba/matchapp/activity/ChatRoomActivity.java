package fiuba.matchapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.pkmmte.view.CircularImageView;

import java.util.ArrayList;

import fiuba.matchapp.R;
import fiuba.matchapp.adapter.ChatRoomThreadAdapter;
import fiuba.matchapp.model.Message;
import fiuba.matchapp.model.User;

public class ChatRoomActivity extends AppCompatActivity {

    private String TAG = ChatRoomActivity.class.getSimpleName();

    private String chatRoomId;
    private RecyclerView recyclerView;
    private ChatRoomThreadAdapter mAdapter;
    private ArrayList<Message> messageArrayList;
    private EditText inputMessage;
    private ImageButton btnSend;
    private CircularImageView circleProfileImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView titleChat = (TextView) findViewById(R.id.title_chat);
        setSupportActionBar(toolbar);

        //TODO cambiar esta imagen por la de perfil de usuario
        circleProfileImg = (CircularImageView) findViewById(R.id.profile_img_toolbar);

        inputMessage = (EditText) findViewById(R.id.message);
        btnSend = (ImageButton) findViewById(R.id.btn_send);

        Intent intent = getIntent();
        chatRoomId = intent.getStringExtra("chat_room_id");
        String title = intent.getStringExtra("name");

        titleChat.setText(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//muestra el volver al home

        if (chatRoomId == null) {
            Toast.makeText(getApplicationContext(), "Chat room not found!", Toast.LENGTH_SHORT).show();
            finish();
        }

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        messageArrayList = new ArrayList<>();

        // TODO sacar los datos de usuario de SharedServer
        String selfUserId = "0";

        mAdapter = new ChatRoomThreadAdapter(this, messageArrayList, selfUserId);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        fetchChatThread();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == android.R.id.home) {
                this.onBackPressed();
            return true;
        };
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * Post de un nuevo mensaje
     * */
    private void sendMessage() {
        final String message = this.inputMessage.getText().toString().trim();

        if (TextUtils.isEmpty(message)) {
            Toast.makeText(getApplicationContext(), "Enter a message", Toast.LENGTH_SHORT).show();
            return;
        }

        this.inputMessage.setText("");


    }


    /**
     * Fetch del historial de conversacion
     * */
    private void fetchChatThread() {

        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();

        String commentId = "1";
        String commentText = "hola";
        String createdAt = ts;

        String userId = "1";
        String userName = "camila";
        User user = new User(userId, userName, null);

        Message message = new Message();
        message.setId(commentId);
        message.setMessage(commentText);
        message.setCreatedAt(createdAt);
        message.setUser(user);

        messageArrayList.add(message);

        mAdapter.notifyDataSetChanged();

        if (mAdapter.getItemCount() > 1) {
            recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);
        }
    }

}
