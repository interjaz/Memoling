package app.memoling.android.entity;

import java.util.Locale;

/**
 * Uses ISO-693-1 notation
 * @author Bartosz
 *
 */
public enum Language {

	Unsupported("",""),
	AB("Abkhaz","ab"),
	AA("Afar","aa"),
	AF("Afrikaans","af"),
	AK("Akan","ak"),
	SQ("Albanian","sq"),
	AM("Amharic","am"),
	AR("Arabic","ar"),
	AN("Aragonese","an"),
	HY("Armenian","hy"),
	AS("Assamese","as"),
	AV("Avaric","av"),
	AE("Avestan","ae"),
	AY("Aymara","ay"),
	AZ("Azerbaijani","az"),
	BM("Bambara","bm"),
	BA("Bashkir","ba"),
	EU("Basque","eu"),
	BE("Belarusian","be"),
	BN("Bengali","bn"),
	BH("Bihari","bh"),
	BI("Bislama","bi"),
	BS("Bosnian","bs"),
	BR("Breton","br"),
	BG("Bulgarian","bg"),
	MY("Burmese","my"),
	CA("Catalan","ca"),
	CH("Chamorro","ch"),
	CE("Chechen","ce"),
	NY("Chichewa","ny"),
	ZH("Chinese","zh"),
	CV("Chuvash","cv"),
	KW("Cornish","kw"),
	CO("Corsican","co"),
	CR("Cree","cr"),
	HR("Croatian","hr"),
	CS("Czech","cs"),
	DA("Danish","da"),
	DIV("Divehi","div"),
	NL("Dutch","nl"),
	DZ("Dzongkha","dz"),
	EN("English","en"),
	EO("Esperanto","eo"),
	ET("Estonian","et"),
	EE("Ewe","ee"),
	FO("Faroese","fo"),
	FJ("Fijian","fj"),
	FI("Finnish","fi"),
	FR("French","fr"),
	FF("Fula","ff"),
	GL("Galician","gl"),
	KA("Georgian","ka"),
	DE("German","de"),
	EL("Greek, Modern","el"),
	GN("Guaraní","gn"),
	GU("Gujarati","gu"),
	HT("Haitian","ht"),
	HA("Hausa","ha"),
	HE("Hebrew (modern)","he"),
	HZ("Herero","hz"),
	HI("Hindi","hi"),
	HO("Hiri Motu","ho"),
	HU("Hungarian","hu"),
	IA("Interlingua","ia"),
	ID("Indonesian","id"),
	IE("Interlingue","ie"),
	GA("Irish","ga"),
	IG("Igbo","ig"),
	IK("Inupiaq","ik"),
	IO("Ido","io"),
	IS("Icelandic","is"),
	IT("Italian","it"),
	IU("Inuktitut","iu"),
	JA("Japanese","ja"),
	JV("Javanese","jv"),
	KL("Kalaallisut, Greenlandic","kl"),
	KN("Kannada","kn"),
	KR("Kanuri","kr"),
	KS("Kashmiri","ks"),
	KK("Kazakh","kk"),
	KM("Khmer","km"),
	KI("Kikuyu, Gikuyu","ki"),
	RW("Kinyarwanda","rw"),
	KY("Kyrgyz","ky"),
	KV("Komi","kv"),
	KG("Kongo","kg"),
	KO("Korean","ko"),
	KU("Kurdish","ku"),
	KJ("Kwanyama, Kuanyama","kj"),
	LA("Latin","la"),
	LB("Luxembourgish, Letzeburgesch","lb"),
	LG("Ganda","lg"),
	LI("Limburgish, Limburgan, Limburger","li"),
	LN("Lingala","ln"),
	LO("Lao","lo"),
	LT("Lithuanian","lt"),
	LU("Luba-Katanga","lu"),
	LV("Latvian","lv"),
	GV("Manx","gv"),
	MK("Macedonian","mk"),
	MG("Malagasy","mg"),
	MS("Malay","ms"),
	ML("Malayalam","ml"),
	MT("Maltese","mt"),
	MI("Maori","mi"),
	MR("Marathi (Mara?hi)","mr"),
	MH("Marshallese","mh"),
	MN("Mongolian","mn"),
	NA("Nauru","na"),
	NV("Navajo, Navaho","nv"),
	NB("Norwegian Bokmål","nb"),
	ND("North Ndebele","nd"),
	NE("Nepali","ne"),
	NG("Ndonga","ng"),
	NN("Norwegian Nynorsk","nn"),
	NO("Norwegian","no"),
	II("Nuosu","ii"),
	NR("South Ndebele","nr"),
	OC("Occitan","oc"),
	OJ("Ojibwe, Ojibwa","oj"),
	CU("Old Church Slavonic, Church Slavic, Church Slavonic, Old Bulgarian, Old Slavonic","cu"),
	OM("Oromo","om"),
	OR("Oriya","or"),
	OS("Ossetian, Ossetic","os"),
	PA("Panjabi, Punjabi","pa"),
	PI("Pali","pi"),
	FA("Persian","fa"),
	PL("Polish","pl"),
	PS("Pashto, Pushto","ps"),
	PT("Portuguese","pt"),
	QU("Quechua","qu"),
	RM("Romansh","rm"),
	RN("Kirundi","rn"),
	RO("Romanian, Moldavian(Romanian from Republic of Moldova)","ro"),
	RU("Russian","ru"),
	SA("Sanskrit (Sa?sk?ta)","sa"),
	SC("Sardinian","sc"),
	SD("Sindhi","sd"),
	SE("Northern Sami","se"),
	SM("Samoan","sm"),
	SG("Sango","sg"),
	SR("Serbian","sr"),
	GD("Scottish Gaelic","gd"),
	SN("Shona","sn"),
	SI("Sinhala, Sinhalese","si"),
	SK("Slovak","sk"),
	SL("Slovene","sl"),
	SO("Somali","so"),
	ST("Southern Sotho","st"),
	ES("Spanish","es"),
	SU("Sundanese","su"),
	SW("Swahili","sw"),
	SS("Swati","ss"),
	SV("Swedish","sv"),
	TA("Tamil","ta"),
	TE("Telugu","te"),
	TG("Tajik","tg"),
	TH("Thai","th"),
	TI("Tigrinya","ti"),
	BO("Tibetan Standard, Tibetan, Central","bo"),
	TK("Turkmen","tk"),
	TL("Tagalog","tl"),
	TN("Tswana","tn"),
	TO("Tonga (Tonga Islands)","to"),
	TR("Turkish","tr"),
	TS("Tsonga","ts"),
	TT("Tatar","tt"),
	TW("Twi","tw"),
	TY("Tahitian","ty"),
	UG("Uighur, Uyghur","ug"),
	UK("Ukrainian","uk"),
	UR("Urdu","ur"),
	UZ("Uzbek","uz"),
	VE("Venda","ve"),
	VI("Vietnamese","vi"),
	VO("Volapük","vo"),
	WA("Walloon","wa"),
	CY("Welsh","cy"),
	WO("Wolof","wo"),
	FY("Western Frisian","fy"),
	XH("Xhosa","xh"),
	YI("Yiddish","yi"),
	YO("Yoruba","yo"),
	ZA("Zhuang, Chuang","za"),
	ZU("Zulu","zu");

	private String m_name;
	private String m_code;
	private int m_value;
	
	private Language(String name, String code) {
		m_name = name;
		m_code = code;
		m_value = calculateValue(code);
	}
	
	public String getCode() {
		return m_code;
	}
	
	public String getName() {
		return m_name;
	}
	
	public static int calculateValue(String code) {
		int value = 0;
		int power = 1;
		code = code.toLowerCase(Locale.US);
		for(int i=code.length()-1;i>=0;i--) {
			value += power * (int)code.charAt(i);
			power *= 100;
		}
		return value;
	}
	
	public int getPosition() {		
		int position = 0;
		
		for(Language language : values()) {
			if(this == language) {
				return position;
			}
			position++;
		}
		
		return 0;
	}
	
	public int getValue() {
		return m_value;
	}
	
	public static Language parse(String object) {
		
		int value = calculateValue(object);
		
		for(Language language : values()) {
			
			if(language.getValue() == value) {
				return language;
			}
		}
		
		// Legacy bug
		if(calculateValue("spa") == value) {
			return Language.ES;
		}
		
		return Language.Unsupported;
	}
}
