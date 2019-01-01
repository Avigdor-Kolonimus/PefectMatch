package com.sanda.wtf.pefectmatch;
/*
 * The final assignment of Software Engineering
 * @author Alexey Titov and Shir Bentabou
 * @date 12.2018
 * @version 3.0
 */
//libraries
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import DataHolder.AuthenticatedUserHolder;
import WorkingClasses.User;

public class Home extends AppCompatActivity {
    //variables
    private EditText usernameInput;
    private EditText passwordInput;
    private Button signupButton; //btnSignUp
    private Button signinButton; //btnD=SignIn
    private RadioGroup radioUserGroup;
    private RadioButton radioUserButton;
    private static final String TAG = "Home";

    // [START declare_database_ref]
    private DatabaseReference mDatabase;
    // [END declare_database_ref]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END initialize_database_ref]
        addListenerOnButton();
    }

    //function for buttons
    public void addListenerOnButton() {
        //RADIO GROUP
        radioUserGroup = findViewById(R.id.rgType);
        //EDIT TEXT fields
        usernameInput = findViewById(R.id.etName);
        passwordInput = findViewById(R.id.etPassword);
        //SIGN UP
        signupButton = findViewById(R.id.btnSignUp);
        signupButton.setOnClickListener(new OnClickListener() {
            //when Sign Up button is pressed
            @Override
            public void onClick(View v) {
                // get selected radio button from radioGroup
                final int selectedId = radioUserGroup.getCheckedRadioButtonId();
                // find the radiobutton by returned value
                radioUserButton = findViewById(selectedId);
                final String typeUser = radioUserButton.getText().toString();
                //check kind of user to open it's profile once signed in
                if (typeUser.equals("Manager")){
                    Toast.makeText(Home.this, "Aren't you naughty?", Toast.LENGTH_LONG).show();
                }else
                    if(typeUser.equals("Applicant")){
                        DatabaseReference ref = mDatabase.child("Block_Dates"); //check if applicants may sign up datewise
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                // This method is called once with the initial value and again
                                // whenever data at this location is updated.
                                String date_app = dataSnapshot.child("dateApplicants").getValue().toString();
                                String block_app = dataSnapshot.child("blockApplicants").getValue().toString();
                                if (block_app.equals("UNBLOCK")) { //if it is not blocked
                                    Log.d(TAG, "Applicant");
                                    Intent intent =new Intent(Home.this ,ApplicantSignUp.class); //pass to sign up intent
                                    startActivity(intent);
                                }else{
                                    Toast.makeText(Home.this, "Algorithm it running! Please sign up after "+date_app, Toast.LENGTH_LONG).show(); //if blocked
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.e(TAG, "onCancelled", databaseError.toException());
                            }
                        });
                    }
                    else{ //last possibility - University
                        DatabaseReference ref = mDatabase.child("Block_Dates");
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                // This method is called once with the initial value and again
                                // whenever data at this location is updated.
                                String date_uni = dataSnapshot.child("dateUniversity").getValue().toString();
                                String block_uni = dataSnapshot.child("blockUniversities").getValue().toString();
                                if (block_uni.equals("UNBLOCK")) { //if it is not blocked
                                    Log.d(TAG, "University");
                                    Toast.makeText(Home.this, "Not implemented!", Toast.LENGTH_LONG).show(); //message
                                }else{
                                    Toast.makeText(Home.this, "Algorithm id running! Please sign up after "+date_uni, Toast.LENGTH_LONG).show(); //if blocked
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.e(TAG, "onCancelled", databaseError.toException());
                            }
                        });
                }
            }
        });
        //SIGN IN
        signinButton = findViewById(R.id.btnSignIn);
        signinButton.setOnClickListener(new OnClickListener() {
            //press on the Sign In button
            @Override
            public void onClick(View v) {
                //username + password
                final String userInputString = usernameInput.getText().toString();
                final String passwordInputString = passwordInput.getText().toString();
                // get selected radio button from radioGroup
                final int selectedId = radioUserGroup.getCheckedRadioButtonId();
                // find the radiobutton by returned value
                radioUserButton = findViewById(selectedId);
                final String typeUser = radioUserButton.getText().toString();
                //check kind of user to open it's profile once signed in
                if (typeUser.equals("Manager")){
                    Log.d(TAG, "Manager");
                    openManager(typeUser, passwordInputString, userInputString);
                }else
                    if(typeUser.equals("Applicant")){
                        Log.d(TAG, "Applicant");
                        openApplicant(typeUser, passwordInputString, userInputString);
                    }
                    else{ //last possibility - University
                        Log.d(TAG, "University"); //sign in not implemented for university
                    }
            }
        });
    }
    //login for Manager type - check input data
    protected void openManager(final String typeUser, final String passwordInputString, final String userInputString){
        //connect to firebase
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child("Users").child(typeUser).addListenerForSingleValueEvent(
                new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean loginIsOk = false;
                    User user = null;
                    if(dataSnapshot.exists()) {
                        String[] value= new String[2];
                        int i=0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            //depending upon what data type you are using, cast to it.
                            value[i] = snapshot.getValue(String.class);
                            i++;
                        }
                        user = new User (value[0], value[1], typeUser);
                        Log.d(TAG, "Open manager check "+value[0]);
                        if (passwordInputString.equals(user.getPassword()) && userInputString.equals(user.getName())) {
                            loginIsOk = true;
                        }
                    }
                    //if is all OK
                    if(loginIsOk) {
                        AuthenticatedUserHolder.instance.setAppUser(user); //open profile
                        openProfile(typeUser);
                    } else {
                          Toast.makeText(Home.this, "Either username or password is incorrect.", Toast.LENGTH_LONG).show(); //not ok - print message
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d(TAG, "onCancelled "+databaseError.getMessage());
                }
                }
         );
    }
    //check for Applicant type
    protected void openApplicant(final String typeUser, final String passwordInputString, final String userInputString) {
        //connect to firebase
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child("Users").child(typeUser).child(userInputString).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean loginIsOk = false;
                        User user = null;
                        String value= "";
                        if(dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                //depending upon what data type you are using, cast to it.
                                value = snapshot.getValue(String.class);
                            }
                        }
                        user = new User(value, userInputString, typeUser);
                        Log.d(TAG, "Open applicant check "+value);
                        if (passwordInputString.equals(user.getPassword())) {
                            loginIsOk = true;
                        }
                        //if is all OK
                        if(loginIsOk) {
                            AuthenticatedUserHolder.instance.setAppUser(user); //open profile
                            openProfile(typeUser);
                        } else {
                            Toast.makeText(Home.this, "Either username or password is incorrect.", Toast.LENGTH_LONG).show(); //not ok - print message
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                }
        );
    }

    //open correct profile of current user signing in
    public void openProfile(String type){
        Toast.makeText(Home.this, "Welcome Back!", Toast.LENGTH_LONG).show();
        if (type.equals("Manager")){
            Log.d(TAG, "Start manager activity");
            Intent intent =new Intent(this ,ManagerProfile.class); //pass to manager profile intent
            startActivity(intent);
        }
        if (type.equals("Applicant")){
            Log.d(TAG, "Start applicant activity");
            Intent intent =new Intent(this ,ApplicantProfile.class); //pass to applicant profile intent
            startActivity(intent);
        }
    }
}