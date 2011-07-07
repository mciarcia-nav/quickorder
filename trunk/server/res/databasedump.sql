# --------------------------------------------------------
# Host:                         127.0.0.1
# Server version:               5.5.13
# Server OS:                    Win32
# HeidiSQL version:             6.0.0.3603
# Date/time:                    2011-07-07 20:09:22
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

# Dumping data for table quickorder.clienti: ~5 rows (approximately)
/*!40000 ALTER TABLE `clienti` DISABLE KEYS */;
REPLACE INTO `clienti` (`COD_FISCALE`, `EMAIL`, `NOME`, `COGNOME`, `SESSO`, `DATA_NASCITA`, `LUOGO_NASCITA`, `IMEI`, `ABILITAZIONE`) VALUES
	('GLLMRA83H23H703Z', 'mario.gallo@quickorder.com', 'Mario', 'Gallo', 'M', '1983-05-23 19:58:10', 'Salerno', '535801-18-753703-8', '');
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
REPLACE INTO `ordinazione_prodotto` (`ORDINAZIONE_ID`, `QUANTITA`, `SUBTOTALE`, `NOTE`, `PRODOTTO_ID`) VALUES
	(6, 1, 2.5, '', 'P0006'),
	(6, 1, 2.5, '', 'B0007'),
	(6, 1, 2, '', 'B0002'),
	(6, 1, 1.2, '', 'B0001'),
	(6, 1, 5, '', 'P0007');
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
REPLACE INTO `ordinazioni` (`ORDINAZIONE_ID`, `FKID_CLIENTE`, `DATA`) VALUES
	(6, 'GLLMRA83H23H703Z', '2011-07-07 20:04:24');
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

# Dumping data for table quickorder.prodotti: ~13 rows (approximately)
/*!40000 ALTER TABLE `prodotti` DISABLE KEYS */;
REPLACE INTO `prodotti` (`CODICE`, `NOME`, `PREZZO`, `TIPOLOGIA`, `DESCRIZIONE`, `VERSIONE`) VALUES
	('B0001', 'Coca-Cola 33cc', 1.2, 1, 'Nulla ti rinfresca di più di una bella coca-cola ghiacciata. Un classico!', 1),
	('B0002', 'Coca-Cola 50cc', 2, 1, 'Nulla ti rinfresca di più di una bella coca-cola ghiacciata. Un classico!', 1),
	('B0003', 'Coca-Cola 1,5L', 2.5, 1, 'Nulla ti rinfresca di più di una bella coca-cola ghiacciata. Un classico!', 1),
	('B0004', 'Acqua Naturale 0,5L', 0.8, 1, 'E\' acqua!', 4),
	('B0005', 'Acqua Naturale 1,5L', 1.5, 1, 'E\' tanta acqua!', 1),
	('B0006', 'Heineken 33cc', 1.5, 1, 'Birra bionda  lager Premium a bassa fermentazione dal gusto fine ed equilibrato e dall’ aroma moderatamente luppolato.', 2),
	('B0007', 'Vino della casa 0,5L', 2.5, 1, 'Vino secco con gradazione alcolica di 12,5 gradi ottenuto da vitigni Aglianico.', 2),
	('B0008', 'Leffe Rossa 33cc', 3.5, 1, 'Cosa c\'è di meglio di una bella rossa ghiacciata come compagnia?', 3),
	('B0009', 'Faxe 1L', 4, 1, 'Una latta di birra per quelli più assetati. Si raccomanda di bere responsabilmente', 3),
	('B0010', 'Vodka Smirnoff', 12, 1, ' La Vodka è tra le bevande alcoliche più antiche e più bevute del mondo.', 3),
	('P0001', 'Broccolino', 3.5, 0, 'Niente c\'è di più gustoso e appagante di un panino con broccoli e salsiccia. Salsiccia fresca e broccoli di stagione!', 1),
	('P0002', 'Crudino', 3, 0, 'Un gustoso panino con prosciutto crudo e formaggio. Adatto per tutti i palati!', 4),
	('P0003', 'Caprese', 3.5, 0, 'Un gustoso panino con pomodorini e mozzarella. Ideale per i mediterranei!', 2),
	('P0004', 'Tonnarello', 2.5, 0, 'Un ricco panino con pomodoro e tonno! Da leccarsi i baffi!', 2),
	('P0005', 'Boscaiolo', 4, 0, 'Una vera goduria questo panino con salsiccia, provola e salsa di funghi. Una vera leccornia da gustare in compagnia.', 2),
	('P0006', 'Estivo', 2.5, 0, 'Gustoso panino con zucchine, pomodori e formaggio. Ideale per i mesi estivi.', 2),
	('P0007', 'Vitello Grasso', 5, 0, 'Enorme panino con doppio hamburger. Non adatto ai deboli di cuore.', 3),
	('P0008', 'Pataniello', 3.5, 0, 'Gustoso panino con salsiccia fresca e patatine fritte. Gnam!', 3),
	('P0009', 'Sconosciuto', 4, 0, 'Panino con hamburger, scamorza, crauti, pomodori e cipolla. Avrete ancora fame dopo?', 3),
	('P0010', 'Metropolitano', 4.5, 0, 'Prosciutto cotto arrostito, pomodori, insalata, cipolla rossa, bacon e formaggio fuso per un\'esplosione di sapori.', 3);
/*!40000 ALTER TABLE `prodotti` ENABLE KEYS */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
