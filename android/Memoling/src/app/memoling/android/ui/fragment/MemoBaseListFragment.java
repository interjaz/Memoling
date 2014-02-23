package app.memoling.android.ui.fragment;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import app.memoling.android.R;
import app.memoling.android.adapter.MemoBaseAdapter;
import app.memoling.android.entity.MemoBase;
import app.memoling.android.entity.MemoBaseInfo;
import app.memoling.android.ui.ApplicationFragment;
import app.memoling.android.ui.ResourceManager;
import app.memoling.android.ui.adapter.DrawerAdapter;
import app.memoling.android.ui.adapter.ModifiableComplexTextAdapter;
import app.memoling.android.ui.view.DrawerView;
import app.memoling.android.ui.view.MemoBaseInfoView;

import com.actionbarsherlock.view.MenuItem;

public class MemoBaseListFragment extends ApplicationFragment {

	private ListView m_lstMemos;
	private ModifiableComplexTextAdapter<MemoBaseInfoView> m_lstAdapter;
	private MemoBaseListEventHandler m_lstHandler;

	private MemoBaseAdapter m_memoBaseAdapter;
	private ArrayList<MemoBaseInfo> m_memoBaseInfos;

	private MemoBaseInfoView m_selectedItem;

	private final static int[] m_memoBaseAdapterResources = new int[] { R.id.memobaselist_listview_lblName,
			R.id.memobaselist_listview_lblMemos, R.id.memobaselist_listview_lblLanguages, };

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View contentView = super.onCreateView(inflater, container, savedInstanceState,
				inflater.inflate(R.layout.fragment_memobaselist, container, false));
		setTitle(getActivity().getString(R.string.memobaselist_title));

		ResourceManager resources = getResourceManager();
		Typeface thinFont = resources.getLightFont();
		Typeface blackFont = resources.getBlackFont();

		m_lstMemos = (ListView) contentView.findViewById(R.id.memobaselist_lstMemo);
		m_lstAdapter = new ModifiableComplexTextAdapter<MemoBaseInfoView>(getActivity(),
				R.layout.adapter_memobaselist_listview, m_memoBaseAdapterResources, new Typeface[] { thinFont,
			thinFont, thinFont }, false);
		m_lstMemos.setAdapter(m_lstAdapter);
		m_lstHandler = new MemoBaseListEventHandler();
		m_lstMemos.setOnItemClickListener(m_lstHandler);
		registerForContextMenu(m_lstMemos);

		m_memoBaseAdapter = new MemoBaseAdapter(getActivity());

		return contentView;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		getActivity().getMenuInflater().inflate(R.menu.memobaselist_list, menu);
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

		m_selectedItem = m_lstAdapter.getItem(info.position);
		android.view.MenuItem item = menu.findItem(R.id.memobaselist_menu_list_activate);
		if (m_selectedItem.getMemoBaseInfo().getMemoBase().getActive()) {
			item.setTitle(R.string.memobaselist_ctxmenu_deactivate);
		} else {
			item.setTitle(R.string.memobaselist_ctxmenu_activate);
		}
	}

	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {

		Context ctx = getActivity();
		final MemoBase memoBase;

		switch (item.getItemId()) {
		case R.id.memobaselist_menu_list_activate:
			memoBase = m_selectedItem.getMemoBaseInfo().getMemoBase();
			memoBase.setActive(!memoBase.getActive());
			m_memoBaseAdapter.update(memoBase);
			m_lstAdapter.notifyDataSetChanged();
			break;
		case R.id.memobaselist_menu_list_delete:
			memoBase = m_selectedItem.getMemoBaseInfo().getMemoBase();

			if (m_lstAdapter.getCount() == 1) {

				new AlertDialog.Builder(ctx).setTitle(ctx.getString(R.string.memobaselist_ctxmenu_deleteTitle))
						.setMessage(ctx.getString(R.string.memobaselist_ctxmenu_deleteImpossible))
						.setNeutralButton(ctx.getString(R.string.memobaselist_ctxmenu_deleteImpossibleOK), null)
						.create().show();

				return super.onContextItemSelected(item);
			}

			// Show warning
			new AlertDialog.Builder(ctx)
					.setTitle(ctx.getString(R.string.memobaselist_ctxmenu_deleteTitle))
					.setMessage(ctx.getString(R.string.memobaselist_ctxmenu_deleteQuestion))
					.setPositiveButton(ctx.getString(R.string.memobaselist_ctxmenu_deleteYes),
							new Dialog.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									m_memoBaseAdapter.delete(memoBase.getMemoBaseId());
									bindData();
								}
							})
					.setNegativeButton(ctx.getString(R.string.memobaselist_ctxmenu_deleteNo),
							new Dialog.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// Do nothing
								}
							}).setIcon(R.drawable.ic_dialog_alert_holo_dark).create().show();

			break;
		case R.id.memobaselist_menu_list_options:
			memoBase = m_selectedItem.getMemoBaseInfo().getMemoBase();

			MemoBaseFragment fragment = new MemoBaseFragment();
			Bundle bundle = new Bundle();
			bundle.putString(MemoBaseFragment.MemoBaseId, memoBase.getMemoBaseId());
			fragment.setArguments(bundle);

			startFragment(fragment);

			break;
		}

		return super.onContextItemSelected(item);
	}

	@Override
	protected boolean onCreateOptionsMenu() {
		MenuItem item;

		item = createMenuItem(0, getString(R.string.memobaselist_newMemoBase)).setIcon(R.drawable.ic_library_add);
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		item = createMenuItem(1, getString(R.string.memobaselist_onlineMemoBase)).setIcon(R.drawable.ic_download);
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == 0) {
			openNewMemoBase();
			return false;
		} else if (item.getItemId() == 1) {
			openDownloadMemoBase();
			return false;
		}
		return true;
	}

	// @Override
	// public void onActivityResult(int requestCode, int resultCode, Intent
	// data) {
	// super.onActivityResult(requestCode, resultCode, data);
	//
	// if (requestCode == FbLoginActivity.CredentialsRequest && resultCode ==
	// Activity.RESULT_OK) {
	// FacebookMe fbMe = new FacebookMe();
	// fbMe.getUser(data.getStringExtra(FbLoginActivity.AccessToken), this);
	// } else {
	// Toast.makeText(getActivity(), R.string.memobaselist_fbFailed,
	// Toast.LENGTH_LONG).show();
	// }
	// }

	private class MemoBaseListEventHandler implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

			Bundle result = new Bundle();
			result.putString(MemoListFragment.MemoBaseId, m_memoBaseInfos.get(position).getMemoBase().getMemoBaseId());
			MemoBaseListFragment.this.finish(result);
		}
	}

	// private class BtnSyncEventHandler implements View.OnClickListener {
	// @Override
	// public void onClick(View v) {
	// FacebookUser user = FacebookUser.read(getActivity());
	// if (user == null) {
	// Intent fbLoginIntent = new Intent(getActivity(), FbLoginActivity.class);
	// getActivity().startActivityForResult(fbLoginIntent,
	// FbLoginActivity.CredentialsRequest);
	// } else {
	// synchronize();
	// }
	// }
	// }
	//
	// @Override
	// public void onFacebookUserFound(FacebookUser user) {
	// FacebookUser.save(user, getActivity());
	// synchronize();
	// }
	//
	// private void synchronize() {
	// // Webservice.synchronize();
	// }

	@Override
	protected void onDataBind(Bundle savedInstanceState) {
		bindData();
	}

	protected void onPopulateDrawer(DrawerAdapter drawer) {

		drawer.addGroup(new DrawerView(R.drawable.ic_back, R.string.memobaselist_backToList));

		drawer.addGroup(new DrawerView(R.drawable.ic_library_add, R.string.memobaselist_addMemoBase, new DrawerView.OnClickListener() {
			@Override
			public void onClick(DrawerView v) {
				openNewMemoBase();
			}
		}));

		drawer.addGroup(new DrawerView(R.drawable.ic_download, R.string.memobaselist_downloadMemoBase,
				new DrawerView.OnClickListener() {
					@Override
					public void onClick(DrawerView v) {
						openDownloadMemoBase();
					}
				}));

	}

	private void bindData() {
		m_lstAdapter.clear();
		m_memoBaseInfos = new ArrayList<MemoBaseInfo>();
		for (MemoBase memoBase : m_memoBaseAdapter.getAll()) {
			m_memoBaseInfos.add(m_memoBaseAdapter.getMemoBaseInfo(memoBase.getMemoBaseId()));
		}
		m_lstAdapter.addAll(MemoBaseInfoView.getAll(m_memoBaseInfos));
	}

	private void openNewMemoBase() {
		Bundle result = new Bundle();
		result.putString(Action, MemoBaseFragment.ActionCreate);
		ApplicationFragment fragment = new MemoBaseFragment();
		fragment.setArguments(result);

		startFragment(fragment);
	}

	private void openDownloadMemoBase() {
		ApplicationFragment fragment = new DownloadFragment();
		startFragment(fragment);
	}
}
