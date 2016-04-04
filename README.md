# ubicomp16-SmartFridge

Application allowing to check if the door is closed or open. Through this application as the user opens the door the image is captured.

# Contributors
>Vignesh Dhamodaran
>Avnesh Reddy Pundru
>Ragarsha Velmula

Progress 3/28/16 - 4/4/16
-> Reduced the Sensor noise using lowpass filter methodology https://en.wikipedia.org/wiki/Low-pass_filter#Simple_infinite_impulse_response_filter
-> Implemented Eulers Angles to get Pitch Roll and Yaw for door open detection https://en.wikipedia.org/wiki/Rotation_formalisms_in_three_dimensions#Conversion_formulae_between_formalisms.
-> Calculated a door open position using sensor data comparison over 10 trials and tested door status working.
-> Designed a web application for app with User Validation.
-> Implemented Video Capture in android and wanted to merge the app as an intent call from the sensor changed door open call.
