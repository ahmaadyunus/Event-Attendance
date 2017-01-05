package com.example.ahmaadyunus.eventattendance;

import android.*;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.ahmaadyunus.eventattendance.config.Config;
import com.example.ahmaadyunus.eventattendance.model.Guest;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class BaseActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    BottomNavigationView mBottomNav;
    private static boolean isLaunch = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        Firebase.setAndroidContext(this);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mBottomNav = (BottomNavigationView) findViewById(R.id.navigation);
        setupDrawerContent(mBottomNav);
        checkPermission();
        if (isLaunch) {
            selectDrawerItem(R.id.nav_home);
            isLaunch = false;
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        selectDrawerItem(R.id.nav_home);
    }

    private void setupDrawerContent(BottomNavigationView bottomNavigationView) {
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        int id = item.getItemId();

                        selectDrawerItem(id);
                        return true;

                    }
                });
    }

    public void selectDrawerItem(int id) {
        switch (id) {
            case R.id.nav_home:
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
//            case R.id.nav_two:
//                fragmentClass = SecondFragment.class;
//                break;
//            case R.id.nav_three:
//                fragmentClass = ThirdFragment.class;
//                break;
//            case R.id.nav_four:
//                fragmentClass = ThirdFragment.class;
//                break;
            default:
                break;
        }


    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();


    }

    public void checkPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(BaseActivity.this,
                android.Manifest.permission.CAMERA);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(BaseActivity.this,
                    android.Manifest.permission.CAMERA)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(BaseActivity.this,
                        new String[]{android.Manifest.permission.CAMERA}, 1);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


}
