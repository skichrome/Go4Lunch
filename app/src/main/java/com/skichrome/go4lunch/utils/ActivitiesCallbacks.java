package com.skichrome.go4lunch.utils;

import com.skichrome.go4lunch.models.FormattedPlace;

public interface ActivitiesCallbacks
{
    interface OnClickRVListener
    {
        void onClickRecyclerView(FormattedPlace mPlace);
    }

    interface OnFragmentReadyListener
    {
        void onListFragmentReady();
    }

    interface MapFragmentListeners
    {
        void getResultOnClickFloatingActionBtn();
        void displayRestaurantDetailsOnClick(FormattedPlace mDetailsRestaurants);
    }

    interface AsyncTaskListeners
    {
        void onPreExecute();
        void doInBackground();
        void onPostExecute(FormattedPlace mPlace);
    }

    interface RxJavaListener
    {
        void onComplete(FormattedPlace mPlace);
    }
}