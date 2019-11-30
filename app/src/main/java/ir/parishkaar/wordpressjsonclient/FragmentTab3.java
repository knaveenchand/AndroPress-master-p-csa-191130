package ir.parishkaar.wordpressjsonclient;

/**
 * Created by admin on 28/07/19.
 */

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.fragment.app.Fragment;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.ViewPager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class FragmentTab3 extends Fragment {

    File leafphotoFile, stemphotoFile;

    ViewPager viewPager;

    Button uploadBtn;
    ImageView imageviewStem, imageviewLeaf2;

    String stemimageFilePath, leafimageFilePath, recdleafimageFilePath;


    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int PICK_IMAGE_REQUEST = 99;
    private static int REQUEST_IMAGE_CAPTURE = 1;

    String imageFilePath;
    Bitmap stemtakenImage, leaftakenImage;

    Bitmap bitmap;
    String myurl = "http://salesapp.matenek.com/salesmatenek/navtests/photocsa.php";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get the view from fragmenttab3.xml
        View view = inflater.inflate(R.layout.fragmenttab3, container, false);

        //        uploadBtn = (Button) view.findViewById(R.id.upload);
        viewPager = (ViewPager) getActivity().findViewById(R.id.pager);

        imageviewStem = (ImageView) view.findViewById(R.id.imgStem);
        imageviewLeaf2 = (ImageView) view.findViewById(R.id.imgLeaf2);

        imageviewStem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Intent mp = new Intent(getActivity(), MultiPhotoActivity.class);
//                startActivity(mp);
                showFileChooser("stem");
            }
        });

        imageviewLeaf2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Intent mp = new Intent(getActivity(), MultiPhotoActivity.class);
//                startActivity(mp);
                showFileChooser("leaf");
            }
        });

        Button btnFfour = (Button) view.findViewById(R.id.btnToFrag4);
        Button btnToUpload = (Button) view.findViewById(R.id.btnToUpload);

        //Retrieve the value
//        recdleafimageFilePath = getArguments().getString("leafimageFilePath");



        btnFfour.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                viewPager.setCurrentItem(1);


            }
        });

        btnToUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploaduserimage();
            }
        });




//        imageviewStem.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
////                Intent mp = new Intent(getActivity(), MultiPhotoActivity.class);
////                startActivity(mp);
//                showFileChooserStem("stem");
//            }
//        });
//


        return view;
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

        }

        if (plantPart == "stem") {
            stemimageFilePath = image.getAbsolutePath();
            leafimageFilePath = null;

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

        // if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            //  Bitmap viewbitmap = (Bitmap) extras.get("data");
            // imageview.setImageBitmap(viewbitmap);

//            String imagedata = extras.getString("image");
//            String oarplantpart = extras.getString("intentPart");


//            Toast.makeText(getContext(),data.getStringExtra("intentPart"),Toast.LENGTH_SHORT);

            if (!(stemimageFilePath == null)) {

                imageviewStem.setImageURI(Uri.parse(stemimageFilePath));
                stemtakenImage = BitmapFactory.decodeFile(stemimageFilePath);

                leafphotoFile = null;
            }
//            leaftakenImage = BitmapFactory.decodeFile(recdleafimageFilePath);
            stemimageFilePath = null;

            if (!(leafimageFilePath == null)) {

                imageviewLeaf2.setImageURI(Uri.parse(leafimageFilePath));
                leaftakenImage = BitmapFactory.decodeFile(leafimageFilePath);
                leafphotoFile = null;
            }
            leafimageFilePath = null;


        } else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(getContext(), "You cancelled the operation", Toast.LENGTH_SHORT).show();
        }

    }

    public void uploaduserimage() {

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, myurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.i("Myresponse", "" + response);
                Toast.makeText(getActivity().getApplicationContext(), "" + response, Toast.LENGTH_SHORT).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Mysmart", "" + error);
                Toast.makeText(getActivity().getApplicationContext(), "" + error, Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();

                String imagesstem = getStringImage(stemtakenImage);
                String imagesleaf = getStringImage(leaftakenImage);
                Log.i("Mynewsam", "" + imagesstem);
                param.put("imageleaf", imagesleaf);
                param.put("imagestem", imagesstem);

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
