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
    Note: This resource is used for the debug build target. Update this file if you just want to run
    the demo app.
    -->
    <string name="google_place_api_key" translatable="false" templateMergeStrategy="preserve">
        YOUR_KEY_HERE
    </string>
</resources>
```