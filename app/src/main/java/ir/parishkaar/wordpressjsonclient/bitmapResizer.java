package ir.parishkaar.wordpressjsonclient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by admin on 11/08/19.
 */

public class bitmapResizer extends  CameraGalleryActivity {

    String imgFilePath;

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
        }
        return (takenImageW);
    }
}
