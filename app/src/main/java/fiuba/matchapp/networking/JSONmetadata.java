package fiuba.matchapp.networking;

import org.json.JSONException;
import org.json.JSONObject;

import fiuba.matchapp.app.MyApplication;

/**
 * Created by german on 5/25/2016.
 */
public class JSONmetadata {
    public static JSONObject getMetadata(int count  ){
        JSONObject metedataJson =new JSONObject();
        try {
            metedataJson.put("version", MyApplication.VERSION);
            metedataJson.put("count", count);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return metedataJson;
    }
}
