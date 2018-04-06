package ke.co.venturisys.rubideliveryapp.fragments;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import ke.co.venturisys.rubideliveryapp.R;

import static ke.co.venturisys.rubideliveryapp.others.Constants.TAG_LOGIN;
import static ke.co.venturisys.rubideliveryapp.others.Extras.changeFragment;
import static ke.co.venturisys.rubideliveryapp.others.Extras.isEmailValid;
import static ke.co.venturisys.rubideliveryapp.others.Extras.isEmpty;
import static ke.co.venturisys.rubideliveryapp.others.Extras.requestInternetAccess;
import static ke.co.venturisys.rubideliveryapp.others.Extras.setImageViewDrawableColor;
import static ke.co.venturisys.rubideliveryapp.others.NetworkingClass.isNetworkAvailable;

/**
 * A simple {@link Fragment} subclass.
 */
public class ResetPasswordFragment extends Fragment {

    View view;
    CoordinatorLayout coordinatorLayout;
    EditText emailAddress;
    Button btnResetPassword;
    ImageView emailAddressImageView;
    Button btnBack;
    ProgressBar progressBar;
    FirebaseAuth auth;

    public ResetPasswordFragment() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_reset_password, container, false);
        assert getActivity() != null;
        auth = FirebaseAuth.getInstance();

        // initialise widgets
        coordinatorLayout = view.findViewById(R.id.reset_parent_layout);
        emailAddress = view.findViewById(R.id.input_email);
        btnResetPassword = view.findViewById(R.id.btn_reset_password);
        emailAddressImageView = view.findViewById(R.id.email_address_image_view);
        btnBack = view.findViewById(R.id.btn_back);
        progressBar = view.findViewById(R.id.progressBar);

        // set color to email address & progress bar icons
        setImageViewDrawableColor(emailAddressImageView.getDrawable(), getResources().getColor(R.color.colorApp));
        setImageViewDrawableColor(progressBar.getIndeterminateDrawable(),
                getResources().getColor(R.color.red));

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(LoginFragment.newInstance(), new Handler(), TAG_LOGIN,
                        (AppCompatActivity) getActivity());
            }
        });

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitResetForm();
            }
        });

        return view;
    }

    private void submitResetForm() {
        assert getActivity() != null;
        if (isNetworkAvailable(getActivity())) {
            if (isEmpty(emailAddress)) emailAddress.setError("Email address required");
            else if (!isEmailValid(emailAddress.getText().toString()))
                emailAddress.setError("Invalid email entered");

            progressBar.setVisibility(View.VISIBLE);
            auth.sendPasswordResetEmail(emailAddress.getText().toString().trim())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity(), getString(R.string.reset_successful), Toast.LENGTH_SHORT).show();
                                changeFragment(LoginFragment.newInstance(), new Handler(), TAG_LOGIN,
                                        (AppCompatActivity) getActivity());
                            } else {
                                Toast.makeText(getContext(), getString(R.string.reset_fail), Toast.LENGTH_SHORT).show();
                            }

                            progressBar.setVisibility(View.GONE);
                        }
                    });
        } else requestInternetAccess(coordinatorLayout, getActivity());
    }

}
