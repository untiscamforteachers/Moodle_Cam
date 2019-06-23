package com.example.moodle_cam;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class MainActivity extends AppCompatActivity {

    static final int REQUEST_TAKE_PHOTO = 1;
    private static final int READ_REQUEST_CODE = 2;




    private  List<Student> theStudent = new ArrayList<Student>();
    private  String currentPhotoPath;
    public static MainActivity instance;
    private boolean hasPicture = false;
    private String nextStudentInStack;
    private boolean csvExists = false;
    private String currentClassName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
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
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show(); //Toast ist ein Popup von Unten... ;D

                dispatchTakePictureIntent(ClickedStudent);
                populateListView(); //update Pictures

            }
        }); //ja, das muss so.

        // Zip -Button
        FloatingActionButton zip = findViewById(R.id.saveZip);
        zip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Zipmessage = "Die Bilder wurden als .zip gespeichert";
                Toast.makeText(MainActivity.this, Zipmessage, Toast.LENGTH_SHORT).show();
                try {
                    File copyDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                    File storageDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
                    File newZip = File.createTempFile(
                            "StudentPictures",  /* prefix */
                            ".zip",         /* suffix */
                            storageDir      /* directory */
                    );
                    ZipUtils.zipFolder(copyDir,newZip);
                    Log.w("MyActivity","Zip in /Documents is created - Starting deleting Pictures");
                    //After all pictures are saved delete all
                    deleteRecursive(copyDir);
                    ArrayAdapter<Student> adapter = new MyListAdapter();
                    ListView list = (ListView) findViewById(R.id.listStudents);
                    list.setAdapter(adapter);
                    adapter.clear();
                    Log.w("MyActivity","Deleting complete");
                    csvExists = false;

                }catch (IOException ex){
                    ex.printStackTrace();
                    Log.wtf("MyActivity","Error by creating the ZIP ", ex);
                }

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



        // Whole Class Button
        nextStudentInStack = null;

        Button stack = (Button) findViewById(R.id.btn_stack);
        stack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (csvExists) {
                    FragmentManager manager = getSupportFragmentManager();
                    MessageFragment dialog = new MessageFragment();
                    dialog.show(manager, "StackDialog");
                    Log.i("TAG", "just showed dialog");
                }

            }


        });

        populateListView(); //update Pictures
    }

    public  void stack(){
        for (int i = 0; i < theStudent.size(); ) {
            if(!hasPicture) {
                    dispatchTakePictureIntent(theStudent.get(i));

                    nextStudentInStack = (i+1)>= theStudent.size()? "Ganze Klasse fertig": theStudent.get(i+1).getName();
                    hasPicture = true;
                    i++;
                    Log.i("TAG", "Just created a Picture in Stack Funktion");
            } else {
                try {
                    Log.i("TAG","Sleeping");
                    Thread.currentThread().sleep(4000);
                    Log.i("TAG","woke up");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
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
                    if(findFoto(tokens[10]) == true) {
                        sample.setExists(true);
                    } else {
                        sample.setExists(false);
                    }
                    theStudent.add(sample);

                    Log.d("MyActivity", "Just created" + sample);

                    if(counter == 1){
                        currentClassName = tokens[5]; //get the classname once for the ZipName.
                    }
                }
                counter++; //skip the first Line with headers
            }

        }catch (IOException ex){
            ex.printStackTrace();
            Log.wtf("MyActivity","Error reading data file on line "+ line , ex);
        }


    }

    private boolean findFoto(String id) {
        File image = new File (getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString() +"/"+id+".jpg");
        Boolean ret = false;
        if (image.exists()) { //test if exists
            ret = true;
            if(image.length() == 0) {
               image.delete();
                ret = false;
            }
        }
        image = null;
        return ret;
    }

    // ++++++++++++++++++++++++++++++++++++++++++[ List populator ]++++++++++++++++++++++++++++


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

            reduceFileSize(new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString() +"/"+currentStudent.getIconID()+".jpg"));

            //fill the Preview  NOTE: Add in camera when pictures are possible
            //ImageView imageView = (ImageView) itemView.findViewById(R.id.previewpicture);
            //imageView.setImageResource(Integer.parseInt(currentStudent.getIconID()));
            String dir =  getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString() +"/"+currentStudent.getIconID()+".jpg";
            if(currentStudent.getExists()== true) {
                File setImage = new File(dir);
                if(setImage.length()!=0) {
                    ImageView imageView = (ImageView) itemView.findViewById(R.id.previewpicture);
                    imageView.setImageURI(Uri.fromFile(setImage));
                }else{setImage.delete();
                    ImageView imageView = (ImageView) itemView.findViewById(R.id.previewpicture);
                    imageView.setImageResource(R.drawable.def_picture);
                }
            }else {
                ImageView imageView = (ImageView) itemView.findViewById(R.id.previewpicture);
                imageView.setImageResource(R.drawable.def_picture);
            }


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

// ++++++++++++++++++++++++++++++++++++++++++[ Zip-Packer ]++++++++++++++++++++++++++++
public static final class ZipUtils {

    public static void zipFolder(final File folder, final File zipFile) throws IOException {
        zipFolder(folder, new FileOutputStream(zipFile));
    }

    public static void zipFolder(final File folder, final OutputStream outputStream) throws IOException {
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)) {
            processFolder(folder, zipOutputStream, folder.getPath().length() + 1);
        }
    }

    private static void processFolder(final File folder, final ZipOutputStream zipOutputStream, final int prefixLength)
            throws IOException {
        for (final File file : folder.listFiles()) {
            if (file.isFile()) {
                final ZipEntry zipEntry = new ZipEntry(file.getPath().substring(prefixLength));
                zipOutputStream.putNextEntry(zipEntry);
                try (FileInputStream inputStream = new FileInputStream(file)) {
                    byte [] buffer = new byte[1024 * 4];
                    int read = 0;
                    while ((read = inputStream.read(buffer)) != -1) {
                        zipOutputStream.write(buffer, 0, read); }
                }
                zipOutputStream.closeEntry();
            } else if (file.isDirectory()) {
                processFolder(file, zipOutputStream, prefixLength);
            }
        }
    }
}

// ++++++++++++++++++++++++++++++++++++++++++[ Camera ]++++++++++++++++++++++++++++

    private void  dispatchTakePictureIntent(Student clickedStudent) {
        String fileID = clickedStudent.getIconID();
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile(fileID);
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

                String msg = "Foto von Schüler: "+clickedStudent.getName();
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
                clickedStudent.setExists(true); //mark as Student with Photo

            }
        }
    }



    private File createImageFile(String fileID) throws IOException {
        // Create an image file name
        // Unnessery: String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = fileID;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);


        File image = new File (storageDir.toString() +"/"+imageFileName+".jpg");
        if(!image.exists()){
            try {
                image.createNewFile();
            }catch(IOException e){
                e.printStackTrace();
            }
        }

        String name = image.getName();

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        Log.w("createImageFile",name +" Path: " +currentPhotoPath);

        return image;
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
                                        //clear Listview to prewent double isertations
                                        ArrayAdapter<Student> adapter = new MyListAdapter();
                                         ListView list = (ListView) findViewById(R.id.listStudents);
                                         list.setAdapter(adapter);
                                         adapter.clear();
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
                        csvExists = true;
                     }
                 }
                break;//first case

            case 1:
                if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
                    hasPicture = false;
                    toaster();
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
    void deleteRecursive(File fileOrDirectory) {

        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();

    }

    // ++++++++++++++++++++++++++++++++++++++++++[ Toaster ]++++++++++++++++++++++++++++
    public void toaster(String name) {
        Toast.makeText(MainActivity.this, "Foto von Schüler : " +name , Toast.LENGTH_LONG).show();
        try {
            Thread.currentThread().sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void toaster() {
        if (nextStudentInStack == null) {
            nextStudentInStack = theStudent.get(0).getName();
        }
        toaster(nextStudentInStack);
    }

    public void setNextStudentInStack(String newName) {
        nextStudentInStack = newName;
    }

    // ++++++++++++++++++++++++++++++++++++++++++[ Compresser ]++++++++++++++++++++++++++++

    public File reduceFileSize(File file){

        try {

            // BitmapFactory options to downsize the image
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 6;
            // factor of downsizing the image

            FileInputStream inputStream = new FileInputStream(file);
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();

            // The new size we want to scale to
            final int REQUIRED_SIZE=75;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while(o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();

            // here i override the original image file
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);

            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 65 , outputStream);

            return file;
        } catch (Exception e) {
            return null;
        }
    }

}
