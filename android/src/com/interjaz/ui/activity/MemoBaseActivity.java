package com.interjaz.ui.activity;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.interjaz.R;
import com.interjaz.entity.Memo;
import com.interjaz.entity.MemoBase;
import com.interjaz.entity.MemoBaseAdapter;
import com.interjaz.entity.MemoBaseInfo;
import com.interjaz.helper.DateHelper;
import com.interjaz.helper.Helper;
import com.interjaz.sync.ConflictResolve;
import com.interjaz.sync.ConflictResolve.OnConflictResolveHaltable;
import com.interjaz.sync.Export;
import com.interjaz.sync.Import;
import com.interjaz.sync.SupervisedSync.OnSyncComplete;
import com.interjaz.ui.GestureActivity;
import com.interjaz.ui.ResourceManager;

public class MemoBaseActivity extends GestureActivity {

	private static final int MemolingFile = 0;
	private static final int CsvFile = 1;
	private static final int Evernote = 2;

	public final static String MemoBaseId = "MemoBaseId";
	public final static String ActionCreate = "ActionCreate";
	public final static int RequestReview = 0;
	public final static int RequestMemolingFile = 1;
	public final static int RequestCsvFile = 2;
	public final static int RequestEvernote = 3;

	private ResourceManager m_resources;

	private String m_memoBaseId;

	private Button m_btnImport;
	private Button m_btnExport;
	private Button m_btnPublish;
	private Button m_btnSchedule;
	private Button m_btnSave;

	private TextView m_lblTitle;
	private TextView m_lblCreated;
	private TextView m_lblLastReviewed;
	private TextView m_lblNoAll;
	private TextView m_lblNoActive;
	private CheckBox m_chbActive;
	private EditText m_txtName;

	private MemoBaseInfo m_memoBaseInfo;
	private MemoBaseAdapter m_memoBaseAdapter;

	private int m_inoutSelectedWhich;
	private int m_conflictResolveWhich;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_memobase);

		m_resources = new ResourceManager(this);

		m_btnImport = (Button) findViewById(R.id.memobase_btnImport);
		m_btnImport.setOnClickListener(new BtnImportEventHandler());
		m_btnImport.setTypeface(m_resources.getThinFont());

		m_btnExport = (Button) findViewById(R.id.memobase_btnExport);
		m_btnExport.setOnClickListener(new BtnExportEventHandler());
		m_btnExport.setTypeface(m_resources.getThinFont());
		
		m_btnPublish = (Button) findViewById(R.id.memobase_btnPublish);
		m_btnPublish.setOnClickListener(new BtnPublishEventHandler());
		m_btnPublish.setTypeface(m_resources.getThinFont());

		m_btnSchedule = (Button) findViewById(R.id.memobase_btnSchedule);
		m_btnSchedule.setOnClickListener(new BtnScheduleEventHandler());
		m_btnSchedule.setTypeface(m_resources.getThinFont());

		m_btnSave = (Button) findViewById(R.id.memobase_btnSave);
		m_btnSave.setOnClickListener(new BtnSaveEventHandler());
		m_btnSave.setTypeface(m_resources.getThinFont());

		m_lblTitle = (TextView) findViewById(R.id.memobase_lblTitle);
		m_lblTitle.setTypeface(m_resources.getThinFont());
		m_lblCreated = (TextView) findViewById(R.id.memobase_lblCreated);
		m_lblCreated.setTypeface(m_resources.getThinFont());
		m_lblLastReviewed = (TextView) findViewById(R.id.memobase_lblLastReviewed);
		m_lblLastReviewed.setTypeface(m_resources.getThinFont());
		m_lblNoAll = (TextView) findViewById(R.id.memobase_lblNoAll);
		m_lblNoAll.setTypeface(m_resources.getThinFont());
		m_lblNoActive = (TextView) findViewById(R.id.memobase_lblNoActive);
		m_lblNoActive.setTypeface(m_resources.getThinFont());

		m_txtName = (EditText) findViewById(R.id.memobase_txtName);
		m_txtName.setTypeface(m_resources.getThinFont());
		m_chbActive = (CheckBox) findViewById(R.id.memobase_chbEnabled);
		m_chbActive.setTypeface(m_resources.getThinFont());

		try {
			m_memoBaseAdapter = new MemoBaseAdapter(this);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Set fonts
		m_resources.setFont(R.id.textView1, m_resources.getCondensedFont());
		m_resources.setFont(R.id.textView2, m_resources.getCondensedFont());
		m_resources.setFont(R.id.textView3, m_resources.getCondensedFont());
		m_resources.setFont(R.id.textView4, m_resources.getCondensedFont());
		m_resources.setFont(R.id.textView5, m_resources.getCondensedFont());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public void onStart() {
		super.onStart();
		Intent intent = getIntent();

		if (intent.getAction() == null) {
			m_memoBaseId = intent.getStringExtra(MemoBaseId);
		} else if (intent.getAction().equals(ActionCreate)) {
			m_memoBaseId = createMemoBase();
		}

		m_memoBaseInfo = m_memoBaseAdapter.getMemoBaseInfo(m_memoBaseId);
		bindData();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		MemoBaseActivity self = MemoBaseActivity.this;

		if (resultCode == Activity.RESULT_OK) {

			String path;
			switch (requestCode) {
			case RequestMemolingFile:
				path = Helper.getPathFromIntent(self, data);
				self.importMemolingFile(path);
				break;
			case RequestCsvFile:
				path = Helper.getPathFromIntent(self, data);
				self.importCsvFile(path);
				break;
			}

		} else {
			switch (requestCode) {
			case RequestMemolingFile:
			case RequestCsvFile:
				Toast.makeText(MemoBaseActivity.this, getString(R.string.memobase_getFileError), Toast.LENGTH_SHORT)
						.show();
				break;
			}
		}
	}

	public class BtnSaveEventHandler implements OnClickListener {

		@Override
		public void onClick(View v) {
			MemoBase memoBase = m_memoBaseInfo.getMemoBase();
			memoBase.setName(m_txtName.getText().toString());
			memoBase.setActive(m_chbActive.isChecked());

			if (m_memoBaseAdapter.update(memoBase) == 1) {
				bindData();
			}
		}

	}

	public class BtnScheduleEventHandler implements OnClickListener {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(MemoBaseActivity.this, SchedulerActivity.class);
			intent.putExtra(SchedulerActivity.MemoBaseId, m_memoBaseId);
			startActivity(intent);
		}
	}

	public class BtnImportEventHandler implements OnClickListener {

		@Override
		public void onClick(View v) {

			showSelectMenu(new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {

					Intent intent;

					switch (which) {
					case MemolingFile:
						intent = new Intent(Intent.ACTION_GET_CONTENT);
						intent.setType("file/*");
						startActivityForResult(intent, RequestMemolingFile);
						break;
					case CsvFile:
						intent = new Intent(Intent.ACTION_GET_CONTENT);
						intent.setType("file/*");
						startActivityForResult(intent, RequestCsvFile);
						break;
					case Evernote:
						break;
					}

				}
			}, false);
		}

	}

	public class BtnPublishEventHandler implements OnClickListener {
		@Override
		public void onClick(View v) {
			Toast.makeText(MemoBaseActivity.this, "Check out next build!1", Toast.LENGTH_SHORT).show();
		}
	}

	public class BtnExportEventHandler implements OnClickListener {

		@Override
		public void onClick(View v) {
			showSelectMenu(new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {

					MemoBaseActivity self = MemoBaseActivity.this;
					String result = "";

					switch (which) {
					case MemolingFile:

						result = Export.exportMemoling(self, new String[] { m_memoBaseId });
						if (result == null) {
							result = self.getString(R.string.memobase_exportFailure);
						} else {
							result = self.getString(R.string.memobase_exportSuccess) + result;
						}

						break;
					case CsvFile:

						result = Export.exportCsv(self, new String[] { m_memoBaseId });
						if (result == null) {
							result = self.getString(R.string.memobase_exportFailure);
						} else {
							result = self.getString(R.string.memobase_exportSuccess) + result;
						}

						break;
					case Evernote:

						if (!Helper.apkInstalled(self, "com.evernote")) {
							Toast.makeText(self, self.getString(R.string.memobase_evernoteNotInstalled),
									Toast.LENGTH_SHORT).show();
							return;
						}
						
						Intent intent = new Intent("com.evernote.action.CREATE_NEW_NOTE");
						intent.putExtra(Intent.EXTRA_TITLE, "Memoling");
						intent.putExtra(Intent.EXTRA_TEXT, Export.exportEvernote(self, new String[] { m_memoBaseId }));
						startActivity(intent);

						return;
					}

					Toast.makeText(self, result, Toast.LENGTH_LONG).show();
				}
			}, true);

		}

	}

	private void bindData() {

		m_txtName.setText(m_memoBaseInfo.getMemoBase().getName());
		m_lblTitle.setText(m_memoBaseInfo.getMemoBase().getName());
		m_lblCreated.setText(DateHelper.toUiDate(m_memoBaseInfo.getMemoBase().getCreated()));
		m_chbActive.setChecked(m_memoBaseInfo.getMemoBase().getActive());
		m_lblNoAll.setText(Integer.valueOf(m_memoBaseInfo.getNoAllMemos()).toString());
		m_lblNoActive.setText(Integer.valueOf(m_memoBaseInfo.getNoActiveMemos()).toString());
		m_lblLastReviewed.setText(DateHelper.toUiDate(m_memoBaseInfo.getLastReviewed()));
	}

	private void importMemolingFile(final String path) {

		Import.importMemosMemolingFile(m_memoBaseId, this, path, m_memoConflictResolve, new OnSyncComplete() {
			@Override
			public void onComplete(boolean result) {
				MemoBaseActivity self = MemoBaseActivity.this;

				if (!result) {
					String strResult = String.format(self.getString(R.string.memobase_importFailed), path);
					Toast.makeText(self, strResult, Toast.LENGTH_LONG).show();
					return;
				}

				String strResult = String.format(self.getString(R.string.memobase_importCompleted), path);
				Toast.makeText(self, strResult, Toast.LENGTH_LONG).show();

				self.bindData();
			}

		});
	}

	private void importCsvFile(final String path) {

		Import.importCsvFile(m_memoBaseId, this, path, m_memoConflictResolve, new OnSyncComplete() {
			@Override
			public void onComplete(boolean result) {
				MemoBaseActivity self = MemoBaseActivity.this;

				if (!result) {
					String strResult = String.format(self.getString(R.string.memobase_importFailed), path);
					Toast.makeText(self, strResult, Toast.LENGTH_LONG).show();
					return;
				}

				String strResult = String.format(self.getString(R.string.memobase_importCompleted), path);
				Toast.makeText(self, strResult, Toast.LENGTH_LONG).show();

				self.bindData();
			}

		});

	}

	private String createMemoBase() {
		MemoBase memoBase = new MemoBase();
		String memoBaseId = UUID.randomUUID().toString();

		memoBase.setActive(true);
		memoBase.setCreated(new Date());
		memoBase.setMemoBaseId(memoBaseId);
		memoBase.setName(getString(R.string.memobase_newMemo));
		m_memoBaseAdapter.add(memoBase);

		return memoBaseId;
	}

	private void showSelectMenu(final DialogInterface.OnClickListener onSelected, boolean export) {

		final int stringArrayRes;
		if (export) {
			stringArrayRes = R.array.memobase_exportList;
		} else {
			stringArrayRes = R.array.memobase_importList;
		}

		new AlertDialog.Builder(this).setTitle(getString(R.string.memobase_inoutTitle))
				.setNeutralButton(getString(R.string.memobase_inoutConfirm), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						onSelected.onClick(dialog, m_inoutSelectedWhich);
					}
				}).setSingleChoiceItems(stringArrayRes, 0, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						m_inoutSelectedWhich = which;
					}
				}).create().show();

	}

	private OnConflictResolveHaltable<Memo> m_memoConflictResolve = new OnConflictResolveHaltable<Memo>() {

		@Override
		public void onConflict(Memo internal, Memo external, final Object wait, final ConflictResolve resolve) {

			new AlertDialog.Builder(MemoBaseActivity.this)
					.setTitle(R.string.memobase_importConflict)
					.setSingleChoiceItems(R.array.memobase_conflict, 0, new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							m_conflictResolveWhich = which;
						}

					})
					.setCancelable(false)
					.setNeutralButton(getString(R.string.memobase_conflictApplyOnce),
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									synchronized (wait) {
										int flag = 1 << m_conflictResolveWhich;
										resolve.setFlags(flag);
										wait.notify();
									}
								}
							})
					.setPositiveButton(getString(R.string.memobase_conflictApplyToAll),
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									synchronized (wait) {
										int flag = 1 << m_conflictResolveWhich;
										resolve.setFlags(flag | ConflictResolve.ForAll);
										wait.notify();
									}
								}
							}).create().show();

		}
	};

	@Override
	public boolean onSwipeLeftToRight() {
		// Duplicate code so it remains generic
		Intent memoListIntnet = new Intent(MemoBaseActivity.this, MemoListActivity.class);
		memoListIntnet.putExtra(MemoListActivity.MemoBaseId, m_memoBaseId);
		startActivity(memoListIntnet);
		return false;
	}

	@Override
	public boolean onSwipeRightToLeft() {
		finish();
		return false;
	}

}
