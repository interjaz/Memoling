<?php
class LanguageTranslations {
	public static function convertIso6391To6393($language) {
		switch ($language) {
			case 'af' :     return 'afr';
			case 'ar' :     return 'ara';
			case 'be' :     return 'bel';
			case 'bg' :     return 'bul';
			case 'br' :     return 'bre';
			case 'cs' :     return 'ces';
			case 'de' :     return 'deu';
			case 'el' :     return 'ell';
			case 'en' :     return 'eng';
			case 'eo' :     return 'epo';
			case 'es' :     return 'spa';
			case 'et' :     return 'est';
			case 'fi' :     return 'fin';
			case 'fa' :     return 'pes';
			case 'fr' :     return 'fra';
			case 'fo' :     return 'fao';
			case 'fy' :     return 'fry';
			case 'hu' :     return 'hun';
			case 'iw' :     return 'heb';
			case 'is' :     return 'isl';
			case 'it' :     return 'ita';
			case 'id' :     return 'ind';
			case 'ja' :     return 'jpn';
			case 'ka' :     return 'kat';
			case 'ko' :     return 'kor';
			case 'ms' :     return 'zsm';
			case 'nl' :     return 'nld';
			case 'pl' :     return 'pol';
			case 'pt' :     return 'por';
			case 'pt-BR' :  return 'por';
			case 'pt-PT' :  return 'por';
			case 'ro' :     return 'ron';
			case 'ru' :     return 'rus';
			case 'sq' :     return 'sqi';
			case 'sv' :     return 'swe';
			case 'sr' :     return 'srp';
			case 'tr' :     return 'tur';
			case 'ug' :     return 'uig';
			case 'uk' :     return 'ukr';
			case 'uz' :     return 'uzb';
			case 'vi' :     return 'vie';
			case 'sw' :     return 'swh';
			case 'zh-CN' :  return 'cmn';
			case 'zh-TW' :  return 'cmn';
		}
		return null;
	}
	public static function convertIso6393To6391($language) {
		switch ($language) {
			case  'afr':	return 'af' ;
			case  'ara':	return 'ar' ;
			case  'bel':	return 'be' ;
			case  'bul':	return 'bg' ;
			case  'bre':	return 'br' ;
			case  'ces':	return 'cs' ;
			case  'deu':	return 'de' ;
			case  'ell':	return 'el' ;
			case  'eng':	return 'en' ;
			case  'epo':	return 'eo' ;
			case  'spa':	return 'es' ;
			case  'est':	return 'et' ;
			case  'fin':	return 'fi' ;
			case  'pes':	return 'fa' ;
			case  'fra':	return 'fr' ;
			case  'fao':	return 'fo' ;
			case  'fry':	return 'fy' ;
			case  'hun':	return 'hu' ;
			case  'heb':	return 'iw' ;
			case  'isl':	return 'is' ;
			case  'ita':	return 'it' ;
			case  'ind':	return 'id' ;
			case  'jpn':	return 'ja' ;
			case  'kat':	return 'ka' ;
			case  'kor':	return 'ko' ;
			case  'zsm':	return 'ms' ;
			case  'nld':	return 'nl' ;
			case  'pol':	return 'pl' ;
			case  'por':	return 'pt' ;
			case  'ron':	return 'ro' ;
			case  'rus':	return 'ru' ;
			case  'sqi':	return 'sq' ;
			case  'swe':	return 'sv' ;
			case  'srp':	return 'sr' ;
			case  'tur':	return 'tr' ;
			case  'uig':	return 'ug' ;
			case  'ukr':	return 'uk' ;
			case  'uzb':	return 'uz' ;
			case  'vie':	return 'vi' ;
			case  'swh':	return 'sw' ;
			case  'cmn':	return 'zh-CN' ;
		}
		return null;
	}
	public static function languageIso6393ToInt($language) {
		switch($language) {
			case 'cmn':	return 1;
			case 'fra':	return 2;
			case 'deu':	return 3;
			case 'ita':	return 4;
			case 'eng':	return 5;
			case 'jpn':	return 6;
			case 'tur':	return 7;
			case 'rus':	return 8;
			case 'nld':	return 9;
			case 'vie':	return 10;
			case 'spa':	return 11;
			case 'epo':	return 12;
			case 'ara':	return 13;
			case 'fin':	return 14;
			case 'hin':	return 15;
			case 'nob':	return 16;
			case 'por':	return 17;
			case 'pol':	return 18;
			case 'wuu':	return 19;
			case 'isl':	return 20;
			case 'bel':	return 21;
			case 'lat':	return 22;
			case 'ukr':	return 23;
			case 'kor':	return 24;
			case 'jbo':	return 25;
			case 'ces':	return 26;
			case 'ron':	return 27;
			case 'tlh':	return 28;
			case 'hun':	return 29;
			case 'srp':	return 30;
			case 'ido':	return 31;
			case 'swh':	return 32;
			case 'eus':	return 33;
			case 'ell':	return 34;
			case 'yue':	return 35;
			case 'pes':	return 36;
			case 'arz':	return 37;
			case 'heb':	return 38;
			case 'ber':	return 39;
			case 'lit':	return 40;
			case 'swe':	return 41;
			case 'dan':	return 42;
			case 'xal':	return 43;
			case 'zsm':	return 44;
			case 'est':	return 45;
			case 'uzb':	return 46;
			case 'hsb':	return 47;
			case 'nds':	return 48;
			case 'mal':	return 49;
			case 'lzh':	return 50;
			case 'ina':	return 51;
			case 'uig':	return 52;
			case 'tgl':	return 53;
			case 'bul':	return 54;
			case 'bos':	return 55;
			case 'sqi':	return 56;
			case 'toki':	return 57;
			case 'glg':	return 58;
			case 'ind':	return 59;
			case 'oci':	return 60;
			case 'dsb':	return 61;
			case 'hrv':	return 62;
			case 'que':	return 63;
			case 'non':	return 64;
			case 'kaz':	return 65;
			case 'kat':	return 66;
			case 'urd':	return 67;
			case 'tat':	return 68;
			case 'afr':	return 69;
			case 'fry':	return 70;
			case 'bre':	return 71;
			case 'fao':	return 72;
			case 'san':	return 73;
			case 'yid':	return 74;
			case 'qya':	return 75;
			case 'nan':	return 76;
			case 'slk':	return 77;
			case 'cycl':	return 78;
			case 'acm':	return 79;
			case 'lvs':	return 80;
			case 'mar':	return 81;
			case 'hye':	return 82;
			case 'ben':	return 83;
			case 'cat':	return 84;
			case 'tha':	return 85;
			case 'orv':	return 86;
			case 'cha':	return 87;
			case 'mon':	return 88;
			case 'slv':	return 89;
			case 'cym':	return 90;
			case 'scn':	return 91;
			case 'gle':	return 92;
			case 'gla':	return 93;
			case 'ain':	return 94;
			case 'vol':	return 95;
			case 'avk':	return 96;
			case 'roh':	return 97;
			case 'tpi':	return 98;
			case 'oss':	return 99;
			case '\N':	return 100;
			case 'ast':	return 101;
			case 'mlt':	return 102;
			case 'cor':	return 103;
			case 'ile':	return 104;
			case 'tel':	return 105;
			case 'sjn':	return 106;
			case 'nov':	return 107;
			case 'ang':	return 108;
			case 'tgk':	return 109;
			case 'bod':	return 110;
			case '':	return 111;
			case 'mri':	return 112;
			case 'ksh':	return 113;
			case 'kur':	return 114;
			case 'ewe':	return 115;
			case 'tpw':	return 116;
			case 'lld':	return 117;
			case 'pms':	return 118;
			case 'lad':	return 119;
			case 'grn':	return 120;
			case 'mlg':	return 121;
			case 'xho':	return 122;
			case 'pnb':	return 123;
			case 'npi':	return 124;
			case 'aze':	return 125;
			case 'prg':	return 126;
			case 'ckt':	return 127;
			case 'hil':	return 128;
			case 'lao':	return 129;
			case 'khm':	return 130;
			case 'N':	return 131;
			case 'arq':	return 132;
			case 'grc':	return 133;
			case 'pcd':	return 134;
		}
	}
	public static function languageIntToIso6393($int) {
		switch($int) {
			case 1:		return 'cmn';
			case 2:		return 'fra';
			case 3:		return 'deu';
			case 4:		return 'ita';
			case 5:		return 'eng';
			case 6:		return 'jpn';
			case 7:		return 'tur';
			case 8:		return 'rus';
			case 9:		return 'nld';
			case 10:	return 'vie';
			case 11:	return 'spa';
			case 12:	return 'epo';
			case 13:	return 'ara';
			case 14:	return 'fin';
			case 15:	return 'hin';
			case 16:	return 'nob';
			case 17:	return 'por';
			case 18:	return 'pol';
			case 19:	return 'wuu';
			case 20:	return 'isl';
			case 21:	return 'bel';
			case 22:	return 'lat';
			case 23:	return 'ukr';
			case 24:	return 'kor';
			case 25:	return 'jbo';
			case 26:	return 'ces';
			case 27:	return 'ron';
			case 28:	return 'tlh';
			case 29:	return 'hun';
			case 30:	return 'srp';
			case 31:	return 'ido';
			case 32:	return 'swh';
			case 33:	return 'eus';
			case 34:	return 'ell';
			case 35:	return 'yue';
			case 36:	return 'pes';
			case 37:	return 'arz';
			case 38:	return 'heb';
			case 39:	return 'ber';
			case 40:	return 'lit';
			case 41:	return 'swe';
			case 42:	return 'dan';
			case 43:	return 'xal';
			case 44:	return 'zsm';
			case 45:	return 'est';
			case 46:	return 'uzb';
			case 47:	return 'hsb';
			case 48:	return 'nds';
			case 49:	return 'mal';
			case 50:	return 'lzh';
			case 51:	return 'ina';
			case 52:	return 'uig';
			case 53:	return 'tgl';
			case 54:	return 'bul';
			case 55:	return 'bos';
			case 56:	return 'sqi';
			case 57:	return 'toki';
			case 58:	return 'glg';
			case 59:	return 'ind';
			case 60:	return 'oci';
			case 61:	return 'dsb';
			case 62:	return 'hrv';
			case 63:	return 'que';
			case 64:	return 'non';
			case 65:	return 'kaz';
			case 66:	return 'kat';
			case 67:	return 'urd';
			case 68:	return 'tat';
			case 69:	return 'afr';
			case 70:	return 'fry';
			case 71:	return 'bre';
			case 72:	return 'fao';
			case 73:	return 'san';
			case 74:	return 'yid';
			case 75:	return 'qya';
			case 76:	return 'nan';
			case 77:	return 'slk';
			case 78:	return 'cycl';
			case 79:	return 'acm';
			case 80:	return 'lvs';
			case 81:	return 'mar';
			case 82:	return 'hye';
			case 83:	return 'ben';
			case 84:	return 'cat';
			case 85:	return 'tha';
			case 86:	return 'orv';
			case 87:	return 'cha';
			case 88:	return 'mon';
			case 89:	return 'slv';
			case 90:	return 'cym';
			case 91:	return 'scn';
			case 92:	return 'gle';
			case 93:	return 'gla';
			case 94:	return 'ain';
			case 95:	return 'vol';
			case 96:	return 'avk';
			case 97:	return 'roh';
			case 98:	return 'tpi';
			case 99:	return 'oss';
			case 100:	return '\N';
			case 101:	return 'ast';
			case 102:	return 'mlt';
			case 103:	return 'cor';
			case 104:	return 'ile';
			case 105:	return 'tel';
			case 106:	return 'sjn';
			case 107:	return 'nov';
			case 108:	return 'ang';
			case 109:	return 'tgk';
			case 110:	return 'bod';
			case 111:	return '';
			case 112:	return 'mri';
			case 113:	return 'ksh';
			case 114:	return 'kur';
			case 115:	return 'ewe';
			case 116:	return 'tpw';
			case 117:	return 'lld';
			case 118:	return 'pms';
			case 119:	return 'lad';
			case 120:	return 'grn';
			case 121:	return 'mlg';
			case 122:	return 'xho';
			case 123:	return 'pnb';
			case 124:	return 'npi';
			case 125:	return 'aze';
			case 126:	return 'prg';
			case 127:	return 'ckt';
			case 128:	return 'hil';
			case 129:	return 'lao';
			case 130:	return 'khm';
			case 131:	return 'N';
			case 132:	return 'arq';
			case 133:	return 'grc';
			case 134:	return 'pcd';
		}
	}
}
?>
