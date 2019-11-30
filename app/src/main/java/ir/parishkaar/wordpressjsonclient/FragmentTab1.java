package ir.parishkaar.wordpressjsonclient;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AlertDialog;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by admin on 28/07/19.
 */

public class FragmentTab1 extends Fragment {

    File leafphotoFile, stemphotoFile;

    ViewPager viewPager;

    Button uploadBtn, btnToUpload;
    ImageView imageviewStem, imageviewLeaf2, imageviewRoot, imageviewOther;

    String stemimageFilePath, leafimageFilePath, rootimageFilePath, otherimageFilePath,
            personName, personPhone, personEmail, personQuery;
    ProgressDialog progressBar;

    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int PICK_IMAGE_REQUEST = 99;
    private static int REQUEST_IMAGE_CAPTURE = 1;
    Spinner spinner2;

    String personCrop;

    String imageFilePath;
    Bitmap stemtakenImage, leaftakenImage, roottakenImage, othertakenImage;

    Bitmap bitmap;
    String myurl = "http://salesapp.matenek.com/salesmatenek/navtests/photocsa.php";

    private SQLiteHandler db;
    private SessionManager session;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get the view from fragmenttab1.xml
        final View view = inflater.inflate(R.layout.fragmenttab1, container, false);


        final RelativeLayout formRelativeLayout = (RelativeLayout) view.findViewById(R.id.formlayout);
        final RelativeLayout imgGridLayout = (RelativeLayout) view.findViewById(R.id.imageGridLayout);
        final RelativeLayout form2RelativeLayout = (RelativeLayout) view.findViewById(R.id.form2layout);

        final EditText personNameInput = (EditText) view.findViewById(R.id.personName);
        final EditText personPhoneInput = (EditText) view.findViewById(R.id.personPhone);
        final EditText personEmailInput = (EditText) view.findViewById(R.id.personEmail);
        final EditText personQueryInput = (EditText) view.findViewById(R.id.personMessage);

        spinner2 = (Spinner) view.findViewById(R.id.personCropSpinner);

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
        db = new SQLiteHandler(getContext());

        // session manager
        session = new SessionManager(getContext());

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

        final Button btnToVisibility2frm1 = (Button) view.findViewById(R.id.btnToFrag2);
        final Button btnToVisibility1frm2 = (Button) view.findViewById(R.id.btntoForm);
        final Button btnToVisibility2frm3 = (Button) view.findViewById(R.id.btnToFrag2frm3);
        final Button btnToVisibility3frm2 = (Button) view.findViewById(R.id.btnToFrag3);

//        pbar = (ProgressBar) view.findViewById(R.id.progressBar1);


        viewPager = (ViewPager) getActivity().findViewById(R.id.pager);

        btnToVisibility2frm1.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
//                viewPager.setCurrentItem(1);

                personName = personNameInput.getText().toString();
                personPhone = personPhoneInput.getText().toString();
                personEmail = personEmailInput.getText().toString();

                if ((TextUtils.isEmpty(personName))   || (TextUtils.isEmpty(personPhone))){
                    Toast.makeText(getContext(),"Name and Phone are mandatory",Toast.LENGTH_SHORT).show();
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

        btnToVisibility1frm2.setOnClickListener(new View.OnClickListener()
                {

                    @Override
                    public void onClick(View v) {
//                viewPager.setCurrentItem(1);

                        imgGridLayout.setVisibility(View.INVISIBLE);
                        formRelativeLayout.setVisibility(View.VISIBLE);
                        form2RelativeLayout.setVisibility(View.INVISIBLE);
                    }
                });

        btnToVisibility3frm2.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v) {
//                viewPager.setCurrentItem(1);

                imgGridLayout.setVisibility(View.INVISIBLE);
                formRelativeLayout.setVisibility(View.INVISIBLE);
                form2RelativeLayout.setVisibility(View.VISIBLE);
            }
        });

        btnToVisibility2frm3.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v) {
//                viewPager.setCurrentItem(1);

                imgGridLayout.setVisibility(View.VISIBLE);
                formRelativeLayout.setVisibility(View.INVISIBLE);
                form2RelativeLayout.setVisibility(View.INVISIBLE);
            }
        });


        imageviewStem = (ImageView) view.findViewById(R.id.imgStem);
        imageviewLeaf2 = (ImageView) view.findViewById(R.id.imgLeaf2);
        imageviewRoot = (ImageView) view.findViewById(R.id.imgRoot);
        imageviewOther = (ImageView) view.findViewById(R.id.imgOther);

        imageviewStem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showFileChooser("stem");
            }
        });

        imageviewLeaf2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showFileChooser("leaf");
            }
        });

        imageviewRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showFileChooser("root");
            }
        });
        imageviewOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showFileChooser("other");
            }
        });

      btnToUpload = (Button) view.findViewById(R.id.btnToUpload);
        btnToUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                personQuery = personQueryInput.getText().toString();


                try {
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
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



        return view;
    }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     * */
    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(getActivity(), LoginActivity.class);
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
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(dataAdapter);
    }




    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(getContext(), " Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getContext(), "Permission Granted Successfully! ", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "Permission Denied :( ", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void showFileChooser(String plantPart) {

        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(pictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {


            leafphotoFile = null;
            try {
                leafphotoFile = createImageFile(plantPart);
            }
            catch (IOException e) {
                e.printStackTrace();
                return;
            }
            Uri leafphotoUri = FileProvider.getUriForFile(getContext(), getActivity().getPackageName() +".provider", leafphotoFile);
            leafphotoFile = null;

            pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, leafphotoUri);
            pictureIntent.putExtra("intentPart", plantPart);
            startActivityForResult(pictureIntent, REQUEST_IMAGE_CAPTURE);

        }
    }

    private File createImageFile(String plantPart) throws IOException {

        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = plantPart + "IMG_" + timeStamp + "_";
        File storageDir =
                getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
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



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            Bundle extras = data.getExtras();
            //  Bitmap viewbitmap = (Bitmap) extras.get("data");
            // imageview.setImageBitmap(viewbitmap);

//            String imagedata = extras.getString("image");
//            String oarplantpart = extras.getString("intentPart");


//            Toast.makeText(getContext(),data.getStringExtra("intentPart"),Toast.LENGTH_SHORT);

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

                    leafphotoFile = null;
            }
//            leaftakenImage = BitmapFactory.decodeFile(recdleafimageFilePath);
            stemimageFilePath = null;

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

                leafphotoFile = null;
            }
            leafimageFilePath = null;

//            **********
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

                leafphotoFile = null;
            }
            rootimageFilePath = null;

//            **********

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

                leafphotoFile = null;
            }
            otherimageFilePath = null;

//            **********

        } else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(getContext(), "You cancelled the operation", Toast.LENGTH_SHORT).show();
        }

    }

    public void uploaduserimage() {

        final ProgressDialog nDialog = new ProgressDialog(getActivity());
        nDialog.setMessage("Uploading. Please wait...");
        nDialog.setTitle("Server Response");
        nDialog.setIndeterminate(true);
        nDialog.setCancelable(false);
        nDialog.setCanceledOnTouchOutside(false);
        nDialog.show();

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, myurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                nDialog.hide();
                Log.i("Myresponse", "" + response);
                Toast.makeText(getActivity().getApplicationContext(), "" + response, Toast.LENGTH_SHORT).show();
                AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
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
                Toast.makeText(getActivity().getApplicationContext(), "" + error, Toast.LENGTH_SHORT).show();

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
