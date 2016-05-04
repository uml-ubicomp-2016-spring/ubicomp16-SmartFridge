# Smart Fridge App Report 

# Contributions
+ [Vignesh Dhamodaran] (http://github.com/vignesh24)<br />
+ [Avnesh Reddy Pundru] (http://github.com/Anveshreddy55) <br />
+ [Ragarsha Velmula](http://github.com/vragarsha) <br />

# Goal
 Since now is a time where most of the people upgrade their phones once a year, we are left with one a lot of unused mobile phones around, So we came up with an idea to put the phone to use by a smart way to detect who is using the fridge by utilizing the sensors and modules, and applying what we have learned in the class. Thus, by mounting the phone on the fridge using a dualside magnet and detect the Door opening and capture the face of the person using the camera and show the results in the web application.   <br/>

#  Features
+ Android Application.
+ Door Status Detection.
+ Efficeint Intent Manager.
+ Detecting the face and face features using google vision api.
+ Sync with Web Interface.
+ Low Space Utilisation.
+ Web Interface.
+ Allow users to register for an account.
+ Send welcome emails to users.
+ Allow users to change their passwords through their profile page.
+ Allow users to sign in and sign out of an account easily.
+ Real-time updating of the server on image upload.

#  Design

*User Stories*<br />
------
The app flow starts by mounting the phone in potrait mode to the fridge and Starting the Smart Fridge App. The app greets you with a status of the door and holds the screen for you, Once the door is opened the app launches the face detection Activity where it detects your face and saves it, then the Upload service will upload the images to the web application. When opened the web application the users will be prompted to register if they are first time users or they will be prompted for login if they have been registered, On login the activity page will display the all the user actvity. So User can monitor all the actvities using the web Application.<br/>
<br />
For a more details on how the app works see the [Demo](https://www.youtube.com/watch?v=GB-xDLorPS0)<br />
<br />
*Tools and Components Used*<br />
------
+ Android Studio
+ Google Mobile Vision Api
+ Http Connection Client
+ Html5
+ Angular
+ Node
+ MySQL database
+ Mobile Accelerometer, Magnetometer and Mobile Front Camera

*Design Flow*<br/>
------
![alt text](https://raw.githubusercontent.com/uml-ubicomp-2016-spring/ubicomp16-SmartFridge/master/App_Flow.png) <br />
<br />

<strong> Door Open Detection: (Sensor Change)</strong> <br />
On app start the sensors are registered and the acclerometer sensor values and magnetometer sensor values is passed through a low pass filter to get a filter the unequalities in the sensor and then the values are used to get the Yaw, Pitch and Roll to detect the angle change. In Our case the pitch value is constant since the device is mounted potrait. The door status is detected by the shake produced during door opening and the angle change form yaw and roll. So two states are outcomes of this Module Door-Open/Door-Closed. <br/>
<br />
<strong> Intent Manager: </strong><br />
Once a call for door open is recevied the Intent manager creates a synchronised lock for the call and thereby calling the Face detecting activity. On more sensor change to Door-Closed the intent manager releases the lock and returns the screen to the Sensor Change Detection State.<br/>
<br />
<strong> Face Detection: </strong><br />
Here the permission for using the camera is obtained from the manifest file and graphics definitons needs to added to the face is obtained and then the face detection starts from the camera source and then the graphics is drawn on a canvas using the face width and height and pixel position by the way starting the image storage and upload module.<br />
<br />
<strong> Image Capture and Upload:</strong> <br />
Once the Face Detection Activity is Started the Image is captured and saved, Since the overlay is drawn over the camera source the overlay wil not appear in the saved image. The app uses the Http Connection Client api built in to upload the images to the server. <br />
<br />
<strong> Backend - Web App :</strong><br />
Once a POST call is made the web app recieves the image, It extracts the timestamp and the happiness percentage from it and saves it along with the image in the database. when an entry is made the database angular makes uses of its MVC features to show the updated entries in the User Inter face <br />
<br />

*Data Collection and Testing*<br />
------
For detecting the door open/close status we recorded the acclerometer and magnetometer sensor values and calculated yaw, pitch and roll tp plotted a graph for calculating the threshold and optimal shake when opening and closing the door. <br />
You can find all the collected sensor data [here](https://github.com/uml-ubicomp-2016-spring/ubicomp16-SmartFridge/tree/master/Sensor%20Data)<br />

<strong>The Generalized Graphs:</strong><br />
Sample 1<br />
![alt text](https://raw.githubusercontent.com/uml-ubicomp-2016-spring/ubicomp16-SmartFridge/master/Sensor%20Data/chart_1.jpg)<br/>
<br />
Sample 2<br />
![alt text](https://raw.githubusercontent.com/uml-ubicomp-2016-spring/ubicomp16-SmartFridge/master/Sensor%20Data/chart_2.jpg)<br />
<br />

*Problems Faced and Improvements Done*<br />
------
+ The Sensor values are abnormal.
  + Fixed by passing the sensor values through a low pass filter.
+ Intents calls causing App to Crash.
  + Implement concurrency control by using a Mutex Lock.
+ OpenCV not compatible with current Android version and Android Studio in beta supporting JNI
  + Used Google Mobile Vision Api as an alternative for OpenCV
+ Upload Service Not Working in HttpClient.
  + Since Android version 22 and above depreciated HttpClient we used Http Connection Client for uploading image.
+ Node not dispying the image.
  + Added a name tag before image to display the image.<br/>

# File Structure 
*Mobile*<br />
------
<strong>Activities:</strong><br />
+ SensorActivity.java
  + Vignesh Dhamodaran
  + This activity manages the sensors and does the Door open/close detection.
+ IntentManager.java
  + Vignesh Dhamodaran 
  + This class maintains the Intent calls and implements synchronised lock procedure.
+ FaceDetectionActivity.java
  + Vignesh Dhamodaran
  + This Activity Does the Face Detection by gathering the camera source and face graphics.
+ UploadActivity.java
  + Anvesh Reddy Pundru
  + This Service performs the image capture and server upload using HttpConnectionClient.
+ CanvasGraphics.java
  + Vignesh Dhamodaran
  + This class provides the graphic definition on canvas for face.
+ PreviewCamera.java
  + Anvesh reddy pundru 
  + This class provides the layout for camera to display.
+ CanvasGraphicsEmbedder.java
  + Vignesh Dhamodaran
  + Embeddeds Camera source and the graphics together
<br />

<strong>Layout</strong><br />
+ activity_sensor.xml
  + Vignesh Dhamodaran
  + Main Activity Layout for Sensor activity
+ content_sensor.xml
  + Vignesh Dhamodaran 
  + Contains Contents for Activity_main.xml
+ main.xml
  + Anvesh Reddy Pundru 
  + Contains Camera preview for face detection
<br />

+ AndroidManifest.xml
  + Vignesh Dhamodaran
  + Contains sensor permissions and activities.

*Web Application*<br />
------

<strong>Views</strong><br/>
+ index.html
  + Anvesh Reddy Pundru
  + Main UI Page
+ register.view.html
  + Ragarsha Velmula
  + Interface for the Users to Register
+ login.view.html
  + Ragarsha Velmula
  + Interface for the Users to login and reset password
+ home.view.html
  + Ragarsha Velmula
  + User Interface to show uploaded Images.
<br />

<strong>Controllers</strong><br />
+ Server.js
  + Ragarsha Velmula 
  + Creating a local host to run the web application
+ app.js
  + Ragarsha Velmula
  + Main controller connecting to the other modules of the web application
+ register.controller.js
  + Anvesh Reddy Pundru
  + Methods allowing users to register
+ login.controller.js
  + Ragarsha Velmula
  + Controls the user login
+ home.controller.js
  + Ragarsha Velmula
  + Real-time updating of data changes in the database
+ authentication.service.js
  + Ragarsha Velmula
  + Checking for the authorized users and their credentials
+ core.js
  + Ragarsha Velmula
  + Assignement of the data imported from the database to the scope variables
+ flash.service.js
  + Ragarsha Velmula
  + Routing of the layouts based on success/error
+ user.service.js
  + Ragarsha Velmula
  + Service methods to call the required routes accordingly
+ user.service.local-storage.js
  + Ragarsha Velmula
  + Functionality for storing the registered users


# Project Evaluation

