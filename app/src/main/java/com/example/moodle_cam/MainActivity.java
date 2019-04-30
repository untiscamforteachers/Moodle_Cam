package com.example.moodle_cam;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        populateListView();
        registerClickCallback();
    }


    private void populateListView() {
        //create a list of items
        String[] myItems = {"P. Hirzler","M. Nesan","J. Stock"}; //hier später CSV inplementieren

        //build adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                R.layout.listofstudents,//Layout to use
                myItems); //Items to be displayed

        //configure list view
        ListView list = (ListView) findViewById(R.id.listStudents);
        list.setAdapter(adapter);
    }
    private void registerClickCallback() {
        ListView list = (ListView) findViewById(R.id.listStudents);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
                TextView textView = (TextView) viewClicked;
                String message = "Gewählt wurde Schüler " + (position+1) +", Name: " + textView.getText().toString();
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();

            }
        });
    }

}
