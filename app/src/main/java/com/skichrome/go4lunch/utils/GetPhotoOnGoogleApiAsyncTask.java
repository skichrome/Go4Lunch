package com.skichrome.go4lunch.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.skichrome.go4lunch.models.FormattedPlace;

import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetPhotoOnGoogleApiAsyncTask extends android.os.AsyncTask<String, Void, Void>
{
    public interface AsyncTaskListeners
    {
        void onPreExecute();
        void doInBackground();
        void onPostExecute(FormattedPlace mPlace);
    }

    private final WeakReference<AsyncTaskListeners> callback;
    private FormattedPlace place;
    private final String apiKey;

    public GetPhotoOnGoogleApiAsyncTask(AsyncTaskListeners mCallback, FormattedPlace mPlace, String mApiKey)
    {
        this.callback = new WeakReference<>(mCallback);
        this.place = mPlace;
        this.apiKey = mApiKey;
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        callback.get().onPreExecute();
    }

    @Override
    protected void onPostExecute(Void mVoid)
    {
        super.onPostExecute(mVoid);
        callback.get().onPostExecute(place);
    }

    @Override
    protected Void doInBackground(String... mStrings)
    {
        try
        {
            Bitmap picture;

            // 1 - Declare a URL Connection on Google Photo Api
            URL url = new URL("https://maps.googleapis.com/maps/api/place/photo?" +
                    "photoreference=" + place.getPhotoReference() +
                    "&sensor=false" +
                    "&maxheight=800" +
                    "&maxwidth=800" +
                    "&key=" + apiKey);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);

            // 2 - Open InputStream to connection
            conn.connect();
            picture = BitmapFactory.decodeStream(conn.getInputStream());
            place.setPhoto(picture);

        }
        catch (FileNotFoundException e)
        {
            Log.e("File input", "getNearbyPlaces : ", e.getCause());
        }
        catch (Exception exception)
        {
            Log.e("PLACE PHOTO", "getPlacePhoto: Exeption !", exception);
        }
        callback.get().doInBackground();
        return null;
    }
}