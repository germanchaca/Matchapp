package fiuba.matchapp.adapter;

import android.content.Context;
import android.content.Intent;
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

    private final FloatingActionButton btnInfo;
    private List<User> data;
    private Context context;

    public SwipeDeckAdapter(List<User> data, Context context, FloatingActionButton btnInfo) {
        this.data = data;
        this.context = context;
        this.btnInfo = btnInfo;
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
        TextView subtitleView = (TextView) v.findViewById(R.id.subtitle);

        final User user = data.get(position);

        if(!TextUtils.isEmpty(user.getPhotoProfile())){
            imageView.setImageBitmap(ImageBase64.Base64ToBitmap(user.getPhotoProfile()));
        }

        textView.setText(user.getName() + ", " + user.getAge());
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
                startProfileActivity(v,user);
            }
        });
        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startProfileActivity(v,user);
            }
        });
        return v;
    }

    private void startProfileActivity(View v,User user) {
        Intent i = new Intent(v.getContext(), ProfileActivity.class);
        i.putExtra("user", (Serializable) user);
        v.getContext().startActivity(i);
    }
}
