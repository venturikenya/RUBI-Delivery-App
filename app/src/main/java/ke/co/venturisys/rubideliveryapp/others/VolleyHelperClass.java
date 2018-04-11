package ke.co.venturisys.rubideliveryapp.others;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import static ke.co.venturisys.rubideliveryapp.others.Constants.TAG;

/**
 * Created by victor on 4/10/2018.
 * Helper class to carry out android volley requests
 * This is a singleton with a private constructor and a public get method to access it
 */

public class VolleyHelperClass extends Application {

    @SuppressLint("StaticFieldLeak")
    private static VolleyHelperClass sVolleyHelperClass;
    private static RequestQueue sRequestQueue;
    private Context mContext;

    private VolleyHelperClass(Context context) {
        mContext = context;
        sRequestQueue = getRequestQueue();
    }

    public VolleyHelperClass() {
    }

    /**
     * Provide access to instance of class
     *
     * @return sVolleyHelperClass - an instance of the class
     */
    public static synchronized VolleyHelperClass getVolleyHelperClass(Context context) {
        if (sVolleyHelperClass == null) {
            sVolleyHelperClass = new VolleyHelperClass(context);
        }
        return sVolleyHelperClass;
    }

    /**
     * Helps to return the instance of RequestQueue provided by Volley
     *
     * @return sRequestQueue - an instance of RequestQueue
     */
    public RequestQueue getRequestQueue() {
        // instantiate only if null
        if (sRequestQueue == null) {
            sRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }

        return sRequestQueue;
    }

    /**
     * Method used to add new RequestQueues coming from different parts of the app
     *
     * @param request Request to be added
     * @param tag     Tag of Request. Helps us if we need to cancel a request later
     */
    public <T> void addToRequestQueue(Request<T> request, String tag) {
        request.setTag(TextUtils.isEmpty(tag) ? /* if tag is empty set default tag */ TAG : tag);
        // set request call and add the request to the request queue
        getRequestQueue().add(request);
    }

    /**
     * Method to create request with a default tag
     */
    public <T> void addToRequestQueue(Request<T> request) {
        addToRequestQueue(request, TAG);
    }

    /*
     * Method to cancel request(s) pertaining to particular tag
     * @param tag Tag of Request
     *
    public void cancelPendingRequest(Object tag){
        if (sRequestQueue != null){
            sRequestQueue.cancelAll(tag);
        }
    } */
}
