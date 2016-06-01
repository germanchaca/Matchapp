package fiuba.matchapp.networking;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by german on 5/25/2016.
 */
public class JsonUtils {
    public static JSONObject getJsonObjectFromLocation(double latitude, double longitude){
        JSONObject locationJson=new JSONObject();
        try {
            locationJson.put("latitude",latitude);
            locationJson.put("longitude",longitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return locationJson;
    }

}
