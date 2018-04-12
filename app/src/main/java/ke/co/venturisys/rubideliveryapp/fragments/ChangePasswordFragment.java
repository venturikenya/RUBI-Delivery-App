package ke.co.venturisys.rubideliveryapp.fragments;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ke.co.venturisys.rubideliveryapp.R;
import ke.co.venturisys.rubideliveryapp.activities.LoginActivity;

import static ke.co.venturisys.rubideliveryapp.others.Constants.TAG_HOME;
import static ke.co.venturisys.rubideliveryapp.others.Extras.changeFragment;
import static ke.co.venturisys.rubideliveryapp.others.Extras.exitToTargetActivity;
import static ke.co.venturisys.rubideliveryapp.others.Extras.isEmpty;
import static ke.co.venturisys.rubideliveryapp.others.Extras.requestInternetAccess;
import static ke.co.venturisys.rubideliveryapp.others.Extras.setImageViewDrawableColor;
import static ke.co.venturisys.rubideliveryapp.others.NetworkingClass.isNetworkAvailable;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChangePasswordFragment extends Fragment {

    View view;
    CoordinatorLayout coordinatorLayout;
    EditText emailAddress;
    TextView instruction;
    Button btnResetPassword;
    ImageView emailAddressImageView;
    Button btnBack;
    ProgressBar progressBar;
    FirebaseAuth auth;
    FirebaseUser user;

    public ChangePasswordFragment() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_reset_password, container, false);
        assert getActivity() != null;
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        // initialise widgets
        coordinatorLayout = view.findViewById(R.id.reset_parent_layout);
        emailAddress = view.findViewById(R.id.input_email);
        instruction = view.findViewById(R.id.forgot_password_msg);
        btnResetPassword = view.findViewById(R.id.btn_reset_password);
        emailAddressImageView = view.findViewById(R.id.email_address_image_view);
        btnBack = view.findViewById(R.id.btn_back);
        progressBar = view.findViewById(R.id.progressBar);

        // update views to reflect changing password
        instruction.setText(getString(R.string.change_password_msg));
        instruction.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        instruction.setGravity(Gravity.CENTER_HORIZONTAL);
        btnResetPassword.setText(getString(R.string.change_password));
        emailAddress.setTransformationMethod(PasswordTransformationMethod.getInstance());
        emailAddress.setHint(getString(R.string.password_placeholder));
        emailAddressImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_lock_open));

        // set color to email address & progress bar icons
        setImageViewDrawableColor(emailAddressImageView.getDrawable(), getResources().getColor(R.color.colorApp));
        setImageViewDrawableColor(progressBar.getIndeterminateDrawable(),
                getResources().getColor(R.color.red));

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(HomeFragment.newInstance(), new Handler(), TAG_HOME,
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
        if (isNetworkAvailable(getActivity()) && user != null) {
            if (isEmpty(emailAddress)) emailAddress.setError("Email address required");
            else if (emailAddress.getText().toString().trim().length() < 8)
                emailAddress.setError(getString(R.string.minimum_password));

            else {
                progressBar.setVisibility(View.VISIBLE);
                user.updatePassword(emailAddress.getText().toString().trim())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getActivity(), getString(R.string.reset_success), Toast.LENGTH_SHORT).show();
                                    auth.signOut();
                                    exitToTargetActivity((AppCompatActivity) getActivity(), LoginActivity.class);
                                } else {
                                    Toast.makeText(getContext(), getString(R.string.reset_fail), Toast.LENGTH_SHORT).show();
                                }

                                progressBar.setVisibility(View.GONE);
                            }
                        });
            }
        } else requestInternetAccess(coordinatorLayout, getActivity());
    }

}
