package ke.co.venturisys.rubideliveryapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;

import ke.co.venturisys.rubideliveryapp.R;
import ke.co.venturisys.rubideliveryapp.activities.ProfileImageActivity;

import static ke.co.venturisys.rubideliveryapp.activities.MainActivity.setCurrentTag;
import static ke.co.venturisys.rubideliveryapp.activities.MainActivity.setNavItemIndex;
import static ke.co.venturisys.rubideliveryapp.others.Constants.TAG_EDIT_PROFILE;
import static ke.co.venturisys.rubideliveryapp.others.Constants.TAG_HOME;
import static ke.co.venturisys.rubideliveryapp.others.Constants.URL;
import static ke.co.venturisys.rubideliveryapp.others.Extras.changeFragment;
import static ke.co.venturisys.rubideliveryapp.others.Extras.loadPictureToImageView;
import static ke.co.venturisys.rubideliveryapp.others.Extras.requestInternetAccess;
import static ke.co.venturisys.rubideliveryapp.others.Extras.setTextViewDrawableColor;
import static ke.co.venturisys.rubideliveryapp.others.URLs.urlProfileImg;

public class ProfileFragment extends GeneralFragment {

    TextView textViewLocation, textViewPhone, textViewEmail, textViewName, textViewDetails;
    ImageView imageViewProfile, imageViewHome, imageViewEdit;
    CoordinatorLayout coordinatorLayoutProfile;

    public ProfileFragment() {
    }

    public static ProfileFragment newInstance() {

        Bundle args = new Bundle();

        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // inflate layout
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        assert getActivity() != null;

        // initialise widgets
        textViewLocation = view.findViewById(R.id.tvProfileLocation);
        textViewPhone = view.findViewById(R.id.tvProfileContacts);
        textViewEmail = view.findViewById(R.id.tvProfileEmail);
        textViewName = view.findViewById(R.id.tvProfileName);
        textViewDetails = view.findViewById(R.id.tvProfileDetails);
        imageViewProfile = view.findViewById(R.id.img_profile_page);
        imageViewHome = view.findViewById(R.id.btnProfileHome);
        imageViewEdit = view.findViewById(R.id.btnProfileEdit);
        coordinatorLayoutProfile = view.findViewById(R.id.profile_coordinator_layout);

        requestInternetAccess(coordinatorLayoutProfile, getActivity());

        // redirect to homepage
        imageViewHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNavItemIndex(0);
                setCurrentTag(TAG_HOME);
                changeFragment(HomeFragment.newInstance(), new Handler(), TAG_HOME,
                        (AppCompatActivity) getActivity());
            }
        });

        // redirect to edit profile page
        imageViewEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(EditProfileFragment.newInstance(true,
                        textViewName.getText().toString(), textViewDetails.getText().toString(),
                        textViewEmail.getText().toString(), textViewPhone.getText().toString(),
                        textViewLocation.getText().toString()),
                        new Handler(),
                        TAG_EDIT_PROFILE, (AppCompatActivity) getActivity());
            }
        });

        // load profile image
        HashMap<String, Object> src = new HashMap<>();
        src.put(URL, urlProfileImg);
        loadPictureToImageView(src, R.mipmap.ic_box, imageViewProfile, true, false,
                false, true);

        // if user clicks on profile image, blow it up to full scale
        imageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = ProfileImageActivity.newIntent(getActivity(), urlProfileImg);
                startActivity(intent);
            }
        });

        // set drawables to text views
        textViewLocation.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pin, 0, 0, 0);
        textViewPhone.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_phone, 0, 0, 0);
        textViewEmail.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_envelope, 0, 0, 0);

        // set color to text view drawables
        setTextViewDrawableColor(textViewLocation, R.color.colorApp, getActivity());
        setTextViewDrawableColor(textViewPhone, R.color.colorApp, getActivity());
        setTextViewDrawableColor(textViewEmail, R.color.colorApp, getActivity());

        return view;
    }
}
