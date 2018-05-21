package com.skichrome.go4lunch.utils;

import com.skichrome.go4lunch.models.FormattedPlace;

public interface ActivitiesCallbacks
{
    interface ShowDetailsListener
    {
        void showRestaurantDetails(FormattedPlace mPlace);
    }

    interface AsyncTaskListeners
    {
        void onPreExecute();
        void doInBackground();
        void onPostExecute(FormattedPlace mPlace);
    }
}