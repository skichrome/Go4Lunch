# Go4Lunch
app developped for Project 6, see [OpenClassooms project](https://openclassrooms.com/projects/trouvez-un-restaurant-pour-dejeuner-avec-vos-collegues)
for more details.

## Google API configuration

This app need a token on Google Map to work properly, you need to get one on Google Cloud Platform.
Once you have a token, go to

```
(yourprojectsdirectory)\Go4Lunch\app\src\debug\res\values
```
and create a file named "google_place_api.xml" and add this to the new file created :

```xml
<resources>
    <!--
    TODO: Before you run your application, you need a Google Maps API key.
    See this page for more information:
    https://developers.google.com/maps/documentation/android/start#get_an_android_certificate_and_the_google_maps_api_key
    Once you have your key (it starts with "AIza"), replace the "google_place_api_key"
    string in this file.
    -->
    <string name="google_place_api_key" translatable="false" templateMergeStrategy="preserve">
        YOUR_KEY_HERE
    </string>
</resources>
```

## Firebase Configuration

You will also need a Firebase project to be able to compile this project.
After you have created the project you have to enable Authentication from email, google, twitter and facebook.
You will also need to enable and configure Cloud Firestore
And for facebook and twitter authentication you will also need tokens from these social networks.
The tokens needs to be placed in file in `values/twitter_facebook_api.xml`

```xml
<resources xmlns:tools="http://schemas.android.com/tools">

<!-- Facebook purpose -->
    <string name="facebook_application_id" templateMergeStrategy="preserve" translatable="false">FACEBOOK_APP_ID_HERE</string>
    <string name="facebook_login_protocol_scheme" templateMergeStrategy="preserve" translatable="false" tools:ignore="UnusedResources">FACEBOOK_LOGIN_PROTOCOL_HERE</string>

    <!-- Twitter purpose -->
    <string name="twitter_consumer_key" templateMergeStrategy="preserve" translatable="false">TWITTER_CONSUMER_KEY_HERE</string>
    <string name="twitter_consumer_secret" templateMergeStrategy="preserve" translatable="false">TWITTER_CONSUMER_SECRET_HERE</string>
</resources>
```