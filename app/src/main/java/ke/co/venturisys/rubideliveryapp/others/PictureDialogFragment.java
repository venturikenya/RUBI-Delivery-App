package ke.co.venturisys.rubideliveryapp.others;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.View;
import android.widget.Button;

import java.io.File;

import ke.co.venturisys.rubideliveryapp.R;

import static ke.co.venturisys.rubideliveryapp.others.Constants.ARG_PATH;
import static ke.co.venturisys.rubideliveryapp.others.Constants.TAG_EDIT_PROFILE;
import static ke.co.venturisys.rubideliveryapp.others.Extras.cameraIntent;
import static ke.co.venturisys.rubideliveryapp.others.Extras.galleryIntent;

public class PictureDialogFragment extends GeneralDialogFragment {

    File directory;

    public PictureDialogFragment() {
        super.alertDialogLayout = R.layout.dialog_picture_picker;
    }

    public static PictureDialogFragment newInstance(File directory) {

        Bundle args = new Bundle();
        args.putSerializable(ARG_PATH, directory);

        PictureDialogFragment fragment = new PictureDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            directory = (File) getArguments().getSerializable(ARG_PATH);
        }
    }

    @Override
    protected void initializeWidgets(View view) {
        setAlertDialogTitle(Html.fromHtml("<font color='" + getResources().getColor(R.color.colorDialogTitle)
                + "'>" + getString(R.string.picture_dialog_title) + "</font>"));
        Button cameraButton = view.findViewById(R.id.photo_camera_button),
                galleryButton = view.findViewById(R.id.photo_gallery_button),
                closeButton = view.findViewById(R.id.photo_close_button);

        // open camera
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitFragment();
                cameraIntent(activity, TAG_EDIT_PROFILE, directory);
            }
        });

        // open gallery
        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitFragment();
                galleryIntent(activity, TAG_EDIT_PROFILE);
            }
        });

        // close fragment
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitFragment();
            }
        });
    }
}
