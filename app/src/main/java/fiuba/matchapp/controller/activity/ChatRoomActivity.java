package fiuba.matchapp.controller.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pkmmte.view.CircularImageView;

import java.util.ArrayList;
import java.util.List;

import fiuba.matchapp.R;
import fiuba.matchapp.adapter.ChatRoomThreadAdapter;
import fiuba.matchapp.adapter.LoadEarlierMessages;
import fiuba.matchapp.model.ChatRoom;
import fiuba.matchapp.model.MyMessage;
import fiuba.matchapp.model.ReceivedMessage;
import fiuba.matchapp.networking.gcm.Config;
import fiuba.matchapp.app.MyApplication;
import fiuba.matchapp.model.Message;
import fiuba.matchapp.model.User;
import fiuba.matchapp.networking.gcm.NotificationUtils;
import fiuba.matchapp.networking.httpRequests.okhttp.GetChatMessagesOkHttp;
import fiuba.matchapp.networking.httpRequests.okhttp.PostNewMessageOkHttp;
import fiuba.matchapp.utils.ImageBase64;
import fiuba.matchapp.utils.NewMessageNotificationHandler;
import fiuba.matchapp.view.LockedProgressDialog;

public class ChatRoomActivity extends AppCompatActivity implements LoadEarlierMessages {

    private String TAG = ChatRoomActivity.class.getSimpleName();

    private ChatRoom chatRoom;

    private RecyclerView recyclerView;
    private ChatRoomThreadAdapter mAdapter;
    private ArrayList<Message> messageArrayList;

    private EditText inputMessage;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private ImageButton btnSend;
    private CircularImageView circleProfileImg;
    private TextView titleChat;
    private LockedProgressDialog progressDialog;
    private RelativeLayout containerChatRoom;
    private RelativeLayout contentRetry;
    private ImageView retryImage;
    private String olderShownMsgId;
    private RelativeLayout contentNoMessages;
    private TextView subtitleNoMessages;

    @Override
    protected void onStop() {
        super.onStop();
        MyApplication.getInstance().cancelAllPendingAppServerRequests();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Intent intent = getIntent();
        if(intent.hasExtra("chatroom")){
            this.chatRoom = (ChatRoom) intent.getSerializableExtra("chatroom");
            Log.d(TAG, "Conversacion con " + chatRoom.getOtherUser().getEmail());
        }else{
            finish();
        }
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    Log.d(TAG,"LLEGA");
                    // new push message is received
                    handlePushNotification(intent);
                }
            }
        };
        initViews();

        messageArrayList = new ArrayList<>();
        mAdapter = new ChatRoomThreadAdapter(this, messageArrayList);
        recyclerView.setAdapter(mAdapter);


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        progressDialog = new LockedProgressDialog(ChatRoomActivity.this, R.style.AppTheme_Dark_Dialog);

        progressDialog.setMessage(getResources().getString(R.string.fetching_chat_history));

        if(chatRoom.hasMessages()){
            fetchChatThread(this.chatRoom.getLastMessage().getId());
        }


    }

    private void initViews() {
        setContentView(R.layout.activity_chat_room);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        titleChat = (TextView) findViewById(R.id.title_chat);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//muestra el volver al home
        circleProfileImg = (CircularImageView) findViewById(R.id.profile_img_toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        inputMessage = (EditText) findViewById(R.id.message);
        btnSend = (ImageButton) findViewById(R.id.btn_send);
        containerChatRoom = (RelativeLayout) findViewById(R.id.containerChatRoom);

        contentRetry = (RelativeLayout) findViewById(R.id.contentNoMessages);
        contentRetry.setVisibility(View.GONE);

        contentNoMessages = (RelativeLayout) findViewById(R.id.contentNoMessages);
        contentNoMessages.setVisibility(View.GONE);

        retryImage = (ImageView) findViewById(R.id.retryImage);
        retryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchChatThread( chatRoom.getLastMessage().getId());
            }
        });
        subtitleNoMessages = (TextView) findViewById(R.id.subtitleNoMessages);
        if(TextUtils.equals(chatRoom.getOtherUser().getGenre(),"Mujer")){
            subtitleNoMessages.setText(getResources().getString(R.string.no_messages_yet_woman));
        }else {
            subtitleNoMessages.setText(getResources().getString(R.string.no_messages_yet));
        }

        titleChat.setText(this.chatRoom.getOtherUser().getAlias());
        if(!TextUtils.isEmpty(this.chatRoom.getOtherUser().getPhotoProfile())){
            circleProfileImg.setImageBitmap(ImageBase64.Base64ToBitmap(this.chatRoom.getOtherUser().getPhotoProfile()));
        }

    }

    /**
     * Handling new push message, will add the message to
     * recycler view and scroll it to bottom
     */
    private void handlePushNotification(Intent intent) {
        if (intent.hasExtra("type")) {
            String type = intent.getStringExtra("type");
            if (type == Config.PUSH_TYPE_NEW_MESSAGE) {

                String chat_room_id = NewMessageNotificationHandler.getChatRoomId(intent);
                ReceivedMessage message = NewMessageNotificationHandler.getMessage(intent);

                if ((TextUtils.equals(chat_room_id,chatRoom.getId()) && (message != null) )){
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
        // by doing this, the activity will be notified each time a new message arrives
        registerNotificationBroadcastReceiver();
        NotificationUtils.clearNotifications();
    }
    private void registerNotificationBroadcastReceiver() {
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.onBackPressed();
            return true;
        }
        ;
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }


    /**
     * Post de un nuevo mensaje
     */
    private void sendMessage() {
        final String message = this.inputMessage.getText().toString().trim();

        if (TextUtils.isEmpty(message)) {
            return;
        }

        PostNewMessageOkHttp request = new PostNewMessageOkHttp(chatRoom.getOtherUser().getEmail(),message) {
            @Override
            protected void onPostChatNewMessageRequestConnectionError() {

            }

            @Override
            protected void onPostChatNewMessageRequestSuccess() {

            }

            @Override
            protected void logout() {

            }
        };
        request.makeRequest();
        String timestamp = Long.toString(System.currentTimeMillis() / 1000);
        MyMessage sentMessage = new MyMessage(message,timestamp);
        addNewMessage(sentMessage);
        this.inputMessage.setText("");

    }


    /**
     * Fetch del historial de conversacion
     */
    private void fetchChatThread(String messageId) {

        progressDialog.show();
        contentRetry.setVisibility(View.GONE);
        contentNoMessages.setVisibility(View.GONE);


        GetChatMessagesOkHttp request = new GetChatMessagesOkHttp(chatRoom.getId(),messageId) {
            @Override
            protected void logout() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        progressDialog.hide();
                        MyApplication.getInstance().logout();
                    }
                });
            }

            @Override
            protected void onConnectionError() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        progressDialog.hide();
                        contentRetry.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        Snackbar.make(containerChatRoom,getResources().getString(R.string.internet_problem) , Snackbar.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            protected void onSuccess(final List<Message> messages, final int lastMessageId) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        recyclerView.setVisibility(View.VISIBLE);
                        if(messages.size() > 0) {
                            ArrayList<Message> temp = new ArrayList<>();
                            temp.addAll(messages);
                            temp.addAll(messageArrayList);
                            messageArrayList.clear();
                            messageArrayList.addAll(temp);

                            //id to fetch more chat history messages from appServer
                            olderShownMsgId = Integer.toString(lastMessageId);

                            if(lastMessageId <= 0){
                                //hide load More button
                                mAdapter.setLoadEarlierMsgs(false);
                                mAdapter.notifyItemChanged(0);
                            }

                            progressDialog.hide();
                            mAdapter.notifyDataSetChanged();
                            if (mAdapter.getItemCount() > 1) {
                                recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);
                            }
                        }else {
                            contentRetry.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.GONE);
                            contentNoMessages.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        };
        request.makeRequest();
    }

    @Override
    public void onLoadMore() {
        if (chatRoom.hasOlderMessages()) {
            fetchChatThread(olderShownMsgId);
        }
    }
}
