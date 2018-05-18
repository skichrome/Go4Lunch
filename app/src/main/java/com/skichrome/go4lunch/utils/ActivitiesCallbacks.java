package com.skichrome.go4lunch.utils;

import com.skichrome.go4lunch.models.FormattedPlace;

public interface ActivitiesCallbacks
{
    interface ListFragmentCallback
    {
        void updatePlaceList();
        void onClickRecyclerView(FormattedPlace mPlace);
    }

    interface MarkersChangedListener
    {
        void getMarkerOnMap();
        void displayRestaurantDetailsOnMarkerClick(FormattedPlace mDetailsRestaurants);
    }

    interface AsynctaskListeners
    {
        void onPreExecute();
        void doInBackground();
        void onPostExecute(FormattedPlace mPlace);
    }

    interface RxJavaListeners
    {
        void onComplete(FormattedPlace mPlace);
    }
}