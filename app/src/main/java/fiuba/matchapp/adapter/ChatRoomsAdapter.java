package fiuba.matchapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pkmmte.view.CircularImageView;

import java.util.ArrayList;
import java.util.Calendar;

import fiuba.matchapp.R;
import fiuba.matchapp.model.DateHelper;
import fiuba.matchapp.model.ChatRoom;
import fiuba.matchapp.utils.ImageBase64;

public class ChatRoomsAdapter extends RecyclerView.Adapter<ChatRoomsAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<ChatRoom> chatRoomArrayList;
    private static String today;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name, message, timestamp, count;
        private  CircularImageView circleImageProfile;


        public ViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            message = (TextView) view.findViewById(R.id.message);
            timestamp = (TextView) view.findViewById(R.id.timestamp);
            count = (TextView) view.findViewById(R.id.count);
            circleImageProfile = (CircularImageView) view.findViewById(R.id.profile_img);
        }
    }


    public ChatRoomsAdapter(Context mContext, ArrayList<ChatRoom> chatRoomArrayList) {
        this.mContext = mContext;
        this.chatRoomArrayList = chatRoomArrayList;

        Calendar calendar = Calendar.getInstance();
        today = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_rooms_list_row, parent, false);


        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ChatRoom chatRoom = chatRoomArrayList.get(position);
        holder.name.setText(chatRoom.getOtherUser().getAlias());
        if(chatRoom.hasMessages()){
            holder.message.setText(chatRoom.getLastMessage().getMessage());
            holder.timestamp.setText(DateHelper.getTimeStamp(chatRoom.getLastMessage().getTimestamp(), mContext));
            holder.message.setVisibility(View.VISIBLE);
            holder.timestamp.setVisibility(View.VISIBLE);
        }else {
            holder.message.setVisibility(View.GONE);
            holder.timestamp.setVisibility(View.GONE);
        }

        //Ahow
        if (chatRoom.getUnreadCount() > 0) {
            holder.count.setText(String.valueOf(chatRoom.getUnreadCount()));
            holder.count.setVisibility(View.VISIBLE);
        } else {
            holder.count.setVisibility(View.GONE);
        }


        if (!TextUtils.isEmpty(chatRoom.getOtherUser().getPhotoProfile())){
            holder.circleImageProfile.setImageBitmap(ImageBase64.Base64ToBitmap(chatRoom.getOtherUser().getPhotoProfile()));
        }
    }

    @Override
    public int getItemCount() {
        return chatRoomArrayList.size();
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ChatRoomsAdapter.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ChatRoomsAdapter.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

}
