package com.sanda.wtf.pefectmatch;
/*
 * The final assignment of Software Engineering
 * @author Alexey Titov and Shir Bentabou
 * @date 12.2018
 * @version 3.0
 */
//libraries
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import WorkingClasses.Applicant;

public class GeneralApplicantInfo extends AppCompatActivity {
    //variables
    private TextView show_username; //tvUserName
    private EditText su_average; //etAverage
    private EditText su_projectGrade;//etProjectGrade
    private Button next_step; //btnNextStep
    //switch
    private Switch su_projectNature; //switchProjectNature
    //spinner
    private Spinner su_faculty; //spinnerFacultyWanted
    private String switch_result="Research";
    private Applicant current;
    private static final String TAG = "GeneralApplicantInfo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_applicantinfo);
        //receive Applicant object from previous intent
        Intent i = getIntent();
        current = (Applicant)i.getSerializableExtra("Applicant"); //get Applicant object from previous intent
        Log.d(TAG, "UserName "+current.getName());
        Log.d(TAG, "ID "+current.getApplicantid());
        Log.d(TAG, "Password "+current.getPassword());
        Log.d(TAG, "Name "+current.getApplicantName());

        //match variables to fields in activity
        show_username = findViewById(R.id.tvUserName);
        show_username.setText(current.getName());
        su_average = findViewById(R.id.etAverage);
        su_projectGrade = findViewById(R.id.etProjectGrade);
        //------------------------------------------------SWITCH------------------------------------
        su_projectNature = findViewById(R.id.switchProjectNature);
        su_projectNature.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) { //show applicant message regarding choice
                    switch_result="Non-Research";
                    Toast.makeText(GeneralApplicantInfo.this, "You have chosen Non-Research", Toast.LENGTH_LONG).show();
                } else {
                    switch_result="Research";
                    Toast.makeText(GeneralApplicantInfo.this, "You have chosen Research", Toast.LENGTH_LONG).show();
                }
            }
        });
        //------------------------------------------------SPINNER-----------------------------------
        su_faculty = findViewById(R.id.spinnerFacultyWanted);
        String[] arraySpinner = new String[] { //spinner values
                "Foreign Language", "Computer Science"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); //set values in spinner
        su_faculty.setAdapter(adapter);
        //------------------------------------------------BUTTON------------------------------------
        next_step = findViewById(R.id.btnNextStep);
        next_step.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(GeneralApplicantInfo.this, "Advancing to next step!", Toast.LENGTH_LONG).show();
                double su_av = -1; //average
                int su_pg = -1; //project grade
                try{ //parse data
                    su_av = Double.parseDouble(su_average.getText().toString());
                    su_pg = Integer.parseInt(su_projectGrade.getText().toString());
                }catch(NumberFormatException e){
                    Log.d(TAG, "Not inserted! Error: "+e.getMessage());
                }
                String su_fac = su_faculty.getSelectedItem().toString();

                //check if all data was inserted check legal values for text fields
                if (su_av <= 0 || su_av > 100 || su_pg <= 0 || su_pg > 100) {
                    Toast.makeText(GeneralApplicantInfo.this, "You have to insert correct data to all fields!", Toast.LENGTH_LONG).show();
                } else {
                    //insert data to applicant object to pass on to next intent
                    current.setProjectNature(switch_result);
                    current.updateProjectGrade(su_pg);
                    current.updateAverage(su_av);
                    current.updateFaculty(su_fac);
                Intent intent = new Intent(GeneralApplicantInfo.this ,CoursesApplicantInfo.class);
                //To pass:
                intent.putExtra("Applicant", current);
                startActivity(intent);
                }
            }
        });
    }
}