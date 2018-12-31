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
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import DataHolder.AuthenticatedUserHolder;
import WorkingClasses.Applicant;
import WorkingClasses.CourseGrade;
import WorkingClasses.User;

public class PreferencesApplicantInfo extends AppCompatActivity {
    //variables
    private Applicant current;
    private CourseGrade courses[] = new CourseGrade[5];
    private String faculty = "";
    private ArrayList<String> arraySpinnerUniversity = new ArrayList<String>();
    private ArrayList<String> arraySpinnerPreference = new ArrayList<String>();
    private Map<Integer,String> preferenceList = new HashMap<Integer, String>();
    private Spinner university; //spinnerUniversity
    private Spinner preference; //spinnerPreference
    private Button addPreference; //btnAddPreference
    private ArrayAdapter<String> adapterUniversity;
    private ArrayAdapter<String> adapterPreference;
    private TableLayout preferenceTable; //table
    //TAG for messages
    private static final String TAG = "PreferencesApplicantInfo";

    // [START declare_database_ref]
    private DatabaseReference mDatabase;
    // [END declare_database_ref]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applicantinfo_preferences);

        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END initialize_database_ref]

        //Match preference table to variable
        preferenceTable = findViewById(R.id.table);
        //receive Applicant & CourseGrade objects from previous intent
        Intent i = getIntent();
        current = (Applicant) i.getSerializableExtra("Applicant");
        courses = (CourseGrade[]) i.getSerializableExtra("CourseGrade");
        faculty = current.getFaculty();
        getUniversityList();

        //-------------------------------------------ADD PREFERENCE---------------------------------
        addPreference = findViewById(R.id.btnAddPreference);
        addPreference.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (arraySpinnerUniversity.size()!=0) {
                    Toast.makeText(PreferencesApplicantInfo.this, "Adding preference to list!", Toast.LENGTH_LONG).show();
                    String uni = university.getSelectedItem().toString();
                    String pref = preference.getSelectedItem().toString();
                    // Remove item/element from List index zero
                    // Remove first item of List
                    arraySpinnerUniversity.remove(uni);
                    adapterUniversity.notifyDataSetChanged();
                    arraySpinnerPreference.remove(pref);
                    adapterPreference.notifyDataSetChanged();

                    //Create TextView for university (to add to table)
                    TextView universityDisplay = new TextView(getApplicationContext());
                    universityDisplay.setGravity(Gravity.CENTER_HORIZONTAL);
                    universityDisplay.setTextColor(0xff000000);
                    universityDisplay.setText(uni);

                    //Create TextView for preference (to add to table)
                    TextView universityPreference = new TextView(getApplicationContext());
                    universityPreference.setGravity(Gravity.CENTER_HORIZONTAL);
                    universityPreference.setTextColor(0xff000000);
                    universityPreference.setText(pref);

                    // Create Table row to insert to existing table
                    TableRow row = new TableRow(getApplicationContext());
                    row.addView(universityDisplay); // create column with university name
                    row.addView(universityPreference); // create column with current preference

                    preferenceTable.addView(row); // Add row to table
                    preferenceList.put(Integer.parseInt(pref),uni);
                    if (arraySpinnerUniversity.size()==0) {
                        preferenceList.put(preferenceList.size()+1,"Fake");
                        addPreference.setText("Finish");
                    }
                }
                else{
                    Toast.makeText(PreferencesApplicantInfo.this, "Finished adding preferences! Sign up completed!", Toast.LENGTH_LONG).show();
                    //add all applicant data to new applicant in firebase
                    //add new user into "Users" (child Applicant)
                    mDatabase.child("Users").child("Applicant").child(current.getName()).child("password").setValue(current.getPassword());
                    //add applicant info to "Applicants"
                    mDatabase.child("Applicants").child(current.getName()).child("applicantId").setValue(current.getApplicantid());
                    mDatabase.child("Applicants").child(current.getName()).child("applicantName").setValue(current.getApplicantName());
                    mDatabase.child("Applicants").child(current.getName()).child("average").setValue(current.getAverage());
                    mDatabase.child("Applicants").child(current.getName()).child("faculty").setValue(current.getFaculty());
                    mDatabase.child("Applicants").child(current.getName()).child("password").setValue(current.getPassword());
                    mDatabase.child("Applicants").child(current.getName()).child("preferenceIndex").setValue(1);
                    mDatabase.child("Applicants").child(current.getName()).child("projectGrade").setValue(current.getProjectGrade());
                    mDatabase.child("Applicants").child(current.getName()).child("projectNature").setValue(current.getProjectNature());
                    mDatabase.child("Applicants").child(current.getName()).child("rank").setValue(0);
                    mDatabase.child("Applicants").child(current.getName()).child("username").setValue(current.getName());
                    //add preference list into "ApplicantPreferences"
                    preferenceList.forEach((key,value)->{
                        mDatabase.child("ApplicantPreferences").child(String.valueOf(current.getApplicantid())).child(key.toString()).setValue(value.toString());
                    });
                    //Add course grades info into "ApplicantCourseGrades"
                    mDatabase.child("ApplicantCourseGrades").child(String.valueOf(current.getApplicantid())).child("First").child("Course").setValue(courses[0].getCourse());
                    mDatabase.child("ApplicantCourseGrades").child(String.valueOf(current.getApplicantid())).child("First").child("Grade").setValue(courses[0].getGrade());
                    mDatabase.child("ApplicantCourseGrades").child(String.valueOf(current.getApplicantid())).child("Second").child("Course").setValue(courses[1].getCourse());
                    mDatabase.child("ApplicantCourseGrades").child(String.valueOf(current.getApplicantid())).child("Second").child("Grade").setValue(courses[1].getGrade());
                    mDatabase.child("ApplicantCourseGrades").child(String.valueOf(current.getApplicantid())).child("Third").child("Course").setValue(courses[2].getCourse());
                    mDatabase.child("ApplicantCourseGrades").child(String.valueOf(current.getApplicantid())).child("Third").child("Grade").setValue(courses[2].getGrade());
                    mDatabase.child("ApplicantCourseGrades").child(String.valueOf(current.getApplicantid())).child("Fourth").child("Course").setValue(courses[3].getCourse());
                    mDatabase.child("ApplicantCourseGrades").child(String.valueOf(current.getApplicantid())).child("Fourth").child("Grade").setValue(courses[3].getGrade());
                    mDatabase.child("ApplicantCourseGrades").child(String.valueOf(current.getApplicantid())).child("Fifth").child("Course").setValue(courses[4].getCourse());
                    mDatabase.child("ApplicantCourseGrades").child(String.valueOf(current.getApplicantid())).child("Fifth").child("Grade").setValue(courses[4].getGrade());
                    //Next screen - applicant's profile
                    User NewUser = new User(current.getPassword(), current.getName(), "Applicant");
                    AuthenticatedUserHolder.instance.setAppUser(NewUser);
                    Intent intent = new Intent(PreferencesApplicantInfo.this ,ApplicantProfile.class);
                    startActivity(intent);
                }
            }
        });

    }

    //Get data from course grades table
    protected void getUniversityList() {
        DatabaseReference ref = mDatabase.child("AllDataFaculty").child(faculty);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                int i = 1;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //depending upon what data type you are using cast to it.
                    String nameU = snapshot.getKey().toString();
                    if (!nameU.equals("Fake")){
                        arraySpinnerUniversity.add(nameU);
                        arraySpinnerPreference.add(String.valueOf(i));
                        i++;
                    }
                }
                setToSpinners();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });
    }

    //Set data from database to spinner
    protected void setToSpinners(){
        //----------------------------------------SPINNER_UNIVERSITY--------------------------------
        university = findViewById(R.id.spinnerUniversity);
        adapterUniversity = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinnerUniversity);
        adapterUniversity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        university.setAdapter(adapterUniversity);
        //----------------------------------------SPINNER_PREFERENCE--------------------------------
        preference = findViewById(R.id.spinnerPreference);
        adapterPreference = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinnerPreference);
        adapterPreference.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        preference.setAdapter(adapterPreference);
    }

}
