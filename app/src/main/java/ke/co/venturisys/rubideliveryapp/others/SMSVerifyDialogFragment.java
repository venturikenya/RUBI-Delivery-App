package ke.co.venturisys.rubideliveryapp.others;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.Button;

import ke.co.venturisys.rubideliveryapp.R;
import ke.co.venturisys.rubideliveryapp.activities.MainActivity;

import static ke.co.venturisys.rubideliveryapp.others.Constants.ARG_PHONE_NUMBER;
import static ke.co.venturisys.rubideliveryapp.others.Extras.exitToTargetActivity;

/**
 * Created by victor on 3/31/18.
 * This class extends Dialog Fragment
 * It is used to enable a user to confirm phone number to be saved in system
 * and that'll receive the SMS verification code
 */

public class SMSVerifyDialogFragment extends GeneralDialogFragment {

    String phoneNumber;

    public SMSVerifyDialogFragment() {
        super.alertDialogLayout = R.layout.dialog_number_verification;
    }

    public static SMSVerifyDialogFragment newInstance(String phoneNumber) {

        Bundle args = new Bundle();
        args.putString(ARG_PHONE_NUMBER, phoneNumber);

        SMSVerifyDialogFragment fragment = new SMSVerifyDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // retrieve phone number
        if (getArguments() != null) phoneNumber = getArguments().getString(ARG_PHONE_NUMBER);
    }

    @Override
    protected void initializeWidgets(View view) {
        setAlertDialogTitle(Html.fromHtml("<font color='" + getResources().getColor(R.color.colorDialogTitle)
                + "'>" + phoneNumber + "</font>"));
        Button cancelButton = view.findViewById(R.id.verify_cancel_button);
        Button sendButton = view.findViewById(R.id.verify_send_button);

        // start process of sending verification sms
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitToTargetActivity((AppCompatActivity) getActivity(), MainActivity.class);
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
