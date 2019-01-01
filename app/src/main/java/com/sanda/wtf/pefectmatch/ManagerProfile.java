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
import android.widget.TextView;
import android.widget.Toast;
//dialog
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.DatePicker;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
//firebase
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
//date
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
//our classes
import DataHolder.AuthenticatedUserHolder;
import WorkingClasses.Algorithm;
import WorkingClasses.Applicant;
import WorkingClasses.Faculty;
import WorkingClasses.User;

public class ManagerProfile extends AppCompatActivity{
    //Defining all variables for this activity
    private Button updateDates;
    private Button blockUniversities;
    private Button blockApplicants;
    private Button runAlgorithm;
    private Button exitButton;
    private TextView blockDate;
    private TextView blockDateUniversities;
    private TextView blockDateApplicants;

    // [START declare_database_ref]
    private DatabaseReference mDatabase;
    // [END declare_database_ref]

    private static final String TAG = "ManagerProfile";
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private int WhereblockDate;                                     //1 - University, 2 - Applicant
    private User AdminUser;                                         //user that is connected
    private boolean permission = false;                             //if manager can run algorithm
    private String blockORunblockApplicant;                         //holds current value
    private String blockORunblockUniversity;                        //holds current value
    private final ArrayList<Applicant> ap = new ArrayList<Applicant>();     //Applicant list (to hold data from various tables)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        WhereblockDate = 0;
        AdminUser = AuthenticatedUserHolder.instance.getAppUser();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);

        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END initialize_database_ref]

        //----------------------------------------------------DATE----------------------------------
        blockDate = findViewById(R.id.tvNextDate);
        blockDateUniversities = findViewById(R.id.tvBlockingUniversities);
        blockDateUniversities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance(); //get today's date
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog( //builds dialog
                        ManagerProfile.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                WhereblockDate = 1;
                dialog.show();
            }
        });
        blockDateApplicants = findViewById(R.id.tvBlockingApplicants);
        blockDateApplicants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance(); //get today's date
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog( //builds dialog
                        ManagerProfile.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                WhereblockDate = 2;
                dialog.show();
            }
        });
        //---------------------------------------------------DIALOG---------------------------------
        //operates dialog with value inserted
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG, "onDateSet: dd/mm/yyy: " + day + "/" + month + "/" + year);
                String date = day + "/" + month + "/" + year;
                if (WhereblockDate == 1) //university
                    if (compareDate(blockDate.getText().toString(), date))
                        blockDateUniversities.setText(date);
                    else
                        Toast.makeText(ManagerProfile.this, "You need to enter a date before "+blockDate.getText().toString(), Toast.LENGTH_LONG).show();
                else { //applicant
                    if (compareDate(date, blockDateUniversities.getText().toString())){
                        blockDate.setText(date);
                        blockDateApplicants.setText(date);
                    }else
                        Toast.makeText(ManagerProfile.this, "You need to enter a date after "+blockDateUniversities.getText().toString(), Toast.LENGTH_LONG).show();
                }
            }
        };

        //call getDataManager to present data from db in manager profile
        getDataManager();
        //call checkPermission to check if user has permission to update data
        checkPermission();

        //-------------------------------------------------UPDATE DATES-----------------------------
        updateDates = findViewById(R.id.btnUpdateDate);
        updateDates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                if (permission) {
                    ref.child("Block_Dates").child("dateApplicants")
                            .setValue(blockDateApplicants.getText().toString());
                    ref.child("Block_Dates").child("dateUniversity")
                            .setValue(blockDateUniversities.getText().toString());
                }else
                    Toast.makeText(ManagerProfile.this, "You are not the manager!", Toast.LENGTH_LONG).show();
            }
        });
        //---------------------------------------------------BLOCK/UNBLOCK--------------------------
        //--------------------------------APPLICANT-------------------------------------------------
        blockApplicants = findViewById(R.id.btnBlockAppl);
        blockApplicants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                if (permission) {
                    blockORunblockApplicant = changeblockORunblock(blockORunblockApplicant); //change value
                    ref.child("Block_Dates").child("blockApplicants")
                            .setValue(blockORunblockApplicant); //set to changed value
                    blockApplicants.setText(blockORunblockApplicant);
                }else
                    Toast.makeText(ManagerProfile.this, "You are not the manager!", Toast.LENGTH_LONG).show();
            }
        });
        //--------------------------------UNIVERSITY------------------------------------------------
        blockUniversities = findViewById(R.id.btnBlockUniv);
        blockUniversities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                if (permission) {
                    blockORunblockUniversity = changeblockORunblock(blockORunblockUniversity); //change value
                    ref.child("Block_Dates").child("blockUniversities")
                            .setValue(blockORunblockUniversity); //set to changed value
                    blockUniversities.setText(blockORunblockUniversity);
                }else
                    Toast.makeText(ManagerProfile.this, "You are not the manager!", Toast.LENGTH_LONG).show();
            }
        });
        //-----------------------------------------------------RUN----------------------------------
        runAlgorithm = findViewById(R.id.btnRunAlgorithm);
        runAlgorithm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "blockApplicants: " + blockORunblockApplicant+" blockUniversities: "+ blockORunblockUniversity);
                if (blockORunblockApplicant.equals("BLOCK") && blockORunblockUniversity.equals("BLOCK")) { //if both blocked
                    Toast.makeText(ManagerProfile.this, "Start", Toast.LENGTH_LONG).show();
                    runAlgorithm.setEnabled(false); //run algorithm
                    runAlgorithm();
                }else{
                    Toast.makeText(ManagerProfile.this, "Applicants and universities should be blocked in order to run algorithm!!", Toast.LENGTH_LONG).show();
                }
            }
        });
        //-----------------------------------------------------EXIT---------------------------------
        exitButton = findViewById(R.id.btnManagerExit);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ManagerProfile.this, "Goodbye! Hope to see you back soon!", Toast.LENGTH_LONG).show();
                AuthenticatedUserHolder.instance.setAppUser(null);
                Intent intent = new Intent(ManagerProfile.this ,Home.class);
                startActivity(intent);
            }
        });
    }
    //gets relevant data from db
    protected void getDataManager(){
        DatabaseReference ref = mDatabase.child("Block_Dates");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                showData(dataSnapshot);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });
    }
    //show data from database
    protected void showData(DataSnapshot dataSnapshot) {
        String[] value = new String[4];
        int i=0;
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            //depending upon what data type you are using, cast to it.
            value[i] = snapshot.getValue(String.class);
            i++;
        }
        blockDate.setText(value[2]); //date to display at page (next calculation)
        blockDateUniversities.setText(value[3]); //blocking date universities
        blockDateApplicants.setText(value[2]); //blocking date applicants

        blockORunblockApplicant = value[0]; //block value applicant
        blockApplicants.setText(blockORunblockApplicant);
        blockORunblockUniversity = value[1]; //block value university
        blockUniversities.setText(blockORunblockUniversity);

        //display all the information
        Log.d(TAG, "showData: applicants blocked: " + value[0]);
        Log.d(TAG, "showData: universities blocked: " + value[1]);
        Log.d(TAG, "showData: date applicants: " + value[2]);
        Log.d(TAG, "showData: date Universities: " + value[3]);
    }
    //compare two dates
    protected boolean compareDate(String d1, String d2){
        try {
            Date date1=new SimpleDateFormat("dd/MM/yyyy").parse(d1);
            Date date2=new SimpleDateFormat("dd/MM/yyyy").parse(d2);
            return date1.after(date2);
        } catch (ParseException e) {
            Log.e(TAG, "compareDate "+e.toString());
        }
        return false;
    }

    //this function checks if user connected is the manager
    protected void checkPermission(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("Users").child("Manager").child("username")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            permission = Objects.requireNonNull(snapshot.getValue()).toString().equals(AdminUser.getName());
                        }catch (NullPointerException e){
                            Log.e(TAG, "Permission "+e.getMessage());
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e(TAG, "Permission "+databaseError.toString());
                    }
                });
    }

    //change value of blockORunblockApplicant
    protected String changeblockORunblock(String s){
        if (s.equals("BLOCK"))
            return "UNBLOCK";
         return "BLOCK";
    }

    //algorithm for perfect match
    protected void runAlgorithm() {
        //connect to firebase
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child("Applicants").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //insert data to applicant object
                        Applicant NewApp = null;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            NewApp = new Applicant(snapshot.child("password").getValue().toString(), snapshot.child("username").getValue().toString());
                            NewApp.setApplicantid(Integer.parseInt(snapshot.child("applicantId").getValue().toString()));
                            NewApp.setApplicantName(snapshot.child("applicantName").getValue().toString());
                            NewApp.updateAverage(Double.parseDouble(snapshot.child("average").getValue().toString()));
                            NewApp.updateFaculty(snapshot.child("faculty").getValue().toString());
                            NewApp.updateProjectGrade(Integer.parseInt(snapshot.child("projectGrade").getValue().toString()));
                            NewApp.setProjectNature(snapshot.child("projectNature").getValue().toString());
                            ap.add(NewApp);
                        }
                        //get applicant grades
                        getGrades();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d(TAG, "onCancelled "+databaseError.getMessage());
                    }
                }
        );
}

//get grades for applicant
protected void getGrades(){
    //connect to firebase
    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    db.child("ApplicantCourseGrades").addListenerForSingleValueEvent(
            new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        //add courses
                        ap.forEach(app -> {
                            if (String.valueOf(app.getApplicantid()).equals(snapshot.getKey())){
                                app.addCourseGrades(snapshot.child("First").child("Course").getValue().toString(),
                                        Integer.parseInt(snapshot.child("First").child("Grade").getValue().toString()));
                                app.addCourseGrades(snapshot.child("Second").child("Course").getValue().toString(),
                                        Integer.parseInt(snapshot.child("Second").child("Grade").getValue().toString()));
                                app.addCourseGrades(snapshot.child("Third").child("Course").getValue().toString(),
                                        Integer.parseInt(snapshot.child("Third").child("Grade").getValue().toString()));
                                app.addCourseGrades(snapshot.child("Fourth").child("Course").getValue().toString(),
                                        Integer.parseInt(snapshot.child("Fourth").child("Grade").getValue().toString()));
                                app.addCourseGrades(snapshot.child("Fifth").child("Course").getValue().toString(),
                                        Integer.parseInt(snapshot.child("Fifth").child("Grade").getValue().toString()));
                            }
                        });
                    }
                    getPreference();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d(TAG, "onCancelled "+databaseError.getMessage());
                }
            }
        );
    }
//the function getter list of preference of applicant
protected void getPreference(){
    //connect to firebase
    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    db.child("ApplicantPreferences").addListenerForSingleValueEvent(
            new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        //add preferences
                        ap.forEach(app -> {
                            if (String.valueOf(app.getApplicantid()).equals(snapshot.getKey())){
                                int i = 1;
                                HashMap<Integer, String> preferencerList = new HashMap<Integer, String>();
                                while (snapshot.child(String.valueOf(i)).exists()){
                                    preferencerList.put(i, snapshot.child(String.valueOf(i)).getValue().toString());
                                    i++;
                                }
                                app.updatePreferencerList(preferencerList);
                            }
                        });
                    }
                    getUniversity();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d(TAG, "onCancelled "+databaseError.getMessage());
                }
            }
    );
}
    //this function gets university data
    protected void getUniversity(){
        //connect to firebase
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child("AllDataFaculty").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //from all applicant list chooses by faculty
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            String department = snapshot.getKey();
                            ArrayList<Applicant> CalculateApplicant = new ArrayList<Applicant>();
                            ap.forEach(app -> {
                                if (app.getFaculty().equals(department)){
                                    CalculateApplicant.add(app);
                                }
                            });
                            //update all faculty data
                            final Map<String, Faculty> u = new HashMap<String,Faculty>();
                            for(DataSnapshot snapshot2 : dataSnapshot.child(department).getChildren()){
                                Faculty fac = new Faculty();
                                String name = snapshot2.getKey().toString();
                                fac.setFacultyName(department);
                                fac.updateMinAverage(Integer.parseInt(snapshot2.child("minAverage").getValue().toString()));
                                fac.updateNumOfApplicants(Integer.parseInt(snapshot2.child("numOfApplicants").getValue().toString()));
                                fac.updateProjectGrade(Integer.parseInt(snapshot2.child("projectGrade").getValue().toString()));
                                fac.updateProjectNature(snapshot2.child("projectNature").getValue().toString());
                                //----------------------------------1-------------------------------
                                fac.updateFormulaWeights(snapshot2.child("Faculty_Formula").child("First").child("courseName").getValue().toString(),
                                        Double.parseDouble(snapshot2.child("Faculty_Formula").child("First").child("weight").getValue().toString()));
                                fac.updateMinGrades(snapshot2.child("Faculty_Formula").child("First").child("courseName").getValue().toString(),
                                        Integer.parseInt(snapshot2.child("Faculty_Formula").child("First").child("minGrade").getValue().toString()));
                                //----------------------------------2-------------------------------
                                fac.updateFormulaWeights(snapshot2.child("Faculty_Formula").child("Second").child("courseName").getValue().toString(),
                                        Double.parseDouble(snapshot2.child("Faculty_Formula").child("Second").child("weight").getValue().toString()));
                                fac.updateMinGrades(snapshot2.child("Faculty_Formula").child("Second").child("courseName").getValue().toString(),
                                        Integer.parseInt(snapshot2.child("Faculty_Formula").child("Second").child("minGrade").getValue().toString()));
                                //----------------------------------3-------------------------------
                                fac.updateFormulaWeights(snapshot2.child("Faculty_Formula").child("Third").child("courseName").getValue().toString(),
                                        Double.parseDouble(snapshot2.child("Faculty_Formula").child("Third").child("weight").getValue().toString()));
                                fac.updateMinGrades(snapshot2.child("Faculty_Formula").child("Third").child("courseName").getValue().toString(),
                                        Integer.parseInt(snapshot2.child("Faculty_Formula").child("Third").child("minGrade").getValue().toString()));
                                //----------------------------------4-------------------------------
                                fac.updateFormulaWeights(snapshot2.child("Faculty_Formula").child("Fourth").child("courseName").getValue().toString(),
                                        Double.parseDouble(snapshot2.child("Faculty_Formula").child("Fourth").child("weight").getValue().toString()));
                                fac.updateMinGrades(snapshot2.child("Faculty_Formula").child("Fourth").child("courseName").getValue().toString(),
                                        Integer.parseInt(snapshot2.child("Faculty_Formula").child("Fourth").child("minGrade").getValue().toString()));
                                //----------------------------------5-------------------------------
                                fac.updateFormulaWeights(snapshot2.child("Faculty_Formula").child("Fifth").child("courseName").getValue().toString(),
                                        Double.parseDouble(snapshot2.child("Faculty_Formula").child("Fifth").child("weight").getValue().toString()));
                                fac.updateMinGrades(snapshot2.child("Faculty_Formula").child("Fifth").child("courseName").getValue().toString(),
                                        Integer.parseInt(snapshot2.child("Faculty_Formula").child("Fifth").child("minGrade").getValue().toString()));
                                //------------------------------------------------------------------------------------------------------
                                u.put(name, fac);
                            }
                            Algorithm a = new Algorithm(); //new algorithm for calculation
                            Map<String, List<Applicant>> resultList = a.calculateAlgorithm(CalculateApplicant, u); //result list for algorithm needs
                            for (Map.Entry<String, List<Applicant>> entry : resultList.entrySet()) {
                                Log.d(TAG,entry.getKey()+" "+entry.getValue().toString());
                            }
                        }
                        Toast.makeText(ManagerProfile.this, "Finish", Toast.LENGTH_LONG).show();
                        //run algorithm
                        runAlgorithm.setEnabled(true);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d(TAG, "onCancelled "+databaseError.getMessage());
                    }
                }
        );
    }
}