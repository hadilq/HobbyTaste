# HobbyTaste

Just a hobby android project. It contains server side and client side together. For running the server side or client
side for development, first you have to generate a keystore.properties file in the root directory of the project like
this

```
keyAlias=...
keyPassword=...
storeFile=...
storePassword=...
jwtSecret=...
```

Then by running this command

```
./gradlew ':server:runDevelop'
```

the server side for development is gonna run. To create an apk file of client side you just need to run

```
./gradlew ':client:assembleDevelopDebug'
```

or if you like to work with my product server you can run

```
./gradlew ':client:assembleProductDebug'
```

Then install the apk file on your Android device.

This project is meant to give you the ability to pick a place on map. I named those places in the project as "store"
but basically they can be any places. Then it let you to upload pictures of that place, so others can comment, like,
etc. on your post.

It's not finished yet, I feel the UI needs more to do, but the basic functionalities are ready to work and I'm
working on it to make it better.
