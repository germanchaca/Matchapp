package fiuba.matchapp.controller.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fiuba.matchapp.R;
import fiuba.matchapp.app.MyApplication;
import fiuba.matchapp.controller.activity.ChatRoomActivity;
import fiuba.matchapp.adapter.ChatRoomsAdapter;
import fiuba.matchapp.networking.httpRequests.okhttp.GetChatRoomsOkHttp;
import fiuba.matchapp.view.LockedProgressDialog;
import fiuba.matchapp.view.SimpleDividerItemDecoration;
import fiuba.matchapp.model.ChatRoom;
import fiuba.matchapp.model.Message;

/**
 * Created by german on 4/11/2016.
 * Este fragment muestra las conversaciones abiertas del usuario loggeado
 */
public class OpenChatsFragment extends Fragment {

    private String TAG = OpenChatsFragment.class.getSimpleName();

    private ArrayList<ChatRoom> chatRoomArrayList;
    private ChatRoomsAdapter mAdapter;
    private RecyclerView recyclerView;
    private Context context;
    private LockedProgressDialog progressDialog;
    private RelativeLayout containerChats;
    private RelativeLayout contentRetry;
    private ImageView retryImage;
    private TextView subtitleRetry;
    private RelativeLayout containerNoChatRooms;

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResumeActivity");
        fetchChatRooms();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_open_chats, container, false);
        init(view);
        return view;
    }

    private void init(View view) {

        context = getActivity().getApplicationContext();

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        chatRoomArrayList = new ArrayList<>();
        mAdapter = new ChatRoomsAdapter(context, chatRoomArrayList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(context));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new ChatRoomsAdapter.RecyclerTouchListener(context, recyclerView, new ChatRoomsAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                ChatRoom chatRoom = chatRoomArrayList.get(position);
                Intent intent = new Intent(context, ChatRoomActivity.class);
                intent.putExtra("chatroom", (Parcelable) chatRoom);
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        containerChats = (RelativeLayout) view.findViewById(R.id.containerChats);
        containerNoChatRooms = (RelativeLayout) view.findViewById(R.id.contentNoChatRooms);
        containerNoChatRooms.setVisibility(View.GONE);

        contentRetry = (RelativeLayout) view.findViewById(R.id.contentRetry);
        contentRetry.setVisibility(View.GONE);
        retryImage = (ImageView) view.findViewById(R.id.retryImage);
        retryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchChatRooms();
            }
        });
        subtitleRetry = (TextView) view.findViewById(R.id.subtitleRetry);
        subtitleRetry.setText(getResources().getString(R.string.retry_fetching_all_chatrooms));

        progressDialog = new LockedProgressDialog(getActivity(), R.style.AppTheme_Dark_Dialog);

        progressDialog.setMessage(getResources().getString(R.string.fetching_all_chats));

    }

    /**
     * Actualiza la cantidad de mensajes no leidos y ultimo mensaje de un chatRoom
     */
    public void updateRow(String chatRoomId, Message message) {
        for (ChatRoom cr : chatRoomArrayList) {
            if (cr.getId().equals(chatRoomId)) {
                int index = chatRoomArrayList.indexOf(cr);
                cr.setLastMessage(message);
                cr.setUnreadCount(cr.getUnreadCount() + 1);
                chatRoomArrayList.remove(index);
                chatRoomArrayList.add(index, cr);
                containerNoChatRooms.setVisibility(View.GONE);
                break;
            }
        }
        mAdapter.notifyDataSetChanged();
    }


    private void fetchChatRooms() {
        progressDialog.show();
        contentRetry.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);


        GetChatRoomsOkHttp request = new GetChatRoomsOkHttp() {
            @Override
            protected void logout() {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        progressDialog.dismiss();
                        MyApplication.getInstance().logout();
                    }
                });
            }

            @Override
            protected void onGetChatOpenRoomsRequestFailedUserConnectionError() {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        progressDialog.dismiss();
                        MyApplication.getInstance().logout();
                        recyclerView.setVisibility(View.GONE);
                        contentRetry.setVisibility(View.VISIBLE);
                        Snackbar.make(containerChats,getResources().getString(R.string.internet_problem) , Snackbar.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            protected void onGetChatRoomsRequestSuccess(final List<ChatRoom> chatRooms) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        if(chatRooms.size() == 0){
                            recyclerView.setVisibility(View.GONE);
                            containerNoChatRooms.setVisibility(View.VISIBLE);
                        }
                        chatRoomArrayList.clear();
                        chatRoomArrayList.addAll(chatRooms);
                        mAdapter.notifyDataSetChanged();

                        progressDialog.dismiss();
                    }
                });
            }
        };
        request.makeRequest();


    }
}
