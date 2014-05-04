package app.memoling.android.ui.fragment;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import app.memoling.android.R;
import app.memoling.android.adapter.MemoAdapter;
import app.memoling.android.adapter.SyncClientAdapter;
import app.memoling.android.adapter.WordAdapter;
import app.memoling.android.entity.Memo;
import app.memoling.android.entity.MemoSentence;
import app.memoling.android.entity.QuizletDefinition;
import app.memoling.android.entity.Word;
import app.memoling.android.helper.RemovableFragmentPagerAdapter;
import app.memoling.android.helper.SentenceProvider;
import app.memoling.android.helper.ShareHelper;
import app.memoling.android.quizlet.QuizletProvider;
import app.memoling.android.quizlet.QuizletProvider.IQuizletGetDefinitions;
import app.memoling.android.ui.FacebookFragment;
import app.memoling.android.ui.adapter.DrawerAdapter;
import app.memoling.android.ui.view.DrawerView;
import app.memoling.android.webservice.WsSentences.IGetComplete;

import com.actionbarsherlock.view.MenuItem;

public class MemoFragment extends FacebookFragment {

	private final static int TabSize = 3;

	public final static String MemoId = "MemoId";
	public final static String MemoIdCloseAfterwards = "MemoIdCloseAfterwards";

	private ViewPager m_pager;
	private MyPagerAdapter m_adapter;

	private boolean m_memoIdCloseAfterwards = false;
	
	private String m_memoId;
	private MemoAdapter m_memoAdapter;
	private Memo m_memo;
	private List<MemoSentence> m_memoSentences;
	private List<QuizletDefinition> m_memoDefinitionsA;
	private List<QuizletDefinition> m_memoDefinitionsB;

	private String m_originalWordA;
	private String m_originalWordB;
	private String m_originalDescriptionA;
	private String m_originalDescriptionB;
	private boolean m_originalActive;

	private ShareHelper m_shareHelper;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View contentView = super.onCreateView(inflater, container, savedInstanceState,
				inflater.inflate(R.layout.fragment_memo, container, false));
		setTitle(getActivity().getString(R.string.memo_title));

		m_memoAdapter = new MemoAdapter(getActivity());

		m_adapter = new MyPagerAdapter(getChildFragmentManager());
		m_pager = (ViewPager) contentView.findViewById(R.id.memo_pager);
		m_pager.setOffscreenPageLimit(TabSize - 1);
		m_pager.setAdapter(m_adapter);
		m_pager.setClickable(true);

		m_shareHelper = new ShareHelper(this, false);

		return contentView;
	}

	@Override
	public boolean onBackPressed() {

		int item = m_pager.getCurrentItem();
		Fragment fragment = m_adapter.getCachedItem(item);

		if (fragment != null) {
			boolean carryOn = ((IMemoPagerFragment) fragment).onBackPressed();

			if (!carryOn) {				
				return false;
			}

			if (item != 0) {
				m_pager.setCurrentItem(item - 1);
				return false;
			} else {
				if(m_memoIdCloseAfterwards) {
					getActivity().finish();
					return false;
				}
			}
		}

		return super.onBackPressed();
	}

	@Override
	protected void onDataBind(Bundle savedInstanceState) {
		m_memoId = getArguments().getString(MemoId);
		m_memoIdCloseAfterwards = getArguments().get(MemoIdCloseAfterwards) != null;
		
		m_memo = m_memoAdapter.getDeep(m_memoId);
		setTitle(m_memo.getWordA().getWord() + " - " + m_memo.getWordB().getWord());
		setSupportProgress(0.5f);

		m_shareHelper.setId(m_memoId);

		m_originalWordA = m_memo.getWordA().getWord();
		m_originalWordB = m_memo.getWordB().getWord();
		m_originalDescriptionA = m_memo.getWordA().getDescription();
		m_originalDescriptionB = m_memo.getWordB().getDescription();
		m_originalActive = m_memo.getActive();

		if(m_adapter.getCacheSize() > 0) {
			for(int i=0;i<3;i++) {
				bindFragment(m_adapter.getCachedItem(i));
			}
		}
		
		SentenceProvider.getSentences(getActivity(), m_memo.getWordA().getWord(), m_memo.getMemoId(), m_memo.getWordA()
				.getLanguage(), m_memo.getWordB().getLanguage(), new IGetComplete() {
			@Override
			public void getComplete(List<MemoSentence> memoSentences) {
				setSupportProgress(1f);
				m_memoSentences = memoSentences;
				if (m_adapter.getCacheSize() > 0) {
					((IMemoPagerFragment) m_adapter.getCachedItem(0)).setTatoeba(m_memoSentences);
					((IMemoPagerFragment) m_adapter.getCachedItem(1)).setTatoeba(m_memoSentences);
				}
			}
		});

		QuizletProvider.getDefinitions(getActivity(), m_memo.getWordA().getWord(), new IQuizletGetDefinitions() {
			@Override
			public void getQuizletDefinitions(List<QuizletDefinition> definitions) {
				m_memoDefinitionsA = definitions;
				if (m_adapter.getCacheSize() > 0) {
					((IMemoPagerFragment) m_adapter.getCachedItem(0)).setQuizlet(m_memoDefinitionsA);
				}
			}
		});

		QuizletProvider.getDefinitions(getActivity(), m_memo.getWordB().getWord(), new IQuizletGetDefinitions() {
			@Override
			public void getQuizletDefinitions(List<QuizletDefinition> definitions) {
				m_memoDefinitionsB = definitions;
				if (m_adapter.getCacheSize() > 0) {
					((IMemoPagerFragment) m_adapter.getCachedItem(1)).setQuizlet(m_memoDefinitionsB);
				}
			}
		});
	}

	private void bindFragment(Fragment fragment) {

		final IMemoPagerFragment ifragment = (IMemoPagerFragment) fragment;
		ifragment.setMemo(m_memo);
		ifragment.setTatoeba(m_memoSentences);
		if (ifragment instanceof MemoFirstFragment) {
			ifragment.setQuizlet(m_memoDefinitionsA);
		} else if (ifragment instanceof MemoSecondFragment) {
			ifragment.setQuizlet(m_memoDefinitionsB);
		}
	}

	@Override
	protected boolean onCreateOptionsMenu() {
		MenuItem item;
		item = createMenuItem(0, getString(R.string.memo_first)).setIcon(R.drawable.ic_1);
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		item = createMenuItem(1, getString(R.string.memo_second)).setIcon(R.drawable.ic_2);
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		item = createMenuItem(2, getString(R.string.memo_details)).setIcon(R.drawable.ic_details);
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == 0) {
			m_pager.setCurrentItem(0);
			return false;
		} else if (item.getItemId() == 1) {
			m_pager.setCurrentItem(1);
			return false;
		} else if (item.getItemId() == 2) {
			m_pager.setCurrentItem(2);
			return false;
		}
		return true;
	}

	private class MyPagerAdapter extends RemovableFragmentPagerAdapter {

		@Override
		public Object instantiateItem(ViewGroup arg0, int arg1) {
			Fragment fragment = (Fragment) super.instantiateItem(arg0, arg1);
			bindFragment(fragment);
			return fragment;
		}

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		public int getItemPosition(Object item) {
			IMemoPagerFragment fragment = (IMemoPagerFragment) item;
			int position = fragment.getPosition();

			if (position >= 0) {
				return position;
			} else {
				return POSITION_NONE;
			}
		}

		@Override
		public Fragment getItem(int position) {
			return createFragment(position);
		}

		@Override
		public int getCount() {
			return TabSize;
		}

		private Fragment createFragment(int position) {

			Fragment fragment = null;
			switch (position) {
			case 0:
				fragment = new MemoFirstFragment();
				break;
			case 1:
				fragment = new MemoSecondFragment();
				break;
			case 2:
				fragment = new MemoDetailsFragment();
				break;
			default:
				break;
			}

			return fragment;
		}
	}

	public static interface IMemoPagerFragment {
		void setMemo(Memo memo);

		void setTatoeba(List<MemoSentence> memoSentences);

		void setQuizlet(List<QuizletDefinition> definitions);

		boolean onBackPressed();

		int getPosition();
	}

	@Override
	public void onDestroyView() {

		String syncClientId =  new SyncClientAdapter(getActivity()).getCurrentSyncClientId();
		Word wordA = m_memo.getWordA();
		Word wordB = m_memo.getWordB();
		
		
		if (!m_originalWordA.equals(wordA.getWord())
				|| !m_originalDescriptionA.equals(wordA.getDescription())) {
			new WordAdapter(getActivity()).update(wordA, syncClientId);
		}
		
		if (!m_originalWordB.equals(wordB.getWord())
				|| !m_originalDescriptionB.equals(wordB.getDescription())) {
			new WordAdapter(getActivity()).update(wordB, syncClientId);
		}

		if (m_originalActive != m_memo.getActive()) {
			m_memoAdapter.update(m_memo, syncClientId);
		}
		
		m_adapter.destroyFragments();

		super.onDestroyView();
	}

	@Override
	protected void onPopulateDrawer(DrawerAdapter drawer) {
		drawer.addGroup(new DrawerView(R.drawable.ic_back, R.string.memo_backToList));
		m_shareHelper.onPopulateDrawerMemo(drawer);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// Does not matter which fragment is used, TextToSpeechHelper is handling data
		m_adapter.getCachedItem(0).onActivityResult(requestCode, resultCode, data);
	}
}
