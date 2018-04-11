package ke.co.venturisys.rubideliveryapp.others;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.media.FaceDetector;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import ke.co.venturisys.rubideliveryapp.R;

import static ke.co.venturisys.rubideliveryapp.others.Constants.CAMERA_FACING;
import static ke.co.venturisys.rubideliveryapp.others.Constants.REQUEST_PHOTO;

public class PictureUtilities {

    private static String mCurrentPhotoPath;


    /*
     * Scales image file, calculates rate of scaling down to given area and then rereads the
     * file to create a scaled-down Bitmap object
     */
    private static Bitmap getScaledBitmap(String path, int destWidth, int destHeight) {
        // Read in dimensions of the image on the disk
        BitmapFactory.Options options = new BitmapFactory.Options();

        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        // Figure out how much to scale down by
        int inSampleSize = 1; // determines how big each pixel in final image should be
        if (srcWidth > destWidth || srcHeight > destHeight) {
            if (srcWidth > srcHeight) {
                inSampleSize = Math.round(srcHeight / destHeight);
            } else {
                inSampleSize = Math.round(srcWidth / destWidth);
            }
        }

        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        // Read in and create final bitmap
        return BitmapFactory.decodeFile(path, options);
    }

    /*
     * Determines how big the ImageView is by getting a conservative estimate
     */
    private static Bitmap getScaledBitmap(String path, Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);

        return getScaledBitmap(path, size.x, size.y);
    }


    /**
     * Method invokes intent to take picture
     */
    static Uri takePicture(Fragment fragment, String TAG, String dir) {

        Uri imageForUpload = null;
        assert fragment.getActivity() != null;
        Context context = fragment.getActivity();
        PackageManager packageManager = context.getPackageManager();

        // call phone's camera
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // resolveActivity returns first activity component that can handle intent, preventing crash
        if (intent.resolveActivity(fragment.getActivity().getPackageManager()) != null) {
            // Create file where image should go
            File photoFile;

            try {
                photoFile = getPhotoFile(TAG, dir);
            } catch (Exception ex) {
                Log.e("Image error", "Error saving image");
                return null;
            }

            // continue only if File was successfully created
            imageForUpload = Uri.fromFile(photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageForUpload);
            // call front camera if present
            if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT))
                intent.putExtra(CAMERA_FACING, 1);
            fragment.startActivityForResult(Intent.createChooser(intent, "Take Picture"), REQUEST_PHOTO);
        } else {
            Toast.makeText(context, "Your device does not have a camera",
                    Toast.LENGTH_LONG).show();
        }

        return imageForUpload;
    }

    static Uri takePicture(Activity activity, String TAG, String dir) {

        Uri imageForUpload = null;
        PackageManager packageManager = activity.getPackageManager();

        // call phone's camera
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // resolveActivity returns first activity component that can handle intent, preventing crash
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            // Create file where image should go
            File photoFile;

            try {
                photoFile = getPhotoFile(TAG, dir);
            } catch (Exception ex) {
                Log.e("Image error", "Error saving image");
                return null;
            }

            // continue only if File was successfully created
            imageForUpload = Uri.fromFile(photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageForUpload);
            // call front camera if present
            if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT))
                intent.putExtra(CAMERA_FACING, 1);
            activity.startActivityForResult(Intent.createChooser(intent, "Take Picture"), REQUEST_PHOTO);
        } else {
            Toast.makeText(activity, "Your device does not have a camera",
                    Toast.LENGTH_LONG).show();
        }

        return imageForUpload;
    }

    /**
     * Method makes file available for viewing from system's Media Provider
     */
    static void galleryAddPic(@NonNull Activity activity) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        activity.sendBroadcast(mediaScanIntent);
    }

    /**
     * Method returns the file containing the photo
     *
     * @return photo file
     */
    private static File getPhotoFile(String TAG, String dir) {
        File externalFilesDir;
        assert dir != null;
        externalFilesDir = new File(dir);

        // Save a file: path for use with ACTION_VIEW intents
        try {
            mCurrentPhotoPath = externalFilesDir.getAbsolutePath();
        } catch (NullPointerException ex) {
            Log.e(TAG, ex.getMessage());
        }

        return new File(externalFilesDir, getPhotoFilename());
    }

    /**
     * Method returns current time in specified format
     */
    public static String getFileName() {
        // get current time and set as file name
        DateFormat df = new SimpleDateFormat("ddMMyyyy_hhmmssSSS", Locale.ROOT);
        Calendar calendar = Calendar.getInstance();
        return df.format(calendar.getTime());
    }

    /**
     * Method returns file name by combining time passes with the JPG extension
     *
     * @return file name of the image file
     */
    private static String getPhotoFilename() {

        return "IMG_" + getFileName() + ".jpg";
    }

    /**
     * Method to recognize and set faces after picture is taken
     *
     * @param imageForUpload Uri of picture taken
     * @param imageView      view to set picture to
     * @param activity       activity of context
     * @return photo taken
     */
    public static Bitmap recogniseFace(Uri imageForUpload, ImageView imageView, Activity activity) {
        Bitmap photo;
        Context context = activity.getApplicationContext();

        activity.getContentResolver().notifyChange(imageForUpload, null);
        photo = getScaledBitmap(imageForUpload.getPath(), activity);

        if (photo != null) {
            // detect only one face to set to image view
            FaceDetector faceDet = new FaceDetector(photo.getWidth(), photo.getHeight(), 2);
            int faces = faceDet.findFaces(photo.copy(Bitmap.Config.RGB_565, false), new FaceDetector.Face[2]);
            if (faces == 0) {
                Toast.makeText(context, "No face Detected.", Toast.LENGTH_SHORT).show();
                imageView.setImageDrawable(ContextCompat.getDrawable(activity, android.R.drawable.ic_menu_camera));
                photo = null;
            } else if (faces > 1) {
                Toast.makeText(context, "Detected more than one face.", Toast.LENGTH_SHORT).show();
                imageView.setImageDrawable(ContextCompat.getDrawable(activity, android.R.drawable.ic_menu_camera));
                photo = null;
            }
            BitmapDrawable ob = new BitmapDrawable(activity.getResources(), photo);
            imageView.setImageDrawable(ob);
            // updateSubmitButtonState();
        } else {
            imageView.setImageDrawable(ContextCompat.getDrawable(activity, R.mipmap.ic_picture));
            Toast.makeText(context, "Error1 while capturing image", Toast.LENGTH_SHORT).show();
        }

        return photo;
    }

    /**
     * Method that retrieves the URI of a picture from its bitmap
     *
     * @param inContext Current context of the app
     * @param inImage   The bitmap of the image
     * @return URI of image
     */
    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(),
                inImage, "Title", null);
        return Uri.parse(path);
    }

    /**
     * Method that retrieves a picture file path from its URI
     *
     * @param uri      URI of the image
     * @param activity Activity hosting the picture
     * @return File path of picture
     */
    public static String getRealPathFromURI(Uri uri, Activity activity) {
        try {
            Cursor cursor = activity.getContentResolver().query(uri, null, null, null, null);
            assert cursor != null;
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            String result = cursor.getString(index);
            cursor.close();
            return result;
        } catch (Exception ex) {
            Log.e(activity.getClass().getName(), ex.getMessage());
        }

        return "";
    }

    /**
     * Function to compress and convert bitmap to string for transmission.
     * It uses the Base64 encoding library
     *
     * @param photo Bitmap to be converted
     * @return String conversion of bitmap
     */
    static String compressImage(Bitmap photo) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (photo == null)
            return "";
        photo.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
        byte[] byteArrayImage = baos.toByteArray();
        // convert byte array to byte string
        return Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
    }
}
