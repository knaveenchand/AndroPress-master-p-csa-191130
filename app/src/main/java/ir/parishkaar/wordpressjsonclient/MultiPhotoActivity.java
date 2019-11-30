package ir.parishkaar.wordpressjsonclient;

import android.app.Activity;
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
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MultiPhotoActivity extends Activity {

    Button upload, btnLeaf, btnStem, btnRoot, btnOther;
    ImageView imageview, imgLeafView, imgStemView, imgRootView, imgOtherView;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int PICK_IMAGE_REQUEST = 99;
    private static int REQUEST_IMAGE_CAPTURE = 1;

    private static final String TAG = "Testing: ";


    String imageFilePath, leafimageFilePath;
    Bitmap takenImage, leaftakenImage;

    Bitmap bitmap;
    String myurl = "http://salesapp.matenek.com/salesmatenek/navtests/photocsa.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_photo);
        upload = (Button) findViewById(R.id.upload);
        btnLeaf = (Button) findViewById(R.id.btnLeaf);
        btnStem = (Button) findViewById(R.id.btnStem);
        btnRoot = (Button) findViewById(R.id.btnRoot);
        btnOther = (Button) findViewById(R.id.btnOther);

        imgLeafView = (ImageView) findViewById(R.id.imageView2);
        imgStemView = (ImageView) findViewById(R.id.imageView3);
        imgRootView = (ImageView) findViewById(R.id.imageView4);
        imgOtherView = (ImageView) findViewById(R.id.imageView5);

        //imageview = (ImageView) findViewById(R.id.imageview);
        //  bitmap = BitmapFactory.decodeResource(getResources(), R.id.imageview);

        showFileChooser("leaf");


//        btnLeaf.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                showFileChooser("btnLeaf");
//            }
//        });

//        imageview.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                showFileChooser("btnLeaf");
//            }
//        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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


    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MultiPhotoActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(MultiPhotoActivity.this, " Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(MultiPhotoActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(MultiPhotoActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
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
                    Toast.makeText(MultiPhotoActivity.this, "Permission Granted Successfully! ", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MultiPhotoActivity.this, "Permission Denied :( ", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void showFileChooser(String plantPart) {

        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(pictureIntent.resolveActivity(getPackageManager()) != null) {

            File leafphotoFile = null;
            try {
                leafphotoFile = createImageFile(plantPart);
            }
            catch (IOException e) {
                e.printStackTrace();
                return;
            }
            Uri leafphotoUri = FileProvider.getUriForFile(this, getPackageName() +".provider", leafphotoFile);
            pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, leafphotoUri);
            pictureIntent.putExtra("intentPart", plantPart);
            startActivityForResult(pictureIntent, REQUEST_IMAGE_CAPTURE);

//            if(plantPart == "btnLeaf") {
//
//                File leafphotoFile = null;
//                try {
//                    leafphotoFile = createImageFile(plantPart);
//                }
//                catch (IOException e) {
//                    e.printStackTrace();
//                    return;
//                }
//                Uri leafphotoUri = FileProvider.getUriForFile(this, getPackageName() +".provider", leafphotoFile);
//                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, leafphotoUri);
//                pictureIntent.putExtra("intentPart", plantPart);
//                startActivityForResult(pictureIntent, REQUEST_IMAGE_CAPTURE);
//
//            }


//            File photoFile = null;
//            try {
//                photoFile = createImageFile(plantPart);
//            }
//            catch (IOException e) {
//                e.printStackTrace();
//                return;
//            }
//            Uri photoUri = FileProvider.getUriForFile(this, getPackageName() +".provider", photoFile);
//            pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
//            startActivityForResult(pictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            //  Bitmap viewbitmap = (Bitmap) extras.get("data");
            // imageview.setImageBitmap(viewbitmap);

            String plantpart = data.getStringExtra("intentPart");

            Toast.makeText(MultiPhotoActivity.this,data.getStringExtra("intentPart"),Toast.LENGTH_SHORT);

//            imgLeafView.setImageURI(Uri.parse(leafimageFilePath));
            leaftakenImage = BitmapFactory.decodeFile(leafimageFilePath);

            Intent goToFragParent = new Intent(MultiPhotoActivity.this, FragmentMainActivity.class);
            goToFragParent.putExtra("myImageURI", imageFilePath);
            goToFragParent.putExtra("leaftakenImage",leafimageFilePath);
            goToFragParent.putExtra("plantimage", plantpart);
            startActivity(goToFragParent);
//            setResult(Activity.RESULT_OK,goToFragParent);
//            finish();


//            if (data.getStringExtra("intentPart") == "btnLeaf") {
//               imgLeafView.setImageURI(Uri.parse(leafimageFilePath));
//               leaftakenImage = BitmapFactory.decodeFile(leafimageFilePath);
//           }
            //imageview.setImageURI(Uri.parse(imageFilePath));

            //imgLeaf.setImageURI(Uri.parse(CreateFile))

//            takenImage = BitmapFactory.decodeFile(imageFilePath);

//            Intent goToFragParent = new Intent(MultiPhotoActivity.this, FragmentMainActivity.class);
//            goToFragParent.putExtra("myImageURI", imageFilePath);
//            startActivity(goToFragParent);

//



            //Uri filePath = data.getData();
//            try {
//                //Getting the Bitmap from Gallery
//                bitmap = getBitmap(getContentResolver(), filePath);
//                Toast.makeText(this, ""+bitmap, Toast.LENGTH_SHORT).show();
//                //Setting the Bitmap to ImageView
//                imageview.setImageBitmap(bitmap);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        } else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "You cancelled the operation", Toast.LENGTH_SHORT).show();
        }

    }

    private File createImageFile(String plantPart) throws IOException {

        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = plantPart + "IMG_" + timeStamp + "_";
        File storageDir =
                getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        leafimageFilePath = image.getAbsolutePath();

//        if (plantPart == "btnLeaf") {
//            leafimageFilePath = image.getAbsolutePath();
//        }
//        imageFilePath = image.getAbsolutePath();
        return image;
    }


    public void uploaduserimage() {

        RequestQueue requestQueue = Volley.newRequestQueue(MultiPhotoActivity.this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, myurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.i("Myresponse", "" + response);
                Toast.makeText(MultiPhotoActivity.this, "" + response, Toast.LENGTH_SHORT).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Mysmart", "" + error);
                Toast.makeText(MultiPhotoActivity.this, "" + error, Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();

                String images = getStringImage(takenImage);
                Log.i("Mynewsam", "" + images);
                param.put("image", images);
                return param;
            }
        };

        requestQueue.add(stringRequest);

    }


    public String getStringImage(Bitmap bitmap) {
        Log.i("MyHitesh", "" + bitmap);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);


        return temp;
    }
}