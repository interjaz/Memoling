package app.memoling.android.ui.view;

import java.util.ArrayList;
import java.util.List;

import app.memoling.android.entity.WiktionaryInfo;
import app.memoling.android.ui.adapter.IGet;

public class WiktionaryInfoView implements IGet<String> {

	private WiktionaryInfo m_wiktionaryInfo;
	private final static float MB1 = 1024*1024.0f;

	@Override
	public String get(int index) {
		switch(index) {
		default:
		case 0:
			return m_wiktionaryInfo.getName();
		case 1:
			return m_wiktionaryInfo.getDescription();
		case 2:
			return Double.toString(Math.ceil((m_wiktionaryInfo.getDownloadSize()/MB1)*100.0)/100f) + " MB";
		case 3:
			return Double.toString(Math.ceil((m_wiktionaryInfo.getRealSize()/MB1)*100.0)/100f) + " MB";
		case 4:
			return m_wiktionaryInfo.getLanguage().getCode();
		}
	}
	
	public WiktionaryInfo get() {
		return m_wiktionaryInfo;
	}
	
	public WiktionaryInfoView(WiktionaryInfo wiktionaryInfo) {
		m_wiktionaryInfo = wiktionaryInfo;
	}

	public static List<WiktionaryInfoView> getAll(List<WiktionaryInfo> wiktionaryInfos) {
		List<WiktionaryInfoView> views = new ArrayList<WiktionaryInfoView>();
		for (WiktionaryInfo wiktionaryInfo : wiktionaryInfos) {
			views.add(new WiktionaryInfoView(wiktionaryInfo));
		}

		return views;
	}
}
