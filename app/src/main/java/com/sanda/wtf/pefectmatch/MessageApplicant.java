package com.sanda.wtf.pefectmatch;
/*
 * The final assignment of Software Engineering
 * @author Alexey Titov and Shir Bentabou
 * @date 12.2018
 * @version 3.0
 */
//libraries
import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class MessageApplicant extends AppCompatActivity {
    //variables
    private TextView nextDate; //tvDate
    private  TextView uniName; //tvUniversityName
    private Button back; //btnBack
    private String app_id = "";
    private static final String TAG = "MessageApplicant";
    // [START declare_database_ref]
    private DatabaseReference mDatabase;
    // [END declare_database_ref]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applicant_messages);

        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END initialize_database_ref]


        //----------------------------------------------BACK----------------------------------------
        back = findViewById(R.id.btnBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessageApplicant.this ,ApplicantProfile.class);
                startActivity(intent);
            }
        });
        //match variables to fields in activity
        nextDate = findViewById(R.id.tvDate);
        uniName = findViewById(R.id.tvUniversityName);
        //receive string from previous intent
        Intent i = getIntent();
        app_id = (String) i.getSerializableExtra("ID");
        //Extract data from firebase
        DatabaseReference ref = mDatabase.child("ApplicantResults").child(app_id).child("YourUniversity");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if (dataSnapshot.exists()) {
                    if (Objects.requireNonNull(dataSnapshot.getValue()).toString().equals("Fake"))
                        uniName.setText("You have not been accepted to any university. Try again next time :(");
                    else
                        uniName.setText(dataSnapshot.getValue().toString());
                }
                else
                    uniName.setText("No result");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });
        ref = mDatabase.child("Block_Dates").child("dateApplicants");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                nextDate.setText(Objects.requireNonNull(dataSnapshot.getValue()).toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });
    }
}
