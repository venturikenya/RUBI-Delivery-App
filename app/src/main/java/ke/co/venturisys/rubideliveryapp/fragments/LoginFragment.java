package ke.co.venturisys.rubideliveryapp.fragments;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;

import ke.co.venturisys.rubideliveryapp.R;
import ke.co.venturisys.rubideliveryapp.activities.MainActivity;

import static ke.co.venturisys.rubideliveryapp.others.Constants.RES_ID;
import static ke.co.venturisys.rubideliveryapp.others.Constants.TAG_REGISTRATION;
import static ke.co.venturisys.rubideliveryapp.others.Extras.changeFragment;
import static ke.co.venturisys.rubideliveryapp.others.Extras.exitToTargetActivity;
import static ke.co.venturisys.rubideliveryapp.others.Extras.isEmpty;
import static ke.co.venturisys.rubideliveryapp.others.Extras.loadPictureToImageView;

public class LoginFragment extends Fragment {

    View view;
    EditText userName, passWord;
    ImageView cityImageView, userNameImageView, passwordImageView,
            googleBtn, facebookBtn;
    Button loginButton;
    TextView createAccount, forgotPassword;

    public LoginFragment() {
    }

    public static LoginFragment newInstance() {

        Bundle args = new Bundle();

        LoginFragment fragment = new LoginFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // inflate layout
        view = inflater.inflate(R.layout.fragment_login, container, false);
        assert getActivity() != null;

        // initialise widgets
        userName = view.findViewById(R.id.login_username);
        passWord = view.findViewById(R.id.login_password);
        cityImageView = view.findViewById(R.id.city_image_view);
        userNameImageView = view.findViewById(R.id.username_image_view);
        passwordImageView = view.findViewById(R.id.password_image_view);
        loginButton = view.findViewById(R.id.login_button);
        createAccount = view.findViewById(R.id.btnCreateAccount);
        forgotPassword = view.findViewById(R.id.btnForgotPassword);
        googleBtn = view.findViewById(R.id.google_plus_btn);
        facebookBtn = view.findViewById(R.id.facebook_btn);

        // enter app if successful and show warning if not
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitLoginForm();
            }
        });

        // redirect to redirection
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(RegistrationFragment.newInstance(), new Handler(), TAG_REGISTRATION,
                        (AppCompatActivity) getActivity());
            }
        });

        // set color to username icon
        userNameImageView.getDrawable().setColorFilter(new PorterDuffColorFilter
                (Color.WHITE, PorterDuff.Mode.SRC_IN));

        // set image of city to login page
        HashMap<String, Object> src = new HashMap<>();
        src.put(RES_ID, R.drawable.ic_nairobi);
        loadPictureToImageView(src, R.drawable.ic_nairobi, cityImageView, false, false,
                false, false);

        return view;
    }

    private void submitLoginForm() {
        if (isEmpty(userName)) userName.setError("User name required");
        else if (isEmpty(passWord)) passWord.setError("Password required");
        else exitToTargetActivity((AppCompatActivity) getActivity(), MainActivity.class);
    }
}
