# Camera_App_Sessions


Capture image session wise with session ID. App is helpfull for navigating to images easily because each bunch of images stores in different category forlders in a single sessions. Each new sessions have new session ID and related images in it. These images are locating in the users's phone's private directory. So, this app not store image on cloud
This App will securely saves the users captured image in private folder of there mobile phone
Easy to found the particular session images using there session ID or the name

This Android Project's 40 % part is referenced from the ProgrammingHunt channel

User have to create different session ID so that each session ID stored individually which will prone to easy to navigate to the images
Students can store there notes in specific folder 
Can be used to make the labels of different class to collect similar type of data with different id.


***** PROJECT STRUCTURES ******


Camera_App/ 

 ├── app/src/main/java/com/example/camera_app/  

 
 │        ├── MainActivity.kt                              # Entry point (session creation & navigation)  

 
 │        ├── CaptureActivity.kt                               # Handles image capturing   

 
 │        ├── ViewImagesActivity.kt                          # Displays images from a selected session

 
 │        ├── DatabaseHelper.kt                               # SQLite database for session metadata

 │
 
 ├── app/src/main/res/layout/                            # XML layout files
 
 │
 
 ├── app/build.gradle                               # App-level Gradle config
 
 ├── build.gradle                                     # Project-level Gradle config
 
 └── AndroidManifest.xml                           # App manifest


***** DEPENDENCIES ******
 dependencies {
 
    implementation "androidx.appcompat:appcompat:1.6.1"
    implementation "androidx.core:core-ktx:1.12.0"
    implementation "androidx.constraintlayout:constraintlayout:2.1.4"

    // CameraX
    def camerax_version = "1.3.0"
    implementation "androidx.camera:camera-core:$camerax_version"
    implementation "androidx.camera:camera-camera2:$camerax_version"
    implementation "androidx.camera:camera-lifecycle:$camerax_version"
    implementation "androidx.camera:camera-view:1.3.0"

    // SQLite (built-in, but optional helpers)
    implementation "androidx.sqlite:sqlite:2.3.1"
}
