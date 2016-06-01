package fiuba.matchapp.networking;

import org.json.JSONException;
import org.json.JSONObject;

import fiuba.matchapp.app.MyApplication;

/**
 * Created by german on 5/25/2016.
 */
public class JsonMetadataUtils {
    public static JSONObject getMetadata(int count) throws JSONException {
        JSONObject metedataJson =new JSONObject();
        metedataJson.put("version", MyApplication.VERSION);
        metedataJson.put("count", count);

        return metedataJson;
    }
    public static JSONObject getMetadata() throws JSONException {
        JSONObject metedataJson =new JSONObject();
        metedataJson.put("version", MyApplication.VERSION);

        return metedataJson;
    }
}
