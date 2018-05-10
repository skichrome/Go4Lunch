package com.skichrome.go4lunch.utils;

import com.skichrome.go4lunch.models.FormattedPlace;

public interface ActivitiesCallbacks
{
    interface MarkersChangedListener
    {
        void getMarkerOnMap();
        void displayRestaurantDetailsOnMarkerClick(FormattedPlace mDetailsRestaurants);
    }
}