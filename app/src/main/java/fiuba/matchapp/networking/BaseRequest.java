package fiuba.matchapp.networking;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;

public class BaseRequest extends Request<JSONObject> {

    private Listener<JSONObject> listener;
    private Map<String, String> params;
    private HashMap<String, String> headers;

    public BaseRequest(String url, Map<String, String> params,
                       Listener<JSONObject> reponseListener, ErrorListener errorListener, int method) {
        super(method, url, errorListener);

        this.setShouldCache(Boolean.FALSE);

        this.listener = reponseListener;
        this.params = params;
    }

    public BaseRequest(String url, Map<String, String> params, HashMap<String, String> headers,
                       Listener<JSONObject> reponseListener, ErrorListener errorListener, int method) {
        this(url, params, reponseListener, errorListener, method);
        this.headers = headers;
    }

    protected Map<String, String> getParams() {
        return params;
    }

    ;

    @Override
    public HashMap<String, String> getHeaders() {
        if (headers == null) headers = new HashMap<String, String>();
        return headers;
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            Log.e("Response", jsonString);
            return Response.success(new JSONObject(jsonString),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }


    @Override
    protected void deliverResponse(JSONObject response) {
        listener.onResponse(response);
    }
}