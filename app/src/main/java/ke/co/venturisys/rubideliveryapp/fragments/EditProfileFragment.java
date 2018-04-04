package ke.co.venturisys.rubideliveryapp.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import net.rimoto.intlphoneinput.IntlPhoneInput;

import java.io.File;
import java.io.IOException;

import ke.co.venturisys.rubideliveryapp.R;
import ke.co.venturisys.rubideliveryapp.others.PictureDialogFragment;
import ke.co.venturisys.rubideliveryapp.others.SMSVerifyDialogFragment;

import static ke.co.venturisys.rubideliveryapp.others.Constants.ARG_DETAILS;
import static ke.co.venturisys.rubideliveryapp.others.Constants.ARG_EMAIL;
import static ke.co.venturisys.rubideliveryapp.others.Constants.ARG_LOCATION;
import static ke.co.venturisys.rubideliveryapp.others.Constants.ARG_NAME;
import static ke.co.venturisys.rubideliveryapp.others.Constants.ARG_PHONE_NUMBER;
import static ke.co.venturisys.rubideliveryapp.others.Constants.ARG_SHOW_FIELDS;
import static ke.co.venturisys.rubideliveryapp.others.Constants.PERMISSION_CAMERA;
import static ke.co.venturisys.rubideliveryapp.others.Constants.PERMISSION_STORAGE;
import static ke.co.venturisys.rubideliveryapp.others.Constants.REQUEST_GALLERY;
import static ke.co.venturisys.rubideliveryapp.others.Constants.REQUEST_PHOTO;
import static ke.co.venturisys.rubideliveryapp.others.Constants.SELECT_PICTURE;
import static ke.co.venturisys.rubideliveryapp.others.Constants.TAG;
import static ke.co.venturisys.rubideliveryapp.others.Constants.VERIFY_SMS;
import static ke.co.venturisys.rubideliveryapp.others.Extras.isEmailValid;
import static ke.co.venturisys.rubideliveryapp.others.Extras.isEmpty;
import static ke.co.venturisys.rubideliveryapp.others.Extras.setImageViewDrawableColor;
import static ke.co.venturisys.rubideliveryapp.others.FileUtilities.createSystemDirs;
import static ke.co.venturisys.rubideliveryapp.others.Permissions.checkPermission;
import static ke.co.venturisys.rubideliveryapp.others.PictureUtilities.getImageUri;
import static ke.co.venturisys.rubideliveryapp.others.PictureUtilities.getRealPathFromURI;
import static ke.co.venturisys.rubideliveryapp.others.PictureUtilities.recogniseFace;

public class EditProfileFragment extends Fragment {

    public static Uri imageForUpload;
    View view;
    RelativeLayout nameLayout, emailLayout;
    TextInputLayout phoneInputLayout;
    IntlPhoneInput phoneInputView;
    EditText editLocation, editDetails, editName, editEmail;
    ImageView imageViewProfile, locationImage, detailsImage, emailImage;
    Button submitButton;
    String myInternationalNumber = "";
    boolean showEmailAndNameFields;
    File directory;
    String name, details, location, email, mCurrentPath;
    Bitmap photo = null;

    public static EditProfileFragment newInstance(boolean showEmailAndNameFields,
                                                  String name, String details, String email,
                                                  String myInternationalNumber, String location) {

        Bundle args = new Bundle();
        args.putBoolean(ARG_SHOW_FIELDS, showEmailAndNameFields);
        args.putString(ARG_NAME, name);
        args.putString(ARG_DETAILS, details);
        args.putString(ARG_EMAIL, email);
        args.putString(ARG_PHONE_NUMBER, myInternationalNumber);
        args.putString(ARG_LOCATION, location);

        EditProfileFragment fragment = new EditProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        assert getActivity() != null;

        // request writing permission
        checkPermission(getActivity(), getActivity(), PERMISSION_STORAGE);

        // check if device has external storage memory
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            // if no external memory, log error
            Log.e("FOLDER CREATION ERROR", "No SDCARD");
        } else {
            // create directory for saving images
            directory = createSystemDirs(getActivity()).get(0);
            Log.e("Directory", "" + directory.getAbsolutePath());
        }

        // retrieve arguments passed to fragment
        if (bundle != null) {
            showEmailAndNameFields = bundle.getBoolean(ARG_SHOW_FIELDS, false);
            name = bundle.getString(ARG_NAME);
            details = bundle.getString(ARG_DETAILS);
            email = bundle.getString(ARG_EMAIL);
            myInternationalNumber = bundle.getString(ARG_PHONE_NUMBER);
            location = bundle.getString(ARG_LOCATION);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.edit_profile, container, false);
        assert getActivity() != null;

        // initialise widgets
        nameLayout = view.findViewById(R.id.name_relative_layout);
        emailLayout = view.findViewById(R.id.email_relative_layout);
        phoneInputView = view.findViewById(R.id.input_phone_number);
        phoneInputLayout = view.findViewById(R.id.input_layout_phone_number);
        editName = view.findViewById(R.id.input_name);
        editEmail = view.findViewById(R.id.input_email);
        editLocation = view.findViewById(R.id.input_location);
        editDetails = view.findViewById(R.id.input_details);
        imageViewProfile = view.findViewById(R.id.img_profile_page);
        locationImage = view.findViewById(R.id.location_image_view);
        detailsImage = view.findViewById(R.id.details_image_view);
        emailImage = view.findViewById(R.id.email_image_view);
        submitButton = view.findViewById(R.id.submit_button);

        imageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check camera permission
                checkPermission(getActivity(), getActivity(), PERMISSION_CAMERA);
                FragmentManager fm = getFragmentManager();
                assert fm != null;
                PictureDialogFragment dialog = PictureDialogFragment.newInstance(directory);
                dialog.show(fm, SELECT_PICTURE);
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitRegistrationForm();
            }
        });

        // set color of editLocation, email and editDetails image views
        setImageViewDrawableColor(locationImage.getDrawable(), Color.WHITE);
        setImageViewDrawableColor(emailImage.getDrawable(), Color.WHITE);
        setImageViewDrawableColor(detailsImage.getDrawable(), Color.WHITE);

        // determine whether to show name and email text fields, only for editing from profile page
        if (showEmailAndNameFields) {
            nameLayout.setVisibility(View.VISIBLE);
            emailLayout.setVisibility(View.VISIBLE);
        }

        // set values to edit text if there are any
        editName.setText(name);
        editEmail.setText(email);
        editDetails.setText(details);
        editLocation.setText(location);
        phoneInputView.setNumber(myInternationalNumber);

        assert getActivity() != null;
        TelephonyManager tm = (TelephonyManager)
                getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        assert tm != null;
        String countryCode = tm.getSimCountryIso();
        phoneInputView.setEmptyDefault(countryCode);

        phoneInputView.setOnValidityChange(new IntlPhoneInput.IntlPhoneInputListener() {
            @Override
            public void done(View view, boolean isValid) {
                myInternationalNumber = phoneInputView.getNumber();
            }
        });

        return view;
    }

    private void submitRegistrationForm() {
        if (showEmailAndNameFields && isEmpty(editName)) editName.setError("Name required");
        else if (showEmailAndNameFields && isEmpty(editEmail))
            editEmail.setError("Email address required");
        else if (showEmailAndNameFields && !isEmailValid(editEmail.getText().toString()))
            editEmail.setError("Invalid email entered");
        else if (isEmpty(editLocation)) editLocation.setError("Location required");
        else if (myInternationalNumber.length() == 0 || myInternationalNumber.equals(""))
            Toast.makeText(getActivity(), "Phone number required", Toast.LENGTH_SHORT).show();
        else if (isEmpty(editDetails)) editDetails.setError("Say something about yourself");
        else if (myInternationalNumber.length() != 13 || !phoneInputView.isValid())
            Toast.makeText(getActivity(), "Invalid phone number", Toast.LENGTH_SHORT).show();
            // if every thing is correct, open dialog for user to confirm number to receive SMS code
        else {
            FragmentManager fm = getFragmentManager();
            SMSVerifyDialogFragment dialog = SMSVerifyDialogFragment.newInstance(myInternationalNumber);
            assert fm != null;
            dialog.show(fm, VERIFY_SMS);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("image", photo);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            photo = savedInstanceState.getParcelable("image");
            imageViewProfile.setImageBitmap(photo);
        }
    }

    /**
     * Method retrieves image sent by return intent as small Bitmap in extras with data as key
     * and displays to ImageView
     *
     * @param requestCode request that was received from take picture
     * @param resultCode  result of operation
     * @param data        return intent
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        assert getActivity() != null;

        if (resultCode == Activity.RESULT_OK) {
            try {
                switch (requestCode) {
                    case REQUEST_PHOTO:
                        if (imageForUpload != null) {
                            photo = recogniseFace(imageForUpload, imageViewProfile, getActivity());
                            // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                            Uri tempUri = getImageUri(getActivity(), photo);
                            // CALL THIS METHOD TO GET THE ACTUAL PATH
                            mCurrentPath = getRealPathFromURI(tempUri, getActivity());
                            Log.e(TAG, mCurrentPath);
                        } else {
                            Toast.makeText(getActivity(), "Error2 while capturing image", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case REQUEST_GALLERY:
                        if (data != null) {
                            Uri uri = data.getData();

                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                                Log.d(TAG, String.valueOf(bitmap));
                                imageViewProfile.setImageBitmap(bitmap);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                }
            } catch (Exception ex) {
                Log.e("BITMAP error", ex.getMessage());
            }
        }
    }
}
