package fiuba.matchapp.controller.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import fiuba.matchapp.R;
import fiuba.matchapp.controller.activity.ChatRoomActivity;
import fiuba.matchapp.adapter.ChatRoomsAdapter;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_open_chats, container, false);
        init(view);

        fetchChatRooms();

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
                intent.putExtra("chat_room_id", chatRoom.getId());
                intent.putExtra("name", chatRoom.getName());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

    }

    /**
     * Actualiza la cantidad de mensajes no leidos y ultimo mensaje de un chatRoom
     */
    private void updateRow(String chatRoomId, Message message) {
        for (ChatRoom cr : chatRoomArrayList) {
            if (cr.getId().equals(chatRoomId)) {
                int index = chatRoomArrayList.indexOf(cr);
                cr.setLastMessage(message.getMessage());
                cr.setUnreadCount(cr.getUnreadCount() + 1);
                chatRoomArrayList.remove(index);
                chatRoomArrayList.add(index, cr);
                break;
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Ahora esta hardCodeado pero luego debe hacer el fetch
     * fetching the chat rooms by making http call
     */
    private void fetchChatRooms() {


        ChatRoom cr = new ChatRoom();
        cr.setId("1");
        cr.setName("german");
        cr.setLastMessage("");
        cr.setUnreadCount(0);
        cr.setTimestamp("1460090232");

        chatRoomArrayList.add(cr);

        ChatRoom cr2 = new ChatRoom();
        cr2.setId("2");
        cr2.setName("luis");
        cr2.setLastMessage("Hola camila!");
        cr2.setUnreadCount(1);
        cr2.setTimestamp("1460090232");

        chatRoomArrayList.add(cr2);

        ChatRoom cr3 = new ChatRoom();
        cr3.setId("3");
        cr3.setName("jose");
        cr3.setLastMessage("chau loco");
        cr3.setUnreadCount(2);
        cr3.setTimestamp("1457413053");

        chatRoomArrayList.add(cr3);

        /*StringRequest strReq = new StringRequest(Request.Method.GET,
                RestAPIContract.CHAT_ROOMS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "response: " + response);

                try {
                    JSONObject obj = new JSONObject(response);

                    if (obj.getBoolean("error") == false) {
                        JSONArray chatRoomsArray = obj.getJSONArray("chat_rooms");
                        for (int i = 0; i < chatRoomsArray.length(); i++) {
                            JSONObject chatRoomsObj = (JSONObject) chatRoomsArray.get(i);
                            ChatRoom cr = new ChatRoom();
                            cr.setId(chatRoomsObj.getString("chat_room_id"));
                            cr.setName(chatRoomsObj.getString("name"));
                            cr.setLastMessage("");
                            cr.setUnreadCount(0);
                            cr.setTimestamp(chatRoomsObj.getString("created_at"));

                            chatRoomArrayList.add(cr);
                        }

                    }

                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                }

                mAdapter.notifyDataSetChanged();

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
            }
        });
        MyApplication.getInstance().addToRequestQueue(strReq);*/
    }
}
