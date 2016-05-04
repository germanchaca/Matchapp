package fiuba.matchapp.view.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.kbeanie.imagechooser.api.ChooserType;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.kbeanie.imagechooser.api.ChosenImages;
import com.kbeanie.imagechooser.api.ImageChooserListener;
import com.kbeanie.imagechooser.api.ImageChooserManager;
import com.squareup.picasso.Picasso;

import java.io.File;

import fiuba.matchapp.R;
import fiuba.matchapp.app.MyApplication;
import fiuba.matchapp.model.User;
import fiuba.matchapp.view.fragment.DatePickerFragment;

/**
 * Created by german on 4/19/2016.
 * TODO: 1.Guardar user en pref en commit changes
 *       2.Borrar Cuenta
 *       3.Cambiar password
 *       4.Editar intereses
 *       5.Volley request
 *       6.Cargando hasta que sube imagen
 *       7.Cargando hasta que sube lo demas
 */
public class EditableProfileActivity extends GetLocationActivity implements ImageChooserListener {

    private final static String TAG = "Profile_Edit";
    ImageView userImage;
    FrameLayout editImage;
    FloatingActionButton fbEditImage;

    private int chooserType;
    private ImageChooserManager imageChooserManager;
    private String filePath;

    private String originalFilePath;
    private String thumbnailFilePath;
    private String thumbnailSmallFilePath;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static String[] PERMISSIONS_CAMERA = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private RelativeLayout layoutEditName;
    private EditText txtName;
    private User user;
    private RelativeLayout layoutEditAlias;
    private EditText txtAlias;
    private RelativeLayout layoutEditDate;
    private TextView txtDate;
    private RelativeLayout layoutRefreshLocation;
    private TextView txtEditAddress;
    private ImageView refreshIcon;
    private RelativeLayout layoutEditMail;
    private EditText txtEmail;
    private boolean hasMailError = false;
    private boolean isUploadingImage = false;
    private FloatingActionButton btnCommitChanges;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_editable_profile);
        user = MyApplication.getInstance().getPrefManager().getUser();

        initViews(user);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarLayout.setTitle(getResources().getString(R.string.edit_profile_toolbar_label));
        toolbarLayout.setExpandedTitleColor(Color.TRANSPARENT);

        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        fbEditImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
    }

    private void initViews(User user) {
        btnCommitChanges = (FloatingActionButton) findViewById(R.id.confirmChanges);
        btnCommitChanges.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                commitChanges();
            }
        });
        userImage = (ImageView) findViewById(R.id.user_image);
        editImage = (FrameLayout) findViewById(R.id.edit_image);
        fbEditImage = (FloatingActionButton) findViewById(R.id.fb_edit_image);
        txtDate = (TextView) findViewById(R.id.textViewBirthDate);

        layoutEditName = (RelativeLayout) findViewById(R.id.layoutEditName);
        txtName = (EditText) findViewById(R.id.txtName);
        txtName.setText(user.getName());
        txtName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validateName();
                }
            }
        });
        layoutEditName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtName.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(txtName, InputMethod.SHOW_FORCED);
                btnCommitChanges.setVisibility(View.VISIBLE);
            }
        });
        layoutEditAlias = (RelativeLayout) findViewById(R.id.layoutEditAlias);
        txtAlias = (EditText) findViewById(R.id.txtAlias);
        txtAlias.setText(user.getAlias());
        txtAlias.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validateAlias();
                }
            }
        });
        layoutEditAlias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtAlias.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(txtAlias, InputMethod.SHOW_FORCED);
                btnCommitChanges.setVisibility(View.VISIBLE);
            }
        });
        layoutEditDate = (RelativeLayout) findViewById(R.id.layoutEditDate);
        txtDate.setText(user.getBirthday());
        layoutEditDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtDate.requestFocus();
                showDatePickerDialog(v);
                btnCommitChanges.setVisibility(View.VISIBLE);
            }
        });

        layoutRefreshLocation = (RelativeLayout) findViewById(R.id.layoutRefreshAddress);
        txtEditAddress = (TextView) findViewById(R.id.txtLocation);
        txtEditAddress.setText(user.getParsedAddress());
        txtEditAddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validateMail();
                }
            }
        });
        layoutRefreshLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtEditAddress.requestFocus();
                refreshLocation();
                btnCommitChanges.setVisibility(View.VISIBLE);
            }
        });
        layoutEditMail = (RelativeLayout) findViewById(R.id.layoutEditMail);
        txtEmail = (EditText) findViewById(R.id.txtEditMail);
        txtEmail.setText(user.getEmail());
        layoutEditMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtEmail.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(txtEmail, InputMethod.SHOW_FORCED);
                btnCommitChanges.setVisibility(View.VISIBLE);
            }
        });
    }
    //Para ser llamado por el icono de concretar

    private boolean validateAlias() {
        String alias = txtAlias.getText().toString();
        if (alias.isEmpty()) {
            txtAlias.setText(user.getAlias());
        }
        return true;
    }

    private boolean validateName() {
        String name = txtName.getText().toString();
        if (name.isEmpty()) {
            txtName.setText(user.getName());
        }
        return true;
    }

    public boolean validateMail() {

        this.hasMailError = false;
        String mail = txtEmail.getText().toString();

        if (mail.isEmpty()) {
            txtEmail.setText(user.getEmail());

        } else {
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
                txtEmail.setError(getResources().getString(R.string.invalid_mail_invalid));
                this.hasMailError = true;
            } else {
                txtEmail.setError(null);
            }
        }

        return true;
    }

    public void showRefreshingLocationLoading() {
        RotateAnimation anim = new RotateAnimation(0f, 350f, 15f, 15f);
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatCount(Animation.INFINITE);
        anim.setDuration(100);

        refreshIcon = (ImageView) findViewById(R.id.icRefreshLocation);
        refreshIcon.startAnimation(anim);
    }

    public void stopRefreshingLocationLoading() {
        if (refreshIcon != null) {
            refreshIcon.setAnimation(null);
        }
    }

    public void refreshLocation() {
        showRefreshingLocationLoading();
        this.initUserLastLocation();
        super.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        super.onConnected(bundle);
        this.user = MyApplication.getInstance().getPrefManager().getUser();
        txtEditAddress.setText(this.user.getParsedAddress());
        stopRefreshingLocationLoading();
        //TODO MANDAR AL SERVIDOR;
    }

    @Override
    public void onConnectionSuspended(int i) {
        super.onConnectionSuspended(i);
        stopRefreshingLocationLoading();
        Toast.makeText(getBaseContext(), getResources().getString(R.string.error_location_refresh_failed), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        super.onConnectionFailed(connectionResult);
        stopRefreshingLocationLoading();
        Toast.makeText(getBaseContext(), getResources().getString(R.string.error_location_refresh_failed), Toast.LENGTH_LONG).show();
    }

    public void showDatePickerDialog(View v) {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setEditText(txtDate);
        newFragment.show(getFragmentManager(), "datePicker");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                commitChanges();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void commitChanges(){
        if (validate()) {
            //TODO mandar datos al server
            //TODO Cargando
            onBackPressed();
        } else {
            new AlertDialog.Builder(getApplicationContext())
                    .setMessage(getResources().getString(R.string.alert_dialog_continue_editing))
                    .setPositiveButton(getResources().getString(R.string.alert_dialog_continue_editing_discard), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            onBackPressed();
                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.alert_dialog_continue_editing_cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .show();
        }


    }
    private boolean validate() {
        validateAlias();
        validateMail();
        validateName();
        return !isUploadingImage && !hasMailError;
    }

    private void chooseImage() {
        ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
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
        ActivityCompat.requestPermissions(this, PERMISSIONS_CAMERA, REQUEST_CAMERA_PERMISSION);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                originalFilePath = image.getFilePathOriginal();
                thumbnailFilePath = image.getFileThumbnail();
                thumbnailSmallFilePath = image.getFileThumbnailSmall();

                if (image != null) {
                    loadImage(userImage, image.getFilePathOriginal());
                    //loadImage(userImage, image.getFileThumbnail());
                    isUploadingImage = true;
                    //TODO mandar al server y mostrar cargando
                    isUploadingImage = false;
                } else {
                    Log.i(TAG, "Chosen Image: Is null");
                }
            }

        });
    }

    @Override
    public void onError(final String reason) {
    }

    private void loadImage(ImageView iv, final String path) {
        Picasso.with(EditableProfileActivity.this)
                .load(Uri.fromFile(new File(path)))
                .fit()
                .centerCrop()
                .into(iv);
    }

    @Override
    public void onImagesChosen(final ChosenImages chosenImages) {
        runOnUiThread(new Runnable() {
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


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("chooser_type", chooserType);
        outState.putString("media_path", filePath);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("chooser_type")) {
                chooserType = savedInstanceState.getInt("chooser_type");
            }

            if (savedInstanceState.containsKey("media_path")) {
                filePath = savedInstanceState.getString("media_path");
            }
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void selectImage() {
        final CharSequence[] items = {getResources().getString(R.string.edit_profile_take_picture), getResources().getString(R.string.edit_profile_choose_picture),
                getResources().getString(R.string.edit_profile_cancel)};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

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
