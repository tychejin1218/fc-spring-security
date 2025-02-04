INSERT IGNORE INTO `sample`.`users` VALUES ('1', 'danny.kim', '$2a$10$KcE2ZV52mbpV3q4eikTXXe320oZy0v9agU8lfQTJUj6HyWE41BRqi', 'BCRYPT');
INSERT IGNORE INTO `sample`.`users` VALUES ('2', 'steve.kim', '$2a$10$KcE2ZV52mbpV3q4eikTXXe320oZy0v9agU8lfQTJUj6HyWE41BRqi', 'BCRYPT');

INSERT IGNORE INTO `sample`.`authorities` VALUES ('1', 'READ', '1');
INSERT IGNORE INTO `sample`.`authorities` VALUES ('2', 'WRITE', '1');
INSERT IGNORE INTO `sample`.`authorities` VALUES ('3', 'READ', '2');

INSERT IGNORE INTO `sample`.`products` VALUES ('1', 'Chocolate', '10', 'USD');
