package ke.co.venturisys.rubideliveryapp.others;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import javax.annotation.Nonnull;

import ke.co.venturisys.rubideliveryapp.AddCustomerMutation;
import ke.co.venturisys.rubideliveryapp.R;
import ke.co.venturisys.rubideliveryapp.activities.MainActivity;

import static ke.co.venturisys.rubideliveryapp.others.Constants.ARG_DETAILS;
import static ke.co.venturisys.rubideliveryapp.others.Constants.ARG_EMAIL;
import static ke.co.venturisys.rubideliveryapp.others.Constants.ARG_LOCATION;
import static ke.co.venturisys.rubideliveryapp.others.Constants.ARG_NAME;
import static ke.co.venturisys.rubideliveryapp.others.Constants.ARG_PHONE_NUMBER;
import static ke.co.venturisys.rubideliveryapp.others.Constants.ARG_PHOTO_URL;
import static ke.co.venturisys.rubideliveryapp.others.Constants.ERROR;
import static ke.co.venturisys.rubideliveryapp.others.Extras.exitToTargetActivity;
import static ke.co.venturisys.rubideliveryapp.others.Extras.requestInternetAccess;
import static ke.co.venturisys.rubideliveryapp.others.NetworkingClass.isNetworkAvailable;

/**
 * Created by victor on 3/31/18.
 * This class extends Dialog Fragment
 * It is used to enable a user to confirm phone number to be saved in system
 * and that'll receive the SMS verification code
 */

public class SMSVerifyDialogFragment extends GeneralDialogFragment {

    String phoneNumber;
    String name, email, location, photo_url, details;

    public SMSVerifyDialogFragment() {
        super.alertDialogLayout = R.layout.dialog_number_verification;
    }

    public static SMSVerifyDialogFragment newInstance(String phoneNumber,
                                                      String name,
                                                      String email,
                                                      String location,
                                                      String photo_url,
                                                      String details) {

        Bundle args = new Bundle();
        args.putString(ARG_PHONE_NUMBER, phoneNumber);
        args.putString(ARG_NAME, name);
        args.putString(ARG_EMAIL, email);
        args.putString(ARG_LOCATION, location);
        args.putString(ARG_PHOTO_URL, photo_url);
        args.putString(ARG_DETAILS, details);

        SMSVerifyDialogFragment fragment = new SMSVerifyDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();

        // retrieve phone number
        if (args != null) {
            phoneNumber = args.getString(ARG_PHONE_NUMBER);
            name = args.getString(ARG_NAME);
            email = args.getString(ARG_EMAIL);
            location = args.getString(ARG_LOCATION);
            photo_url = args.getString(ARG_PHOTO_URL);
            details = args.getString(ARG_DETAILS);
        }
    }

    @Override
    protected void initializeWidgets(View view) {
        assert getActivity() != null;
        setAlertDialogTitle(Html.fromHtml("<font color='" + getResources().getColor(R.color.colorDialogTitle)
                + "'>" + phoneNumber + "</font>"));
        Button cancelButton = view.findViewById(R.id.verify_cancel_button);
        Button sendButton = view.findViewById(R.id.verify_send_button);
        final CoordinatorLayout coordinatorLayout = view.findViewById(R.id.dialog_parent);

        // start process of sending verification sms
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable(getActivity())) {
                    // register user by mutating(post) user's info to GraphQL server
                    MyApolloClient.getMyApolloClient().mutate(
                            AddCustomerMutation.builder()
                                    .name(name)
                                    .email(email)
                                    .phone(phoneNumber)
                                    .details(details)
                                    .location(location)
                                    .image(photo_url)
                                    .build()
                    ).enqueue(new ApolloCall.Callback<AddCustomerMutation.Data>() {
                        @Override
                        public void onResponse(@Nonnull Response<AddCustomerMutation.Data> response) {
                            // run on UI thread to update data instantaneously
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), "Profile created/edited successfully", Toast.LENGTH_SHORT).show();
                                    // redirect user to main activity
                                    exitToTargetActivity((AppCompatActivity) getActivity(), MainActivity.class);
                                }
                            });
                        }

                        @Override
                        public void onFailure(@Nonnull ApolloException e) {
                            // Log error so as to fix it
                            Log.e(ERROR, "onFailure: Something went wrong. " + e.getMessage());
                        }
                    });
                } else {
                    exitFragment();
                    requestInternetAccess(coordinatorLayout, getActivity());
                }
            }
        });

        // close fragment
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitFragment();
            }
        });
    }
}
