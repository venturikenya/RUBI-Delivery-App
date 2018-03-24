package ke.co.venturisys.rubideliveryapp.others;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiManager;
import android.view.View;
import android.widget.Button;

import ke.co.venturisys.rubideliveryapp.R;

import static ke.co.venturisys.rubideliveryapp.others.Constants.REQUEST_MOBILE_NETWORK;
import static ke.co.venturisys.rubideliveryapp.others.NetworkingClass.isNetworkAvailable;

/**
 * Created by victor on 3/24/18.
 * Class that checks for the app's permissions
 * It also holds a dialog fragment that enables a user to connect to the internet
 * via either via or a preferred mobile network
 */

public class Permissions {

    /**
     * This class extends Dialog Fragment
     * It is used to enable a user to choose between connect to a Wi-Fi or mobile network
     * if it is detected that there is no internet connection
     */
    public static class InternetConnectionDialogFragment extends GeneralDialogFragment {

        public InternetConnectionDialogFragment() {
            super.alertDialogLayout = R.layout.dialog_internet_picker;
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            // return images in the buttons back to original colors
            Drawable mDrawable = activity.getResources().getDrawable(R.drawable.ic_wifi_option);
            mDrawable.setColorFilter(new
                    PorterDuffColorFilter(Color.DKGRAY, PorterDuff.Mode.SRC_IN));
            mDrawable = activity.getResources().getDrawable(R.drawable.ic_network_option);
            mDrawable.setColorFilter(new
                    PorterDuffColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN));
        }

        @Override
        protected void initializeWidgets(View view) {
            setAlertDialogTitle(R.string.internet_dialog_title);
            Button wifiButton = view.findViewById(R.id.internet_wifi_button);
            Button mobileButton = view.findViewById(R.id.internet_mobile_network_button);
            Button closeButton = view.findViewById(R.id.internet_close_button);

            /* if user selects wifi, activate the WiFi manager which switches the WiFi connection on
            then exit the fragment once connection is confirmed */
            wifiButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // change color of image in button
                    Drawable mDrawable = activity.getResources().getDrawable(R.drawable.ic_wifi_option);
                    mDrawable.setColorFilter(new
                            PorterDuffColorFilter(Color.BLUE,
                            PorterDuff.Mode.SRC_IN));

                    // switch WiFi on
                    WifiManager wifiManager = (WifiManager) activity.getApplicationContext()
                            .getSystemService(Context.WIFI_SERVICE);
                    assert wifiManager != null;
                    wifiManager.setWifiEnabled(true);

                    // wait for connection to be confirmed then exit if successful
                    Thread t = new Thread() {
                        @Override
                        public void run() {
                            try {
                                // check if connected!
                                while (!isNetworkAvailable(activity)) {
                                    //Wait to connect
                                    Thread.sleep(10);
                                }

                                exitFragment();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    t.start();
                }
            });

            mobileButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Drawable mDrawable = activity.getResources().getDrawable(R.drawable.ic_network_option);
                    mDrawable.setColorFilter(new
                            PorterDuffColorFilter(Color.BLUE,
                            PorterDuff.Mode.SRC_IN));

                    Intent settingsIntent = new Intent(Intent.ACTION_MAIN);

                    settingsIntent.setClassName("com.android.settings",
                            "com.android.settings.Settings$DataUsageSummaryActivity");

                    settingsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    startActivityForResult(settingsIntent, REQUEST_MOBILE_NETWORK);
                }
            });

            // close fragment
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    exitFragment();
                }
            });
        }
    }

}
