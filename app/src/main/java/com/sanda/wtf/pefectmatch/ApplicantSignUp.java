package com.sanda.wtf.pefectmatch;
/*
 * The final assignment of Software Engineering
 * @author Alexey Titov and Shir Bentabou
 * @date 12.2018
 * @version 3.0
 */
//libraries
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import WorkingClasses.Applicant;
import WorkingClasses.User;

public class ApplicantSignUp extends AppCompatActivity {
    //Variables
    private EditText su_name; //etApplicantName
    private EditText su_id; //etApplicantId
    private EditText su_username; //etApplicantUserName
    private EditText su_password; //etApplicantPassword
    private Button signUpApplicant; //btnSignUpApplicant
    private static final String TAG = "SignUpApplicant";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signupapplicant);
        //--------------------------------------------EDITTEXTs-------------------------------------
        su_name = findViewById(R.id.etApplicantName);
        su_id = findViewById(R.id.etApplicantId);
        su_username = findViewById(R.id.etApplicantUserName);
        su_password = findViewById(R.id.etApplicantPassword);
        //SIGN UP
        signUpApplicant = findViewById(R.id.btnSignUpApplicant);
        signUpApplicant.setOnClickListener(new View.OnClickListener() {
            //press on the Sign In button
            @Override
            public void onClick(View v) {
                //username + password+id+name
                final String userInputString = su_name.getText().toString(); //applicant name
                final String passwordInputString = su_password.getText().toString(); //applicant password
                final String usernameInputString = su_username.getText().toString(); //applicant username
                final String idInputString = su_id.getText().toString(); //applicant ID
                //check that all fields have input
                if (userInputString.isEmpty() || passwordInputString.isEmpty() || passwordInputString.isEmpty() || idInputString.isEmpty()) {
                    Toast.makeText(ApplicantSignUp.this, "Please fill in all details!", Toast.LENGTH_LONG).show();
                }
                else {
                    //connect to firebase
                    final DatabaseReference dbUserName = FirebaseDatabase.getInstance().getReference();
                    dbUserName.child("Users").child("Applicant").child(usernameInputString).addListenerForSingleValueEvent(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    boolean loginIsOk = false;
                                    User user = null;
                                    if (!dataSnapshot.exists()) {
                                        DatabaseReference dbId = FirebaseDatabase.getInstance().getReference();
                                        dbId.child("ApplicantCourseGrades").child(idInputString).addListenerForSingleValueEvent(
                                                new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        boolean loginIsOk = false;
                                                        if (!dataSnapshot.exists()) {
                                                            loginIsOk = true;
                                                        } else {
                                                            Toast.makeText(ApplicantSignUp.this, "This ID already exists! Enter another.", Toast.LENGTH_LONG).show();
                                                        }
                                                        //if is all OK
                                                        if (loginIsOk) {
                                                            //create applicant object and pass to next activity
                                                            Applicant current = new Applicant(passwordInputString, usernameInputString);
                                                            current.setApplicantName(userInputString);
                                                            current.setApplicantid(Integer.parseInt(idInputString));
                                                            Log.d(TAG, "NEXT page create");
                                                            Intent intent =new Intent(ApplicantSignUp.this ,GeneralApplicantInfo.class);
                                                            //To pass:
                                                            intent.putExtra("Applicant", current);
                                                            startActivity(intent);
                                                        }
                                                    }
                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                                    }
                                                });
                                    } else
                                        Toast.makeText(ApplicantSignUp.this, "This username is already taken! Enter another.", Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                }
                            }
                    );
                }
            }
        });
    }
}