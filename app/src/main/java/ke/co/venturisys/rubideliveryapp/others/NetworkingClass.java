package ke.co.venturisys.rubideliveryapp.others;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static ke.co.venturisys.rubideliveryapp.others.Constants.NAME;
import static ke.co.venturisys.rubideliveryapp.others.Constants.PIC;

/**
 * Created by victor on 3/24/18.
 * This class is used to carry out requests like GET, process responses
 * and check internet connectivity
 */

public class NetworkingClass {

    private static JSONObject sJSONObject = new JSONObject();

    /**
     * Method checks whether there is internet connectivity
     * It calls the getActiveNetworkInfo and then checks if returns null, which it then returns
     * as true for network connection or false if otherwise
     *
     * @param context Current context
     * @return State of net connection
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Method to carry out POST requests to a given url and return response to request using Volley
     *
     * @param url      URL to send request to
     * @param message  Message to be sent
     * @param activity Current activity that the user is in
     * @return JSON Object response to request
     */
    public static JSONObject POST(String url, Message message, Activity activity) {
        try {
            // build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate(NAME, message.getName());
            jsonObject.accumulate(PIC, message.getPic());

            VolleyHelperClass.getVolleyHelperClass(activity.getApplicationContext()).
                    addToRequestQueue(new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    sJSONObject = response;
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            System.out.println("Something went wrong");
                            error.printStackTrace();
                        }
                    }) {
                        @Override
                        public Map<String, String> getHeaders() {
                            Map<String, String> headers = new HashMap<>();
                            headers.put("Accept", "application/json");
                            headers.put("Content-type", "application/json");
                            return headers;
                        }
                    });
        } catch (JSONException ex) {
            ex.printStackTrace();
        }

        return sJSONObject;
    }

    /**
     * Method to carry out POST requests to a given url and return response to request using OkHttpClient
     * Preferable when there is need to send a file to the url
     *
     * @param url     URL to send request to
     * @param message Message to be sent
     * @return JSON Object response to request
     */
    public static JSONObject POST(final String url, Message message) {
        final MultiPartRequestClass multiPartRequestClass = new MultiPartRequestClass();
        multiPartRequestClass.addObject(NAME, message.getName());
        multiPartRequestClass.addObject(PIC, message.getPic());

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                sJSONObject = multiPartRequestClass.execute(url);
            }
        });
        t.start();

        return sJSONObject;
    }

}
