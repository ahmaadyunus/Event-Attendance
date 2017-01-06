package com.example.ahmaadyunus.eventattendance;

import android.*;
import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ahmaadyunus.eventattendance.config.Config;
import com.example.ahmaadyunus.eventattendance.model.Guest;
import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends BaseActivity
        implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;
    private FirebaseAuth firebaseAuth;

    private FirebaseAuth.AuthStateListener authStateListener;
    Button scan,signout_btn;


    TextView name_TV,noktp_TV,address_TV,email_TV,mobile_TV, invited_TV,arrived_TV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View contentView = getLayoutInflater().inflate(R.layout.activity_main,null);
        drawerLayout.addView(contentView,0);
        Firebase.setAndroidContext(this);

        checkPermission();
        //setValue();
        scan = (Button)findViewById(R.id.scan_btn);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Scan();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        setValue();
    }

    public void Scan (){
        try {
            mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
            setContentView(mScannerView);

            mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
            mScannerView.startCamera();         // Start camera
        }catch (Exception e){

        }
    }



    @Override
    protected void onPause() {
        try {
            super.onPause();
            mScannerView.stopCamera();
        }catch (Exception e){

        }
    }


    @Override
    public void handleResult(Result result) {
        if(result!=null) {
            try {
                mScannerView.stopCamera();
                final ProgressDialog progress_dialog = new ProgressDialog(MainActivity.this);
                progress_dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress_dialog.setTitle(R.string.searching);
                progress_dialog.setCancelable(false);
                progress_dialog.setMessage(getString(R.string.please_wait));
                progress_dialog.setProgress(0);
                progress_dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        return;

                    }
                });
                progress_dialog.show();


                Log.e("handler", result.getText()); // Prints scan results
                Log.e("handler", result.getBarcodeFormat().toString()); // Prints the scan format (qrcode)
                final String QR_code = result.getText();
                Firebase ref = new Firebase(Config.FIREBASE_URL);
                ref.child("first_event").child("participant").orderByChild("id").equalTo(QR_code).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.getValue() != null) {

                            for (final DataSnapshot postSnapshot : snapshot.getChildren()) {
                                //Getting the data from snapshot
                                final Guest guest = postSnapshot.getValue(Guest.class);
                                //Adding it to a string

                                LayoutInflater inflater = getLayoutInflater();
                                View dialoglayout = inflater.inflate(R.layout.result_found, null);
                                AlertDialog.Builder alert1 = new AlertDialog.Builder(MainActivity.this);
                                name_TV = (TextView) dialoglayout.findViewById(R.id.name_participant_TV);
                                noktp_TV = (TextView) dialoglayout.findViewById(R.id.noktp_participant_TV);
                                address_TV = (TextView) dialoglayout.findViewById(R.id.address_participant_TV);
                                email_TV = (TextView) dialoglayout.findViewById(R.id.email_participant_TV);
                                mobile_TV = (TextView) dialoglayout.findViewById(R.id.mobile_participant_TV);

                                name_TV.setText(": " + guest.getName());
                                noktp_TV.setText(": " + guest.getNo_ktp());
                                address_TV.setText(": " + guest.getAddress());
                                email_TV.setText(": " + guest.getEmail());
                                mobile_TV.setText(": " + guest.getMobile());

                                alert1.setTitle(R.string.participant);
                                alert1.setView(dialoglayout);
                                setContentView(R.layout.activity_main);
                                alert1.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        postSnapshot.getRef().child("status").setValue("arrived");
                                        setValue();
                                    }
                                });
                                progress_dialog.hide();
                                alert1.show();

                            }
                        } else {
                            progress_dialog.hide();
                            setContentView(R.layout.activity_main);
                            final AlertDialog.Builder alert2 = new AlertDialog.Builder(MainActivity.this);
                            alert2.setTitle(R.string.participant_notfound);
                            alert2.setMessage(R.string.message_not_found);
                            setContentView(R.layout.activity_main);
                            alert2.setNegativeButton("try again", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Scan();
                                }
                            });
                            alert2.show();
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        System.out.println("The read failed: " + firebaseError.getMessage());
                    }
                });
            } catch (Exception e) {

            }
        }else{
            Intent getMainScreen = new Intent(this, MainActivity.class);//pentru test, de sters
            startActivity(getMainScreen);

        }


        // show the scanner result into dialog box.

    }
    public void checkPermission(){
        int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CAMERA);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.CAMERA)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.CAMERA},1);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }
    public void setValue(){
        try{
            Firebase ref = new Firebase(Config.FIREBASE_URL);
            invited_TV = (TextView) findViewById(R.id.invited_num_TV);
            arrived_TV = (TextView) findViewById(R.id.arrived_num_TV);
            ref.child("first_event").child("participant").orderByChild("status").equalTo("invited").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    Integer invited = (int)snapshot.getChildrenCount();
                    invited_TV.setText(String.valueOf(invited));
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
            ref.child("first_event").child("participant").orderByChild("status").equalTo("arrived").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    Integer arrived = (int)snapshot.getChildrenCount();
                    arrived_TV.setText(String.valueOf(arrived));
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });


        }catch (Exception e){

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
