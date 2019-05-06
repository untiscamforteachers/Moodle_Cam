package com.example.moodle_cam;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int READ_REQUEST_CODE = 42;
    private static final int CREATE_REQUEST_CODE = 41;
    private static final int OPEN_REQUEST_CODE = 40;
    private static final int SAVE_REQUEST_CODE = 43;

    private List<Student> theStudent = new ArrayList<Student>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        populateStudentList();
        populateListView();
        registerClickCallback();


    }


    private void populateListView() {
        ArrayAdapter<Student> adapter = new MyListAdapter();
        ListView list = (ListView) findViewById(R.id.listStudents);
        list.setAdapter(adapter);
    }
    private void registerClickCallback() {
        ListView list = (ListView) findViewById(R.id.listStudents);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
                Student ClickedStudent = theStudent.get(position);
                String message = "Gewählt wurde Schüler: " + ClickedStudent.getLast_name();
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show(); //Toast ist ein Popup von Unten... ;D
                startCamera();

            }

            private void startCamera() {
                // TODO
            }

        }); //ja, das muss so.

        // Zip -Button
        FloatingActionButton zip = findViewById(R.id.saveZip);
        zip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ZipSaveDirectory = "/bin/bilder/00124";
                String Zipmessage = "Die Bilder wurden in: " + ZipSaveDirectory +".zip gespeichert";
                Toast.makeText(MainActivity.this, Zipmessage, Toast.LENGTH_LONG).show();
            }
        });

        // CSV -Button
        Button csv = (Button) findViewById(R.id.btn_csv);
        csv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String CSVmessage = "Wählen Sie den Pfad der CSV";
                Toast.makeText(MainActivity.this, CSVmessage, Toast.LENGTH_SHORT).show();

                // öffne File-Browser
                performFileSearch();

            }
        });

    }

    private class  MyListAdapter extends ArrayAdapter<Student> {
        public MyListAdapter() {
            super(MainActivity.this, R.layout.listofstudents, theStudent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if(itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.listofstudents, parent, false);
            }
            //find the Student to work with
            Student currentStudent = theStudent.get(position);

            //fill the Preview
            ImageView imageView = (ImageView) itemView.findViewById(R.id.previewpicture);
            imageView.setImageResource(currentStudent.getIconID());

            //Firstname
            TextView txtFirstName = (TextView) itemView.findViewById(R.id.txtFirstname);
            txtFirstName.setText(currentStudent.getName());

            //Lastname
            TextView txtLastName = (TextView) itemView.findViewById(R.id.txtLastname);
            txtLastName.setText(currentStudent.getLast_name());

            return itemView;
        }
    }

    private void populateStudentList() {    //hier später CSV inplementieren!!!!
        theStudent.add(new Student("Remig","Okla", R.drawable.pb0));
        theStudent.add(new Student("Josh","Stock", R.drawable.pb1));
        theStudent.add(new Student("Domenic","Urank(oder so)", R.drawable.pb2));
        theStudent.add(new Student("Jonas","Starmack", R.drawable.pb3));
        theStudent.add(new Student("Such","Doge", R.drawable.pb4));
    }

    //CSV Opener
    public void performFileSearch() {

        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Filter to only show results that can be "opened"
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only  .csv  using the image MIME data type.
        // For all it would be "*/*".
        intent.setType("text/comma-separated-values");

        startActivityForResult(intent, READ_REQUEST_CODE);
        onActivityResult(READ_REQUEST_CODE,READ_REQUEST_CODE);

        @Override
        public void onActivityResult(int requestCode, int resultCode,
        Intent resultData) {

            if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK)
            {

                if (resultData != null) {
                        Uri path = resultData.getData();

                        Toast.makeText(MainActivity.this, path , Toast.LENGTH_SHORT).show();

                }


            }
        }

    }
    ﻿﻿﻿
}
