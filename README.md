# ubicomp16-SmartFridge

Application allowing to check if the door is closed or open. Through this application as the user opens the door the image is captured.



## Contributors
+ [Vignesh Dhamodaran] (http://github.com/vignesh24)<br />
+ [Avnesh Reddy Pundru] (http://github.com/Anveshreddy55) <br />
+ [Ragarsha Velmula](http://github.com/vragarsha) <br />

## Video Demo

<a href="https://www.youtube.com/watch?v=GB-xDLorPS0" target="_blank"><img src="ScreenShots/Mobile%20App/2016-05-04%2003.40.12.png" width="40%" height="40%"/></a>

<br />
Demo on [Youtube](https://www.youtube.com/watch?v=GB-xDLorPS0)<br />

## Progress 3/28/16 - 4/4/16
###Vignesh Dhamodaran
[x] Reduced the Sensor noise using lowpass filter methodology  https://en.wikipedia.org/wiki/Low-pass_filter#Simple_infinite_impulse_response_filter<br />
[x] Implemented Eulers Angles to get Pitch Roll and Yaw for door open detection https://en.wikipedia.org/wiki/Rotation_formalisms_in_three_dimensions#Conversion_formulae_between_formalisms.<br />
###Ragarsha Velmula
[x] Calculated a door open position using sensor data comparison over 10 trials and tested door status working.<br />
[x]Designed a web application for app with User Validation.<br />
###Anvesh Reddy 
[x] Implemented Video Capture in android and wanted to merge the app as an intent call from the sensor changed door open call.<br />

## Progress 4/5/16 - 4/11/16
###Vignesh Dhamodaran and Anvesh Reddy
[x] Added the VideoCapture for the app sensor based call to start the Face detetction Service. Had to rewrite video capture written by Anvesh since opencv uses an entirely different Approach.<br />
[x] Implemented OpenCV with the Video Capture Module which starts the face detection Module the module builds and complies all the native code. Since Android Studio just started to support NDK and cannot ship the entire library with the app and depends on opencv Manager app there is a dependency issue.<br />
###Ragarsha Velmula
[x] Worked on Solving the OpenCV manager app and SmartFridge Application Linkage issue.<br />
[x] Implementing Backend using Nodejs for the app<br />

## Progress 4/12/16 - 4/18/16
###Vignesh Dhamodaran 
[x] Worked on fixing the opencv error and considering google vison API for face detection and tracking. <br />
[x] Working on google Vision implementation for face detection and tracking.
###Ragarsha Velmula
[x] Trying to create a RestFull Api server in Express and Node.js. <br/>
[x] For quering and persisting data connecting a MongoDB database to the API server.
###Anvesh Reddy 
[x] Working on sending data from android application to server and then storing the same in a mysql database on the server.

## Progress 4/19/16 - 4/25/16
###Vignesh Dhamodaran 
[x] Implemented Face detection and tracking using google vision api <br/>
[x] Implemented the service for uploading the data to server <br/>
[x] Optimized Code in terms of synchronised calls and added implemented IntentManger to call intents efficeiently<br/>
###Ragarsha Velmula
[x] Encountered trouble using the MongoDB database, had to avert to mysql database. </br>
[x] Image representation on the website is still the only issue. 

## Progress 4/26/16 - 5/2/16
###Vignesh Dhamodaran 
[x] Implemented Happiness scale for faces detected <br />
[x] Integrated Mobile app with Backend <br />
[x] Tested and verifed mobile app working
###Ragarsha Velmula
[x] Sovled the image representation issue <br />
[x] Integrated with the WebApp with Mobile App <br />
[x] Tested and verifed web app working
###Anvesh Reddy 
[x] Tested and implemented the upload to server from mobile app

