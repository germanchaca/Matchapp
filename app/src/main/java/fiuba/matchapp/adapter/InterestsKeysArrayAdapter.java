package fiuba.matchapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import fiuba.matchapp.R;
import fiuba.matchapp.model.ChatRoom;
import fiuba.matchapp.model.DateHelper;
import fiuba.matchapp.model.Interest;
import fiuba.matchapp.model.InterestCategory;
import fiuba.matchapp.model.Message;

public class InterestsKeysArrayAdapter extends RecyclerView.Adapter<InterestsKeysArrayAdapter.ViewHolder> {
        private Context mContext;
        private ArrayList<InterestCategory> categoriesArrayList;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView name, count;

            public ViewHolder(View view) {
                super(view);
                name = (TextView) view.findViewById(R.id.name);
                count = (TextView) view.findViewById(R.id.count);
            }
        }


        public InterestsKeysArrayAdapter(Context mContext, ArrayList<InterestCategory> categoriesArrayList) {
            this.mContext = mContext;
            this.categoriesArrayList = categoriesArrayList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.category_interest_list_row, parent, false);

            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            String category = categoriesArrayList.get(position).getName();
            holder.name.setText(category);
            holder.count.setText(String.valueOf(categoriesArrayList.get(position).getCount()));
            holder.count.setVisibility(View.VISIBLE);
        }

        @Override
        public int getItemCount() {
            return categoriesArrayList.size();
        }

        public interface ClickListener {
            void onClick(View view, int position);

            void onLongClick(View view, int position);
        }

        public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

            private GestureDetector gestureDetector;
            private InterestsKeysArrayAdapter.ClickListener clickListener;

            public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final InterestsKeysArrayAdapter.ClickListener clickListener) {
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
