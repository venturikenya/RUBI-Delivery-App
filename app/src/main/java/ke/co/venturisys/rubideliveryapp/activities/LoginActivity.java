package ke.co.venturisys.rubideliveryapp.activities;

import android.support.v4.app.Fragment;

import ke.co.venturisys.rubideliveryapp.fragments.LoginFragment;

public class LoginActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        setTitle("");
        return LoginFragment.newInstance();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
