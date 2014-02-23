package app.memoling.android.entity;

import java.util.Locale;

import android.content.Context;
import app.memoling.android.R;
import app.memoling.android.preference.Preferences;

/**
 * Uses ISO-693-1 notation
 * @author Bartosz
 *
 */
public enum Language {
	
	Unsupported(0, ""),
	AB(1,"ab"),
	AA(2,"aa"),
	AF(3,"af"),
	AK(4,"ak"),
	SQ(5,"sq"),
	AM(6,"am"),
	AR(7,"ar"),
	AN(8,"an"),
	HY(9,"hy"),
	AS(10,"as"),
	AV(11,"av"),
	AE(12,"ae"),
	AY(13,"ay"),
	AZ(14,"az"),
	BM(15,"bm"),
	BA(16,"ba"),
	EU(17,"eu"),
	BE(18,"be"),
	BN(19,"bn"),
	BH(20,"bh"),
	BI(21,"bi"),
	BS(22,"bs"),
	BR(23,"br"),
	BG(24,"bg"),
	MY(25,"my"),
	CA(26,"ca"),
	CH(27,"ch"),
	CE(28,"ce"),
	NY(29,"ny"),
	ZH(30,"zh"),
	CV(31,"cv"),
	KW(32,"kw"),
	CO(33,"co"),
	CR(34,"cr"),
	HR(35,"hr"),
	CS(36,"cs"),
	DA(37,"da"),
	DIV(38,"div"),
	NL(39,"nl"),
	DZ(40,"dz"),
	EN(41,"en"),
	EO(42,"eo"),
	ET(43,"et"),
	EE(44,"ee"),
	FO(45,"fo"),
	FJ(46,"fj"),
	FI(47,"fi"),
	FR(48,"fr"),
	FF(49,"ff"),
	GL(50,"gl"),
	KA(51,"ka"),
	DE(52,"de"),
	EL(53,"el"),
	GN(54,"gn"),
	GU(55,"gu"),
	HT(56,"ht"),
	HA(57,"ha"),
	HE(58,"he"),
	HZ(59,"hz"),
	HI(60,"hi"),
	HO(61,"ho"),
	HU(62,"hu"),
	IA(63,"ia"),
	ID(64,"id"),
	IE(65,"ie"),
	GA(66,"ga"),
	IG(67,"ig"),
	IK(68,"ik"),
	IO(69,"io"),
	IS(70,"is"),
	IT(71,"it"),
	IU(72,"iu"),
	JA(73,"ja"),
	JV(74,"jv"),
	KL(75,"kl"),
	KN(76,"kn"),
	KR(77,"kr"),
	KS(78,"ks"),
	KK(79,"kk"),
	KM(80,"km"),
	KI(81,"ki"),
	RW(82,"rw"),
	KY(83,"ky"),
	KV(84,"kv"),
	KG(85,"kg"),
	KO(86,"ko"),
	KU(87,"ku"),
	KJ(88,"kj"),
	LA(89,"la"),
	LB(90,"lb"),
	LG(91,"lg"),
	LI(92,"li"),
	LN(93,"ln"),
	LO(94,"lo"),
	LT(95,"lt"),
	LU(96,"lu"),
	LV(97,"lv"),
	GV(98,"gv"),
	MK(99,"mk"),
	MG(100,"mg"),
	MS(101,"ms"),
	ML(102,"ml"),
	MT(103,"mt"),
	MI(104,"mi"),
	MR(105,"mr"),
	MH(106,"mh"),
	MN(107,"mn"),
	NA(108,"na"),
	NV(109,"nv"),
	NB(110,"nb"),
	ND(111,"nd"),
	NE(112,"ne"),
	NG(113,"ng"),
	NN(114,"nn"),
	NO(115,"no"),
	II(116,"ii"),
	NR(117,"nr"),
	OC(118,"oc"),
	OJ(119,"oj"),
	CU(120,"cu"),
	OM(121,"om"),
	OR(122,"or"),
	OS(123,"os"),
	PA(124,"pa"),
	PI(125,"pi"),
	FA(126,"fa"),
	PL(127,"pl"),
	PS(128,"ps"),
	PT(129,"pt"),
	QU(130,"qu"),
	RM(131,"rm"),
	RN(132,"rn"),
	RO(133,"ro"),
	RU(134,"ru"),
	SA(135,"sa"),
	SC(136,"sc"),
	SD(137,"sd"),
	SE(138,"se"),
	SM(139,"sm"),
	SG(140,"sg"),
	SR(141,"sr"),
	GD(142,"gd"),
	SN(143,"sn"),
	SI(144,"si"),
	SK(145,"sk"),
	SL(146,"sl"),
	SO(147,"so"),
	ST(148,"st"),
	ES(149,"es"),
	SU(150,"su"),
	SW(151,"sw"),
	SS(152,"ss"),
	SV(153,"sv"),
	TA(154,"ta"),
	TE(155,"te"),
	TG(156,"tg"),
	TH(157,"th"),
	TI(158,"ti"),
	BO(159,"bo"),
	TK(160,"tk"),
	TL(161,"tl"),
	TN(162,"tn"),
	TO(163,"to"),
	TR(164,"tr"),
	TS(165,"ts"),
	TT(166,"tt"),
	TW(167,"tw"),
	TY(168,"ty"),
	UG(169,"ug"),
	UK(170,"uk"),
	UR(171,"ur"),
	UZ(172,"uz"),
	VE(173,"ve"),
	VI(174,"vi"),
	VO(175,"vo"),
	WA(176,"wa"),
	CY(177,"cy"),
	WO(178,"wo"),
	FY(179,"fy"),
	XH(180,"xh"),
	YI(181,"yi"),
	YO(182,"yo"),
	ZA(183,"za"),
	ZU(184,"zu");

	private int m_resourceId;
	private String m_code;
	private int m_value;
	private static String[] m_resources;
	
	
	private Language(int resourceId, String code) {
		m_resourceId = resourceId;
		m_code = code;
		m_value = calculateValue(code);
	}
	
	public static void init(Context context) {
		m_resources = context.getResources().getStringArray(R.array.languages);
	}
	
	public String getCode() {
		return m_code;
	}
	
	public String getName(Context context) {
		if(m_resources == null) {
			m_resources = context.getResources().getStringArray(R.array.languages);
		}
		
		try {
			return m_resources[m_resourceId];	
		} catch(Exception ex) {
			return "";
		}
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
	
	public Locale toLocale(Context context) {
		
		if(this == Language.EN) {
			return new Preferences(context).getEnglishAccent();
		}
		
		if(this == Language.ES) {
			return new Locale("spa", "ESP");
		}
		
		if(this == Language.FR) {
			return Locale.FRANCE;
		}
		
		if(this == Language.IT) {
			return Locale.ITALY;
		}

		if(this == Language.DE) {
			return Locale.GERMANY;
		}

		if(this == Language.CA) {
			return Locale.CANADA;
		}

		if(this == Language.ZH) {
			return Locale.CHINA;
		}
		
		if(this == Language.JA) {
			return Locale.JAPAN;
		}
		
		if(this == Language.KR) {
			return Locale.KOREA;
		}
		
		if(this == Language.TW) {
			return Locale.TAIWAN;
		}	

		return new Locale(this.m_code);
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
