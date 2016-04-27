package fiuba.matchapp.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import fiuba.matchapp.R;
import fiuba.matchapp.model.Interest;

public class InterestsRecyclerAdapter extends RecyclerView.Adapter<InterestsRecyclerAdapter.InterestViewHolder> {

    List<Interest> interests;
    OnItemClickListener mItemClickListener;

    public InterestsRecyclerAdapter(List<Interest> interests) {
        this.interests = interests;
    }

    @Override
    public InterestViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_item_autolabel, viewGroup, false);
        return new InterestViewHolder(v);
    }

    @Override
    public void onBindViewHolder(InterestViewHolder viewHolder, int i) {
        viewHolder.interestDescription.setText(interests.get(i).getDescription());
        viewHolder.cbSelected.setChecked(interests.get(i).isSelected());

    }

    public void setItemSelected(int position, boolean isSelected) {
        if (position != -1) {
            interests.get(position).setSelected(isSelected);
            notifyDataSetChanged();
        }
    }

    public List<Interest> getInterests(){
        return interests;
    }

    @Override
    public int getItemCount() {
        return interests.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public void setInterests(List<Interest> interests) {
        this.interests = interests;
    }


    public interface OnItemClickListener {

        void onItemClick(View view, int position);
    }

    public class InterestViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {

        CardView cv;
        TextView interestDescription;
        CheckBox cbSelected;

        InterestViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            cv = (CardView) itemView.findViewById(R.id.cv);
            interestDescription = (TextView) itemView.findViewById(R.id.interest_description);
            cbSelected = (CheckBox) itemView.findViewById(R.id.cbSelected);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getPosition());

            }
        }
    }


}
