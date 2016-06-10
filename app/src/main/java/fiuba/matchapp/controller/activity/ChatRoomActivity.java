package fiuba.matchapp.controller.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
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

import com.pkmmte.view.CircularImageView;

import java.util.ArrayList;

import fiuba.matchapp.R;
import fiuba.matchapp.adapter.ChatRoomThreadAdapter;
import fiuba.matchapp.networking.gcm.Config;
import fiuba.matchapp.app.MyApplication;
import fiuba.matchapp.model.Message;
import fiuba.matchapp.model.User;

public class ChatRoomActivity extends AppCompatActivity {

    private String TAG = ChatRoomActivity.class.getSimpleName();

    private RecyclerView recyclerView;
    private ChatRoomThreadAdapter mAdapter;
    private ArrayList<Message> messageArrayList;
    private EditText inputMessage;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private ImageButton btnSend;
    private CircularImageView circleProfileImg;
    private TextView titleChat;
    private User user;
    private String selfUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();

        Intent intent = getIntent();
        this.user  = (User) intent.getSerializableExtra("user");
        if(this.user == null)
            return;

        titleChat.setText(this.user.getAlias());
        
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        messageArrayList = new ArrayList<>();

        selfUserId = MyApplication.getInstance().getPrefManager().getUser().getId();

        mAdapter = new ChatRoomThreadAdapter(this, messageArrayList, selfUserId);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push message is received
                    handlePushNotification(intent);
                }
            }
        };


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        fetchChatThread();
    }

    private void initViews() {
        setContentView(R.layout.activity_chat_room);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        titleChat = (TextView) findViewById(R.id.title_chat);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//muestra el volver al home
        circleProfileImg = (CircularImageView) findViewById(R.id.profile_img_toolbar);

        inputMessage = (EditText) findViewById(R.id.message);
        btnSend = (ImageButton) findViewById(R.id.btn_send);
    }

    /**
     * Handling new push message, will add the message to
     * recycler view and scroll it to bottom
     * */
    private void handlePushNotification(Intent intent) {
        if(intent.hasExtra("user_id")){
            String userId = intent.getStringExtra("user_id");
            if (intent.hasExtra("message")){
                Message message = (Message) intent.getSerializableExtra("message");

                if( this.user.getEmail() == userId){
                    addNewMessage(message);
                }
            }
        }
    }

    private void addNewMessage(Message message) {
        messageArrayList.add(message);
        mAdapter.notifyDataSetChanged();
        if (mAdapter.getItemCount() > 1) {
            recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);
        }
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
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }


    /**
     * Post de un nuevo mensaje
     * */
    private void sendMessage() {
        final String message = this.inputMessage.getText().toString().trim();

        if (TextUtils.isEmpty(message)) {
            return;
        }

        this.inputMessage.setText("");



    }


    /**
     * Fetch del historial de conversacion
     * */
    private void fetchChatThread() {

        Long tsLong = System.currentTimeMillis() / 1000;
        String ts = tsLong.toString();

        String commentId = "1";
        String commentText = "hola";
        String createdAt = ts;

        String userId = "1";
        String userName = "camila";
        User user = MyApplication.getInstance().getPrefManager().getUser();

        Message message = new Message();
        message.setId(commentId);
        message.setMessage(commentText);
        message.setCreatedAt(createdAt);
        message.setUser(user);

        addNewMessage(message);
    }

        /**
         * Fetching all the messages of a single chat room
         * */
        /*private void fetchChatThread() {

            String endPoint = RestAPIContract.CHAT_THREAD.replace("_ID_", chatRoomId);
            Log.e(TAG, "endPoint: " + endPoint);

            StringRequest strReq = new StringRequest(Request.Method.GET,
                    endPoint, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    Log.e(TAG, "response: " + response);

                    try {
                        JSONObject obj = new JSONObject(response);

                        // check for error
                        if (obj.getBoolean("error") == false) {
                            JSONArray commentsObj = obj.getJSONArray("messages");

                            for (int i = 0; i < commentsObj.length(); i++) {
                                JSONObject commentObj = (JSONObject) commentsObj.get(i);

                                String commentId = commentObj.getString("message_id");
                                String commentText = commentObj.getString("message");
                                String createdAt = commentObj.getString("created_at");

                                JSONObject userObj = commentObj.getJSONObject("user");
                                String userId = userObj.getString("user_id");
                                String userName = userObj.getString("username");
                                User user = new User(userId, userName, null);

                                Message message = new Message();
                                message.setId(commentId);
                                message.setMessage(commentText);
                                message.setCreatedAt(createdAt);
                                message.setUser(user);

                                messageArrayList.add(message);
                            }

                            mAdapter.notifyDataSetChanged();
                            if (mAdapter.getItemCount() > 1) {
                                recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);
                            }

                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "json parsing error: " + e.getMessage());
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    NetworkResponse networkResponse = error.networkResponse;
                    Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                }
            });

            //Adding request to request queue
            MyApplication.getInstance().addToRequestQueue(strReq);
        }
        */
}
