package ke.co.venturisys.rubideliveryapp.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ke.co.venturisys.rubideliveryapp.R;
import ke.co.venturisys.rubideliveryapp.fragments.OrderFragment;

import static ke.co.venturisys.rubideliveryapp.others.Constants.EXTRA_USER_BACKDROP_ICON;
import static ke.co.venturisys.rubideliveryapp.others.Constants.EXTRA_USER_BACKDROP_TITLE;
import static ke.co.venturisys.rubideliveryapp.others.Extras.exitToTargetActivity;
import static ke.co.venturisys.rubideliveryapp.others.Extras.setUpActionBar;

public class OrderActivity extends AppCompatActivity {

    /*
     * Called in Home Fragment to create Intent containing extra info as needed.
     * Helps to hide OrderActivity's needed extras
     */
    public static Intent newIntent(Context context, int icon, String title) {
        Intent intent = new Intent(context, OrderActivity.class);
        intent.putExtra(EXTRA_USER_BACKDROP_TITLE, title);
        intent.putExtra(EXTRA_USER_BACKDROP_ICON, icon);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        // set up toolbar
        setUpActionBar(getString(R.string.activity_title_order), this);
        // host fragment in this activity
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.frame);
        if (fragment == null) {
            fragment = createFragment();
            fm.beginTransaction()
                    .add(R.id.frame, fragment)
                    .commit();
        }
    }

    /*
     * Returns an instance of OrderFragment
     * by passing the data passed in its intent to the newInstance method of the fragment
     * where the data will be stored in the fragment arguments
     */
    private Fragment createFragment() {
        String title = getIntent().getStringExtra(EXTRA_USER_BACKDROP_TITLE);
        int icon = getIntent().getIntExtra(EXTRA_USER_BACKDROP_ICON, R.drawable.ruby_small);
        return OrderFragment.newInstance(icon, title);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        exitToTargetActivity(this, MainActivity.class);
    }
}
