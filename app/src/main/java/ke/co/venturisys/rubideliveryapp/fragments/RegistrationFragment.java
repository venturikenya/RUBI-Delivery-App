package ke.co.venturisys.rubideliveryapp.fragments;


import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
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

import ke.co.venturisys.rubideliveryapp.R;
import ke.co.venturisys.rubideliveryapp.database.helpers.SignBaseHelper;
import ke.co.venturisys.rubideliveryapp.database.schemas.CartDbSchema.CartTable;

import static ke.co.venturisys.rubideliveryapp.others.Constants.ERROR;
import static ke.co.venturisys.rubideliveryapp.others.Constants.RES_ID;
import static ke.co.venturisys.rubideliveryapp.others.Constants.TAG;
import static ke.co.venturisys.rubideliveryapp.others.Constants.TAG_EDIT_PROFILE;
import static ke.co.venturisys.rubideliveryapp.others.Constants.TAG_LOGIN;
import static ke.co.venturisys.rubideliveryapp.others.Extras.changeFragment;
import static ke.co.venturisys.rubideliveryapp.others.Extras.getContentValues;
import static ke.co.venturisys.rubideliveryapp.others.Extras.isEmailValid;
import static ke.co.venturisys.rubideliveryapp.others.Extras.isEmpty;
import static ke.co.venturisys.rubideliveryapp.others.Extras.loadPictureToImageView;
import static ke.co.venturisys.rubideliveryapp.others.Extras.requestInternetAccess;
import static ke.co.venturisys.rubideliveryapp.others.Extras.setImageViewDrawableColor;
import static ke.co.venturisys.rubideliveryapp.others.NetworkingClass.isNetworkAvailable;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegistrationFragment extends Fragment {

    View view;
    CoordinatorLayout coordinatorLayout;
    EditText firstName, lastName, passWord, emailAddress;
    ImageView cityImageView, firstNameImageView, lastNameImageView, passwordImageView, emailAddressImageView,
            googleBtn, facebookBtn;
    Button registerButton;
    TextView loginLink;
    ProgressBar progressBar;
    FirebaseAuth auth;
    SQLiteDatabase mDatabase;

    public RegistrationFragment() {
    }

    public static RegistrationFragment newInstance() {

        Bundle args = new Bundle();

        RegistrationFragment fragment = new RegistrationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_registration, container, false);
        assert getActivity() != null;

        // Get Fire-base auth instance
        auth = FirebaseAuth.getInstance();

        // initialise widgets
        coordinatorLayout = view.findViewById(R.id.register_parent_layout);
        firstName = view.findViewById(R.id.register_first_name);
        lastName = view.findViewById(R.id.register_last_name);
        passWord = view.findViewById(R.id.register_password);
        emailAddress = view.findViewById(R.id.register_email_address);
        cityImageView = view.findViewById(R.id.city_image_view);
        firstNameImageView = view.findViewById(R.id.first_name_image_view);
        lastNameImageView = view.findViewById(R.id.last_name_image_view);
        emailAddressImageView = view.findViewById(R.id.email_address_image_view);
        passwordImageView = view.findViewById(R.id.password_image_view);
        registerButton = view.findViewById(R.id.register_button);
        googleBtn = view.findViewById(R.id.google_plus_btn);
        facebookBtn = view.findViewById(R.id.facebook_btn);
        progressBar = view.findViewById(R.id.progressBar);
        loginLink = view.findViewById(R.id.link_to_login);
        mDatabase = new SignBaseHelper(getActivity()).getWritableDatabase();

        // redirect to edit profile page if successful and show warning if not
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitRegistrationForm();
            }
        });

        // redirect to logging in
        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(LoginFragment.newInstance(), new Handler(), TAG_LOGIN,
                        (AppCompatActivity) getActivity());
            }
        });

        // set color to username, email & progress bar icons
        setImageViewDrawableColor(firstNameImageView.getDrawable(), Color.WHITE);
        setImageViewDrawableColor(lastNameImageView.getDrawable(), Color.WHITE);
        setImageViewDrawableColor(emailAddressImageView.getDrawable(), Color.WHITE);

        // set image of city to login page
        HashMap<String, Object> src = new HashMap<>();
        src.put(RES_ID, R.drawable.ic_nairobi);
        loadPictureToImageView(src, R.drawable.ic_nairobi, cityImageView, false, false,
                false, false);

        return view;
    }

    private void submitRegistrationForm() {
        assert getActivity() != null;
        if (isNetworkAvailable(getActivity())) {
            String password = passWord.getText().toString().trim();
            if (isEmpty(firstName)) firstName.setError("First name required");
            else if (isEmpty(lastName)) lastName.setError("Last name required");
            else if (isEmpty(emailAddress)) emailAddress.setError("Email address required");
            else if (!isEmailValid(emailAddress.getText().toString()))
                emailAddress.setError("Invalid email entered");
            else if (isEmpty(passWord)) passWord.setError("Password required");
            else if (password.length() < 8)
                passWord.setError(getString(R.string.minimum_password));
            else {
                final String first_name = firstName.getText().toString().trim(),
                        last_name = lastName.getText().toString().trim();
                final String name = first_name + " " + last_name;
                final String email = emailAddress.getText().toString().trim();
                progressBar.setVisibility(View.VISIBLE);
                // create user
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                                progressBar.setVisibility(View.GONE);
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(getActivity(), getString(R.string.auth_failed),
                                            Toast.LENGTH_SHORT).show();
                                    Log.e(ERROR, "Error message" + task.getException());
                                } else {
                                    // save user's details to SQLite database
                                    ContentValues values = getContentValues(email, name);
                                    mDatabase.insert(CartTable.NAME, null, values);
                                    changeFragment(EditProfileFragment.newInstance(false,
                                            first_name, last_name, "", email, "",
                                            "", ""),
                                            new Handler(),
                                            TAG_EDIT_PROFILE, (AppCompatActivity) getActivity());
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
