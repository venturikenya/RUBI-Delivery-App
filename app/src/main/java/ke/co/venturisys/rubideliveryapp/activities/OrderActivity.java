package ke.co.venturisys.rubideliveryapp.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import ke.co.venturisys.rubideliveryapp.R;
import ke.co.venturisys.rubideliveryapp.fragments.OrderFragment;

import static ke.co.venturisys.rubideliveryapp.others.Constants.EXTRA_USER_BACKDROP_ICON;
import static ke.co.venturisys.rubideliveryapp.others.Constants.EXTRA_USER_BACKDROP_TITLE;
import static ke.co.venturisys.rubideliveryapp.others.Extras.exitToTargetActivity;

public class OrderActivity extends SingleFragmentActivity {

    /*
     * Called in Home Fragment to create Intent containing extra info as needed.
     * Helps to hide OrderActivity's needed extras
     */
    public static Intent newIntent(Context context, String icon, String title) {
        Intent intent = new Intent(context, OrderActivity.class);
        intent.putExtra(EXTRA_USER_BACKDROP_TITLE, title);
        intent.putExtra(EXTRA_USER_BACKDROP_ICON, icon);
        return intent;
    }

    /*
     * Returns an instance of OrderFragment
     * by passing the data passed in its intent to the newInstance method of the fragment
     * where the data will be stored in the fragment arguments
     */
    @Override
    protected Fragment createFragment() {
        setTitle(getString(R.string.activity_title_order));
        String title = getIntent().getStringExtra(EXTRA_USER_BACKDROP_TITLE);
        String icon = getIntent().getStringExtra(EXTRA_USER_BACKDROP_ICON);
        return OrderFragment.newInstance(icon, title);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        exitToTargetActivity(this, MainActivity.class);
    }
}
