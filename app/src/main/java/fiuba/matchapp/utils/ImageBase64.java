package fiuba.matchapp.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class ImageBase64 {

    public static String getEncoded64ImageStringFromBitmap (Bitmap bitmap){

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        byte[] byteFormat = stream.toByteArray();
        String imgString = Base64.encodeToString(byteFormat, Base64.DEFAULT);

        return imgString;
    }

    public static Bitmap Base64ToBitmap(String myImageData)
    {

        try {
            byte[] imageAsBytes = Base64.decode(myImageData.getBytes(),Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
        }catch (IllegalArgumentException e){
            //e.printStackTrace();
            return null;
        }
    }
}

