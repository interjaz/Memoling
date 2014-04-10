package app.memoling.android.ui.fragment;

import java.util.Date;
import java.util.UUID;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import app.memoling.android.Config;
import app.memoling.android.R;
import app.memoling.android.adapter.MemoBaseAdapter;
import app.memoling.android.adapter.MemoBaseGenreAdapter;
import app.memoling.android.adapter.SyncClientAdapter;
import app.memoling.android.entity.Memo;
import app.memoling.android.entity.MemoBase;
import app.memoling.android.entity.MemoBaseInfo;
import app.memoling.android.helper.DateHelper;
import app.memoling.android.helper.Helper;
import app.memoling.android.sync.file.ConflictResolve;
import app.memoling.android.sync.file.ConflictResolve.OnConflictResolveHaltable;
import app.memoling.android.sync.file.Export;
import app.memoling.android.sync.file.Import;
import app.memoling.android.sync.file.SupervisedSync.OnSyncComplete;
import app.memoling.android.ui.ApplicationFragment;
import app.memoling.android.ui.FacebookFragment;
import app.memoling.android.ui.ResourceManager;
import app.memoling.android.ui.adapter.DrawerAdapter;
import app.memoling.android.ui.adapter.ModifiableComplexTextAdapter;
import app.memoling.android.ui.view.DrawerView;
import app.memoling.android.ui.view.MemoBaseGenreView;
import app.memoling.android.webservice.helper.PublishedMemoBaseUpload;
import app.memoling.android.webservice.helper.PublishedMemoBaseUpload.ExceptionReasons;
import app.memoling.android.webservice.helper.PublishedMemoBaseUpload.IPublishedMemoBaseUpload;

import com.actionbarsherlock.view.MenuItem;

public class MemoBaseFragment extends FacebookFragment implements IPublishedMemoBaseUpload {

	private static final int MemolingFile = 0;
	private static final int CsvFile = 1;
	private static final int Evernote = 2;

	public final static String MemoBaseId = "MemoBaseId";
	public final static String ActionCreate = "ActionCreate";
	public final static int RequestReview = 0;
	public final static int RequestMemolingFile = 1;
	public final static int RequestCsvFile = 2;
	public final static int RequestEvernote = 3;

	private String m_memoBaseId;

	private Button m_btnUpload;
	
	private TextView m_lblCreated;
	private TextView m_lblLastReviewed;
	private TextView m_lblNoAll;
	private TextView m_lblNoActive;
	private TextView m_lblLibraryName;
	private CheckBox m_chbActive;
	private EditText m_txtName;
	private EditText m_txtDescription;
	private Spinner m_cbxGenre;

	private TableLayout m_layLibrary;

	private MemoBaseInfo m_memoBaseInfo;
	private MemoBaseAdapter m_memoBaseAdapter;

	private ModifiableComplexTextAdapter<MemoBaseGenreView> m_genreAdapter;
	private MemoBaseGenreAdapter m_genreDataAdapter;

	private PublishedMemoBaseUpload m_publishedUpload;

	private int m_inoutSelectedWhich;
	private int m_conflictResolveWhich;
	
	private InputMethodManager m_inputManager;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View contentView = super.onCreateView(inflater, container, savedInstanceState,
				inflater.inflate(R.layout.fragment_memobase, container, false));

		ResourceManager resources = getResourceManager();
		Typeface thinFont = resources.getLightFont();

		m_lblCreated = (TextView) contentView.findViewById(R.id.memobase_lblCreated);
		m_lblCreated.setTypeface(thinFont);
		m_lblLastReviewed = (TextView) contentView.findViewById(R.id.memobase_lblLastReviewed);
		m_lblLastReviewed.setTypeface(thinFont);
		m_lblNoAll = (TextView) contentView.findViewById(R.id.memobase_lblNoAll);
		m_lblNoAll.setTypeface(thinFont);
		m_lblNoActive = (TextView) contentView.findViewById(R.id.memobase_lblNoActive);
		m_lblNoActive.setTypeface(thinFont);

		m_txtName = (EditText) contentView.findViewById(R.id.memobase_txtName);
		m_txtName.setTypeface(thinFont);
		m_chbActive = (CheckBox) contentView.findViewById(R.id.memobase_chbEnabled);
		m_chbActive.setTypeface(thinFont);

		m_layLibrary = (TableLayout) contentView.findViewById(R.id.memobase_layoutLibrary);
		m_lblLibraryName = (TextView) contentView.findViewById(R.id.memobase_lblLibraryName);
		resources.setFont(m_lblLibraryName, thinFont);
		m_txtDescription = (EditText) contentView.findViewById(R.id.memobase_txtDescription);
		resources.setFont(m_txtDescription, thinFont);

		m_btnUpload = (Button) contentView.findViewById(R.id.memobase_btnUpload);
		m_btnUpload.setOnClickListener(new BtnUploadEventHandler());
		resources.setFont(m_btnUpload, thinFont);

		m_cbxGenre = (Spinner) contentView.findViewById(R.id.memobase_cbxGenre);
		m_genreAdapter = new ModifiableComplexTextAdapter<MemoBaseGenreView>(getActivity(),
				R.layout.adapter_textdropdown_dark, new int[] { R.id.memo_lblLang }, new Typeface[] { thinFont });
		m_cbxGenre.setAdapter(m_genreAdapter);

		m_genreDataAdapter = new MemoBaseGenreAdapter(getActivity());
		m_memoBaseAdapter = new MemoBaseAdapter(getActivity());

		// Set fonts
		resources.setFont(contentView, R.id.memo_lblLang, thinFont);
		resources.setFont(contentView, R.id.textView1, thinFont);
		resources.setFont(contentView, R.id.textView3, thinFont);
		resources.setFont(contentView, R.id.textView5, thinFont);
		resources.setFont(contentView, R.id.textView6, thinFont);
		resources.setFont(contentView, R.id.textView8, thinFont);
		resources.setFont(contentView, R.id.textView9, thinFont);

		m_publishedUpload = new PublishedMemoBaseUpload(this, this);
		m_inputManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		

		return contentView;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		Context ctx = getActivity();

		if (resultCode == Activity.RESULT_OK) {

			String path;
			switch (requestCode) {
			case RequestMemolingFile:
				path = Helper.getPathFromIntent(ctx, data);
				importMemolingFile(path);
				break;
			case RequestCsvFile:
				path = Helper.getPathFromIntent(ctx, data);
				importCsvFile(path);
				break;
			}

		} else {

			switch (requestCode) {
			case RequestMemolingFile:
			case RequestCsvFile:
				Toast.makeText(ctx, getString(R.string.memobase_getFileError), Toast.LENGTH_SHORT).show();
				break;
			}
		}
	}

	@Override
	public boolean onBackPressed() {
		if (m_layLibrary.getVisibility() == View.VISIBLE) {
			m_layLibrary.setVisibility(View.GONE);
			return false;
		}

		return true;
	}

	@Override
	protected boolean onCreateOptionsMenu() {
		MenuItem item;
		item = createMenuItem(0, getString(R.string.memobase_scheduler)).setIcon(R.drawable.ic_scheduler);
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == 0) {
			openScheduler();
			return false;
		}
		return true;
	}
	
	private void importMemoBase() {

		showSelectMenu(new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				Intent intent;

				switch (which) {
				case MemolingFile:
					intent = new Intent(Intent.ACTION_GET_CONTENT);
					intent.setType("file/*");
					getActivity().startActivityForResult(intent, RequestMemolingFile);
					break;
				case CsvFile:
					intent = new Intent(Intent.ACTION_GET_CONTENT);
					intent.setType("file/*");
					getActivity().startActivityForResult(intent, RequestCsvFile);
					break;
				case Evernote:
					break;
				}

			}
		}, false);
	}

	// TODO: Change this to dialog - remove on back also if changed
	private void publishMemoBase() {

		if (m_memoBaseInfo.getNoAllMemos() < Config.MinNumberOfMemosForUpload) {
			Toast.makeText(getActivity(), R.string.memobase_errorMin, Toast.LENGTH_SHORT).show();
			return;
		}

		m_layLibrary.setVisibility(View.VISIBLE);
	}

	public class BtnUploadEventHandler implements OnClickListener {
		@Override
		public void onClick(View v) {

			m_layLibrary.setVisibility(View.GONE);
			m_inputManager.hideSoftInputFromWindow(m_txtName.getWindowToken(), 0);
			m_inputManager.hideSoftInputFromWindow(m_txtDescription.getWindowToken(), 0);
			
			String genreId = m_genreAdapter.getItem(m_cbxGenre.getSelectedItemPosition()).getGenre()
					.getMemoBaseGenreId();
			m_publishedUpload.upload(m_memoBaseId, genreId, m_txtDescription.getText().toString());
		}
	}

	private void exportMemoBase() {
		showSelectMenu(new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				Context context = getActivity();
				String result = "";

				switch (which) {
				case MemolingFile:

					result = Export.exportMemoling(context, new String[] { m_memoBaseId });
					if (result == null) {
						result = context.getString(R.string.memobase_exportFailure);
					} else {
						result = context.getString(R.string.memobase_exportSuccess) + result;
					}

					break;
				case CsvFile:

					result = Export.exportCsv(context, new String[] { m_memoBaseId });
					if (result == null) {
						result = context.getString(R.string.memobase_exportFailure);
					} else {
						result = context.getString(R.string.memobase_exportSuccess) + result;
					}

					break;
				case Evernote:

					if (!Helper.apkInstalled(context, "com.evernote")) {
						Toast.makeText(context, context.getString(R.string.memobase_evernoteNotInstalled),
								Toast.LENGTH_SHORT).show();
						return;
					}

					Intent intent = new Intent("com.evernote.action.CREATE_NEW_NOTE");
					intent.putExtra(Intent.EXTRA_TITLE, "Memoling");
					intent.putExtra(Intent.EXTRA_TEXT, Export.exportEvernote(context, new String[] { m_memoBaseId }));
					startActivity(intent);

					return;
				}

				Toast.makeText(context, result, Toast.LENGTH_LONG).show();
			}
		}, true);

	}

	private void bindData() {

		m_memoBaseInfo = m_memoBaseAdapter.getMemoBaseInfo(m_memoBaseId);
		setTitle(m_memoBaseInfo.getMemoBase().getName());

		m_txtName.setText(m_memoBaseInfo.getMemoBase().getName());
		m_lblCreated.setText(DateHelper.toUiDate(m_memoBaseInfo.getMemoBase().getCreated()));
		m_chbActive.setChecked(m_memoBaseInfo.getMemoBase().getActive());
		m_lblNoAll.setText(Integer.valueOf(m_memoBaseInfo.getNoAllMemos()).toString());
		m_lblNoActive.setText(Integer.valueOf(m_memoBaseInfo.getNoActiveMemos()).toString());
		m_lblLastReviewed.setText(DateHelper.toUiDate(m_memoBaseInfo.getLastReviewed()));
		m_lblLibraryName.setText(m_txtName.getText());
		m_genreAdapter.clear();
		for (MemoBaseGenreView genre : MemoBaseGenreView.getAll(m_genreDataAdapter.getAll())) {
			m_genreAdapter.add(genre);
		}
	}

	private void importMemolingFile(final String path) {

		Import.importMemosMemolingFile(m_memoBaseId, getActivity(), path, m_memoConflictResolve, new OnSyncComplete() {
			@Override
			public void onComplete(boolean result) {
				Context context = getActivity();

				if(context == null) {
					return;
				}
				
				if (!result) {
					String strResult = String.format(context.getString(R.string.memobase_importFailed), path);
					Toast.makeText(context, strResult, Toast.LENGTH_LONG).show();
					return;
				}

				String strResult = String.format(context.getString(R.string.memobase_importCompleted), path);
				Toast.makeText(context, strResult, Toast.LENGTH_LONG).show();

				MemoBaseFragment.this.bindData();
			}

		});
	}

	private void importCsvFile(final String path) {

		Import.importCsvFile(m_memoBaseId, getActivity(), path, m_memoConflictResolve, new OnSyncComplete() {
			@Override
			public void onComplete(boolean result) {
				Context context = getActivity();

				if (!result) {
					String strResult = String.format(context.getString(R.string.memobase_importFailed), path);
					Toast.makeText(context, strResult, Toast.LENGTH_LONG).show();
					return;
				}

				String strResult = String.format(context.getString(R.string.memobase_importCompleted), path);
				Toast.makeText(context, strResult, Toast.LENGTH_LONG).show();

				MemoBaseFragment.this.bindData();
			}

		});

	}

	private String createMemoBase() {
		MemoBase memoBase = new MemoBase();
		String memoBaseId = UUID.randomUUID().toString();

		memoBase.setActive(true);
		memoBase.setCreated(new Date());
		memoBase.setMemoBaseId(memoBaseId);
		memoBase.setName(getString(R.string.memobase_newMemoBase));
		m_memoBaseAdapter.insert(memoBase, new SyncClientAdapter(getActivity()).getCurrentSyncClientId());

		return memoBaseId;
	}

	private void showSelectMenu(final DialogInterface.OnClickListener onSelected, boolean export) {

		final int stringArrayRes;
		if (export) {
			stringArrayRes = R.array.memobase_exportList;
		} else {
			stringArrayRes = R.array.memobase_importList;
		}

		new AlertDialog.Builder(getActivity()).setTitle(getString(R.string.memobase_inoutTitle))
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

			new AlertDialog.Builder(getActivity())
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
	public void onSuccess() {
		Toast.makeText(getActivity(), R.string.memobase_uploadSuccess, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onException(Exception ex) {
		String exMsg = ex.getMessage();

		if (exMsg.equals(ExceptionReasons.NoMemosToUpload)) {
			Toast.makeText(getActivity(), R.string.memobase_errorMin, Toast.LENGTH_SHORT).show();
		} else if (exMsg.equals(ExceptionReasons.WsAuthError)) {
			Toast.makeText(getActivity(), R.string.memobase_errorAuth, Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(getActivity(), R.string.memobase_error, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onPopulateDrawer(DrawerAdapter drawer) {

		drawer.addGroup(new DrawerView(R.drawable.ic_back, R.string.memobase_backToList));

		drawer.addGroup(new DrawerView(R.drawable.ic_scheduler, R.string.memobase_lblOpenScheduler, new DrawerView.OnClickListener() {
			@Override
			public void onClick(DrawerView v) {
				openScheduler();
			}
		}));
		
		drawer.addGroup(new DrawerView(R.drawable.ic_import, R.string.memobase_lblImport, new DrawerView.OnClickListener() {
			@Override
			public void onClick(DrawerView v) {
				importMemoBase();
			}
		}));

		drawer.addGroup(new DrawerView(R.drawable.ic_export, R.string.memobase_lblExport, new DrawerView.OnClickListener() {
			@Override
			public void onClick(DrawerView v) {
				exportMemoBase();
			}
		}));

		drawer.addGroup(new DrawerView(R.drawable.ic_publish, R.string.memobase_lblPublish, new DrawerView.OnClickListener() {
			@Override
			public void onClick(DrawerView v) {
				publishMemoBase();
			}
		}));

	}

	@Override
	protected void onDataBind(Bundle savedInstanceState) {

		String action = getArguments().getString(Action);

		if (action == null) {
			m_memoBaseId = getArguments().getString(MemoBaseId);
		} else if (action.equals(ActionCreate)) {
			m_memoBaseId = createMemoBase();
		}

		bindData();
	}

	@Override
	public void onDestroyView() {
		save();
		super.onDestroyView();
	}

	private void openScheduler() {
		ApplicationFragment fragment = new SchedulerFragment();
		Bundle bundle = new Bundle();
		bundle.putString(SchedulerFragment.MemoBaseId, m_memoBaseId);
		fragment.setArguments(bundle);
		
		startFragment(fragment);
	}
	
	private void save() {
		MemoBase memoBase = m_memoBaseInfo.getMemoBase();
		memoBase.setName(m_txtName.getText().toString());
		memoBase.setActive(m_chbActive.isChecked());

		m_memoBaseAdapter.update(memoBase, new SyncClientAdapter(getActivity()).getCurrentSyncClientId());
		bindData();
	}

}
