package com.example.moodle_cam;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    private static final int READ_REQUEST_CODE = 2;
    private static final int TAKE_PICTURE = 3;
    private Uri imageUri;



    private List<Student> theStudent = new ArrayList<Student>();
    private String stringUri;
    private String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //populateStudentList();
        populateListView();
        registerClickCallback();


    }


    // ++++++++++++++++++++++++++++++++++++++++++[ Button-Controller ]++++++++++++++++++++++++++++
    private void registerClickCallback() {
        ListView list = (ListView) findViewById(R.id.listStudents);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
                Student ClickedStudent = theStudent.get(position);
                String message = "Gewählt wurde Schüler: " + ClickedStudent.getName();
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show(); //Toast ist ein Popup von Unten... ;D

                dispatchTakePictureIntent();
                galleryAddPic();

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

                // open File-Browser
                performFileSearch();

            }
        });

    }
    // ++++++++++++++++++++++++++++++++++++++++++[ CSV extractor ]++++++++++++++++++++++++++++

    private void readStudentData() {
        String line = "";
        int counter = 0;

        try {
            InputStream is = openFileInput("Student.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));


            while ( (line = reader.readLine()) != null ){
                if(counter !=0 ) {
                    //split by "*tab*"
                    String[] tokens = line.split("\t");

                    //read the data
                    Student sample = new Student();
                    sample.setName(tokens[0]);
                    sample.setIconID(tokens[10]);
                    theStudent.add(sample);

                    Log.d("MyActivity", "Just created" + sample);
                }
                counter++; //skip the first Line with headers
            }
        }catch (IOException ex){
            ex.printStackTrace();
            Log.wtf("MyActivity","Error reading data file on line "+ line , ex);
        }


    }

    // ++++++++++++++++++++++++++++++++++++++++++[ List populator ]++++++++++++++++++++++++++++

   // private void populateStudentList() {    //old method to populate dummys!!!!
    //     theStudent.add(new Student("Remig",R.drawable.pb0));
    //     theStudent.add(new Student("Josh", R.drawable.pb1));
    //     theStudent.add(new Student("Domenic", R.drawable.pb2));
    //      theStudent.add(new Student("Jonas", R.drawable.pb3));
    //      theStudent.add(new Student("Such", R.drawable.pb4));
    //   }

    private void populateListView() {
        ArrayAdapter<Student> adapter = new MyListAdapter();
        ListView list = (ListView) findViewById(R.id.listStudents);
        list.setAdapter(adapter);
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

            //fill the Preview  NOTE: Add in camera when pictures are possible
            //ImageView imageView = (ImageView) itemView.findViewById(R.id.previewpicture);
            //imageView.setImageResource(Integer.parseInt(currentStudent.getIconID()));


            //Firstname
            TextView txtFirstName = (TextView) itemView.findViewById(R.id.txtFirstname);
            txtFirstName.setText(currentStudent.getName());


            return itemView;
        }
    }
    // ++++++++++++++++++++++++++++++++++++++++++[ CSV Opener ]++++++++++++++++++++++++++++


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



    }
// ++++++++++++++++++++++++++++++++++++++++++[ Camera ]++++++++++++++++++++++++++++

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.wtf("MainActivity","Error occurred while creating the File");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.moodle_cam.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);

            }
        }
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String studentID = "87231";
        String imageFileName = studentID+timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        Log.w("createImageFile","Path: " +currentPhotoPath);

        return image;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        switch(requestCode){
            case 2: //open the File-Browser

                if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

                    if (resultData != null) {
                        try {
                              String destination = getFilesDir().getPath();
                              InputStream src = getContentResolver().openInputStream(resultData.getData()); // use the uri to create an inputStream

                             try {

                                      convertInputStreamToFile(src, destination);

                                        //get the values from the csv for the list Objects after copy
                                         readStudentData();
                                          populateListView();


                             } catch (IOException e)    {
                                      e.printStackTrace();
                             }
                        } catch (FileNotFoundException ex) {
                        }
                             String destination = getFilesDir().getPath();
                            Toast.makeText(MainActivity.this, "Success!: CSV-File copyed to : " +destination  , Toast.LENGTH_SHORT).show();
                     }
                 }
                break;//first case

            case 1:
                if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {

                    galleryAddPic();
                }
        }

    }
    public static void convertInputStreamToFile(InputStream is, String destination) throws IOException
    {
        OutputStream outputStream = null;
        try
        {
            File file = new File(destination + "/Student.csv");
            outputStream = new FileOutputStream(file);

            int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = is.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        }
        finally
        {
            if(outputStream != null)
            {
                outputStream.close();
            }
        }

    }

}
