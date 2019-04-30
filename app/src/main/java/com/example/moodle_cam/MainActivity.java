package com.example.moodle_cam;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

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
            }
        }); //ja, das muss so.

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

}
