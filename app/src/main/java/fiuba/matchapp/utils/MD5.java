package fiuba.matchapp.utils;

import android.util.Log;

import java.security.MessageDigest;

/**
 * Created by german on 5/23/2016.
 * Hashea la password segun el algoritmo md5.
 * @return Hash md5 de password
 */
public class MD5 {

    public static String getHashedPassword(String password) {

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");

            md.update(password.getBytes());

            byte byteData[] = md.digest();

            StringBuffer hexString = new StringBuffer();

            for (int i=0;i<byteData.length;i++) {
                String hex=Integer.toHexString(0xff & byteData[i]);
                if(hex.length()==1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        }

        catch(java.security.NoSuchAlgorithmException missing) {
            Log.e("MainActivity", "No se encontro el algoritmo md5");
            return "Error.";
        }

    }

}
