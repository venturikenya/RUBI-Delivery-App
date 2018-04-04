package ke.co.venturisys.rubideliveryapp.others;

import android.app.Activity;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import ke.co.venturisys.rubideliveryapp.R;

import static ke.co.venturisys.rubideliveryapp.others.Constants.BUFFER;
import static ke.co.venturisys.rubideliveryapp.others.Constants.TAG;

/**
 * Created by victor on 4/01/18.
 * This class is for operations that involve file I/O
 * Contains method to create non-existent directories and method to zip file
 */

public class FileUtilities {

    /**
     * Function that takes an array of file names and creates a zip file containing the files
     *
     * @param _files      Array of the string file names that are to be zipped
     * @param zipFileName Name of the file to be created. Should end with '.zip'
     */
    public static void zip(String[] _files, String zipFileName) {
        try {
            // BufferedInputStream class adds functionality to another input stream,
            // the ability to buffer the input and to support the mark and reset methods.
            BufferedInputStream origin;
            FileOutputStream dest = new FileOutputStream(zipFileName);
            // create zip out stream to write to
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
                    dest));
            // buffer determines the buffer memory size while reading and writing data it to
            // the zip stream
            byte data[] = new byte[BUFFER];

            for (String _file : _files) {
                if (_file == null) break;
                else {
                    Log.v("Compression " + TAG, "Adding: " + _file);
                    FileInputStream fileInputStream = new FileInputStream(_file);
                    origin = new BufferedInputStream(fileInputStream, BUFFER);

                    ZipEntry entry = new ZipEntry(_file.substring(_file.lastIndexOf("/") + 1));
                    out.putNextEntry(entry);
                    int count;

                    while ((count = origin.read(data, 0, BUFFER)) != -1) {
                        out.write(data, 0, count);
                    }
                    origin.close();
                }
            }

            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This is the method used to create the directories
     * required to save files generated while using the app
     * It checks the directories exist and creates them in they don't
     * The directories are then added to a list for use in the app
     *
     * @param activity Current activity that the user is in
     * @return A list of created system directories
     */
    public static List<File> createSystemDirs(@NonNull Activity activity) {
        // directory for saving images
        File pictures = new File(Environment.getExternalStorageDirectory() + File.separator
                + activity.getResources().getString(R.string.app_name) + File.separator + "Pictures");

        // directory for saving documents
        File documents = new File(Environment.getExternalStorageDirectory() + File.separator
                + activity.getResources().getString(R.string.app_name) + File.separator + "Documents");

        // create directories if they don't exist
        if (!pictures.isDirectory()) {
            //noinspection ResultOfMethodCallIgnored
            pictures.mkdirs(); // create directory and any immediate required preceding directories
        }
        if (!documents.isDirectory()) {
            //noinspection ResultOfMethodCallIgnored
            documents.mkdirs();
        }

        // add the dirs to the list for returning
        List<File> dirs = new ArrayList<>();
        dirs.add(pictures);
        dirs.add(documents);

        return dirs;
    }
}
