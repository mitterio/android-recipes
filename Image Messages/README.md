# Image Message Demo App for Android
This is a demo app showing how to send an image message on a simple chat app using Mitter.io's payload types.

## Before you start

Navigate to this project and follow the instructions to get a local server running before you try to run this app.

* [Server for running this demo](https://github.com/mitterio/server-timeline-events-demo)

## How to setup?

Get a copy of this project by doing a git clone:

```
git clone https://github.com/mitterio/android-recipes.git
```

After that, navigate to the **Image Messages** directory, open your project using Android Studio and make following changes:

* **Add your application ID**. Open `App.kt` and replace the existing application ID with your own application ID.
* **Add a channel ID**. Open `MainActivity.kt` and replace the existing channel ID with the channel ID that you received after running the local server (as mentioned in the previous section).

## How to run?

Run the  project using Android Studio on **2** or more devices/emulators and choose a user to login from the first screen that pops up.

Send images messages from one user to another by clicking on the image button beside the send button to see image messages in action.

## More details

This project is a part of the Image Messages recipe on Mitter.io's website. Visit the following link to learn more about this implementation:

* Recipe - [link here]
