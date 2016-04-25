CREATE DATABASE `smartfridge`;
CREATE TABLE `fridgeinfo` (
  `fridge_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_name` varchar(25) DEFAULT NULL,
  `user_image` blob,
  `image_date` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`fridge_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;
