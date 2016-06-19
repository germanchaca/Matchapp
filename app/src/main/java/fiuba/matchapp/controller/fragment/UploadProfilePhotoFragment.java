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
import android.provider.MediaStore;
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
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import fiuba.matchapp.R;
import fiuba.matchapp.app.MyApplication;
import fiuba.matchapp.controller.activity.FinishingSignUpActivity;
import fiuba.matchapp.controller.activity.MainActivity;
import fiuba.matchapp.model.User;
import fiuba.matchapp.networking.httpRequests.PutUpdatePhothoProfileUser;
import fiuba.matchapp.networking.httpRequests.RestAPIContract;
import fiuba.matchapp.utils.ImageBase64;
import fiuba.matchapp.view.LockedProgressDialog;

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
    private ProgressDialog progressDialog;

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

        progressDialog = new LockedProgressDialog(getContext(), R.style.AppTheme_Dark_Dialog);

        progressDialog.setMessage(getResources().getString(R.string.refresh_account_details));

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

                    Uri imageUri = Uri.fromFile(new File(image.getFilePathOriginal()));
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap( getContext().getContentResolver(), imageUri);

                        String encodedImage = ImageBase64.getEncoded64ImageStringFromBitmap(bitmap);
                        dataPasser.onProfilePhotoDataPass(encodedImage);

                        sendPhotoToAppServer(encodedImage);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //Bitmap bitmap = ((BitmapDrawable) userImage.getDrawable()).getBitmap();
                    //String encodedImage = ImageBase64.getEncoded64ImageStringFromBitmap(bitmap);

                    //dataPasser.onProfilePhotoDataPass(encodedImage);

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

    private void sendPhotoToAppServer(final String profilePhoto) {
        progressDialog.show();

        PutUpdatePhothoProfileUser request = new PutUpdatePhothoProfileUser(MyApplication.getInstance().getPrefManager().getUser(), profilePhoto) {
            @Override
            protected void onUpdatePhotoProfileSuccess() {
                User user = MyApplication.getInstance().getPrefManager().getUser();
                user.setPhotoProfile(profilePhoto);
                MyApplication.getInstance().getPrefManager().storeUser(user);
                progressDialog.dismiss();
                launchMainActivity();
            }

            @Override
            protected void onAppServerUpdatePhotoProfileDefaultError() {
                progressDialog.dismiss();

                displayAlertDialog();
            }

            @Override
            protected void onAppServerConnectionError() {
                progressDialog.dismiss();

                displayAlertDialog();
            }

            @Override
            protected void logOut() {
                progressDialog.dismiss();
                MyApplication.getInstance().logout();
            }


        };
        request.make();

        Log.d(TAG, "ProfilePhoto to send: " + profilePhoto);
    }

    private void launchMainActivity() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();
    }
    public void displayAlertDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
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

