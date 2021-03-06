package fiuba.matchapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import fiuba.matchapp.R;
import fiuba.matchapp.controller.activity.ProfileActivity;
import fiuba.matchapp.model.User;
import fiuba.matchapp.utils.AdressUtils;
import fiuba.matchapp.utils.ImageBase64;

/**
 * Created by german on 4/17/2016.
 */
public class SwipeDeckAdapter extends BaseAdapter {

    private List<User> data;
    private Context context;

    public SwipeDeckAdapter(List<User> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View v = convertView;
        if (v == null) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.candidate_card, parent, false);
        }
        ImageView imageView = (ImageView) v.findViewById(R.id.offer_image);
        TextView textView = (TextView) v.findViewById(R.id.sample_text);
        TextView subtitleView = (TextView) v.findViewById(R.id.subtitle_text);

        User user = (User) getItem(position);

        if(!TextUtils.isEmpty(user.getPhotoProfile())){
            imageView.setImageBitmap(ImageBase64.Base64ToBitmap(user.getPhotoProfile()));
        }

        textView.setText(user.getAlias() + ", " + user.getAge());
        try {
            subtitleView.setText(AdressUtils.getParsedAddress(user.getLatitude(),user.getLongitude()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Layer type: ", Integer.toString(v.getLayerType()));
                Log.i("Hwardware Accel type:", Integer.toString(View.LAYER_TYPE_HARDWARE));
                startProfileActivity(v,position);
            }
        });

        return v;
    }

    private void startProfileActivity(View v,int position) {
        Intent i = new Intent(v.getContext(), ProfileActivity.class);
        User user = (User) getItem(position);
        i.putExtra("user", (Parcelable) user);
        v.getContext().startActivity(i);
    }
}
