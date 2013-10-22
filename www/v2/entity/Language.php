<?php

class Language {
	
	public $Name;
	public $Iso;
	
	public function __construct($name, $iso) {
		$this->Name = $name;
		$this->Iso = $iso;
	}
	
	private static $m_languages;

	public static function getLanguages() {
		
		if(isset(Language::$m_languages)) {
			return Language::$m_languages;
		}		
		
		$lang[] = new Language("","");
		$lang[] = new Language("Abkhaz","ab");
		$lang[] = new Language("Afar","aa");
		$lang[] = new Language("Afrikaans","af");
		$lang[] = new Language("Akan","ak");
		$lang[] = new Language("Albanian","sq");
		$lang[] = new Language("Amharic","am");
		$lang[] = new Language("Arabic","ar");
		$lang[] = new Language("Aragonese","an");
		$lang[] = new Language("Armenian","hy");
		$lang[] = new Language("Assamese","as");
		$lang[] = new Language("Avaric","av");
		$lang[] = new Language("Avestan","ae");
		$lang[] = new Language("Aymara","ay");
		$lang[] = new Language("Azerbaijani","az");
		$lang[] = new Language("Bambara","bm");
		$lang[] = new Language("Bashkir","ba");
		$lang[] = new Language("Basque","eu");
		$lang[] = new Language("Belarusian","be");
		$lang[] = new Language("Bengali","bn");
		$lang[] = new Language("Bihari","bh");
		$lang[] = new Language("Bislama","bi");
		$lang[] = new Language("Bosnian","bs");
		$lang[] = new Language("Breton","br");
		$lang[] = new Language("Bulgarian","bg");
		$lang[] = new Language("Burmese","my");
		$lang[] = new Language("Catalan","ca");
		$lang[] = new Language("Chamorro","ch");
		$lang[] = new Language("Chechen","ce");
		$lang[] = new Language("Chichewa","ny");
		$lang[] = new Language("Chinese","zh");
		$lang[] = new Language("Chuvash","cv");
		$lang[] = new Language("Cornish","kw");
		$lang[] = new Language("Corsican","co");
		$lang[] = new Language("Cree","cr");
		$lang[] = new Language("Croatian","hr");
		$lang[] = new Language("Czech","cs");
		$lang[] = new Language("Danish","da");
		$lang[] = new Language("Divehi","div");
		$lang[] = new Language("Dutch","nl");
		$lang[] = new Language("Dzongkha","dz");
		$lang[] = new Language("English","en");
		$lang[] = new Language("Esperanto","eo");
		$lang[] = new Language("Estonian","et");
		$lang[] = new Language("Ewe","ee");
		$lang[] = new Language("Faroese","fo");
		$lang[] = new Language("Fijian","fj");
		$lang[] = new Language("Finnish","fi");
		$lang[] = new Language("French","fr");
		$lang[] = new Language("Fula","ff");
		$lang[] = new Language("Galician","gl");
		$lang[] = new Language("Georgian","ka");
		$lang[] = new Language("German","de");
		$lang[] = new Language("Greek, Modern","el");
		$lang[] = new Language("Guaraní","gn");
		$lang[] = new Language("Gujarati","gu");
		$lang[] = new Language("Haitian","ht");
		$lang[] = new Language("Hausa","ha");
		$lang[] = new Language("Hebrew (modern)","he");
		$lang[] = new Language("Herero","hz");
		$lang[] = new Language("Hindi","hi");
		$lang[] = new Language("Hiri Motu","ho");
		$lang[] = new Language("Hungarian","hu");
		$lang[] = new Language("Interlingua","ia");
		$lang[] = new Language("Indonesian","id");
		$lang[] = new Language("Interlingue","ie");
		$lang[] = new Language("Irish","ga");
		$lang[] = new Language("Igbo","ig");
		$lang[] = new Language("Inupiaq","ik");
		$lang[] = new Language("Ido","io");
		$lang[] = new Language("Icelandic","is");
		$lang[] = new Language("Italian","it");
		$lang[] = new Language("Inuktitut","iu");
		$lang[] = new Language("Japanese","ja");
		$lang[] = new Language("Javanese","jv");
		$lang[] = new Language("Kalaallisut, Greenlandic","kl");
		$lang[] = new Language("Kannada","kn");
		$lang[] = new Language("Kanuri","kr");
		$lang[] = new Language("Kashmiri","ks");
		$lang[] = new Language("Kazakh","kk");
		$lang[] = new Language("Khmer","km");
		$lang[] = new Language("Kikuyu, Gikuyu","ki");
		$lang[] = new Language("Kinyarwanda","rw");
		$lang[] = new Language("Kyrgyz","ky");
		$lang[] = new Language("Komi","kv");
		$lang[] = new Language("Kongo","kg");
		$lang[] = new Language("Korean","ko");
		$lang[] = new Language("Kurdish","ku");
		$lang[] = new Language("Kwanyama, Kuanyama","kj");
		$lang[] = new Language("Latin","la");
		$lang[] = new Language("Luxembourgish, Letzeburgesch","lb");
		$lang[] = new Language("Ganda","lg");
		$lang[] = new Language("Limburgish, Limburgan, Limburger","li");
		$lang[] = new Language("Lingala","ln");
		$lang[] = new Language("Lao","lo");
		$lang[] = new Language("Lithuanian","lt");
		$lang[] = new Language("Luba-Katanga","lu");
		$lang[] = new Language("Latvian","lv");
		$lang[] = new Language("Manx","gv");
		$lang[] = new Language("Macedonian","mk");
		$lang[] = new Language("Malagasy","mg");
		$lang[] = new Language("Malay","ms");
		$lang[] = new Language("Malayalam","ml");
		$lang[] = new Language("Maltese","mt");
		$lang[] = new Language("Maori","mi");
		$lang[] = new Language("Marathi (Mara?hi)","mr");
		$lang[] = new Language("Marshallese","mh");
		$lang[] = new Language("Mongolian","mn");
		$lang[] = new Language("Nauru","na");
		$lang[] = new Language("Navajo, Navaho","nv");
		$lang[] = new Language("Norwegian Bokmål","nb");
		$lang[] = new Language("North Ndebele","nd");
		$lang[] = new Language("Nepali","ne");
		$lang[] = new Language("Ndonga","ng");
		$lang[] = new Language("Norwegian Nynorsk","nn");
		$lang[] = new Language("Norwegian","no");
		$lang[] = new Language("Nuosu","ii");
		$lang[] = new Language("South Ndebele","nr");
		$lang[] = new Language("Occitan","oc");
		$lang[] = new Language("Ojibwe, Ojibwa","oj");
		$lang[] = new Language("Old Church Slavonic, Church Slavic, Church Slavonic, Old Bulgarian, Old Slavonic","cu");
		$lang[] = new Language("Oromo","om");
		$lang[] = new Language("Oriya","or");
		$lang[] = new Language("Ossetian, Ossetic","os");
		$lang[] = new Language("Panjabi, Punjabi","pa");
		$lang[] = new Language("Pali","pi");
		$lang[] = new Language("Persian","fa");
		$lang[] = new Language("Polish","pl");
		$lang[] = new Language("Pashto, Pushto","ps");
		$lang[] = new Language("Portuguese","pt");
		$lang[] = new Language("Quechua","qu");
		$lang[] = new Language("Romansh","rm");
		$lang[] = new Language("Kirundi","rn");
		$lang[] = new Language("Romanian, Moldavian(Romanian from Republic of Moldova)","ro");
		$lang[] = new Language("Russian","ru");
		$lang[] = new Language("Sanskrit (Sa?sk?ta)","sa");
		$lang[] = new Language("Sardinian","sc");
		$lang[] = new Language("Sindhi","sd");
		$lang[] = new Language("Northern Sami","se");
		$lang[] = new Language("Samoan","sm");
		$lang[] = new Language("Sango","sg");
		$lang[] = new Language("Serbian","sr");
		$lang[] = new Language("Scottish Gaelic","gd");
		$lang[] = new Language("Shona","sn");
		$lang[] = new Language("Sinhala, Sinhalese","si");
		$lang[] = new Language("Slovak","sk");
		$lang[] = new Language("Slovene","sl");
		$lang[] = new Language("Somali","so");
		$lang[] = new Language("Southern Sotho","st");
		$lang[] = new Language("Spanish","es");
		$lang[] = new Language("Sundanese","su");
		$lang[] = new Language("Swahili","sw");
		$lang[] = new Language("Swati","ss");
		$lang[] = new Language("Swedish","sv");
		$lang[] = new Language("Tamil","ta");
		$lang[] = new Language("Telugu","te");
		$lang[] = new Language("Tajik","tg");
		$lang[] = new Language("Thai","th");
		$lang[] = new Language("Tigrinya","ti");
		$lang[] = new Language("Tibetan Standard, Tibetan, Central","bo");
		$lang[] = new Language("Turkmen","tk");
		$lang[] = new Language("Tagalog","tl");
		$lang[] = new Language("Tswana","tn");
		$lang[] = new Language("Tonga (Tonga Islands)","to");
		$lang[] = new Language("Turkish","tr");
		$lang[] = new Language("Tsonga","ts");
		$lang[] = new Language("Tatar","tt");
		$lang[] = new Language("Twi","tw");
		$lang[] = new Language("Tahitian","ty");
		$lang[] = new Language("Uighur, Uyghur","ug");
		$lang[] = new Language("Ukrainian","uk");
		$lang[] = new Language("Urdu","ur");
		$lang[] = new Language("Uzbek","uz");
		$lang[] = new Language("Venda","ve");
		$lang[] = new Language("Vietnamese","vi");
		$lang[] = new Language("Volapük","vo");
		$lang[] = new Language("Walloon","wa");
		$lang[] = new Language("Welsh","cy");
		$lang[] = new Language("Wolof","wo");
		$lang[] = new Language("Western Frisian","fy");
		$lang[] = new Language("Xhosa","xh");
		$lang[] = new Language("Yiddish","yi");
		$lang[] = new Language("Yoruba","yo");
		$lang[] = new Language("Zhuang, Chuang","za");
		$lang[] = new Language("Zulu","zu");
		
		Language::$m_languages = $lang;
		return Language::$m_languages;
	}
	
	public static function parse($code) {
		$languages = Language::getLanguages();
		
		foreach($languages as $language) {
			if(strcasecmp($code,$language->Iso) == 0) {
				return $language;
			}
		}
		
		return $languages[0];
	}
}

					
?>