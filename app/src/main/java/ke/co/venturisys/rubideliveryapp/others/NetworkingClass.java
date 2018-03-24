package ke.co.venturisys.rubideliveryapp.others;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by victor on 3/24/18.
 * This class is used to carry out requests like GET, process responses
 * and check internet connectivity
 */

public class NetworkingClass {

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

}
