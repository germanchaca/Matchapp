package fiuba.matchapp.controller.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fiuba.matchapp.R;
import fiuba.matchapp.app.MyApplication;
import fiuba.matchapp.controller.fragment.InterestsRecyclerViewFragment;
import fiuba.matchapp.model.Interest;
import fiuba.matchapp.model.User;
import fiuba.matchapp.model.UserInterest;
import fiuba.matchapp.networking.httpRequests.GetInterestsRequest;
import fiuba.matchapp.networking.httpRequests.PutUpdateUserData;
import fiuba.matchapp.utils.InterestsUtils;
import fiuba.matchapp.view.LockedProgressDialog;

/**
 * Created by ger on 15/06/16.
 */
public class InterestEditActivity extends AppCompatActivity {
    private static final String TAG = "InterestEditActivity";
    private LockedProgressDialog progressDialog;
    private FloatingActionButton btnConfirmChanges;
    private InterestsRecyclerViewFragment fragment;
    private FrameLayout partenLayout;
    private String category;
    private Map<String, List<UserInterest>> mapUserInterestsByCategory;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_interest);

        initViews();

        Intent intent = getIntent();
        if(intent.hasExtra("category")){
            category = intent.getStringExtra("category");
            Log.d(TAG,"Lanza fragment:" + category);

            GetInterestsRequest request = new GetInterestsRequest() {
                @Override
                protected void onGetInterestsSuccess(List<Interest> interests) {

                    Map<String,List<Interest>> mapInterestsByCategory = InterestsUtils.getStringListMap(interests);
                    //todos los intereses
                    for (Map.Entry<String, List<Interest>> entry : mapInterestsByCategory.entrySet())
                    {
                        if(TextUtils.equals(entry.getKey(), category)){
                            //intereses ya seleccionados por el usuario
                            mapUserInterestsByCategory = InterestsUtils.getStringUserInterestsListMap(MyApplication.getInstance().getPrefManager().getUser().getInterests());
                            for (Map.Entry<String, List<UserInterest>> entryUser : mapUserInterestsByCategory.entrySet())
                            {
                                progressDialog.dismiss();
                                if (TextUtils.equals(entryUser.getKey(),category)){
                                    //tengo en entryUser.getValue() la lista de intereses ya seleccionados por el usuario
                                    fragment = InterestsRecyclerViewFragment.newInstance(entry.getKey(), (ArrayList<Interest>) entry.getValue(),entryUser.getValue());

                                    android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
                                    android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
                                    ft.replace(R.id.contentFragment, fragment);
                                    ft.commit();

                                    Log.d(TAG,"Lanza fragment");
                                }
                            }
                        }
                    }
                }

                @Override
                protected void onGetInterestsDefaultError() {
                    progressDialog.dismiss();
                    showConnectionError();
                }

                @Override
                protected void onGetInterestsConnectionError() {
                    progressDialog.dismiss();
                    showConnectionError();
                }

                @Override
                protected void logout() {
                    progressDialog.dismiss();
                    MyApplication.getInstance().logout();
                }
            };
            progressDialog = new LockedProgressDialog(InterestEditActivity.this, R.style.AppTheme_Dark_Dialog);

            progressDialog.setMessage(getResources().getString(R.string.searching_for_interests));
            progressDialog.show();
            request.make();
        }

    }

    private void showConnectionError() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.connection_problem));
        builder.setPositiveButton(getResources().getString(R.string.connection_problem_ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                back();
            }
        });

        builder.show();
    }

    private void initViews() {
        partenLayout = (FrameLayout) findViewById(R.id.contentFragment);
        btnConfirmChanges = (FloatingActionButton) findViewById(R.id.confirmChanges);
        btnConfirmChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendChangesToAppServer();
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.edit_interesets_profile_toolbar_label));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    private void sendChangesToAppServer() {
        progressDialog.setMessage(getResources().getString(R.string.refresh_account_details));

        if( InterestsUtils.interestsIsEmpty(fragment.mInterestsList) ) {
            showSnackBarError(getResources().getString(R.string.intro_error_empty_interest));
        }else{
            sendInterestsToAppServer(fragment.mInterestsList);
        }
    }

    private void sendInterestsToAppServer(List<Interest> resultInterestsInThisCategory) {

        final List<UserInterest> allUserInterests = new ArrayList<>();

        for (Map.Entry<String, List<UserInterest>> entryUser : mapUserInterestsByCategory.entrySet())
        {
            if(!TextUtils.equals(entryUser.getKey(),category)){
                allUserInterests.addAll(entryUser.getValue());
            }else {
                for(Interest interest:resultInterestsInThisCategory){
                    if(interest.isSelected()){
                        allUserInterests.add(interest.getUserInterest());
                    }
                }
            }
        }

        progressDialog.show();
        PutUpdateUserData request = new PutUpdateUserData(MyApplication.getInstance().getPrefManager().getUser()) {
            @Override
            protected void onUpdateDataSuccess() {
                User user = MyApplication.getInstance().getPrefManager().getUser();
                user.setInterests(allUserInterests);
                MyApplication.getInstance().getPrefManager().storeUser(user);
                progressDialog.dismiss();
                finish();
            }

            @Override
            protected void onAppServerDefaultError() {
                showSnackBarError(getApplicationContext().getString(R.string.internet_problem));
                progressDialog.dismiss();
            }

            @Override
            protected void onAppServerConnectionError() {
                showSnackBarError(getApplicationContext().getString(R.string.internet_problem));
                progressDialog.dismiss();
            }

            @Override
            protected void logout() {
                progressDialog.dismiss();
                MyApplication.getInstance().logout();
            }
        };
        request.changeInterests(allUserInterests);
        request.make();

    }

    private void showSnackBarError(String message) {
        Snackbar.make(partenLayout,message , Snackbar.LENGTH_LONG).show();
        progressDialog.dismiss();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == android.R.id.home) {
            this.onBackPressed();
            return true;
        };
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.alert_dialog_continue_editing));
        builder.setPositiveButton(getResources().getString(R.string.alert_dialog_continue_editing_discard), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                back();
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.alert_dialog_continue_editing_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // do nothing
            }
        });
        builder.show();
    }

    public void back(){
        super.onBackPressed();
    }
}
