# ubicomp16-SmartFridge

Application allowing to check if the door is closed or open. Through this application as the user opens the door the image is captured.

## Contributors
+ [Vignesh Dhamodaran] (http://github.com/vignesh24)<br />
+ [Avnesh Reddy Pundru] (http://github.com/Anveshreddy55) <br />
+ [Ragarsha Velmula](http://github.com/vragarsha) <br />

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
###Vignesh Dhamodaran and ###Anvesh Reddy
[x] Added the VideoCapture for the app sensor based call to start the Face detetction Service. Had to rewrite video capture written by Anvesh since opencv uses an entirely different Approach.<br />
[x] Implemented OpenCV with the Video Capture Module which starts the face detection Module the module builds and complies all the native code. Since Android Studio just started to support NDK and cannot ship the entire library with the app and depends on opencv Manager app there is a dependency issue.<br />
###Ragarsha Velmula
[x] Worked on Solving the OpenCV manager app and SmartFridge Application Linkage issue.<br />
[x] Implementing Backed for the app<br />
