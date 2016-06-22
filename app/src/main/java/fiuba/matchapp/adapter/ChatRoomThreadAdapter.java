package fiuba.matchapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

import fiuba.matchapp.R;
import fiuba.matchapp.model.DateHelper;
import fiuba.matchapp.model.Message;
import fiuba.matchapp.model.MyMessage;

/**
 * Created by german on 4/8/2016.
 * Esta clase adapter alinea a la derecha y a la izquierda inflando 2 layouts diferentes
 */
public class ChatRoomThreadAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static String TAG = ChatRoomThreadAdapter.class.getSimpleName();
    private final LoadEarlierMessages mLoadEarlierMessages;

    private int MORE = 200;
    private int SELF = 100;
    private int OTHER = 300;
    private static String today;

    private Context mContext;
    private ArrayList<Message> messageArrayList;

    public void setLoadEarlierMsgs(boolean loadEarlierMsgs) {
        isLoadEarlierMsgs = loadEarlierMsgs;
    }

    private boolean isLoadEarlierMsgs = true;

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView message, timestamp;

        public ViewHolder(View view) {
            super(view);
            message = (TextView) itemView.findViewById(R.id.message);
            timestamp = (TextView) itemView.findViewById(R.id.timestamp);
        }
    }

    public class SelfViewHolder extends RecyclerView.ViewHolder {
        TextView message, timestamp;
        ImageView sentBadge, errorBadge;

        public SelfViewHolder(View view) {
            super(view);
            message = (TextView) itemView.findViewById(R.id.message);
            timestamp = (TextView) itemView.findViewById(R.id.timestamp);
            sentBadge = (ImageView) itemView.findViewById(R.id.sentBadge);
            errorBadge = (ImageView) itemView.findViewById(R.id.errorBadge);
        }
    }

    public class LoadMoreViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout btnLoadMore;

        public LoadMoreViewHolder(View view) {
            super(view);
            btnLoadMore = (RelativeLayout) itemView.findViewById(R.id.containerLoadMore);
        }
    }

    public ChatRoomThreadAdapter(Context mContext, ArrayList<Message> messageArrayList) {
        this.mContext = mContext;
        this.messageArrayList = messageArrayList;
        mLoadEarlierMessages = (LoadEarlierMessages) mContext;

        Calendar calendar = Calendar.getInstance();
        today = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;

        if(viewType == MORE){
            itemView =LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_loadmore, parent, false);
            return new LoadMoreViewHolder(itemView);

        } else if (viewType == SELF) {
            // mi mensaje
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_self, parent, false);

            return new SelfViewHolder(itemView);
        } else {
            // mensaje de otros
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_other, parent, false);
            return new ViewHolder(itemView);
        }

    }

    @Override
    public int getItemViewType(int position) {

        if (position == 0) {
            return MORE;
        }else {
            Message message = messageArrayList.get(position-1);
            if (message.isMine()){
                return SELF;
            }
            return OTHER;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);
        if(type== MORE) {
            if (isLoadEarlierMsgs) {
                ((LoadMoreViewHolder) holder).btnLoadMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mLoadEarlierMessages != null) {
                            mLoadEarlierMessages.onLoadMore();
                        }
                    }
                });
            }else {
                ((LoadMoreViewHolder) holder).btnLoadMore.setVisibility(View.GONE);
            }
        }else if(type== SELF) {
            MyMessage message = (MyMessage) messageArrayList.get(position-1);

            ((SelfViewHolder) holder).message.setText(message.getMessage());

            String timestamp = DateHelper.getTimeStamp(message.getTimestamp(), mContext);

            if( message.isSent()){
                ((SelfViewHolder) holder).sentBadge.setVisibility(View.VISIBLE);
            }else {
                ((SelfViewHolder) holder).sentBadge.setVisibility(View.GONE);
            }
            if( message.hasError()){
                ((SelfViewHolder) holder).errorBadge.setVisibility(View.VISIBLE);
            }else {
                ((SelfViewHolder) holder).errorBadge.setVisibility(View.GONE);
            }

            ((SelfViewHolder) holder).timestamp.setText(timestamp);
        }else{
            Message message = messageArrayList.get(position-1);

            ((ViewHolder) holder).message.setText(message.getMessage());

            String timestamp = DateHelper.getTimeStamp(message.getTimestamp(), mContext);

            ((ViewHolder) holder).timestamp.setText(timestamp);
        }
    }

    @Override
    public int getItemCount() {
        return messageArrayList.size() + 1;
    }

}
