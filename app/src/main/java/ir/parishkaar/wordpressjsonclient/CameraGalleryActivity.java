package ir.parishkaar.wordpressjsonclient;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ir.parishkaar.wordpressjsonclient.helper.SQLiteHandler;
import ir.parishkaar.wordpressjsonclient.helper.SessionManager;


public class CameraGalleryActivity extends AppCompatActivity implements LocationProvider.LocationCallback
{

    //Define a request code to send to Google Play services
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
     int currentLatitude = 0;
     int currentLongitude = 0;

    Button uploadBtn, btnToUpload;

    ImageView imageviewStem, imageviewLeaf2, imageviewRoot, imageviewOther;

    String stemimageFilePath, leafimageFilePath, rootimageFilePath, otherimageFilePath,
            personName, personPhone, personEmail, personQuery, personCrop,
            personLocationLat, personLocationLong;

    Bitmap stemtakenImage, leaftakenImage, roottakenImage, othertakenImage, takenImage;
    String myurl = "http://salesapp.matenek.com/salesmatenek/navtests/photocsav3testing.php";

    File photoFile;
    Spinner spinner2;
    LocationManager locationManager;

    public EditText personLocationLatInput;
    public EditText personLocationLongInput;

    private LocationProvider mLocationProvider;



    private static final int PERMISSION_REQUEST_CODE = 1;

    private SQLiteHandler db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_gallery);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final RelativeLayout formRelativeLayout = (RelativeLayout) findViewById(R.id.formlayout);
        final RelativeLayout imgGridLayout = (RelativeLayout) findViewById(R.id.imageGridLayout);
        final RelativeLayout form2RelativeLayout = (RelativeLayout) findViewById(R.id.form2layout);

        final EditText personNameInput = (EditText) findViewById(R.id.personName);
        final EditText personPhoneInput = (EditText) findViewById(R.id.personPhone);
        final EditText personEmailInput = (EditText) findViewById(R.id.personEmail);
        final EditText personQueryInput = (EditText) findViewById(R.id.personMessage);
        personLocationLatInput = (EditText) findViewById(R.id.editLat);
        personLocationLongInput = (EditText) findViewById(R.id.editLong);

        mLocationProvider = new LocationProvider(this, this);


        spinner2 = (Spinner) findViewById(R.id.personCropSpinner);

        addItemsOnSpinner2();

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                personCrop = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();

        String name = user.get("name");
        String email = user.get("email");

        // Displaying the user details on the screen
        personNameInput.setText(name);
        personEmailInput.setText(email);


//        personCrop = String.valueOf(spinner2.getSelectedItem());
//        personCrop = spinner2.getSelectedItem().toString().trim();

        final Button btnToVisibility2frm1 = (Button) findViewById(R.id.btnToFrag2);
        final Button btnToVisibility1frm2 = (Button) findViewById(R.id.btntoForm);
        final Button btnToVisibility2frm3 = (Button) findViewById(R.id.btnToFrag2frm3);
        final Button btnToVisibility3frm2 = (Button) findViewById(R.id.btnToFrag3);

//        pbar = (ProgressBar) view.findViewById(R.id.progressBar1);


        imageviewStem = (ImageView) findViewById(R.id.imgStem);
        imageviewLeaf2 = (ImageView) findViewById(R.id.imgLeaf2);
        imageviewRoot = (ImageView) findViewById(R.id.imgRoot);
        imageviewOther = (ImageView) findViewById(R.id.imgOther);

        btnToVisibility2frm1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                viewPager.setCurrentItem(1);

                personName = personNameInput.getText().toString();
                personPhone = personPhoneInput.getText().toString();
                personEmail = personEmailInput.getText().toString();

                if ((TextUtils.isEmpty(personName)) || (TextUtils.isEmpty(personPhone))) {
                    Toast.makeText(CameraGalleryActivity.this, "Name and Phone are mandatory", Toast.LENGTH_SHORT).show();
                    if (TextUtils.isEmpty(personName)) {
                        personNameInput.requestFocus();
                        personNameInput.setHint("please enter name");//it gives user to hint
                        personNameInput.setError("please enter name");//it gives user to info message //use any one //
                    }
                    if (TextUtils.isEmpty(personPhone)) {
                        personPhoneInput.requestFocus();
                        personPhoneInput.setHint("please enter Phone");//it gives user to hint
                        personPhoneInput.setError("please enter Phone");//it gives user to info message //use any one //
                    }

                } else {
//                        personNameInput.setHint("please enter name");//it gives user to hint
//                        personNameInput.setError("please enter name");//it gives user to info message //use any one //
                    imgGridLayout.setVisibility(View.VISIBLE);
                    formRelativeLayout.setVisibility(View.INVISIBLE);
                    form2RelativeLayout.setVisibility(View.INVISIBLE);

                }

            }
        });

        btnToVisibility1frm2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                viewPager.setCurrentItem(1);

                imgGridLayout.setVisibility(View.INVISIBLE);
                formRelativeLayout.setVisibility(View.VISIBLE);
                form2RelativeLayout.setVisibility(View.INVISIBLE);


            }
        });

        btnToVisibility3frm2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                viewPager.setCurrentItem(1);

                imgGridLayout.setVisibility(View.INVISIBLE);
                formRelativeLayout.setVisibility(View.INVISIBLE);
                form2RelativeLayout.setVisibility(View.VISIBLE);
            }
        });

        btnToVisibility2frm3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                viewPager.setCurrentItem(1);

                imgGridLayout.setVisibility(View.VISIBLE);
                formRelativeLayout.setVisibility(View.INVISIBLE);
                form2RelativeLayout.setVisibility(View.INVISIBLE);
            }
        });

        imageviewLeaf2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showFileChooser(CameraGalleryActivity.this, "leaf");
            }
        });

        imageviewStem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showFileChooser(CameraGalleryActivity.this, "stem");
            }
        });


        imageviewRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showFileChooser(CameraGalleryActivity.this, "root");
            }
        });
        imageviewOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showFileChooser(CameraGalleryActivity.this, "other");
            }
        });

        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);

        }


        btnToUpload = (Button) findViewById(R.id.btnToUpload);
        btnToUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                personQuery = personQueryInput.getText().toString();


                try {
                    InputMethodManager imm = (InputMethodManager) CameraGalleryActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(CameraGalleryActivity.this.getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    // TODO: handle exception
                }
                uploaduserimage();


            }
        });

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkPermission()) {
                // Code for above or equal 23 API Oriented Device
                // Your Permission granted already .Do next code
            } else {
                requestPermission();
            }
        }


    }
//End of Create

    @Override
    protected void onResume() {
        super.onResume();
        mLocationProvider.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLocationProvider.disconnect();
    }
    /**
     * Location functions
     * from crazyprogrammer
     * https://stackoverflow.com/questions/17519198/how-to-get-the-current-location-latitude-and-longitude-in-android
     */
    public void handleNewLocation(Location location) {
//        Log.d(TAG, location.toString());

        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();

        personLocationLatInput.setText(String.valueOf(currentLatitude));
        personLocationLongInput.setText(String.valueOf(currentLongitude));

        personLocationLat = String.valueOf(currentLatitude);
        personLocationLong = String.valueOf(currentLongitude);


    }


    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     * */
    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(CameraGalleryActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    // add items into spinner dynamically
    public void addItemsOnSpinner2() {

        List<String> list = new ArrayList<String>();
        list.add("Bajra (Pearl Millet)");
        list.add("Beans");
        list.add("Bhendi (Okra)");
        list.add("Brinjal");
        list.add("Cabbage");
        list.add("Carrot");
        list.add("Cauliflower");
        list.add("Chickpea (Bengal Gram)");
        list.add("Chillies");
        list.add("Coriander");
        list.add("Cotton");
        list.add("Cowpea");
        list.add("Fenugreek");
        list.add("Groundnut");
        list.add("Maize");
        list.add("Red Gram");
        list.add("Rice");
        list.add("Sorghum");
        list.add("Soybean");
        list.add("Sugar Cane");
        list.add("Sunflower");
        list.add("Tomato");
        list.add("Wheat");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(CameraGalleryActivity.this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(dataAdapter);
    }


    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(CameraGalleryActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(CameraGalleryActivity.this, " Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(CameraGalleryActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(CameraGalleryActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void showFileChooser (final Context context, final String plantpart) {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose an option");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {
                    if (plantpart.equals("leaf")) {
                        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if(takePicture.resolveActivity(getPackageManager()) != null) {

                            photoFile = null;
                            try {
                                photoFile = createImageFile("leaf");
                            }
                            catch (IOException e) {
                                e.printStackTrace();
                                return;
                            }

                            if (photoFile != null) {
                                Uri leafphotoUri = FileProvider.getUriForFile(CameraGalleryActivity.this, getPackageName() +".provider", photoFile);
                                takePicture.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, leafphotoUri);
                                startActivityForResult(takePicture, 11);
                            }
                            photoFile = null;
                        }
                    }

                    if (plantpart.equals("stem")) {
                        Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        if(takePicture.resolveActivity(getApplication().getPackageManager()) != null) {

                            photoFile = null;
                            try {
                                photoFile = createImageFile("stem");
                            }
                            catch (IOException e) {
                                e.printStackTrace();
                                return;
                            }
                            Uri leafphotoUri = FileProvider.getUriForFile(context, CameraGalleryActivity.this.getPackageName() +".provider", photoFile);
//                                    (, getApplication().getPackageName() +".provider", photoFile);
                            photoFile = null;

                            takePicture.putExtra(MediaStore.EXTRA_OUTPUT, leafphotoUri);
//                            startActivityForResult(pictureIntent, REQUEST_IMAGE_CAPTURE);
                        }
                        startActivityForResult(takePicture, 12);
                    }
                    if (plantpart.equals("root")) {
                        Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        if(takePicture.resolveActivity(getApplication().getPackageManager()) != null) {

                            photoFile = null;
                            try {
                                photoFile = createImageFile("root");
                            }
                            catch (IOException e) {
                                e.printStackTrace();
                                return;
                            }
                            Uri leafphotoUri = FileProvider.getUriForFile(context, CameraGalleryActivity.this.getPackageName() +".provider", photoFile);
//                                    (, getApplication().getPackageName() +".provider", photoFile);
                            photoFile = null;

                            takePicture.putExtra(MediaStore.EXTRA_OUTPUT, leafphotoUri);
//                            startActivityForResult(pictureIntent, REQUEST_IMAGE_CAPTURE);
                        }
                        startActivityForResult(takePicture, 13);
                    }
                    if (plantpart.equals("other")) {
                        Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        if(takePicture.resolveActivity(getPackageManager()) != null) {

                            photoFile = null;
                            try {
                                photoFile = createImageFile("other");
                            }
                            catch (IOException e) {
                                e.printStackTrace();
                                return;
                            }
                            Uri leafphotoUri = FileProvider.getUriForFile(CameraGalleryActivity.this, getPackageName() +".provider", photoFile);
//                                    (, getApplication().getPackageName() +".provider", photoFile);
                            photoFile = null;

                            takePicture.putExtra(MediaStore.EXTRA_OUTPUT, leafphotoUri);
//                            startActivityForResult(pictureIntent, REQUEST_IMAGE_CAPTURE);                        startActivityForResult(takePicture, 14);
                            startActivityForResult(takePicture, 14);

                        }
                    }


                } else if (options[item].equals("Choose from Gallery")) {
                    if (plantpart.equals("leaf")){
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhoto , 21);
                    }
                    if (plantpart.equals("stem")){
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhoto , 22);
                    }
                    if (plantpart.equals("root")){
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhoto , 23);
                    }
                    if (plantpart.equals("other")){
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhoto , 24);
                    }

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private File createImageFile(String plantPart) throws IOException {

        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = plantPart + "_IMG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        if (plantPart == "leaf") {
            leafimageFilePath = image.getAbsolutePath();
            stemimageFilePath = null;
            rootimageFilePath = null;
            otherimageFilePath = null;

        }

        if (plantPart == "stem") {
            stemimageFilePath = image.getAbsolutePath();
            leafimageFilePath = null;
            rootimageFilePath = null;
            otherimageFilePath = null;

        }

        if (plantPart == "root") {
            rootimageFilePath = image.getAbsolutePath();
            leafimageFilePath = null;
            stemimageFilePath = null;
            otherimageFilePath = null;

        }

        if (plantPart == "other") {
            otherimageFilePath = image.getAbsolutePath();
            leafimageFilePath = null;
            stemimageFilePath = null;
            rootimageFilePath = null;
        }
//        stemimageFilePath = image.getAbsolutePath();


//        if (plantPart == "btnLeaf") {
//            leafimageFilePath = image.getAbsolutePath();
//        }
//        imageFilePath = image.getAbsolutePath();
        return image;
    }

    public Bitmap bitmapResizer(String imgFilePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        Bitmap takenImage = BitmapFactory.decodeFile(imgFilePath, options);
        // original measurements
        int origWidthL = takenImage.getWidth();
        int origHeightL = takenImage.getHeight();

        final int destWidthL = 600;//or the width you need

        Bitmap takenImageW = null;

        if(origWidthL > destWidthL) {
            // picture is wider than we want it, we calculate its target height
            int destHeightL = origHeightL / (origWidthL / destWidthL);
            // we create an scaled bitmap so it reduces the image, not just trim it
            takenImageW = Bitmap.createScaledBitmap(takenImageW, destWidthL, destHeightL, false);
        } else {
            takenImageW = takenImage;
        }
        return (takenImageW);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 11:
                    if (resultCode == RESULT_OK) {

                        if (!(leafimageFilePath == null)) {

                            imageviewLeaf2.setImageURI(Uri.parse(leafimageFilePath));
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inSampleSize = 2;
                            leaftakenImage = BitmapFactory.decodeFile(leafimageFilePath,options);
                            // original measurements
                            int origWidthL = leaftakenImage.getWidth();
                            int origHeightL = leaftakenImage.getHeight();

                            final int destWidthL = 600;//or the width you need

                            if(origWidthL > destWidthL) {
                                // picture is wider than we want it, we calculate its target height
                                int destHeightL = origHeightL / (origWidthL / destWidthL);
                                // we create an scaled bitmap so it reduces the image, not just trim it
                                Bitmap leaftakenImageW = Bitmap.createScaledBitmap(leaftakenImage, destWidthL, destHeightL, false);

                                leaftakenImage = leaftakenImageW;
                            }

                            photoFile = null;
                        }
                        leafimageFilePath = null;

                    }

                    break;
                case 21:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage =  data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null) {
                            Cursor cursor = getContentResolver().query(selectedImage,
                                    filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();

                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String picturePath = cursor.getString(columnIndex);
                                imageviewLeaf2.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                                cursor.close();
                                leaftakenImage = bitmapResizer(picturePath);

                            }

                        }

                    }
                    break;
                case 12:
                    if (!(stemimageFilePath == null)) {

                        imageviewStem.setImageURI(Uri.parse(stemimageFilePath));
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 4;

                        stemtakenImage = BitmapFactory.decodeFile(stemimageFilePath, options);
                        // original measurements
                        int origWidth = stemtakenImage.getWidth();
                        int origHeight = stemtakenImage.getHeight();

                        final int destWidth = 600;//or the width you need

                        if(origWidth > destWidth) {
                            // picture is wider than we want it, we calculate its target height
                            int destHeight = origHeight / (origWidth / destWidth);
                            // we create an scaled bitmap so it reduces the image, not just trim it
                            Bitmap stemtakenImageW = Bitmap.createScaledBitmap(stemtakenImage, destWidth, destHeight, false);

                            stemtakenImage = stemtakenImageW;
                        }

                        photoFile = null;
                    }
//            leaftakenImage = BitmapFactory.decodeFile(recdleafimageFilePath);
                    stemimageFilePath = null;


                    break;
                case 22:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage =  data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null) {
                            Cursor cursor = getContentResolver().query(selectedImage,
                                    filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();

                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String picturePath = cursor.getString(columnIndex);
                                imageviewStem.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                                cursor.close();
                                stemtakenImage = bitmapResizer(picturePath);

                            }
                        }

                    }
                    break;
                case 13:
                    if (!(rootimageFilePath == null)) {

                        imageviewRoot.setImageURI(Uri.parse(rootimageFilePath));
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 2;
                        roottakenImage = BitmapFactory.decodeFile(rootimageFilePath,options);
                        // original measurements
                        int origWidthL = roottakenImage.getWidth();
                        int origHeightL = roottakenImage.getHeight();

                        final int destWidthL = 600;//or the width you need

                        if(origWidthL > destWidthL) {
                            // picture is wider than we want it, we calculate its target height
                            int destHeightL = origHeightL / (origWidthL / destWidthL);
                            // we create an scaled bitmap so it reduces the image, not just trim it
                            Bitmap roottakenImageW = Bitmap.createScaledBitmap(roottakenImage, destWidthL, destHeightL, false);

                            roottakenImage = roottakenImageW;
                        }

                        photoFile = null;
                    }
                    rootimageFilePath = null;

                    break;
                case 23:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage =  data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null) {
                            Cursor cursor = getContentResolver().query(selectedImage,
                                    filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();

                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String picturePath = cursor.getString(columnIndex);
                                imageviewRoot.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                                cursor.close();
                                roottakenImage = bitmapResizer(picturePath);

                            }
                        }

                    }
                    break;
                case 14:
                    if (!(otherimageFilePath == null)) {

                        imageviewOther.setImageURI(Uri.parse(otherimageFilePath));
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 2;
                        othertakenImage = BitmapFactory.decodeFile(otherimageFilePath,options);
                        // original measurements
                        int origWidthL = othertakenImage.getWidth();
                        int origHeightL = othertakenImage.getHeight();

                        final int destWidthL = 600;//or the width you need

                        if(origWidthL > destWidthL) {
                            // picture is wider than we want it, we calculate its target height
                            int destHeightL = origHeightL / (origWidthL / destWidthL);
                            // we create an scaled bitmap so it reduces the image, not just trim it
                            Bitmap othertakenImageW = Bitmap.createScaledBitmap(othertakenImage, destWidthL, destHeightL, false);

                            othertakenImage = othertakenImageW;
                        }

                        photoFile = null;
                    }
                    otherimageFilePath = null;
                    break;
                case 24:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage =  data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null) {
                            Cursor cursor = getContentResolver().query(selectedImage,
                                    filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();

                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String picturePath = cursor.getString(columnIndex);
                                imageviewOther.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                                cursor.close();
                                othertakenImage = bitmapResizer(picturePath);


                            }
                        }

                    }
                    break;
            }
        }
    }

    public void uploaduserimage() {

        final ProgressDialog nDialog = new ProgressDialog(CameraGalleryActivity.this);
        nDialog.setMessage("Uploading. Please wait...");
        nDialog.setTitle("Server Response");
        nDialog.setIndeterminate(true);
        nDialog.setCancelable(false);
        nDialog.setCanceledOnTouchOutside(false);
        nDialog.show();

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, myurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                nDialog.hide();
                Log.i("Myresponse", "" + response);
                Toast.makeText(getApplicationContext(), "" + response, Toast.LENGTH_SHORT).show();
                AlertDialog alertDialog = new AlertDialog.Builder(CameraGalleryActivity.this).create();
                alertDialog.setTitle("Server Response");
                alertDialog.setMessage("Status: " + response);
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                nDialog.hide();
                Log.i("Mysmart", "" + error);
                Toast.makeText(getApplicationContext(), "" + error, Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                String imagesstem = null, imagesleaf = null, imagesroot = null, imagesother = null;

                if(stemtakenImage != null) {
                    imagesstem = getStringImage(stemtakenImage);
                }
                if (leaftakenImage != null) {
                    imagesleaf = getStringImage(leaftakenImage);
                }
                if (roottakenImage != null) {
                    imagesroot = getStringImage(roottakenImage);
                }
                if (othertakenImage != null) {
                    imagesother = getStringImage(othertakenImage);
                }


//                Log.i("Mynewsam", "" + imagesstem);
                param.put("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
                param.put("personname", personName);
                param.put("personphone", personPhone);
                param.put("personemail", personEmail);
                param.put("personquery", personQuery);
                if (imagesleaf != null) {
                    param.put("imageleaf", imagesleaf);
                }
                if (imagesstem != null) {
                    param.put("imagestem", imagesstem);
                }
                if (imagesroot !=null) {
                    param.put("imageroot", imagesroot);
                }
                if (imagesother !=null) {
                    param.put("imageother", imagesother);
                }
                if ((personLocationLat != null) && (personLocationLong != null)) {
                    param.put("personlatitude", personLocationLat);
                    param.put("personlongitude", personLocationLong);
                }
                param.put("personcrop", personCrop);

                return param;
            }
        };

        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);

    }

    public String getStringImage(Bitmap bitmap) {
        Log.i("MyHitesh", "" + bitmap);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 20, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);


        return temp;
    }




}
