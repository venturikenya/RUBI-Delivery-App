package ke.co.venturisys.rubideliveryapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Objects;

import javax.annotation.Nonnull;

import ke.co.venturisys.rubideliveryapp.FindCustomerQuery;
import ke.co.venturisys.rubideliveryapp.R;
import ke.co.venturisys.rubideliveryapp.activities.ProfileImageActivity;
import ke.co.venturisys.rubideliveryapp.others.MyApolloClient;

import static ke.co.venturisys.rubideliveryapp.activities.MainActivity.setCurrentTag;
import static ke.co.venturisys.rubideliveryapp.activities.MainActivity.setNavItemIndex;
import static ke.co.venturisys.rubideliveryapp.others.Constants.ERROR;
import static ke.co.venturisys.rubideliveryapp.others.Constants.TAG_EDIT_PROFILE;
import static ke.co.venturisys.rubideliveryapp.others.Constants.TAG_HOME;
import static ke.co.venturisys.rubideliveryapp.others.Constants.URL;
import static ke.co.venturisys.rubideliveryapp.others.Extras.changeFragment;
import static ke.co.venturisys.rubideliveryapp.others.Extras.loadPictureToImageView;
import static ke.co.venturisys.rubideliveryapp.others.Extras.requestInternetAccess;
import static ke.co.venturisys.rubideliveryapp.others.Extras.setImageViewDrawableColor;
import static ke.co.venturisys.rubideliveryapp.others.Extras.setTextViewDrawableColor;
import static ke.co.venturisys.rubideliveryapp.others.NetworkingClass.isNetworkAvailable;

public class ProfileFragment extends GeneralFragment {

    TextView textViewLocation, textViewPhone, textViewEmail, textViewName, textViewDetails;
    ImageView imageViewProfile, imageViewHome, imageViewEdit;
    CoordinatorLayout coordinatorLayoutProfile;
    ProgressBar progressBar; // shown while user info is being loaded
    FirebaseAuth auth;
    FirebaseUser user;
    String first_name, last_name;

    String photo_url;

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

        // get current user
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

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
        progressBar = view.findViewById(R.id.progressBar);
        setImageViewDrawableColor(progressBar.getIndeterminateDrawable(), getResources()
                .getColor(R.color.colorWeb));

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
                        first_name, last_name, textViewDetails.getText().toString(),
                        textViewEmail.getText().toString(), textViewPhone.getText().toString(),
                        textViewLocation.getText().toString(), photo_url),
                        new Handler(),
                        TAG_EDIT_PROFILE, (AppCompatActivity) getActivity());
            }
        });

        // if user clicks on profile image, blow it up to full scale
        imageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = user.getEmail();
                Intent intent = ProfileImageActivity.newIntent(getActivity(), photo_url, email);
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


        if (isNetworkAvailable(Objects.requireNonNull(getActivity()))) {
            updateProfile();
        } else requestInternetAccess(coordinatorLayoutProfile, getActivity());

        return view;
    }

    /*
     * This method queries the Customer table and returns all the details of the user
     * Please ensure that the user is logged in and there is internet connection
     */
    private void updateProfile() {
        assert getActivity() != null;
        if (user != null) {
            MyApolloClient.getMyApolloClient().query(
                    FindCustomerQuery.builder().email(Objects.requireNonNull(user.getEmail())).build()
            ).enqueue(new ApolloCall.Callback<FindCustomerQuery.Data>() {
                @Override
                public void onResponse(@Nonnull final Response<FindCustomerQuery.Data> response) {
                    try {
                        // update UI on activity's thread
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                // hide progress bar
                                progressBar.setVisibility(View.GONE);

                                FindCustomerQuery.Node node = Objects.requireNonNull(
                                        Objects.requireNonNull(response.data()).allCustomers()).
                                        edges().get(0).node();

                                if (node != null) {
                                    // update text views
                                    first_name = node.firstName();
                                    last_name = node.lastName();
                                    textViewName.setText(first_name.concat(" " + last_name));
                                    textViewDetails.setText(node.description());
                                    textViewLocation.setText(node.location());
                                    textViewPhone.setText(node.mobileContact());
                                    textViewEmail.setText(node.email());

                                    // load profile image
                                    photo_url = node.profilePicture();
                                    HashMap<String, Object> src = new HashMap<>();
                                    src.put(URL, photo_url);
                                    loadPictureToImageView(src, R.mipmap.ic_box, imageViewProfile, true, false,
                                            false, true);
                                }
                            }
                        });
                    } catch (Exception ex) {
                        Log.e(ERROR, "Something went wrong, " + ex.getMessage());
                        ex.printStackTrace();
                    }
                }

                @Override
                public void onFailure(@Nonnull ApolloException e) {
                    // Log error so as to fix it
                    Log.e(ERROR, "onFailure: Something went wrong. " + e.getMessage());
                }
            });
        }
    }
}
