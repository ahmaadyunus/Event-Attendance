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

public class BaseActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

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
        mBottomNav.setOnNavigationItemSelectedListener(this);
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        selectDrawerItem(id);
        return true;

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

}
