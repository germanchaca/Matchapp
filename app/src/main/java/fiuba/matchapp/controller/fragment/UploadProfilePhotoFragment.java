package fiuba.matchapp.controller.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kbeanie.imagechooser.api.ChooserType;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.kbeanie.imagechooser.api.ChosenImages;
import com.kbeanie.imagechooser.api.ImageChooserListener;
import com.kbeanie.imagechooser.api.ImageChooserManager;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import fiuba.matchapp.R;
import fiuba.matchapp.app.MyApplication;
import fiuba.matchapp.model.User;
import fiuba.matchapp.networking.httpRequests.RestAPIContract;
import fiuba.matchapp.utils.ImageBase64;

/**
 * Created by german on 4/28/2016.
 */
public class UploadProfilePhotoFragment extends Fragment implements ImageChooserListener {

    private static String TAG = "UploadProfileFragment";
    ImageView userImage;
    FrameLayout editImage;
    FloatingActionButton fbEditImage;

    private int chooserType;
    private ImageChooserManager imageChooserManager;
    private String filePath;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_CAMERA_PERMISSION = 1;

    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static String[] PERMISSIONS_CAMERA = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private OnProfilePhotoDataPass dataPasser;

    public interface OnProfilePhotoDataPass {
        public void onProfilePhotoDataPass(String data);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        dataPasser = (OnProfilePhotoDataPass) context;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_upload_photo, container, false);

        userImage = (ImageView) view.findViewById(R.id.user_image);
        editImage = (FrameLayout) view.findViewById(R.id.edit_image);
        fbEditImage = (FloatingActionButton) view.findViewById(R.id.fb_edit_image);

        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(v.getContext());
            }
        });

        fbEditImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(v.getContext());
            }
        });
        return view;
    }


    private void chooseImage() {
        ActivityCompat.requestPermissions(getActivity(), PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        chooserType = ChooserType.REQUEST_PICK_PICTURE;

        imageChooserManager = new ImageChooserManager(this,
                ChooserType.REQUEST_PICK_PICTURE, true);

        Bundle bundle = new Bundle();
        bundle.putBoolean(Intent.EXTRA_ALLOW_MULTIPLE, false);
        imageChooserManager.setExtras(bundle);
        imageChooserManager.setImageChooserListener(this);
        imageChooserManager.clearOldFiles();

        try {
            filePath = imageChooserManager.choose();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void takePicture() {
        ActivityCompat.requestPermissions(getActivity(), PERMISSIONS_CAMERA, REQUEST_CAMERA_PERMISSION);
        chooserType = ChooserType.REQUEST_CAPTURE_PICTURE;

        imageChooserManager = new ImageChooserManager(this,
                ChooserType.REQUEST_CAPTURE_PICTURE, true);

        imageChooserManager.setImageChooserListener(this);
        imageChooserManager.clearOldFiles();
        try {
            filePath = imageChooserManager.choose();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == getActivity().RESULT_OK
                && (requestCode == ChooserType.REQUEST_PICK_PICTURE || requestCode == ChooserType.REQUEST_CAPTURE_PICTURE)) {
            if (imageChooserManager == null) {
                reinitializeImageChooser();
            }
            imageChooserManager.submit(requestCode, data);
        }
    }

    @Override
    public void onImageChosen(final ChosenImage image) {
        filePath = image.getFilePathOriginal();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (image != null) {
                    loadImage(userImage, image.getFilePathOriginal(), getActivity());

                    Bitmap bitmap = ((BitmapDrawable) userImage.getDrawable()).getBitmap();
                    String encodedImage = ImageBase64.getEncoded64ImageStringFromBitmap(bitmap);

                    dataPasser.onProfilePhotoDataPass(encodedImage);

                } else {
                    Log.i(TAG, "Chosen Image: Is null");
                }
            }

        });
    }

    @Override
    public void onError(final String reason) {
    }

    private void loadImage(ImageView iv, final String path, Context context) {
        Picasso.with(context)
                .load(Uri.fromFile(new File(path)))
                .fit()
                .centerCrop()
                .into(iv);
    }

    @Override
    public void onImagesChosen(final ChosenImages chosenImages) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onImageChosen(chosenImages.getImage(0));
            }
        });

    }

    private void reinitializeImageChooser() {
        imageChooserManager = new ImageChooserManager(this, chooserType, true);
        Bundle bundle = new Bundle();
        bundle.putBoolean(Intent.EXTRA_ALLOW_MULTIPLE, false);
        imageChooserManager.setExtras(bundle);
        imageChooserManager.setImageChooserListener(this);
        imageChooserManager.reinitialize(filePath);
    }

    private void selectImage(final Context context) {
        final CharSequence[] items = {getResources().getString(R.string.edit_profile_take_picture), getResources().getString(R.string.edit_profile_choose_picture),
                getResources().getString(R.string.edit_profile_cancel)};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(getResources().getString(R.string.edit_profile_title_select_picture));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals(getResources().getString(R.string.edit_profile_take_picture))) {
                    takePicture();
                } else if (items[item].equals(getResources().getString(R.string.edit_profile_choose_picture))) {
                    chooseImage();
                } else if (items[item].equals(getResources().getString(R.string.edit_profile_cancel))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void sendEncodedImageToServer(final String encodedImage) {
        // checking for valid login session
        User user = MyApplication.getInstance().getPrefManager().getUser();
        if (user == null) {
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(this.getContext(), R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getResources().getString(R.string.sending_image_server));
        progressDialog.show();

        String endPoint = RestAPIContract.PUT_USER(user.getId());

        StringRequest strReq = new StringRequest(Request.Method.PUT,
                endPoint, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "response: " + response);
                progressDialog.dismiss();
                try {
                    JSONObject obj = new JSONObject(response);

                    // check for error
                    if (obj.getBoolean("error") == false) {
                        onImageSuccesfullyUploaded(encodedImage);
                        return;
                    } else {
                        displayAlertDialog();
                        Log.d(TAG, getResources().getString(R.string.connection_problem));
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("photo_profile", encodedImage);
                Log.e(TAG, "params: " + params.toString());
                return params;
            }
        };
        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);

        return;

    }

    private void onImageSuccesfullyUploaded(String encodedImage) {
        //Store user photo on SharedPreferences
        User user = MyApplication.getInstance().getPrefManager().getUser();
        user.setPhotoProfile(encodedImage);
        MyApplication.getInstance().getPrefManager().storeUser(user);
    }

    public void displayAlertDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this.getContext());
        alert.setMessage(getResources().getString(R.string.connection_problem));
        alert.setPositiveButton(getResources().getString(R.string.connection_problem_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                userImage.setImageDrawable(getResources().getDrawable(R.drawable.empty_profile_phd_350x350));//setea de nuevo el default
                dialog.dismiss();

            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }
}

