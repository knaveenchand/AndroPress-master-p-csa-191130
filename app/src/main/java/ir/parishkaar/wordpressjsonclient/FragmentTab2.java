package ir.parishkaar.wordpressjsonclient;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.fragment.app.Fragment;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


/**
 * Created by admin on 28/07/19.
 */

public class FragmentTab2 extends Fragment {

    ViewPager viewPager;

    Button uploadBtn;
    ImageView imageviewLeaf;

    String leafimageFilePath;


    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int PICK_IMAGE_REQUEST = 99;
    private static int REQUEST_IMAGE_CAPTURE = 1;

    String imageFilePath;
    Bitmap leaftakenImage;

    Bitmap bitmap;
    String myurl = "http://salesapp.matenek.com/salesmatenek/navtests/photocsa.php";
//
//    DataPassListener mCallback;
//
//    public interface DataPassListener{
//        public void passData(String data);
//    }
//
//    @Override
//    public void onAttach(Context context)
//    {
//        super.onAttach(context);
//        // This makes sure that the host activity has implemented the callback interface
//        // If not, it throws an exception
//        try
//        {
//            mCallback = (OnImageClickListener) context;
//        }
//        catch (ClassCastException e)
//        {
//            throw new ClassCastException(context.toString()+ " must implement OnImageClickListener");
//        }
//    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {




        // Get the view from fragmenttab2.xml
        View view = inflater.inflate(R.layout.fragmenttab2, container, false);

//        uploadBtn = (Button) view.findViewById(R.id.upload);
        viewPager = (ViewPager) getActivity().findViewById(R.id.pager);

//        imageviewLeaf = (ImageView) view.findViewById(R.id.imgLeaf);
//        imageviewStem = (ImageView) view.findViewById(R.id.imgStem);

//        imageviewLeaf.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
////                Intent mp = new Intent(getActivity(), MultiPhotoActivity.class);
////                startActivity(mp);
//                showFileChooser("leaf");
//            }
//        });
//
//        Button btnFthree = (Button) view.findViewById(R.id.btnToFrag3);
//        btnFthree.setOnClickListener(new View.OnClickListener()
//        {
//
//            @Override
//            public void onClick(View v)
//            {
//
////                mCallback.passData("Text to pass FragmentB");
//
//
//                viewPager.setCurrentItem(2);
//
//
//            }
//        });



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

            File leafphotoFile = null;
            try {
                leafphotoFile = createImageFile(plantPart);
            }
            catch (IOException e) {
                e.printStackTrace();
                return;
            }
            Uri leafphotoUri = FileProvider.getUriForFile(getContext(), getActivity().getPackageName() +".provider", leafphotoFile);
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

        leafimageFilePath = image.getAbsolutePath();

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


                imageviewLeaf.setImageURI(Uri.parse(leafimageFilePath));
//              leaftakenImage = BitmapFactory.decodeFile(leafimageFilePath);


        } else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(getContext(), "You cancelled the operation", Toast.LENGTH_SHORT).show();
        }

    }


}
