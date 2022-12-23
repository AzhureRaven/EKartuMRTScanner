/*
SQLyog Community v13.1.9 (64 bit)
MySQL - 10.4.24-MariaDB : Database - e_kartu_mrt
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`e_kartu_mrt` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;

USE `e_kartu_mrt`;

/*Table structure for table `drute` */

DROP TABLE IF EXISTS `drute`;

CREATE TABLE `drute` (
  `Id_Rute` int(11) NOT NULL,
  `Id_Stasiun` int(11) NOT NULL,
  `Stasiun_Ke` int(11) NOT NULL,
  `Jarak_Next` int(11) NOT NULL,
  PRIMARY KEY (`Id_Rute`,`Id_Stasiun`),
  KEY `Id_Stasiun` (`Id_Stasiun`),
  CONSTRAINT `drute_ibfk_1` FOREIGN KEY (`Id_Rute`) REFERENCES `rute` (`Id_Rute`),
  CONSTRAINT `drute_ibfk_2` FOREIGN KEY (`Id_Stasiun`) REFERENCES `stasiun` (`Id_Stasiun`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Data for the table `drute` */

insert  into `drute`(`Id_Rute`,`Id_Stasiun`,`Stasiun_Ke`,`Jarak_Next`) values 
(1,1,2,0),
(1,3,1,400),
(2,4,1,600),
(2,7,2,0),
(3,2,1,800),
(3,4,2,700),
(3,5,3,0),
(4,1,1,500),
(4,3,4,0),
(4,5,2,800),
(4,6,3,200),
(5,3,3,100),
(5,5,2,200),
(5,6,4,0),
(5,7,1,300),
(6,3,1,300),
(6,7,2,0);

/*Table structure for table `e_kartu` */

DROP TABLE IF EXISTS `e_kartu`;

CREATE TABLE `e_kartu` (
  `Id_Kartu` int(11) NOT NULL AUTO_INCREMENT,
  `Nama_Lengkap` varchar(50) NOT NULL,
  `Username` varchar(50) NOT NULL,
  `Password` varchar(255) NOT NULL,
  `Email` varchar(50) NOT NULL,
  `Tgl_Lahir` datetime NOT NULL,
  `Kelamin` varchar(1) NOT NULL,
  `Tgl_Register` datetime NOT NULL DEFAULT current_timestamp(),
  `Saldo` decimal(12,2) NOT NULL,
  `Status_Kartu` int(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`Id_Kartu`),
  UNIQUE KEY `uq_username` (`Username`),
  UNIQUE KEY `uq_email` (`Email`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4;

/*Data for the table `e_kartu` */

insert  into `e_kartu`(`Id_Kartu`,`Nama_Lengkap`,`Username`,`Password`,`Email`,`Tgl_Lahir`,`Kelamin`,`Tgl_Register`,`Saldo`,`Status_Kartu`) values 
(1,'Abraham Arthur Fendy','ArthurFendy','d09ff7095f292b110574459c147ae602944155e0dc9512542dea2eed8d4a015d','abrahamarthurfendy@gmail.com','2022-11-09 11:28:04','L','2022-11-09 11:28:12',57500.00,1),
(2,'James Martin Fendy','JamesFendy','d09ff7095f292b110574459c147ae602944155e0dc9512542dea2eed8d4a015d','jamesmartinfendy@gmail.com','2008-07-08 00:00:00','L','2022-11-12 10:11:49',10000.00,1),
(3,'Kevin Jonathan','Kevin','0e4dd66217fc8d2e298b78c8cd9392870dcd065d0ff675d0edff5bcd227837e9','kevin@gmail.com','2022-10-23 00:00:00','L','2022-11-23 16:25:08',2000000.00,1),
(4,'Grace Elizabeth Fendy','GraceFendy','d09ff7095f292b110574459c147ae602944155e0dc9512542dea2eed8d4a015d','graceelizabethfendy@gmail.com','2022-10-25 00:00:00','P','2022-11-25 08:06:05',352000.00,1),
(5,'Jojo','Jojo','54af2a2960e582263c45971cdd40da4ae31ede1db5395629d910f056479de12d','jojo@gmail.com','2016-11-23 00:00:00','L','2022-12-23 09:17:46',69500.00,1);

/*Table structure for table `e_tiket` */

DROP TABLE IF EXISTS `e_tiket`;

CREATE TABLE `e_tiket` (
  `Id_Tiket` int(11) NOT NULL AUTO_INCREMENT,
  `Id_Kartu` int(11) NOT NULL,
  `Id_Stasiun_Awal` int(11) NOT NULL,
  `Id_Stasiun_Akhir` int(11) DEFAULT NULL,
  `Id_Rute` int(11) NOT NULL,
  `Harga` decimal(12,2) DEFAULT NULL,
  `Tgl_Cetak` datetime NOT NULL DEFAULT current_timestamp(),
  `Tgl_Masuk` datetime DEFAULT NULL,
  `Tgl_Keluar` datetime DEFAULT NULL,
  `Mode_Tiket` int(1) NOT NULL DEFAULT 2 COMMENT '2 = tiket, 1 = non-tiket',
  `Status_Tiket` int(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`Id_Tiket`),
  KEY `Id_Stasiun_Awal` (`Id_Stasiun_Awal`),
  KEY `Id_Kartu` (`Id_Kartu`),
  KEY `Id_Rute` (`Id_Rute`),
  KEY `Id_Stasiun_Akhir` (`Id_Stasiun_Akhir`),
  CONSTRAINT `e_tiket_ibfk_1` FOREIGN KEY (`Id_Kartu`) REFERENCES `e_kartu` (`Id_Kartu`),
  CONSTRAINT `e_tiket_ibfk_2` FOREIGN KEY (`Id_Stasiun_Awal`) REFERENCES `stasiun` (`Id_Stasiun`),
  CONSTRAINT `e_tiket_ibfk_4` FOREIGN KEY (`Id_Rute`) REFERENCES `rute` (`Id_Rute`),
  CONSTRAINT `e_tiket_ibfk_5` FOREIGN KEY (`Id_Stasiun_Akhir`) REFERENCES `stasiun` (`Id_Stasiun`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4;

/*Data for the table `e_tiket` */

insert  into `e_tiket`(`Id_Tiket`,`Id_Kartu`,`Id_Stasiun_Awal`,`Id_Stasiun_Akhir`,`Id_Rute`,`Harga`,`Tgl_Cetak`,`Tgl_Masuk`,`Tgl_Keluar`,`Mode_Tiket`,`Status_Tiket`) values 
(1,1,3,1,1,40000.00,'2022-11-13 18:08:34','2022-11-19 15:37:03','2022-11-19 15:38:07',2,0),
(2,1,1,6,4,65000.00,'2022-11-12 18:10:30','2022-11-16 08:07:14','2022-11-16 09:07:31',2,0),
(5,1,5,2,3,112500.00,'2022-11-18 17:43:03','2022-12-01 19:28:52','2022-12-01 19:29:19',2,0),
(6,1,2,5,3,112500.00,'2022-11-18 19:29:58',NULL,NULL,2,1),
(10,1,4,2,3,0.00,'2022-11-19 15:55:51','2022-11-19 15:55:51','2022-11-19 15:56:23',1,0),
(11,1,4,2,3,60000.00,'2022-11-19 16:07:34','2022-11-19 16:07:34','2022-11-19 16:08:29',1,0),
(12,1,1,6,4,65000.00,'2022-11-20 06:53:28','2022-11-20 06:53:28','2022-11-20 06:54:23',1,0),
(13,1,3,1,1,40000.00,'2022-11-20 06:57:49','2022-11-20 06:57:49','2022-11-20 06:58:01',1,0),
(14,4,7,4,2,48000.00,'2022-12-01 19:23:42',NULL,NULL,2,1),
(15,1,3,1,1,40000.00,'2022-12-03 07:18:57','2022-12-21 08:58:30','2022-12-21 08:58:46',2,0),
(16,1,1,3,1,40000.00,'2022-12-21 08:04:59',NULL,NULL,2,1),
(17,1,1,6,4,65000.00,'2022-12-23 09:09:21','2022-12-23 09:11:09','2022-12-23 09:11:52',2,0),
(18,1,3,1,1,40000.00,'2022-12-23 09:12:28',NULL,NULL,2,1),
(19,1,3,1,1,40000.00,'2022-12-23 09:12:56','2022-12-23 09:12:56','2022-12-23 09:13:34',1,0),
(20,5,3,5,4,50000.00,'2022-12-23 10:00:43','2022-12-23 10:02:51','2022-12-23 10:04:32',2,0),
(21,5,5,4,3,52500.00,'2022-12-23 10:01:08','2022-12-23 10:04:57','2022-12-23 10:05:22',2,0),
(22,5,5,4,3,52500.00,'2022-12-23 10:07:02','2022-12-23 10:07:02','2022-12-23 10:08:07',1,0),
(23,5,4,7,2,48000.00,'2022-12-23 10:12:24','2022-12-23 10:12:24','2022-12-23 10:12:49',1,0);

/*Table structure for table `kereta` */

DROP TABLE IF EXISTS `kereta`;

CREATE TABLE `kereta` (
  `Id_Kereta` int(11) NOT NULL AUTO_INCREMENT,
  `Id_Rute` int(11) NOT NULL,
  `Nama_Kereta` varchar(50) NOT NULL,
  `Gerbong` int(11) NOT NULL,
  `Status_Kereta` int(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`Id_Kereta`),
  KEY `Id_Rute` (`Id_Rute`),
  CONSTRAINT `kereta_ibfk_1` FOREIGN KEY (`Id_Rute`) REFERENCES `rute` (`Id_Rute`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4;

/*Data for the table `kereta` */

insert  into `kereta`(`Id_Kereta`,`Id_Rute`,`Nama_Kereta`,`Gerbong`,`Status_Kereta`) values 
(1,1,'adsa',4,1);

/*Table structure for table `rute` */

DROP TABLE IF EXISTS `rute`;

CREATE TABLE `rute` (
  `Id_Rute` int(11) NOT NULL AUTO_INCREMENT,
  `Nama_Rute` varchar(50) NOT NULL,
  `PPM` decimal(8,2) NOT NULL,
  `Status_Rute` int(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`Id_Rute`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4;

/*Data for the table `rute` */

insert  into `rute`(`Id_Rute`,`Nama_Rute`,`PPM`,`Status_Rute`) values 
(1,'ISTTS-Pakuwon Mall',100.00,1),
(2,'Purimas-Carnival',80.00,1),
(3,'Galaxy Mall-City Hall',75.00,1),
(4,'Pakuwon-ISTTS',50.00,1),
(5,'Carnival-Zoo',80.00,1),
(6,'ISTTS-Carnival',30.00,1);

/*Table structure for table `stasiun` */

DROP TABLE IF EXISTS `stasiun`;

CREATE TABLE `stasiun` (
  `Id_Stasiun` int(11) NOT NULL AUTO_INCREMENT,
  `Alamat` varchar(100) NOT NULL,
  `Nama_Stasiun` varchar(50) NOT NULL,
  `Status_Stasiun` int(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`Id_Stasiun`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4;

/*Data for the table `stasiun` */

insert  into `stasiun`(`Id_Stasiun`,`Alamat`,`Nama_Stasiun`,`Status_Stasiun`) values 
(1,'Jl. Raya Laguna KJW Putih Tambak No.2, Kejawaan Putih Tamba, Kec. Mulyorejo, Kota SBY, Jawa Timur 60','Pakuwon City Mall',1),
(2,'Galaxy Mall Lantai 1. 242 - 245, Jl. Dharmahusada Indah Timur No.37, Mulyorejo, Kec. Mulyorejo, Kota','Galaxy Mall 1',1),
(3,'Jl. Ngagel Jaya Tengah No.73-77, Baratajaya, Kec. Gubeng, Kota SBY, Jawa Timur 60284','ISTTS',1),
(4,'Jl. I Gusti Ngurah Rai No.44, Gn. Anyar, Kec. Gn. Anyar, Kota SBY, Jawa Timur 60294','Apartemen Purimas Sewa',1),
(5,'Jl. Walikota Mustajab No.59, Ketabang, Kec. Genteng, Kota SBY, Jawa Timur 60272','Surabaya City Hall',1),
(6,'Jl. Setail No.1, Darmo, Kec. Wonokromo, Kota SBY, Jawa Timur 60241','Surabaya Zoo',1),
(7,'Jl. Ahmad Yani No.333, Dukuh Menanggal, Kec. Gayungan, Kota SBY, Jawa Timur 60234','Carnival Park Surabaya',1),
(8,'tes','tes',2),
(9,'adsadas','asdasd',2);

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
