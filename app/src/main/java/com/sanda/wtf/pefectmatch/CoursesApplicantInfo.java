package com.sanda.wtf.pefectmatch;
/*
 * The final assignment of Software Engineering
 * @author Alexey Titov and Shir Bentabou
 * @date 12.2018
 * @version 3.0
 */
//librries
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import WorkingClasses.Applicant;
import WorkingClasses.CourseGrade;

public class CoursesApplicantInfo extends AppCompatActivity {
    //Variables
    private CourseGrade courses[] = new CourseGrade[5];
    private TextView course1name; //tvCourse1Name
    private TextView course2name; //tvCourse2Name
    private TextView course3name; //tvCourse3Name
    private TextView course4name; //tvCourse4Name
    private TextView course5name; //tvCourse5Name
    private EditText course1grade; //etCourse1Grade
    private EditText course2grade; //etCourse2Grade
    private EditText course3grade; //etCourse3Grade
    private EditText course4grade; //etCourse4Grade
    private EditText course5grade; //etCourse5Grade
    private Button next_courses;    //btnApplicantGrades

    private Applicant current;
    private static final String TAG = "CoursesApplicantInfo";

    // [START declare_database_ref]
    private DatabaseReference mDatabase;
    // [END declare_database_ref]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_applicantinfo_courses);

        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END initialize_database_ref]

        //receive Applicant object from previous intent
        Intent i = getIntent();
        current = (Applicant) i.getSerializableExtra("Applicant");
        //match between variables and fields in activity
        course1name = findViewById(R.id.tvCourse1Name);
        course2name = findViewById(R.id.tvCourse2Name);
        course3name = findViewById(R.id.tvCourse3Name);
        course4name = findViewById(R.id.tvCourse4Name);
        course5name = findViewById(R.id.tvCourse5Name);
        course1grade = findViewById(R.id.etCourse1Grade);
        course2grade = findViewById(R.id.etCourse2Grade);
        course3grade = findViewById(R.id.etCourse3Grade);
        course4grade = findViewById(R.id.etCourse4Grade);
        course5grade = findViewById(R.id.etCourse5Grade);

        getCourseNames();
        //---------------------------------------BUTTON---------------------------------------------
        next_courses = findViewById(R.id.btnApplicantGrades);
        next_courses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CoursesApplicantInfo.this, "Advancing to last step!", Toast.LENGTH_LONG).show();
                int grade1=-1;
                int grade2=-1;
                int grade3=-1;
                int grade4=-1;
                int grade5=-1;
                try{
                    grade1 = Integer.parseInt(course1grade.getText().toString());
                    grade2 = Integer.parseInt(course2grade.getText().toString());
                    grade3 = Integer.parseInt(course3grade.getText().toString());
                    grade4 = Integer.parseInt(course4grade.getText().toString());
                    grade5 = Integer.parseInt(course5grade.getText().toString());
                }catch(NumberFormatException e) {
                    Log.d(TAG, "No insert " + e.getMessage());
                }
                //check if all data was inserted check legal values for text fields
                if (grade1 <= 0 || grade1 > 100 || grade2 <= 0 || grade2 > 100 || grade3 <= 0 || grade3 > 100 || grade4 <= 0 || grade4 > 100 || grade5 <= 0 || grade5 > 100) {
                    Toast.makeText(CoursesApplicantInfo.this, "You have to insert correct data to all fields!", Toast.LENGTH_LONG).show();
                } else {
                    //all data into courses
                    courses[0].setGrade(grade1);
                    courses[1].setGrade(grade2);
                    courses[2].setGrade(grade3);
                    courses[3].setGrade(grade4);
                    courses[4].setGrade(grade5);

                    Intent intent = new Intent(CoursesApplicantInfo.this ,PreferencesApplicantInfo.class);
                    //To pass:
                    intent.putExtra("Applicant", current); //all applicant info
                    intent.putExtra("CourseGrade", courses); //applicant courses & grades

                    startActivity(intent);
                }
            }
        });
    }
    //Get data from course grades table
    protected void getCourseNames() {
        DatabaseReference ref = mDatabase.child("FacultyToCourses").child(String.valueOf(current.getFaculty()));
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String course[] = new String[5];
                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //depending upon what data type you are using cast to it.
                    course[i] = Objects.requireNonNull(snapshot.getValue()).toString();
                    i++;
                }

                //Show data in courses table
                course1name.setText(course[0]);
                course2name.setText(course[1]);
                course3name.setText(course[2]);
                course4name.setText(course[3]);
                course5name.setText(course[4]);

                courses[0] = new CourseGrade(course[0]);
                courses[1] = new CourseGrade(course[1]);
                courses[2] = new CourseGrade(course[2]);
                courses[3] = new CourseGrade(course[3]);
                courses[4] = new CourseGrade(course[4]);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });
    }
}