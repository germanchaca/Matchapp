package fiuba.matchapp.controller.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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
import fiuba.matchapp.networking.gcm.Config;
import fiuba.matchapp.app.MyApplication;
import fiuba.matchapp.model.Message;
import fiuba.matchapp.model.User;
import fiuba.matchapp.networking.httpRequests.GetChatHistoryRequest;
import fiuba.matchapp.networking.httpRequests.PostChatNewMessageRequest;
import fiuba.matchapp.utils.ImageBase64;
import fiuba.matchapp.view.LockedProgressDialog;

public class ChatRoomActivity extends AppCompatActivity implements LoadEarlierMessages {

    private String TAG = ChatRoomActivity.class.getSimpleName();

    private RecyclerView recyclerView;
    private ChatRoomThreadAdapter mAdapter;
    private ArrayList<Message> messageArrayList;
    private EditText inputMessage;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private ImageButton btnSend;
    private CircularImageView circleProfileImg;
    private TextView titleChat;
    private String selfUserId;
    private ChatRoom chatRoom;
    private LockedProgressDialog progressDialog;
    private RelativeLayout containerChatRoom;
    private RelativeLayout contentRetry;
    private ImageView retryImage;
    private TextView subtitleRetry;
    private User userMatched;
    private boolean hasChatRoomId = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();

        Intent intent = getIntent();
        if(intent.hasExtra("chatroom")){
            this.chatRoom = (ChatRoom) intent.getSerializableExtra("chatroom");
            this.userMatched = chatRoom.getUser();
            hasChatRoomId = true;
        }
        if(intent.hasExtra("user")){
            this.userMatched = intent.getParcelableExtra("user");
        }

        selfUserId = MyApplication.getInstance().getPrefManager().getUser().getId();


        titleChat.setText(this.userMatched.getAlias());
        if(!TextUtils.isEmpty(this.userMatched.getPhotoProfile())){
            circleProfileImg.setImageBitmap(ImageBase64.Base64ToBitmap(this.userMatched.getPhotoProfile()));
        }


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        messageArrayList = new ArrayList<>();
        mAdapter = new ChatRoomThreadAdapter(this, messageArrayList, selfUserId);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

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

        progressDialog = new LockedProgressDialog(ChatRoomActivity.this, R.style.AppTheme_Dark_Dialog);

        progressDialog.setMessage(getResources().getString(R.string.fetching_chat_history));
        if (hasChatRoomId){
            fetchChatThread(chatRoom.getId());
        }

    }

    private void loadMore() {
        if (hasChatRoomId) {
            if(this.chatRoom.getLastMessage() != null){
                fetchChatThread(this.chatRoom.getLastMessage().getId());
            }
        }
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
        containerChatRoom = (RelativeLayout) findViewById(R.id.containerChatRoom);

        contentRetry = (RelativeLayout) findViewById(R.id.contentRetry);
        contentRetry.setVisibility(View.GONE);
        retryImage = (ImageView) findViewById(R.id.retryImage);
        retryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchChatThread( chatRoom.getLastMessage().getId());
            }
        });
        subtitleRetry = (TextView) findViewById(R.id.subtitleRetry);
        subtitleRetry.setText(getResources().getString(R.string.fetching_chat_history));

    }

    /**
     * Handling new push message, will add the message to
     * recycler view and scroll it to bottom
     */
    private void handlePushNotification(Intent intent) {
        if (intent.hasExtra("type")) {
            String type = intent.getStringExtra("type");
            if (type == Config.PUSH_TYPE_NEW_MESSAGE) {

                if (intent.hasExtra("chat_room_id")) {
                    String chat_room_id = intent.getStringExtra("chat_room_id");
                    if (intent.hasExtra("message_id")) {
                        String message_id = intent.getStringExtra("message_id");
                        if (intent.hasExtra("message")) {
                            String messageBody = intent.getStringExtra("message");
                            if (intent.hasExtra("created_at")) {
                                String timestamp = intent.getStringExtra("created_at");

                                Message message = new Message(message_id,messageBody,timestamp,Message.STATUS_UNREAD,"0");
                                addNewMessage(message);
                            }
                        }
                    }
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
        String userId;
        if(userMatched == null){
            userId = chatRoom.getUser().getId();
        }else{
            userId = userMatched.getId();
        }

        PostChatNewMessageRequest request = new PostChatNewMessageRequest(userId,message) {
            @Override
            protected void onPostChatNewMessageRequestFailedDefaultError() {
                //TODO tildar como no enviado con opción de volver a enviar
            }

            @Override
            protected void onPostChatNewMessageRequestFailedUserConnectionError() {
                //TODO tildar como no enviado con opción de volver a enviar
            }

            @Override
            protected void onPostChatNewMessageRequestSuccess() {
                //TODO tildar como enviado
            }
        };
        request.make();
        Message sentMessage = new Message();
        sentMessage.setCreatedAt(Long.toString(System.currentTimeMillis() / 1000));
        sentMessage.setMessage(message);
        sentMessage.setStatus(Message.STATUS_UNSENT);
        addNewMessage(sentMessage);

        this.inputMessage.setText("");


    }


    /**
     * Fetch del historial de conversacion
     */
    private void fetchChatThread(String messageId) {

        progressDialog.show();
        contentRetry.setVisibility(View.GONE);

        GetChatHistoryRequest request = new GetChatHistoryRequest(chatRoom.getId(),messageId) {
            @Override
            protected void onGetChatHistoryRequestFailedDefaultError() {
                progressDialog.hide();
                contentRetry.setVisibility(View.VISIBLE);
                Snackbar.make(containerChatRoom,getResources().getString(R.string.internet_problem) , Snackbar.LENGTH_LONG).show();

            }

            @Override
            protected void onGetChatHistoryRequestFailedUserConnectionError() {
                progressDialog.hide();
                contentRetry.setVisibility(View.VISIBLE);
                Snackbar.make(containerChatRoom,getResources().getString(R.string.internet_problem) , Snackbar.LENGTH_LONG).show();

            }

            @Override
            protected void onGetChatHistoryRequestSuccess(List<Message> chatHistory) {
                progressDialog.hide();

                if(chatHistory.size() > 0) {
                    ArrayList<Message> temp = new ArrayList<>();
                    temp.addAll(chatHistory);
                    temp.addAll(messageArrayList);
                    messageArrayList.clear();
                    messageArrayList.addAll(temp);

                    mAdapter.notifyDataSetChanged();
                    if (mAdapter.getItemCount() > 1) {
                        recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);
                    }
                }
            }


            @Override
            protected void logout() {
                progressDialog.hide();
                MyApplication.getInstance().logout();
            }
        };
        request.make();



    }

    @Override
    public void onLoadMore() {
        loadMore();
    }
}
