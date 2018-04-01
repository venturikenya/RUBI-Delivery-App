package ke.co.venturisys.rubideliveryapp.fragments;


import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.HashMap;

import ke.co.venturisys.rubideliveryapp.R;

import static ke.co.venturisys.rubideliveryapp.others.Constants.RES_ID;
import static ke.co.venturisys.rubideliveryapp.others.Constants.TAG_EDIT_PROFILE;
import static ke.co.venturisys.rubideliveryapp.others.Extras.changeFragment;
import static ke.co.venturisys.rubideliveryapp.others.Extras.isEmailValid;
import static ke.co.venturisys.rubideliveryapp.others.Extras.isEmpty;
import static ke.co.venturisys.rubideliveryapp.others.Extras.loadPictureToImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegistrationFragment extends Fragment {

    View view;
    EditText userName, passWord, emailAddress;
    ImageView cityImageView, userNameImageView, passwordImageView, emailAddressImageView,
            googleBtn, facebookBtn;
    Button registerButton;

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

        // initialise widgets
        userName = view.findViewById(R.id.register_username);
        passWord = view.findViewById(R.id.register_password);
        emailAddress = view.findViewById(R.id.register_email_address);
        cityImageView = view.findViewById(R.id.city_image_view);
        userNameImageView = view.findViewById(R.id.username_image_view);
        emailAddressImageView = view.findViewById(R.id.email_address_image_view);
        passwordImageView = view.findViewById(R.id.password_image_view);
        registerButton = view.findViewById(R.id.register_button);
        googleBtn = view.findViewById(R.id.google_plus_btn);
        facebookBtn = view.findViewById(R.id.facebook_btn);

        // redirect to edit profile page if successful and show warning if not
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitRegistrationForm();
            }
        });

        // set color to username & email icons
        userNameImageView.getDrawable().setColorFilter(new PorterDuffColorFilter
                (Color.WHITE, PorterDuff.Mode.SRC_IN));
        emailAddressImageView.getDrawable().setColorFilter(new PorterDuffColorFilter
                (Color.WHITE, PorterDuff.Mode.SRC_IN));

        // set image of city to login page
        HashMap<String, Object> src = new HashMap<>();
        src.put(RES_ID, R.drawable.ic_nairobi);
        loadPictureToImageView(src, R.drawable.ic_nairobi, cityImageView, false, false,
                false, false);

        return view;
    }

    private void submitRegistrationForm() {
        if (isEmpty(userName)) userName.setError("User name required");
        else if (isEmpty(emailAddress)) emailAddress.setError("Email address required");
        else if (!isEmailValid(emailAddress.getText().toString()))
            emailAddress.setError("Invalid email entered");
        else if (isEmpty(passWord)) passWord.setError("Password required");
        else changeFragment(new EditProfileFragment(), new Handler(),
                    TAG_EDIT_PROFILE, (AppCompatActivity) getActivity());
    }
}
