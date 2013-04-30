package com.interjaz.ui.activity;

import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.interjaz.R;
import com.interjaz.entity.MemoBase;
import com.interjaz.entity.MemoBaseAdapter;
import com.interjaz.entity.MemoBaseInfo;
import com.interjaz.facebook.FacebookMe;
import com.interjaz.facebook.FacebookUser;
import com.interjaz.facebook.IFacebookUserFound;
import com.interjaz.sync.Sync;
import com.interjaz.ui.GestureActivity;
import com.interjaz.ui.ResourceManager;
import com.interjaz.ui.adapter.ModifiableComplexTextAdapter;
import com.interjaz.ui.view.MemoBaseInfoView;
import com.interjaz.webservice.Webservice;

public class MemoBaseListActivity extends GestureActivity implements IFacebookUserFound {

	private ResourceManager mResource;

	private ListView m_lstMemos;
	private ModifiableComplexTextAdapter<MemoBaseInfoView> m_lstAdapter;
	private MemoBaseListEventHandler m_lstHandler;

	private MemoBaseAdapter m_memoBaseAdapter;
	private ArrayList<MemoBaseInfo> m_memoBaseInfos;

	private Button m_btnNewMemoBase;
	private Button m_btnSync;

	private int m_selectedItemPosition;

	private final static int[] m_memoBaseAdapterResources = new int[] { R.id.memobaselist_listview_lblCreated,
			R.id.memobaselist_listview_lblMemos, R.id.memobaselist_listview_lblName };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_memobaselist);

		mResource = new ResourceManager(this);

		m_lstMemos = (ListView) findViewById(R.id.memobaselist_lstMemo);
		m_lstAdapter = new ModifiableComplexTextAdapter<MemoBaseInfoView>(this, R.layout.adapter_memobaselist_listview,
				m_memoBaseAdapterResources, new Typeface[] { mResource.getThinFont(), mResource.getThinFont(),
						mResource.getThinFont() }, true);
		m_lstMemos.setAdapter(m_lstAdapter);
		m_lstHandler = new MemoBaseListEventHandler();
		m_lstMemos.setOnItemClickListener(m_lstHandler);
		registerForContextMenu(m_lstMemos);

		m_btnNewMemoBase = (Button) findViewById(R.id.memobaselist_btnNewMemoBase);
		m_btnNewMemoBase.setOnClickListener(new BtnNewMemoBaseEventHandler());
		mResource.setFont(m_btnNewMemoBase, mResource.getThinFont());

		m_btnSync = (Button) findViewById(R.id.memobaselist_btnSync);
		m_btnSync.setOnClickListener(new BtnSyncEventHandler());
		mResource.setFont(m_btnSync, mResource.getThinFont());

		try {
			m_memoBaseAdapter = new MemoBaseAdapter(this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		mResource.setFont(R.layout.adapter_memobaselist_listview, R.id.textView1, mResource.getCondensedFont());
		mResource.setFont(R.layout.adapter_memobaselist_listview, R.id.textView2, mResource.getCondensedFont());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_memobaselist, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {

		switch (item.getItemId()) {
		case R.id.main_menu_statistics:
			Intent statisicsIntent = new Intent(this, StatisticsActivity.class);
			startActivity(statisicsIntent);
			break;
		case R.id.main_menu_settings:
			break;
		case R.id.main_menu_about:
			Intent aboutIntent = new Intent(this, AboutActivity.class);
			startActivity(aboutIntent);
			break;
		}

		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		getMenuInflater().inflate(R.menu.memobaselist_list, menu);
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		m_selectedItemPosition = (int) info.position;
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		Context ctx = MemoBaseListActivity.this;
		final String memoBaseId = m_lstAdapter.getItem(m_selectedItemPosition).getMemoBaseInfo().getMemoBase()
				.getMemoBaseId();
		Intent intent;

		switch (item.getItemId()) {
		case R.id.memobaselist_menu_list_delete:

			if (m_lstAdapter.getCount() == 1) {

				new AlertDialog.Builder(ctx).setTitle(ctx.getString(R.string.memobaselist_ctxmenu_deleteTitle))
						.setMessage(ctx.getString(R.string.memobaselist_ctxmenu_deleteImpossible))
						.setNeutralButton(ctx.getString(R.string.memobaselist_ctxmenu_deleteImpossibleOK), null)
						.create().show();

				return super.onContextItemSelected(item);
			}

			// Show warning
			new AlertDialog.Builder(ctx).setTitle(ctx.getString(R.string.memobaselist_ctxmenu_deleteTitle))
					.setMessage(ctx.getString(R.string.memobaselist_ctxmenu_deleteQuestion))
					.setPositiveButton(ctx.getString(R.string.memobaselist_ctxmenu_deleteYes), new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							m_memoBaseAdapter.delete(memoBaseId);
							bindData();
						}
					}).setNegativeButton(ctx.getString(R.string.memobaselist_ctxmenu_deleteNo), new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// Do nothing
						}
					}).setIcon(R.drawable.ic_dialog_alert_holo_dark).create().show();

			break;
		case R.id.memobaselist_menu_list_options:

			intent = new Intent(MemoBaseListActivity.this, MemoBaseActivity.class);
			intent.putExtra(MemoBaseActivity.MemoBaseId, memoBaseId);
			startActivity(intent);

			break;
		}

		return super.onContextItemSelected(item);
	}

	@Override
	protected void onStart() {
		super.onStart();
		bindData();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == FbLoginActivity.CredentialsRequest && resultCode == Activity.RESULT_OK) {
			FacebookMe fMe = new FacebookMe();
			fMe.getUser(data.getStringExtra(FbLoginActivity.AccessToken), this);
		} else {
			Toast.makeText(this, R.string.memobaselist_fbFailed, Toast.LENGTH_LONG).show();
		}
	}

	private void bindData() {
		m_lstAdapter.clear();
		m_memoBaseInfos = new ArrayList<MemoBaseInfo>();
		for (MemoBase memoBase : m_memoBaseAdapter.getAll()) {
			m_memoBaseInfos.add(m_memoBaseAdapter.getMemoBaseInfo(memoBase.getMemoBaseId()));
		}
		m_lstAdapter.addAll(MemoBaseInfoView.getAll(m_memoBaseInfos));
	}

	private class MemoBaseListEventHandler implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Intent memoBaseIntent = new Intent(MemoBaseListActivity.this, MemoListActivity.class);
			memoBaseIntent.putExtra(MemoListActivity.MemoBaseId, m_memoBaseInfos.get(position).getMemoBase()
					.getMemoBaseId());
			startActivity(memoBaseIntent);
		}
	}

	private class BtnNewMemoBaseEventHandler implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			Intent newLibrary = new Intent(MemoBaseListActivity.this, MemoBaseActivity.class);
			newLibrary.setAction(MemoBaseActivity.ActionCreate);
			startActivity(newLibrary);
		}
	}

	private class BtnSyncEventHandler implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			FacebookUser user = FacebookUser.read(MemoBaseListActivity.this);
			if (user == null) {
				Intent fbLoginIntent = new Intent(MemoBaseListActivity.this, FbLoginActivity.class);
				startActivityForResult(fbLoginIntent, FbLoginActivity.CredentialsRequest);
			} else {
				synchronize();
			}
		}
	}

	@Override
	public boolean onSwipeRightToLeft() {
		finish();
		return false;
	}

	@Override
	public void onFacebookUserFound(FacebookUser user) {
		FacebookUser.save(user, this);
		synchronize();
	}
	
	private void synchronize() {
		//Webservice.synchronize();
	}
}
