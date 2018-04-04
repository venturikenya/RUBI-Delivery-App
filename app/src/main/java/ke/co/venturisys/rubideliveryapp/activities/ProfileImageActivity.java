package ke.co.venturisys.rubideliveryapp.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.HashMap;

import ke.co.venturisys.rubideliveryapp.R;
import ke.co.venturisys.rubideliveryapp.others.Permissions;
import ke.co.venturisys.rubideliveryapp.others.PictureDialogFragment;

import static ke.co.venturisys.rubideliveryapp.others.Constants.EXTRA_PROFILE_IMAGE_URL;
import static ke.co.venturisys.rubideliveryapp.others.Constants.PERMISSION_CAMERA;
import static ke.co.venturisys.rubideliveryapp.others.Constants.PERMISSION_STORAGE;
import static ke.co.venturisys.rubideliveryapp.others.Constants.REQUEST_GALLERY;
import static ke.co.venturisys.rubideliveryapp.others.Constants.REQUEST_PHOTO;
import static ke.co.venturisys.rubideliveryapp.others.Constants.SELECT_PICTURE;
import static ke.co.venturisys.rubideliveryapp.others.Constants.TAG;
import static ke.co.venturisys.rubideliveryapp.others.Constants.URI;
import static ke.co.venturisys.rubideliveryapp.others.Constants.URL;
import static ke.co.venturisys.rubideliveryapp.others.Extras.exitToTargetActivity;
import static ke.co.venturisys.rubideliveryapp.others.Extras.loadPictureToImageView;
import static ke.co.venturisys.rubideliveryapp.others.FileUtilities.createSystemDirs;
import static ke.co.venturisys.rubideliveryapp.others.PictureUtilities.getImageUri;
import static ke.co.venturisys.rubideliveryapp.others.PictureUtilities.getRealPathFromURI;
import static ke.co.venturisys.rubideliveryapp.others.PictureUtilities.recogniseFace;

public class ProfileImageActivity extends AppCompatActivity {

    public static Uri imageForUpload;
    ImageView profileImageView;
    Button editProfilePhoto;
    FloatingActionButton share;
    File directory;
    Bitmap photo = null;
    String mCurrentPath;

    /*
     * Creates intent configured with extra to receive user's profile image
     */
    public static Intent newIntent(Context packageContext, String imgProfile) {
        Intent intent = new Intent(packageContext, ProfileImageActivity.class);
        intent.putExtra(EXTRA_PROFILE_IMAGE_URL, imgProfile);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_image);
        // receive profile image url from extra
        String imgProfile = getIntent().getStringExtra(EXTRA_PROFILE_IMAGE_URL);

        // request writing permission
        Permissions.checkPermission(this, this, PERMISSION_STORAGE);

        // check if device has external storage memory
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            // if no external memory, log error
            Log.e("FOLDER CREATION ERROR", "No SDCARD");
        } else {
            // create directory for saving images
            directory = createSystemDirs(this).get(0);
            Log.e("Directory", "" + directory.getAbsolutePath());
        }

        assert getSupportActionBar() != null;
        // set up button on toolbar
        final Drawable upArrow = getResources().getDrawable(R.drawable.left_arrow);
        upArrow.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        // initialise widgets
        profileImageView = findViewById(R.id.activity_profile_image_view);
        editProfilePhoto = findViewById(R.id.edit_profile_photo);
        share = findViewById(R.id.share_button);
        // set profile image to image view
        HashMap<String, Object> src = new HashMap<>();
        src.put(URL, imgProfile);
        loadPictureToImageView(src, R.drawable.avatar, profileImageView, true, false,
                false, false);

        // change profile image
        editProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check camera permission
                Permissions.checkPermission(ProfileImageActivity.this, ProfileImageActivity.this, PERMISSION_CAMERA);
                FragmentManager fm = getSupportFragmentManager();
                assert fm != null;
                PictureDialogFragment dialog = PictureDialogFragment.newInstance(directory);
                dialog.show(fm, SELECT_PICTURE);
            }
        });

        // share image set to image view
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bm = convertImageViewToBitmap(profileImageView);
                if (bm != null) {
                    shareProfileImage(bm);
                }
            }
        });
    }

    //function to convert imageView to Bitmap

    private Bitmap convertImageViewToBitmap(ImageView v) {
        return ((BitmapDrawable) v.getDrawable()).getBitmap();
    }

    /*
     * Saves bitmap obtained from image view and sets up an implicit view to share it
     */
    private void shareProfileImage(Bitmap bm) {
        Toast.makeText(this, "Sharing image", Toast.LENGTH_SHORT).show();
        String image = getRealPathFromURI(getImageUri(this, bm), this);
        File file = new File(image);
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/*");
        share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        if (share.resolveActivity(getPackageManager()) != null)
            startActivity(Intent.createChooser(share, "Share via"));
        else Toast.makeText(this, "No app to share image", Toast.LENGTH_SHORT).show();
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

        if (resultCode == Activity.RESULT_OK) {
            try {
                switch (requestCode) {
                    case REQUEST_PHOTO:
                        if (imageForUpload != null) {
                            photo = recogniseFace(imageForUpload, profileImageView, this);
                            // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                            Uri tempUri = getImageUri(this, photo);
                            // CALL THIS METHOD TO GET THE ACTUAL PATH
                            mCurrentPath = getRealPathFromURI(tempUri, this);
                            Log.e(TAG, mCurrentPath);
                            HashMap<String, Object> src = new HashMap<>();
                            src.put(URI, tempUri);
                            loadPictureToImageView(src, R.drawable.avatar, profileImageView,
                                    true, false, false, false);
                        } else {
                            Toast.makeText(this, "Error2 while capturing image", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case REQUEST_GALLERY:
                        if (data != null) {
                            Uri uri = data.getData();

                            try {
                                HashMap<String, Object> src = new HashMap<>();
                                src.put(URI, uri);
                                loadPictureToImageView(src, R.drawable.avatar, profileImageView,
                                        true, false, false, false);
                            } catch (Exception e) {
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

    @Override
    public void onBackPressed() {
        exitToTargetActivity(this, MainActivity.class);
        super.onBackPressed();
    }
}
