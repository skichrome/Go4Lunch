# Go4Lunch
app developped for Project 6, see [OpenClassooms project](https://openclassrooms.com/projects/trouvez-un-restaurant-pour-dejeuner-avec-vos-collegues)
for more details.

## Google API configuration
This app need a token on Google Map to work properly, you need to get one on Google Cloud Platform.

## Firebase Configuration

You will also need a [Firebase](https://console.firebase.google.com/u/0/) project to be able to compile this project.
After you have created the project you have to download google-services.json and replace the content in the project file with your version, 
and you have to enable Authentication from email, google, twitter and facebook.
You will also need to enable and configure Cloud Firestore
And for facebook and twitter authentication you will also need tokens from these social networks.

## Credentials

Once you have your tokens, create a file named `credentials.properties` in your root project and add these lines :

```gradle
googlePlaceApiKey=YOUR_GOOGLE_API_KEY
facebookAppId=YOUR_FACEBOOK_APP_ID
facebookLoginProtocolScheme=YOUR_FB_PROTOCOL_SCHEME
twitterConsumerKey=YOUR_TWITTER_CONS_KEY
twitterConsumerSecret=YOUR_TWITTER_SECRET
```

## Keystore configuration

If you want to generate a signed APK, you must create a file named `keystore.properties` in your root project and add these lines :

```gradle
storeFile=X:/path/to/you/keystore.jks (Windows)
storePassword=keystore.jks_PASSWD
keyAlias=YOUR_KEY_ALIAS
keyPassword=YOUR_KEY_PASSWD
```