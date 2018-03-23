package ke.co.venturisys.rubideliveryapp.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;

import ke.co.venturisys.rubideliveryapp.R;

import static ke.co.venturisys.rubideliveryapp.others.Constants.URL;
import static ke.co.venturisys.rubideliveryapp.others.Extras.loadPictureToImageView;
import static ke.co.venturisys.rubideliveryapp.others.Extras.setTextViewDrawableColor;
import static ke.co.venturisys.rubideliveryapp.others.URLs.urlProfileImg;

public class ProfileFragment extends GeneralFragment {

    TextView textViewLocation, textViewPhone, textViewEmail;
    ImageView imageViewProfile;

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
        imageViewProfile = view.findViewById(R.id.img_profile_page);

        // load profile image
        HashMap<String, Object> src = new HashMap<>();
        src.put(URL, urlProfileImg);
        loadPictureToImageView(src, R.mipmap.ic_box, imageViewProfile, true, false, false);

        // set drawables to text views
        textViewLocation.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pin,0,0,0);
        textViewPhone.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_phone,0,0,0);
        textViewEmail.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_envelope,0,0,0);

        // set color to text view drawables
        setTextViewDrawableColor(textViewLocation, R.color.colorApp, getActivity());
        setTextViewDrawableColor(textViewPhone, R.color.colorApp, getActivity());
        setTextViewDrawableColor(textViewEmail, R.color.colorApp, getActivity());

        return view;
    }
}
