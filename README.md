# Free Map

## Running
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

the server side for development is gonna run. Notice you may need to change these parameters in build.gradle file

```
ext.global_serverAddress = "192.168.1.7"
ext.global_serverPort = "8090"
```

Also if you have a protoc executable for .proto files then be aware that my protoc version is

```
$protoc --version
libprotoc 3.2.0
```

Your generated DTO files may be different respect to my generated DTOs. Please don't commit them. However, to create
an apk file for client side you just need to run

```
./gradlew ':client:assembleDevelopDebug'
```

or if you like to work with my product server you can run

```
./gradlew ':client:assembleProductDebug'
```

Then install the apk file on your Android device.

## App description

This project is meant to give you the ability to pick a place on map and make a post to see by others. As you can see.
I named those places in the project as "store" but basically they can be any places. While posting the place, it let
you to upload pictures of that place, so others can comment, like, etc. on your post.

I tried to simplify the process of making an account. The user has a generated username on authentication. It's not
even unique! So provided the most anonymity imaginable(beside the security).

## State
It's not finished yet, I feel the UI needs more to do, but the basic functionalities are ready to work and I'm
working on it to make it better.
