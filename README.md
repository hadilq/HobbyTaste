# HobbyTaste

Just a hobby android project. It contains server side and client side. For running the server side, you have to have
a mysql database server. Then by defining global_serverAddress, global_serverPort and global_serverScheme in
build.gradle file of the root project, you can specify the address of the server. For start you can do this, just
like me, by determining an address in a LAN as the server address. You can find other options in the same place.
For running the server side and client side you have to generate a keystore.properties file like this

```
keyAlias=...
keyPassword=...
storeFile=...
storePassword=...
jwtSecret=...
```

Then you can run the server simply by this command

```
./gradlew ':server:bootRun'
```

and simply build the client side by

```
./gradlew assembleDebug
```

This project is meant to give you the ability to pick a place on map. I named those places in the project as "store"
but basically they can be any place. Then it let you to upload pictures of that place, so others can comment, like,
etc. on that post.

It's not finished yet, I feel the UI needs more to do, but the basic functionalities are ready to work and I'm
working on it to make it better.
