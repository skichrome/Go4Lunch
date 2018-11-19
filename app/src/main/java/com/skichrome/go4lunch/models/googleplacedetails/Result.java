
package com.skichrome.go4lunch.models.googleplacedetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Result {

    @SerializedName("formatted_address")
    @Expose
    private String formattedAddress;
    @SerializedName("formatted_phone_number")
    @Expose
    private String formattedPhoneNumber;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("place_id")
    @Expose
    private String placeId;
    @SerializedName("opening_hours")
    @Expose
    private OpeningHours openingHours;
    @SerializedName("rating")
    @Expose
    private Double rating;
    @SerializedName("website")
    @Expose
    private String website;

    public String getFormattedAddress() { return formattedAddress; }

    public void setFormattedAddress(String formattedAddress) { this.formattedAddress = formattedAddress; }

    public String getFormattedPhoneNumber() { return formattedPhoneNumber; }

    public void setFormattedPhoneNumber(String formattedPhoneNumber) { this.formattedPhoneNumber = formattedPhoneNumber; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getPlaceId() { return placeId; }

    public void setPlaceId(String placeId) { this.placeId = placeId; }

    public OpeningHours getOpeningHours() { return openingHours; }

    public void setOpeningHours(OpeningHours openingHours) { this.openingHours = openingHours; }

    public Double getRating() { return rating; }

    public void setRating(Double rating) { this.rating = rating; }

    public String getWebsite() { return website; }

    public void setWebsite(String website) { this.website = website; }

}
