-- 推送失败的案卷
DROP TABLE IF EXISTS `to_rec_transit`;
CREATE TABLE `to_rec_transit` (
  `rec_id` bigint NOT NULL,
  `create_time` datetime NOT NULL,
  `syn_flag` int DEFAULT NULL,
  `syn_date` datetime DEFAULT NULL,
  `sender_code` varchar(255) DEFAULT NULL,
  `call_result` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`rec_id`)
);

-- 推送失败的多媒体
DROP TABLE IF EXISTS `to_rec_media_transit`;
CREATE TABLE `to_rec_media_transit` (
  `media_id` bigint NOT NULL,
  `create_time` datetime NOT NULL,
  `syn_flag` int DEFAULT NULL,
  `syn_date` datetime DEFAULT NULL,
  `sender_code` varchar(255) DEFAULT NULL,
  `call_result` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`media_id`)
)