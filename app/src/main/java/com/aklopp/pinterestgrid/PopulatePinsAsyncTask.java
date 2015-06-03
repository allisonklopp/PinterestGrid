package com.aklopp.pinterestgrid;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Async task that loads and parses the JSON, and populates the grid adapter.
 * Created by Allison on 6/2/2015.
 */
class PopulatePinsAsyncTask extends AsyncTask<String, String, Void> {
    private static final String TAG = "PopulatePinsAsyncTask";

    // Tags to parse JSON
    private static final String DATA_TAG = "data";
    private static final String PINS_TAG = "pins";
    private static final String USER_TAG = "user";
    private static final String DESCRIPTION_TAG = "description";
    private static final String IMAGES_TAG = "images";
    private static final String IMAGE_SIZE_TAG = "237x";
    private static final String URL_TAG = "url";

    private Context mContext;
    private final GridViewAdapter mGridAdapter;

    public PopulatePinsAsyncTask(Context context, GridViewAdapter pinGrid)
    {
        this.mContext = context;
        mGridAdapter = pinGrid;
    }

    @Override
    protected Void doInBackground(String... urls) {
        String result =  getJsonAsString(urls[0]);

        // Parse JSON data
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONObject data = jsonObject.getJSONObject(DATA_TAG);
            JSONArray pins = data.getJSONArray(PINS_TAG);
            if (pins.length() == 0)
            {
                showErrorToast();
            }
            else
            {
                JSONObject user = data.getJSONObject(USER_TAG);
                for (int i = 0; i < pins.length(); i++) {
                    JSONObject pin = pins.getJSONObject(i);
                    String pinDescription = pin.getString(DESCRIPTION_TAG);

                    JSONObject images = pin.getJSONObject(IMAGES_TAG);
                    JSONObject size237x = images.getJSONObject(IMAGE_SIZE_TAG);
                    String imageURL = size237x.getString(URL_TAG);

                    addToGrid(imageURL, pinDescription);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSONException: " + e);
            showErrorToast();
        }
        return null;
    }

    /**
     * Get the image for the URL given and add the pin to the grid.
     * @param imageURL
     * @param pinDescription
     */
    private void addToGrid(final String imageURL, final String pinDescription) {
        final Bitmap pinImage = getImage(imageURL);

        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mGridAdapter.add(new PinItem(pinImage, pinDescription));
            }
        });
    }

    /**
     * Get the image from this URL.
     * @param url
     * @return bitmap image
     */
    private Bitmap getImage(String url)
    {
        Bitmap pinImage = null;
        try {
            InputStream in = new java.net.URL(url).openStream();
            pinImage = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e(TAG, "Error getting image for URL (" + url + ") : " + e.getMessage());
        }
        return pinImage;
    }

    /**
     * Gets the JSON from the specified URL.
     * @param url
     * @return JSON string
     */
    public String getJsonAsString(String url){
        InputStream inputStream = null;
        String result = "";
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
            inputStream = httpResponse.getEntity().getContent();

            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                showErrorToast();

        } catch (Exception e) {
            Log.d(TAG, "Problem getting JSON: " + e.getLocalizedMessage());
        }

        return result;
    }

    /**
     * Converts the input stream object to a string.
     * @param inputStream
     * @return compiled string
     * @throws IOException
     */
    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }

    /**
     * Display a toast to the user if anything goes wrong with parsing.
     * Assume it's invalid username for now. Optimally, delve into what went wrong
     * and give more info.
     */
    private void showErrorToast()
    {
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, "Invalid username entered!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
