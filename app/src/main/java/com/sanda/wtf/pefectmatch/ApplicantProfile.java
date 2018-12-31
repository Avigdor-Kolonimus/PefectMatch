package com.sanda.wtf.pefectmatch;
/*
 * The final assignment of Software Engineering
 * @author Alexey Titov and Shir Bentabou
 * @date 12.2018
 * @version 3.0
 */
//libraries
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import DataHolder.AuthenticatedUserHolder;
import WorkingClasses.Applicant;
import WorkingClasses.CourseGrade;

import static java.lang.Double.*;

public class ApplicantProfile extends AppCompatActivity {
    //variables
    private static final String CHANNEL_ID = "my_channel_01";
    //Defining all variables for this activity
    private Button exitButton;
    private Button updateInfo;           //btnUpdateGeneral
    private Button UpdateCourseGrades;   //btnUpdateCourseGrades
    private ImageButton mail;            //ibtnMail
    private static final String TAG = "ApplicantProfile";
    // [START declare_database_ref]
    private DatabaseReference mDatabase;
    // [END declare_database_ref]
    private Applicant AppUser; //Applicant object

    //Variables that show Applicant general data
    private TextView faculty; //tvFaculty
    private EditText average; //etAverage
    private Spinner projectNature; //spinner
    private EditText projectGrade; //etProjectGrade

    //Variables that show Applicant courses data
    private TextView course1; //tvCourse1
    private TextView course2; //tvCourse2
    private TextView course3; //tvCourse3
    private TextView course4; //tvCourse4
    private TextView course5;//tvCourse5
    private EditText course_grade1; //etCourseGrade1
    private EditText course_grade2; //etCourseGrade2
    private EditText course_grade3; //etCourseGrade3
    private EditText course_grade4; //etCourseGrade4
    private EditText course_grade5; //etCourseGrade5

    //variable for applicant id
    String app_id = "";

    //Arrays for courses data
    private CourseGrade[] courses = new CourseGrade[5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppUser = new Applicant(AuthenticatedUserHolder.instance.getAppUser().getPassword(),
                AuthenticatedUserHolder.instance.getAppUser().getName());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applicant_profile);
        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END initialize_database_ref]
        //----------------------------------------UPDATE_GRADES-------------------------------------
        UpdateCourseGrades = findViewById(R.id.btnUpdateCourseGrades);
        UpdateCourseGrades.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference ref = mDatabase.child("ApplicantCourseGrades").child(String.valueOf(AppUser.getApplicantid()));
                try {
                    Integer First = Integer.parseInt(course_grade2.getText().toString());
                    Integer Second = Integer.parseInt(course_grade4.getText().toString());
                    Integer Third = Integer.parseInt(course_grade5.getText().toString());
                    Integer Fourth = Integer.parseInt(course_grade3.getText().toString());
                    Integer Fifth = Integer.parseInt(course_grade1.getText().toString());
                    if (First < 0 || First > 100)
                        Toast.makeText(ApplicantProfile.this, "First grade is incorrect", Toast.LENGTH_LONG).show();
                    else
                        ref.child("First").child("Grade").setValue(First);
                    if (Second < 0 || Second > 100)
                        Toast.makeText(ApplicantProfile.this, "Second grade is incorrect", Toast.LENGTH_LONG).show();
                    else
                        ref.child("Second").child("Grade").setValue(Second);
                    if (Third < 0 || Third > 100)
                        Toast.makeText(ApplicantProfile.this, "Third grade is incorrect", Toast.LENGTH_LONG).show();
                    else
                        ref.child("Third").child("Grade").setValue(Third);
                    if (Fourth < 0 || Fourth > 100)
                        Toast.makeText(ApplicantProfile.this, "Fourth grade is incorrect", Toast.LENGTH_LONG).show();
                    else
                        ref.child("Fourth").child("Grade").setValue(Fourth);
                    if (Fifth < 0 || Fifth > 100)
                        Toast.makeText(ApplicantProfile.this, "First grade is incorrect", Toast.LENGTH_LONG).show();
                    else
                        ref.child("Fifth").child("Grade").setValue(Fifth);
                }catch (NumberFormatException e){
                    Toast.makeText(ApplicantProfile.this, "You need enter only natural number", Toast.LENGTH_LONG).show();
                }
            }
        });
        //----------------------------------------UPDATE_GENERAL------------------------------------
        updateInfo = findViewById(R.id.btnUpdateGeneral);
        updateInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference ref = mDatabase.child("Applicants").child(AppUser.getName());
                Double _average = Double.parseDouble(average.getText().toString());
                String _projectNature =  projectNature.getSelectedItem().toString();
                int _projectGrade = -1;
                try {
                    _projectGrade = Integer.parseInt(projectGrade.getText().toString());
                }catch(NumberFormatException e){
                    Toast.makeText(ApplicantProfile.this, "New value of project grade is incorrect", Toast.LENGTH_LONG).show();
                }
                Log.e(TAG, _average+" "+_projectNature+" "+_projectGrade);
                if (_average<0 || _average>100)
                    Toast.makeText(ApplicantProfile.this, "New value of average is incorrect", Toast.LENGTH_LONG).show();
                else
                    ref.child("average").setValue(_average);
                ref.child("projectNature").setValue(_projectNature);
                if (_projectGrade<0 || _projectGrade>100)
                    Toast.makeText(ApplicantProfile.this, "New value of project grade is incorrect", Toast.LENGTH_LONG).show();
                else
                    ref.child("projectGrade").setValue(_projectGrade);
            }
        });
        //-----------------------------------------------------EXIT---------------------------------
        exitButton = findViewById(R.id.btnApplicantExit);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ApplicantProfile.this, "Goodbye! Hope to see you back soon!", Toast.LENGTH_LONG).show();
                AuthenticatedUserHolder.instance.setAppUser(null);
                Intent intent = new Intent(ApplicantProfile.this ,Home.class);
                startActivity(intent);
            }
        });
        //Setting variables to fields in Applicant Profile - General data
        faculty = findViewById(R.id.tvFaculty);
        average = findViewById(R.id.etAverage);
        //------------------------------------------------SPINNER-----------------------------------
        projectNature = findViewById(R.id.spinner);
        String[] arraySpinner = new String[] {
                "Research", "Non-Research"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        projectNature.setAdapter(adapter);
        //-----------------------------------------------------MAIL---------------------------------
        mail = findViewById(R.id.ibtnMail);
        mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ApplicantProfile.this ,MessageApplicant.class);
                intent.putExtra("ID", app_id);
                startActivity(intent);
            }
        });
        //------------------------------------------------------------------------------------------
        projectGrade = findViewById(R.id.etProjectGrade); //etProjectGrade
        //call getDataManager to present data from db in manager profile
        //Setting variables to fields in Applicant Profile - Courses data
        course1 = findViewById(R.id.tvCourse1); //tvCourse1
        course2 = findViewById(R.id.tvCourse2); //tvCourse2
        course3 = findViewById(R.id.tvCourse3); //tvCourse3
        course4 = findViewById(R.id.tvCourse4); //tvCourse4
        course5 = findViewById(R.id.tvCourse5); //tvCourse5
        course_grade1 = findViewById(R.id.etCourseGrade1); //etCourseGrade1
        course_grade2 = findViewById(R.id.etCourseGrade2); //etCourseGrade2
        course_grade3 = findViewById(R.id.etCourseGrade3); //etCourseGrade3
        course_grade4 = findViewById(R.id.etCourseGrade4); //etCourseGrade4
        course_grade5 = findViewById(R.id.etCourseGrade5); //etCourseGrade5

        getDataApplicant();
        Log.e(TAG, "End OnCreate");
        ThreadForNotification();
    }
    //Get applicant data from database to show in profile
    protected void getDataApplicant(){
        getApplicantData();
    }

    //Get data from course grades table
    protected void getCourseGrades(){
        Log.e(TAG, "Applicant ID in getCourseGrades "+ AppUser.getApplicantid());
        DatabaseReference ref = mDatabase.child("ApplicantCourseGrades").child(String.valueOf(AppUser.getApplicantid()));
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String course;
                int grade;
                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //depending upon what data type you are using caste to it.
                    course = Objects.requireNonNull(snapshot.child("Course").getValue()).toString();
                    grade = Integer.parseInt(Objects.requireNonNull(snapshot.child("Grade").getValue()).toString());
                    courses[i] = new CourseGrade(course, grade);
                    i++;
                    Log.e(TAG, "Course "+i+" "+ course);
                    Log.e(TAG, "Grade "+i+" "+ grade);
                }

                //Show data in applicant profile
                course1.setText(courses[0].getCourse());
                course2.setText(courses[1].getCourse());
                course3.setText(courses[2].getCourse());
                course4.setText(courses[3].getCourse());
                course5.setText(courses[4].getCourse());
                course_grade1.setText(String.valueOf(courses[0].getGrade()));
                course_grade2.setText(String.valueOf(courses[1].getGrade()));
                course_grade3.setText(String.valueOf(courses[2].getGrade()));
                course_grade4.setText(String.valueOf(courses[3].getGrade()));
                course_grade5.setText(String.valueOf(courses[4].getGrade()));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });

    }
    //Get data from applicant table
    protected void getApplicantData(){
        Log.e(TAG, "User Name in getApplicantData "+ AppUser.getName());
        DatabaseReference ref = mDatabase.child("Applicants").child(AppUser.getName());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String[] value = new String[10];
                int i=0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //depending upon what data type you are using caste to it.
                    value[i] = Objects.requireNonNull(snapshot.getValue()).toString();
                    i++;
                }
                //log for data
                Log.e(TAG, "ApplicantID "+ value[0]);
                Log.e(TAG, "Applicant Name " +value[1]);
                Log.e(TAG, "Average" +value[2]);
                Log.e(TAG, "Faculty " +value[3]);
                Log.e(TAG, "Preference Index"+value[5]);
                Log.e(TAG, "Project Grade"+value[6]);
                Log.e(TAG, "Project Nature" +value[7]);
                Log.e(TAG, "Rank " +value[8]);

                //Insert data into Applicant object
                AppUser.setApplicantid(Integer.parseInt(value[0]));
                app_id = value[0];
                AppUser.setApplicantName(value[1]);
                AppUser.updateAverage(parseDouble(value[2]));
                AppUser.updateFaculty(value[3]);
                //value[5] - Preference Index does not show in profile
                AppUser.updateProjectGrade(Integer.parseInt(value[6]));
                AppUser.updateProjectNature(value[7]);
                //value[8] - Rank does not show in profile

                //Show data in applicant profile
                faculty.setText(AppUser.getFaculty());
                average.setText(String.valueOf(AppUser.getAverage()));
                if (value[7].equals("Research"))
                    projectNature.setSelection(0);
                else
                    projectNature.setSelection(1);
                projectGrade.setText(String.valueOf(AppUser.getProjectGrade()));
                getCourseGrades();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });
    }
    //the function of notification
    public void testNoti(){
        int NOTIFICATION_ID = 234;
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence name = "my_channel";
            String Description = "This is my channel";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(Description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mChannel.setShowBadge(false);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_splash)
                .setContentTitle("Perfect Match - Results Arrived")
                .setContentText("You got your result! Click here!");

        Intent resultIntent = new Intent(this, MessageApplicant.class);
        resultIntent.putExtra("ID", app_id);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MessageApplicant.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
    //the function for thread
    protected void ThreadForNotification(){
        new Thread() {
            public void run() {
                final boolean[] flag = {true};
                while (flag[0]) {
                    try {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //Extract data from firebase
                                DatabaseReference ref = mDatabase.child("Notification").child(String.valueOf(AppUser.getApplicantid())).child("Messege");
                                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @SuppressLint("SetTextI18n")
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        // This method is called once with the initial value and again
                                        // whenever data at this location is updated.
                                        if (dataSnapshot.exists()) {
                                            if (Objects.requireNonNull(dataSnapshot.getValue()).toString().equals("1")) {
                                                ref.setValue(0);
                                                testNoti();
                                                flag[0] = false;
                                            }
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Log.e(TAG, "onCancelled", databaseError.toException());
                                    }
                                });
                            }
                        });
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }
}