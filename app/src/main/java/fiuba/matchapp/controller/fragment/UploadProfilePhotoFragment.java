package fiuba.matchapp.controller.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.kbeanie.imagechooser.api.ChooserType;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.kbeanie.imagechooser.api.ChosenImages;
import com.kbeanie.imagechooser.api.ImageChooserListener;
import com.kbeanie.imagechooser.api.ImageChooserManager;
import com.squareup.picasso.Picasso;

import java.io.File;

import fiuba.matchapp.R;

/**
 * Created by german on 4/28/2016.
 */
public class UploadProfilePhotoFragment extends Fragment implements ImageChooserListener {

    private static String TAG = "UploadProfileFragment";
    ImageView userImage;
    FrameLayout editImage;
    FloatingActionButton fbEditImage;

    public Boolean isEmpty;
    private int					chooserType;
    private ImageChooserManager	imageChooserManager;
    private String				filePath;

    private String originalFilePath;
    private String thumbnailFilePath;
    private String thumbnailSmallFilePath;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_CAMERA_PERMISSION = 1;

    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static String[] PERMISSIONS_CAMERA = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isEmpty = true;
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
        ;
        return view;
    }


    private void chooseImage() {
        ActivityCompat.requestPermissions(getActivity(), PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
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

                originalFilePath = image.getFilePathOriginal();
                thumbnailFilePath = image.getFileThumbnail();
                thumbnailSmallFilePath = image.getFileThumbnailSmall();

                if (image != null) {
                    loadImage(userImage, image.getFilePathOriginal(),getActivity());
                    isEmpty = false;
                    //TODO: mandarle la foto al server en base64
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
}
