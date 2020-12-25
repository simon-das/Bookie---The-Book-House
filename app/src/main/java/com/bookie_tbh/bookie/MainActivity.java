package com.bookie_tbh.bookie;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;

import com.bookie_tbh.bookie.ui.search.SearchFragment;
import com.bookie_tbh.bookie.ui.home.HomeFragment;
import com.bookie_tbh.bookie.ui.downloads.DownloadsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FirebaseAuth mAuth;
    private BottomNavigationView navView;
    private Toast backToast;
    private long backPressedTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//      bottom navigation bar
        navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        navView.setOnNavigationItemSelectedListener(navListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, new HomeFragment()).commit();

//      toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }




//  method for toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

//  method for toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.navigation_login){
            signInDialog();
        }
        return true;
    }

//  method on pressed back button of device
    @Override
    public void onBackPressed() {

        String fragmentId;

        if (getSupportFragmentManager().getBackStackEntryCount() > 0 ) {
             fragmentId = String.valueOf(getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName());
        }else {
            fragmentId = String.valueOf(R.id.navigation_home);
        }
        Log.d("fragment", fragmentId + " " + getSupportFragmentManager().getBackStackEntryCount());
        if (fragmentId.equals(String.valueOf(R.id.navigation_home))){

            if (backPressedTime + 2000 > System.currentTimeMillis()){
                backToast.cancel();
                super.onBackPressed();
            }else {
                backToast = Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT);
                backToast.show();
                backPressedTime = System.currentTimeMillis();
            }
        }else if(fragmentId.equals(String.valueOf(R.id.navigation_search)) || fragmentId.equals(String.valueOf(R.id.navigation_downloads))){
            navView.setSelectedItemId(R.id.navigation_home);
        }else{
            super.onBackPressed();
        }
    }

    //  sign-in dialog
    public void signInDialog(){
        SignInDialog signInDialog = new SignInDialog();
        signInDialog.show(getSupportFragmentManager(), "Sign-in Dialog");
    }

//  method for bottom navigation bar
    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            Fragment fragment = null;

            switch (item.getItemId()){
                case R.id.navigation_home:
//                  clear back stack
                    getSupportFragmentManager().popBackStackImmediate(null, getSupportFragmentManager().POP_BACK_STACK_INCLUSIVE);

                    fragment = new HomeFragment();
                    break;
                case R.id.navigation_search:
                    fragment = new SearchFragment();
                    break;
                case R.id.navigation_downloads:
                    fragment = new DownloadsFragment();
                    break;
            }
            if (item.getItemId() != R.id.navigation_home)
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, fragment).addToBackStack(String.valueOf(item.getItemId())).commit();
            return true;
        }
    };

}
