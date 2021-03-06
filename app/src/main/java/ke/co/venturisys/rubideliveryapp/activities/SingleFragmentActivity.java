package ke.co.venturisys.rubideliveryapp.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import ke.co.venturisys.rubideliveryapp.R;

import static ke.co.venturisys.rubideliveryapp.others.Extras.setUpActionBar;

public abstract class SingleFragmentActivity extends AppCompatActivity {

    // activity_title of toolbar
    String title;

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
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

    @Override
    protected void onResume() {
        super.onResume();
        // set up toolbar
        setUpActionBar(title, this);
    }

    protected abstract Fragment createFragment();

}
