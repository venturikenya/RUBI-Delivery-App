package ke.co.venturisys.rubideliveryapp.others;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import static ke.co.venturisys.rubideliveryapp.others.Constants.IS_FIRST_TIME_LAUNCH;
import static ke.co.venturisys.rubideliveryapp.others.Constants.PREF_WELCOME_NAME;

/**
 * Created by victor on 3/30/18
 * Class is used to check and ensure that the intro slider is shown
 * only when the app launches for the first time
 * Courtesy of: https://www.androidhive.info/2016/05/android-build-intro-slider-app/
 */

public class PreferencesManager {

    private SharedPreferences sharedPreferences;

    public PreferencesManager(@NonNull Context context) {
        int PRIVATE_MODE = 0; // ensures only the app can open the shared preference
        sharedPreferences = context.getSharedPreferences(PREF_WELCOME_NAME, PRIVATE_MODE);
    }

    /*
     * Detect whether the app is launching for the first time
     */
    public boolean isFirstTimeLaunch() {
        return sharedPreferences.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.apply();
    }
}
