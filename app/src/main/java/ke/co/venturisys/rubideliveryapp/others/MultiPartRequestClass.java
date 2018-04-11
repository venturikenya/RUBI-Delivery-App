package ke.co.venturisys.rubideliveryapp.others;

import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static ke.co.venturisys.rubideliveryapp.others.Constants.ERROR;
import static ke.co.venturisys.rubideliveryapp.others.Constants.TAG;

/**
 * Created by victor on 3/24/18.
 * This class carries out requests using ok http client library
 * Courtesy of https://developerandroidguide.blogspot.co.ke/2017/05/upload-image-using-okhttp.html
 * and https://github.com/miskoajkula/Large-File-upload/
 */

class MultiPartRequestClass {
    private MultipartBody.Builder multipartBody;
    private OkHttpClient OkHttpClient;

    MultiPartRequestClass() {
        this.multipartBody = new MultipartBody.Builder();
        this.multipartBody.setType(MultipartBody.FORM);
        this.OkHttpClient = new OkHttpClient();
    }

    /**
     * Add a value and its tag to the multipart request form
     *
     * @param name  Tag of the value
     * @param value Value to be sent
     */
    void addObject(String name, Object value) {
        if (value != null)
            this.multipartBody.addFormDataPart(name, String.valueOf(value));
    }

    /**
     * Method builds the multipart form,
     * creates and carries out a network request
     * and parses the network response,
     * converting it into a JSONObject for further processing
     *
     * @param url Link to send the form to
     * @return JSONObject form of response
     */
    JSONObject execute(String url) {
        // response
        JSONObject jsonObject = new JSONObject();
        // create network request with request builder parse
        Request request = new Request.Builder()
                .url(url)
                .post(this.multipartBody.build())
                .build();
        Log.v("REQUEST " + TAG, "" + request);

        // create new response instance to execute network request
        try {
            Response response = OkHttpClient.newCall(request).execute();
            Log.v("RESPONSE " + TAG, "" + response);

            // show error if response was unsuccessful
            if (!response.isSuccessful()) {
                throw new IOException("OkHttpClient " + ERROR + ":" + response);
            }

            // response code, if needed
            /* int code = response.networkResponse().code(); */

            if (response.isSuccessful()) {
                jsonObject = new JSONObject(Objects.requireNonNull(response.body()).string());
            }

        } catch (Exception ex) {
            Log.e("OkHttpClient " + ERROR, "Something went wrong");
            ex.printStackTrace();
        } finally {
            // free up resources
            multipartBody = null;
            if (OkHttpClient != null) OkHttpClient = null;
        }

        return jsonObject;
    }
}
