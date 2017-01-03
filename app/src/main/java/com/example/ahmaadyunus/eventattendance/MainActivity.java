package com.example.ahmaadyunus.eventattendance;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.ahmaadyunus.eventattendance.config.Config;
import com.example.ahmaadyunus.eventattendance.model.Guest;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends AppCompatActivity
        implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    TextView name_TV,noktp_TV,address_TV,email_TV,mobile_TV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Firebase.setAndroidContext(this);
    }
    public void Scan (View view){
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);

        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();         // Start camera

    }

    @Override
    protected void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result result) {
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

        final Firebase ref = new Firebase(Config.FIREBASE_URL);
        Log.e("handler", result.getText()); // Prints scan results
        Log.e("handler", result.getBarcodeFormat().toString()); // Prints the scan format (qrcode)
        final String QR_code = result.getText().toString();
        ref.child("first_event").child("participant").orderByChild("id").equalTo(QR_code).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(snapshot.getValue()!=null) {
                    progress_dialog.hide();
                    for (final DataSnapshot postSnapshot : snapshot.getChildren()) {
                        //Getting the data from snapshot
                        final Guest guest = postSnapshot.getValue(Guest.class);
                        //Adding it to a string
                        LayoutInflater inflater = getLayoutInflater();
                        View dialoglayout = inflater.inflate(R.layout.result_found, null);
                        AlertDialog.Builder alert1 = new AlertDialog.Builder(MainActivity.this);
                        name_TV = (TextView)dialoglayout.findViewById(R.id.name_participant_TV);
                        noktp_TV = (TextView)dialoglayout.findViewById(R.id.noktp_participant_TV);
                        address_TV = (TextView)dialoglayout.findViewById(R.id.address_participant_TV);
                        email_TV = (TextView)dialoglayout.findViewById(R.id.email_participant_TV);
                        mobile_TV = (TextView)dialoglayout.findViewById(R.id.mobile_participant_TV);

                        name_TV.setText(": "+guest.getName());
                        noktp_TV.setText(": "+guest.getNo_ktp());
                        address_TV.setText(": "+guest.getAddress());
                        email_TV.setText(": "+guest.getEmail());
                        mobile_TV.setText(": "+guest.getMobile());

                        alert1.setTitle(R.string.participant);
                        alert1.setView(dialoglayout);
                        setContentView(R.layout.activity_main);
                        alert1.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        postSnapshot.getRef().child("status").setValue("present");
                                    }
                                });
                        alert1.show();

                    }
                }else{
                    progress_dialog.hide();
                    setContentView(R.layout.activity_main);
                    final AlertDialog.Builder alert2 = new AlertDialog.Builder(MainActivity.this);
                    alert2.setTitle(R.string.participant_notfound);
                    alert2.setMessage(R.string.message_not_found);
                    setContentView(R.layout.activity_main);
                    alert2.setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
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

        // show the scanner result into dialog box.

    }
}
