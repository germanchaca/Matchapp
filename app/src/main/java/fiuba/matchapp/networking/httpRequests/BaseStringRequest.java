package fiuba.matchapp.networking.httpRequests;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by german on 5/25/2016.
 */
public class BaseStringRequest extends Request<String> {

    private Response.Listener<String> listener;
    private Map<String, String> params;
    private HashMap<String, String> headers;
    private String body;

    public BaseStringRequest(String url, Map<String, String> params,
                             Response.Listener<String> reponseListener, Response.ErrorListener errorListener, int method) {
        super(method, url, errorListener);


        /*if(method != Request.Method.GET) {
            this.setShouldCache(Boolean.FALSE);
        }else {
            this.setShouldCache(Boolean.TRUE);
        }*/

        this.listener = reponseListener;
        this.params = params;
    }

    public BaseStringRequest(String url, HashMap<String, String> headers, String body,
                             Response.Listener<String> reponseListener, Response.ErrorListener errorListener, int method){
        this(url, null, reponseListener, errorListener, method);
        this.headers = headers;
        this.body = body;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        byte[] bodyArray =  this.body.getBytes(Charset.forName("UTF-8"));
        return bodyArray;
    }
    protected Map<String, String> getParams()
            throws com.android.volley.AuthFailureError {
        return params;
    };

    @Override
    public HashMap<String, String> getHeaders() {
        if(headers == null) headers = new HashMap<String, String>();
        return headers;
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        Log.d("BaseString", "entra");
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            Log.e("Response", jsonString);
            return Response.success(jsonString,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }
    @Override
    public String getBodyContentType() {
        return "application/json; charset=utf-8";
    }
    @Override
    protected void deliverResponse(String response) {
        Log.d("BaseString", "entraRes");

        listener.onResponse(response);
    }

}