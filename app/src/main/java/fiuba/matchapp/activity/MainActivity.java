package fiuba.matchapp.activity;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import fiuba.matchapp.R;
import fiuba.matchapp.fragment.Connect;
import fiuba.matchapp.fragment.OpenChatsFragment;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    private String TAG_OPENCHATS_FRAGMENT = OpenChatsFragment.class.getSimpleName();
    private String TAG_CONNECT = Connect.class.getSimpleName();

    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       // if (MyApplication.getInstance().getPrefManager().getUser() == null) {
            //launchLoginActivity();
        //}

        if (savedInstanceState == null) {
            // During initial setup, plug in the openChats fragment.
            OpenChatsFragment openChatsFragment = new OpenChatsFragment();
            openChatsFragment.setArguments(getIntent().getExtras());

            getFragmentManager().beginTransaction().add(R.id.contentFragment, openChatsFragment, TAG_OPENCHATS_FRAGMENT).addToBackStack(TAG_OPENCHATS_FRAGMENT).commit();
        }


    }
    private void launchLoginActivity() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

        //Intent intent = new Intent(this,LoginActivity.class);
        //startActivity(intent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch ( id) {

            case R.id.action_logout:
                break;

            case R.id.action_profile:

                Intent intent = new Intent(context, EditableProfileActivity.class);
                startActivity(intent);
                break;

            case R.id.icon_game:

                Fragment fragmentGame = getFragmentManager().findFragmentByTag(TAG_CONNECT);
                if (fragmentGame  == null){
                    fragmentGame = new Connect();
                }
                getFragmentManager().beginTransaction().replace(R.id.contentFragment, fragmentGame, TAG_CONNECT).addToBackStack(TAG_CONNECT).commit();

                break;

            case R.id.icon_chats:

                Fragment fragmentChats = getFragmentManager().findFragmentByTag(TAG_OPENCHATS_FRAGMENT);
                if (fragmentChats  == null){
                    fragmentChats = new OpenChatsFragment();
                }
                getFragmentManager().beginTransaction().replace(R.id.contentFragment, fragmentChats, TAG_OPENCHATS_FRAGMENT).addToBackStack(TAG_OPENCHATS_FRAGMENT).commit();

                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
