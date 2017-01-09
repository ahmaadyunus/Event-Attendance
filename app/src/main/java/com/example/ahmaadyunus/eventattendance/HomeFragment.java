package com.example.ahmaadyunus.eventattendance;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.ahmaadyunus.eventattendance.config.Config;
import com.example.ahmaadyunus.eventattendance.model.Guest;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements ZXingScannerView.ResultHandler{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private ZXingScannerView mScannerView;
    Button scan,signout_btn;
    TextView name_TV,noktp_TV,address_TV,email_TV,mobile_TV, invited_TV,arrived_TV;
    View view;
    Toolbar toolbar;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }
    @Override
    public void handleResult(Result result) {
        if(result!=null) {
            try {
                mScannerView.stopCamera();
                final ProgressDialog progress_dialog = new ProgressDialog(getActivity());
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

                                LayoutInflater inflater = getLayoutInflater(Bundle.EMPTY);
                                View dialoglayout = inflater.inflate(R.layout.result_found, null);
                                AlertDialog.Builder alert1 = new AlertDialog.Builder(getActivity());
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
                            final AlertDialog.Builder alert2 = new AlertDialog.Builder(getActivity());
                            alert2.setTitle(R.string.participant_notfound);
                            alert2.setMessage(R.string.message_not_found);

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

            return;

        }
    }


        @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
            setValue();
            view = inflater.inflate(R.layout.fragment_home, container, false);
            mScannerView = new ZXingScannerView(getActivity());
            scan = (Button)view.findViewById(R.id.scan_btn);
            invited_TV = (TextView) view.findViewById(R.id.invited_num_TV);
            arrived_TV = (TextView) view.findViewById(R.id.arrived_num_TV);
            scan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Scan();
                }
            });
        // Inflate the layout for this fragment
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
//            throw new RuntimeException(context.toString()
  //                  + " must implement OnFragmentInteractionListener");
        }
        setValue();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    public void setValue(){
        try{
            Firebase ref = new Firebase(Config.FIREBASE_URL);
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
    public void Scan (){
        try {   // Programmatically initialize the scanner view
            getActivity().setContentView(mScannerView);

            mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
            mScannerView.startCamera();         // Start camera
        }catch (Exception e){

        }
    }
}
