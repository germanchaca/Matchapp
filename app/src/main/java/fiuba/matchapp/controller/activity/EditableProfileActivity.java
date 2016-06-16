package fiuba.matchapp.controller.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fiuba.matchapp.R;
import fiuba.matchapp.adapter.InterestsKeysArrayAdapter;
import fiuba.matchapp.app.MyApplication;
import fiuba.matchapp.controller.baseActivity.GetLocationActivity;
import fiuba.matchapp.model.InterestCategory;
import fiuba.matchapp.model.User;
import fiuba.matchapp.model.UserInterest;
import fiuba.matchapp.networking.httpRequests.DeleteUserRequest;
import fiuba.matchapp.networking.httpRequests.PutUpdateUserData;
import fiuba.matchapp.utils.AdressUtils;
import fiuba.matchapp.utils.ImageBase64;
import fiuba.matchapp.utils.InterestsUtils;
import fiuba.matchapp.view.SimpleDividerItemDecoration;

public class EditableProfileActivity extends GetLocationActivity implements ImageChooserListener {

    private final static String TAG = "Profile_Edit";
    ImageView userImage;
    FrameLayout editImage;
    FloatingActionButton fbEditImage;

    private int chooserType;
    private ImageChooserManager imageChooserManager;
    private String filePath;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static String[] PERMISSIONS_CAMERA = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private RelativeLayout layoutEditName;
    private EditText txtName;
    private User user;
    private RelativeLayout layoutEditAlias;
    private EditText txtAlias;

    private RelativeLayout layoutRefreshLocation;
    private TextView txtEditAddress;
    private ImageView refreshIcon;
    private RelativeLayout layoutEditMail;
    private TextView txtEmail;
    private boolean hasMailError = false;
    private boolean isUploadingImage = false;
    private FloatingActionButton btnCommitChanges;
    private LinearLayout parentLinearLayout;

    private RelativeLayout layoutDeleteAccount;
    private ProgressDialog progressDialog;
    private RecyclerView interestsMenuContainer;
    private ArrayList<InterestCategory> adapterUserInterestsList;
    private InterestsKeysArrayAdapter adapter;
    private RelativeLayout layoutEditAge;
    private EditText txtAge;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editable_profile);
        user = MyApplication.getInstance().getPrefManager().getUser();
        initViews(user);

    }

    @Override
    protected void onResume() {
        super.onResume();

        adapterUserInterestsList.clear();
        user = MyApplication.getInstance().getPrefManager().getUser();
        fillUserCategoryInterestsList();
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onBackPressed() {
        onServerConnectionFailedBackPressed();
    }

    private void initViews(final User user) {
        parentLinearLayout = (LinearLayout) findViewById(R.id.parentLayout);
        btnCommitChanges = (FloatingActionButton) findViewById(R.id.confirmChanges);
        editImage = (FrameLayout) findViewById(R.id.edit_image);
        userImage = (ImageView) findViewById(R.id.user_image);
        fbEditImage = (FloatingActionButton) findViewById(R.id.fb_edit_image);
        layoutEditName = (RelativeLayout) findViewById(R.id.layoutEditName);
        txtName = (EditText) findViewById(R.id.txtName);
        layoutEditAlias = (RelativeLayout) findViewById(R.id.layoutEditAlias);
        txtAlias = (EditText) findViewById(R.id.txtAlias);
        layoutEditAge = (RelativeLayout) findViewById(R.id.layoutEditAge);
        txtAge = (EditText) findViewById(R.id.txtAge);
        layoutRefreshLocation = (RelativeLayout) findViewById(R.id.layoutRefreshAddress);
        txtEditAddress = (TextView) findViewById(R.id.txtLocation);
        layoutEditMail = (RelativeLayout) findViewById(R.id.layoutEditMail);
        txtEmail = (TextView) findViewById(R.id.txtEditMail);
        layoutDeleteAccount = (RelativeLayout) findViewById(R.id.DeleteAccount);

        initTootlBar();
        initPhotoProfile();
        initEditName(user);
        initEditAge(user);
        initEditAlias(user);
        initEditAddress(user);
        initEditMail(user);
        initRecyclerViewInterests();

        initEditPhotoIcons();
        initDeleteAccount();
        initBtnCommitChanges();
    }

    private void initEditAge(User user) {

        txtAge.setText(Integer.toString(user.getAge()));
        txtAge.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validateAge();
                } else {
                    showBtnCommitChanges();
                }
            }
        });
        layoutEditName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtName.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(txtName, InputMethod.SHOW_FORCED);
                showBtnCommitChanges();
            }
        });
    }



    private void initEditPhotoIcons() {
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


    private void initRecyclerViewInterests() {
        interestsMenuContainer = (RecyclerView) findViewById(R.id.nnterestsMenuContainer);
        adapterUserInterestsList = new ArrayList<>();

        fillUserCategoryInterestsList();
        adapter = new InterestsKeysArrayAdapter(this, adapterUserInterestsList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        interestsMenuContainer.setLayoutManager(layoutManager);
        interestsMenuContainer.addItemDecoration(new SimpleDividerItemDecoration(getApplicationContext()));
        interestsMenuContainer.setItemAnimator(new DefaultItemAnimator());
        interestsMenuContainer.setAdapter(adapter);

        interestsMenuContainer.addOnItemTouchListener(new InterestsKeysArrayAdapter.RecyclerTouchListener(getApplicationContext(), interestsMenuContainer, new InterestsKeysArrayAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                InterestCategory selectedInterestCategory = adapterUserInterestsList.get(position);
                Intent intent = new Intent(EditableProfileActivity.this, InterestEditActivity.class);
                String categoryName = selectedInterestCategory.getName();
                intent.putExtra("category", categoryName);
                startActivity(intent);

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }



    private void initPhotoProfile() {
        if(!TextUtils.isEmpty(this.user.getPhotoProfile())){
            this.userImage.setImageBitmap(ImageBase64.Base64ToBitmap(this.user.getPhotoProfile()));
        }
    }

    private void initDeleteAccount() {
        layoutDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parentLinearLayout.requestFocus();
                showProgressDialog();

                DeleteUserRequest request = new DeleteUserRequest(MyApplication.getInstance().getPrefManager().getUser()) {
                    @Override
                    protected void onDeleteAppServerTokenSuccess() {
                        hideProgressDialog();
                        MyApplication.getInstance().deletteAccount();
                    }

                    @Override
                    protected void onDeleteUserFailedDefaultError() {
                        hideProgressDialog();
                        Snackbar.make(parentLinearLayout,getResources().getString(R.string.internet_problem),Snackbar.LENGTH_LONG).show();
                    }

                    @Override
                    protected void onDeleteUserFailedUserConnectionError() {
                        hideProgressDialog();
                        Snackbar.make(parentLinearLayout,getResources().getString(R.string.internet_problem),Snackbar.LENGTH_LONG).show();
                    }

                    @Override
                    protected void logOut() {
                        hideProgressDialog();
                        MyApplication.getInstance().logout();
                    }

                };
                request.make();
            }
        });
    }



    private void onServerConnectionFailedBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.edit_profile_alert_dialog_connection_failed));
        builder.setPositiveButton(getResources().getString(R.string.alert_dialog_continue_editing_discard), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                back();            }
        });
        builder.setNegativeButton(getResources().getString(R.string.alert_dialog_continue_editing_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }
    public void back(){
        super.onBackPressed();
    }
    private void showProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getResources().getString(R.string.edit_profile_uploading_data_to_server));
        progressDialog.show();
    }

    private void hideProgressDialog() {
        progressDialog.dismiss();
    }

    private void initEditMail(User user) {
        txtEmail.setText(user.getEmail());
    }

    private void initEditAddress(User user) {
        try {
            txtEditAddress.setText(AdressUtils.getParsedAddress(user.getLatitude(),user.getLongitude()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        layoutRefreshLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parentLinearLayout.requestFocus();
                refreshLocation();
                showBtnCommitChanges();
            }
        });
    }

    private void initBtnCommitChanges() {
        btnCommitChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commitChanges();
            }
        });
    }

    private void initEditName(User user) {
        txtName.setText(user.getName());
        txtName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validateName();
                } else {
                    showBtnCommitChanges();
                }
            }
        });
        layoutEditName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtName.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(txtName, InputMethod.SHOW_FORCED);
                showBtnCommitChanges();
            }
        });
    }

    private void initEditAlias(User user) {
        txtAlias.setText(user.getAlias());
        txtAlias.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validateAlias();
                } else {
                    showBtnCommitChanges();
                }
            }
        });
        layoutEditAlias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtAlias.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(txtAlias, InputMethod.SHOW_FORCED);
                showBtnCommitChanges();
            }
        });
    }

   /* private void initEditDate(User user) {
        txtDate.setText(user.getBirthday());
        layoutEditDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parentLinearLayout.requestFocus();
                showDatePickerDialog(v);
                showBtnCommitChanges();
            }
        });
    }*/

    private void initTootlBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarLayout.setTitle(getResources().getString(R.string.edit_profile_toolbar_label));
        toolbarLayout.setExpandedTitleColor(Color.TRANSPARENT);
    }
    //Para ser llamado por el icono de concretar

    private void validateAlias() {
        String alias = txtAlias.getText().toString();
        if (alias.isEmpty()) {
            txtAlias.setText(user.getAlias());
        }
    }

    private void validateName() {
        String name = txtName.getText().toString();
        if (name.isEmpty()) {
            txtName.setText(user.getName());
        }
    }
    private void validateAge() {
        String age = txtAge.getText().toString();
        if (age.isEmpty()) {
            txtAge.setText(user.getAge());
        }
    }

    /*public void validateMail() {
        String mail = txtEmail.getText().toString();

        if (mail.isEmpty()) {
            txtEmail.setText(user.getEmail());
            this.icEditMail.setVisibility(View.VISIBLE);
            this.hasMailError = false;
        } else {
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
                txtEmail.setError(getResources().getString(R.string.invalid_mail_invalid));
                this.icEditMail.setVisibility(View.GONE);
                this.hasMailError = true;
            } else {
                txtEmail.setError(null);
                this.icEditMail.setVisibility(View.VISIBLE);
                this.hasMailError = false;
            }
        }
    }*/


    public void stopRefreshingLocationLoading() {
        if (refreshIcon != null) {
            refreshIcon.setAnimation(null);
        }
    }

    public void refreshLocation() {
        this.initUserLastLocation();
        super.locationServiceConnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        super.onConnected(bundle);
        this.user = MyApplication.getInstance().getPrefManager().getUser();
        try {
            txtEditAddress.setText(AdressUtils.getParsedAddress(this.user.getLatitude(),this.user.getLongitude()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        stopRefreshingLocationLoading();
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

    /*public void showDatePickerDialog(View v) {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setEditText(txtDate);
        newFragment.show(getFragmentManager(), "datePicker");
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void commitChanges() {
        validate();
        boolean makeRequest = false;
        showProgressDialog();
        final String name = txtName.getText().toString();
        final String alias = txtAlias.getText().toString();
        final int age = Integer.parseInt(txtAge.getText().toString());

        PutUpdateUserData request = new PutUpdateUserData(user) {
            @Override
            protected void onUpdateDataSuccess() {

                user.setName(name);
                user.setAge(age);
                user.setAlias(alias);

                if (!((latitude == 0) && (longitude == 0)) && (( (latitude != user.getLatitude()) || (longitude!= user.getLongitude()) ))) {
                    user.setLatitude(latitude);
                    user.setLongitude(longitude);
                }
                MyApplication.getInstance().getPrefManager().storeUser(user);
                hideProgressDialog();
                successCommitChanges();

            }

            @Override
            protected void onAppServerDefaultError() {
                hideProgressDialog();
                onServerConnectionFailedBackPressed();
            }

            @Override
            protected void onAppServerConnectionError() {
                hideProgressDialog();
                onServerConnectionFailedBackPressed();
            }

            @Override
            protected void logout() {
                hideProgressDialog();
                MyApplication.getInstance().logout();
            }
        };

        if(!TextUtils.equals(user.getName(),name)){
            request.changeName(name);
            makeRequest=true;
        }
        if(!TextUtils.equals(user.getAlias(),alias)){
            request.changeAlias(alias);
            makeRequest=true;
        }
        if(user.getAge() == age){
            request.changeAge(age);
            makeRequest=true;
        }
        if (!((latitude == 0) && (longitude == 0)) && (( (latitude != user.getLatitude()) || (longitude!= user.getLongitude()) ))){
            request.changeLocation(latitude,longitude);
            makeRequest=true;
        }
        if(makeRequest){
            request.make();
        }else{
            successCommitChanges();
        }
    }

    private void successCommitChanges() {
        hideProgressDialog();
        finish();
    }

    private boolean validate() {
        validateName();
        validateAlias();
        validateAge();
        //validateMail();
        return (!isUploadingImage && !hasMailError);
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

                if (image != null) {
                    loadImage(userImage, image.getFilePathOriginal());
                    //loadImage(userImage, image.getFileThumbnail());
                    isUploadingImage = true;
                    //TODO mandar al server y mostrar cargando
                    isUploadingImage = false;
                    showBtnCommitChanges();
                } else {
                    Log.i(TAG, "Chosen Image: Is null");
                }
            }

        });
    }

    private void showBtnCommitChanges() {
        btnCommitChanges.setVisibility(View.VISIBLE);
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

    private void fillUserCategoryInterestsList() {
        Map<String, List<UserInterest>> interestsMap = InterestsUtils.getStringUserInterestsListMap(this.user.getInterests());
        for (Map.Entry<String, List<UserInterest>> entry : interestsMap.entrySet())
        {
            Log.d(TAG, "UserInterestCategory: " + entry.getKey());
            InterestCategory interestCategory = new InterestCategory(entry.getKey(), entry.getValue().size());
            adapterUserInterestsList.add(interestCategory);
        }
    }

/*    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.edit_profile_dialog_change_password_title));
        builder.setIcon(R.drawable.ic_https_24dp);

        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        input.setHint(R.string.edit_profile_dialog_change_password_hint);

        builder.setView(input);

        builder.setPositiveButton(getResources().getString(R.string.edit_profile_dialog_change_password_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newPassword = input.getText().toString();
                //TODO: enviar esto al servidor
                showProgressDialog();

                //esto en realidad es cuando responde el server
                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                //mandar data de volley
                                // onLoginFailed();
                                hideProgressDialog();
                                //mostrarError si server devuelve error
                            }
                        }, 1000);
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.edit_profile_dialog_change_password_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }*/
}

