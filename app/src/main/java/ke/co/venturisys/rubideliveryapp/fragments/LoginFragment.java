package ke.co.venturisys.rubideliveryapp.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Objects;

import ke.co.venturisys.rubideliveryapp.R;
import ke.co.venturisys.rubideliveryapp.activities.MainActivity;

import static ke.co.venturisys.rubideliveryapp.others.Constants.ERROR;
import static ke.co.venturisys.rubideliveryapp.others.Constants.RES_ID;
import static ke.co.venturisys.rubideliveryapp.others.Constants.TAG_REGISTRATION;
import static ke.co.venturisys.rubideliveryapp.others.Constants.TAG_RESET_PASSWORD;
import static ke.co.venturisys.rubideliveryapp.others.Extras.changeFragment;
import static ke.co.venturisys.rubideliveryapp.others.Extras.exitToTargetActivity;
import static ke.co.venturisys.rubideliveryapp.others.Extras.isEmailValid;
import static ke.co.venturisys.rubideliveryapp.others.Extras.isEmpty;
import static ke.co.venturisys.rubideliveryapp.others.Extras.loadPictureToImageView;
import static ke.co.venturisys.rubideliveryapp.others.Extras.requestInternetAccess;
import static ke.co.venturisys.rubideliveryapp.others.Extras.setImageViewDrawableColor;
import static ke.co.venturisys.rubideliveryapp.others.NetworkingClass.isNetworkAvailable;

public class LoginFragment extends Fragment {

    View view;
    CoordinatorLayout coordinatorLayout;
    EditText emailAddress, passWord;
    ImageView cityImageView, emailAddressImageView, passwordImageView,
            googleBtn, facebookBtn;
    Button loginButton;
    TextView createAccount, forgotPassword;
    FirebaseAuth auth;
    ProgressBar progressBar;

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

        // Get Fire-base auth instance
        auth = FirebaseAuth.getInstance();

        // check if there is user already logged in and go directly to main if true
        if (auth.getCurrentUser() != null) {
            exitToTargetActivity((AppCompatActivity) getActivity(), MainActivity.class);
        }

        // initialise widgets
        coordinatorLayout = view.findViewById(R.id.login_parent_layout);
        emailAddress = view.findViewById(R.id.login_email_address);
        passWord = view.findViewById(R.id.login_password);
        cityImageView = view.findViewById(R.id.city_image_view);
        emailAddressImageView = view.findViewById(R.id.email_address_image_view);
        passwordImageView = view.findViewById(R.id.password_image_view);
        loginButton = view.findViewById(R.id.login_button);
        createAccount = view.findViewById(R.id.btnCreateAccount);
        forgotPassword = view.findViewById(R.id.btnForgotPassword);
        googleBtn = view.findViewById(R.id.google_plus_btn);
        facebookBtn = view.findViewById(R.id.facebook_btn);
        progressBar = view.findViewById(R.id.progressBar);

        // enter app if successful and show warning if not
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitLoginForm();
            }
        });

        // redirect to reset password
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(new ResetPasswordFragment(), new Handler(), TAG_RESET_PASSWORD,
                        (AppCompatActivity) getActivity());
            }
        });

        // redirect to account creation
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(RegistrationFragment.newInstance(), new Handler(), TAG_REGISTRATION,
                        (AppCompatActivity) getActivity());
            }
        });

        // set color to email address icon
        setImageViewDrawableColor(emailAddressImageView.getDrawable(), Color.WHITE);

        // set image of city to login page
        HashMap<String, Object> src = new HashMap<>();
        src.put(RES_ID, R.drawable.ic_nairobi);
        loadPictureToImageView(src, R.drawable.ic_nairobi, cityImageView, false, false,
                false, false);

        return view;
    }

    private void submitLoginForm() {
        assert getActivity() != null;
        if (isNetworkAvailable(getActivity())) {
            String password = passWord.getText().toString().trim(),
                    email = emailAddress.getText().toString().trim();
            if (isEmpty(emailAddress)) emailAddress.setError("Email address required");
            else if (!isEmailValid(emailAddress.getText().toString()))
                emailAddress.setError("Invalid email entered");
            else if (isEmpty(passWord)) passWord.setError("Password required");
            else if (password.length() < 8)
                passWord.setError(getString(R.string.minimum_password));
            else {
                progressBar.setVisibility(View.VISIBLE);

                //authenticate user
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                progressBar.setVisibility(View.GONE);
                                if (!task.isSuccessful()) {
                                    // there was an error
                                    Toast.makeText(getActivity(), getString(R.string.auth_failed),
                                            Toast.LENGTH_SHORT).show();
                                    try {
                                        if (Objects.requireNonNull(task.getException()).toString().contains("InvalidCredentialsException"))
                                            passWord.setError("Invalid password");
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                    try {
                                        if (Objects.requireNonNull(task.getException()).toString().contains("InvalidUserException"))
                                            emailAddress.setError("Please enter registered email address");
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                    Log.e(ERROR, "Error message" + task.getException());

                                } else {
                                    Toast.makeText(getActivity(), "Welcome!", Toast.LENGTH_SHORT).show();
                                    exitToTargetActivity((AppCompatActivity) getActivity(), MainActivity.class);
                                }
                            }
                        });
            }
        } else requestInternetAccess(coordinatorLayout, getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
}
