# --------------------------------------------------------
# Host:                         127.0.0.1
# Server version:               5.5.13
# Server OS:                    Win32
# HeidiSQL version:             6.0.0.3603
# Date/time:                    2011-07-02 20:11:28
# --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

# Dumping database structure for quickorder
DROP DATABASE IF EXISTS `quickorder`;
CREATE DATABASE IF NOT EXISTS `quickorder` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `quickorder`;


# Dumping structure for table quickorder.clienti
DROP TABLE IF EXISTS `clienti`;
CREATE TABLE IF NOT EXISTS `clienti` (
  `COD_FISCALE` varchar(255) NOT NULL,
  `EMAIL` varchar(255) NOT NULL,
  `NOME` varchar(255) NOT NULL,
  `COGNOME` varchar(255) NOT NULL,
  `SESSO` char(1) NOT NULL,
  `DATA_NASCITA` datetime NOT NULL,
  `LUOGO_NASCITA` varchar(255) NOT NULL,
  `IMEI` varchar(255) NOT NULL,
  `ABILITAZIONE` bit(1) NOT NULL,
  PRIMARY KEY (`COD_FISCALE`),
  UNIQUE KEY `EMAIL` (`EMAIL`),
  UNIQUE KEY `IMEI` (`IMEI`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

# Dumping data for table quickorder.clienti: ~2 rows (approximately)
/*!40000 ALTER TABLE `clienti` DISABLE KEYS */;
REPLACE INTO `clienti` (`COD_FISCALE`, `EMAIL`, `NOME`, `COGNOME`, `SESSO`, `DATA_NASCITA`, `LUOGO_NASCITA`, `IMEI`, `ABILITAZIONE`) VALUES
	('GLLMRA83H23H703Z', 'mario.gallo@alice.it', 'Mario', 'Gallo', 'M', '1983-05-23 19:23:04', 'Salerno', '00808515260608076', ''),
	('RSSNRTAA20123AAA', 'anna@russo.com', 'Anna Rita', 'Russo', 'F', '1987-12-20 19:09:26', 'Battipaglia', '35410732558882270', '');
/*!40000 ALTER TABLE `clienti` ENABLE KEYS */;


# Dumping structure for table quickorder.ordinazione_prodotto
DROP TABLE IF EXISTS `ordinazione_prodotto`;
CREATE TABLE IF NOT EXISTS `ordinazione_prodotto` (
  `ORDINAZIONE_ID` bigint(20) NOT NULL,
  `QUANTITA` int(11) DEFAULT NULL,
  `SUBTOTALE` double DEFAULT NULL,
  `NOTE` varchar(255) DEFAULT NULL,
  `PRODOTTO_ID` varchar(255) DEFAULT NULL,
  KEY `FK16B29B9A81FF21FA` (`ORDINAZIONE_ID`),
  KEY `FK16B29B9A5185749A` (`PRODOTTO_ID`),
  CONSTRAINT `FK16B29B9A5185749A` FOREIGN KEY (`PRODOTTO_ID`) REFERENCES `prodotti` (`CODICE`),
  CONSTRAINT `FK16B29B9A81FF21FA` FOREIGN KEY (`ORDINAZIONE_ID`) REFERENCES `ordinazioni` (`ORDINAZIONE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

# Dumping data for table quickorder.ordinazione_prodotto: ~0 rows (approximately)
/*!40000 ALTER TABLE `ordinazione_prodotto` DISABLE KEYS */;
/*!40000 ALTER TABLE `ordinazione_prodotto` ENABLE KEYS */;


# Dumping structure for table quickorder.ordinazioni
DROP TABLE IF EXISTS `ordinazioni`;
CREATE TABLE IF NOT EXISTS `ordinazioni` (
  `ORDINAZIONE_ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `FKID_CLIENTE` varchar(255) NOT NULL,
  `DATA` datetime NOT NULL,
  PRIMARY KEY (`ORDINAZIONE_ID`),
  KEY `FKC5A46040334C4D15` (`FKID_CLIENTE`),
  CONSTRAINT `FKC5A46040334C4D15` FOREIGN KEY (`FKID_CLIENTE`) REFERENCES `clienti` (`COD_FISCALE`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

# Dumping data for table quickorder.ordinazioni: ~0 rows (approximately)
/*!40000 ALTER TABLE `ordinazioni` DISABLE KEYS */;
/*!40000 ALTER TABLE `ordinazioni` ENABLE KEYS */;


# Dumping structure for table quickorder.prodotti
DROP TABLE IF EXISTS `prodotti`;
CREATE TABLE IF NOT EXISTS `prodotti` (
  `CODICE` varchar(255) NOT NULL,
  `NOME` varchar(255) NOT NULL,
  `PREZZO` double NOT NULL,
  `TIPOLOGIA` int(11) NOT NULL,
  `DESCRIZIONE` varchar(255) NOT NULL,
  `VERSIONE` int(11) NOT NULL,
  PRIMARY KEY (`CODICE`),
  UNIQUE KEY `NOME` (`NOME`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

# Dumping data for table quickorder.prodotti: ~12 rows (approximately)
/*!40000 ALTER TABLE `prodotti` DISABLE KEYS */;
REPLACE INTO `prodotti` (`CODICE`, `NOME`, `PREZZO`, `TIPOLOGIA`, `DESCRIZIONE`, `VERSIONE`) VALUES
	('B0001', 'Coca-Cola 33cc', 1.2, 1, 'Nulla ti rinfresca di più di una bella coca-cola ghiacciata. Un classico!', 1),
	('B0002', 'Coca-Cola 50cc', 2, 1, 'Nulla ti rinfresca di più di una bella coca-cola ghiacciata. Un classico!', 1),
	('B0003', 'Coca-Cola 1,5L', 2.5, 1, 'Nulla ti rinfresca di più di una bella coca-cola ghiacciata. Un classico!', 1),
	('B0004', 'Acqua Naturale 0,5L', 0.8, 1, 'E\' acqua!', 0),
	('B0005', 'Acqua Naturale 1,5L', 1.5, 1, 'E\' tanta acqua!', 1),
	('B0006', 'Heineken 33cc', 1.5, 1, 'Birra bionda  lager Premium a bassa fermentazione dal gusto fine ed equilibrato e dall’ aroma moderatamente luppolato.', 2),
	('B0007', 'Vino della casa 0,5L', 2.5, 1, 'Vino secco con gradazione alcolica di 12,5 gradi ottenuto da vitigni Aglianico.', 2),
	('P0001', 'Broccolino', 3.5, 0, 'Niente c\'è di più gustoso e appagante di un panino con broccoli e salsiccia. Salsiccia fresca e broccoli di stagione!', 1),
	('P0002', 'Crudino', 3, 0, 'Un gustoso panino con prosciutto crudo e formaggio. Adatto per tutti i palati!', 0),
	('P0003', 'Caprese', 3.5, 0, 'Un gustoso panino con pomodorini e mozzarella. Ideale per i mediterranei!', 2),
	('P0004', 'Tonnarello', 2.5, 0, 'Un ricco panino con pomodoro e tonno! Da leccarsi i baffi!', 2),
	('P0005', 'Boscaiolo', 4, 0, 'Una vera goduria questo panino con salsiccia, provola e salsa di funghi. Una vera leccornia da gustare in compagnia.', 2);
/*!40000 ALTER TABLE `prodotti` ENABLE KEYS */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
