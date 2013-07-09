package app.memoling.android.ui.activity;

import java.text.DateFormatSymbols;
import java.util.ArrayList;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import app.memoling.android.R;
import app.memoling.android.entity.Schedule;
import app.memoling.android.schedule.BaseSchedule;
import app.memoling.android.schedule.Scheduler;
import app.memoling.android.ui.AdActivity;
import app.memoling.android.ui.ResourceManager;
import app.memoling.android.ui.adapter.ModifiableComplexTextAdapter;
import app.memoling.android.ui.view.ScheduleView;

public class SchedulerActivity extends AdActivity {

	public static final String MemoBaseId = "MemoBaseId";

	private ResourceManager m_resources;

	private TextView m_txtHours;
	private TextView m_txtMinutes;

	// Days
	private Button m_btnMo;
	private Button m_btnTu;
	private Button m_btnWe;
	private Button m_btnTh;
	private Button m_btnFr;
	private Button m_btnSa;
	private Button m_btnSu;

	// Days Handlers
	private BtnMoEventHandler m_btnMoEventHandler = new BtnMoEventHandler();
	private BtnTuEventHandler m_btnTuEventHandler = new BtnTuEventHandler();
	private BtnWeEventHandler m_btnWeEventHandler = new BtnWeEventHandler();
	private BtnThEventHandler m_btnThEventHandler = new BtnThEventHandler();
	private BtnFrEventHandler m_btnFrEventHandler = new BtnFrEventHandler();
	private BtnSaEventHandler m_btnSaEventHandler = new BtnSaEventHandler();
	private BtnSuEventHandler m_btnSuEventHandler = new BtnSuEventHandler();

	private ScheduleView m_editItem;

	private Button m_btnSubmit;
	private Button m_btnNew;
	private Button m_btnEdit;
	private Button m_btnDelete;

	private ListView m_lstList;
	private ModifiableComplexTextAdapter<ScheduleView> m_lstListAdapter;

	private String m_memoBaseId = "testId";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scheduler);
		onCreate_Ads();

		m_resources = new ResourceManager(this);

		((TextView) findViewById(R.id.textView1)).setTypeface(m_resources.getThinFont());

		m_txtHours = (TextView) findViewById(R.id.scheduler_txtHours);
		m_txtHours.setTypeface(m_resources.getThinFont());
		m_txtMinutes = (TextView) findViewById(R.id.scheduler_txtMinutes);
		m_txtMinutes.setTypeface(m_resources.getThinFont());

		String[] days = new DateFormatSymbols().getShortWeekdays();
		
		m_btnMo = (Button) findViewById(R.id.scheduler_btnMo);
		m_btnMo.setTypeface(m_resources.getThinFont());
		m_btnMo.setOnClickListener(m_btnMoEventHandler);
		m_btnMo.setText(days[2]);
		m_btnTu = (Button) findViewById(R.id.scheduler_btnTu);
		m_btnTu.setTypeface(m_resources.getThinFont());
		m_btnTu.setOnClickListener(m_btnTuEventHandler);
		m_btnTu.setText(days[3]);
		m_btnWe = (Button) findViewById(R.id.scheduler_btnWe);
		m_btnWe.setTypeface(m_resources.getThinFont());
		m_btnWe.setOnClickListener(m_btnWeEventHandler);
		m_btnWe.setText(days[4]);
		m_btnTh = (Button) findViewById(R.id.scheduler_btnTh);
		m_btnTh.setTypeface(m_resources.getThinFont());
		m_btnTh.setOnClickListener(m_btnThEventHandler);
		m_btnTh.setText(days[5]);
		m_btnFr = (Button) findViewById(R.id.scheduler_btnFr);
		m_btnFr.setTypeface(m_resources.getThinFont());
		m_btnFr.setOnClickListener(m_btnFrEventHandler);
		m_btnFr.setText(days[6]);
		m_btnSa = (Button) findViewById(R.id.scheduler_btnSa);
		m_btnSa.setTypeface(m_resources.getThinFont());
		m_btnSa.setOnClickListener(m_btnSaEventHandler);
		m_btnSa.setText(days[7]);
		m_btnSu = (Button) findViewById(R.id.scheduler_btnSu);
		m_btnSu.setTypeface(m_resources.getThinFont());
		m_btnSu.setOnClickListener(m_btnSuEventHandler);
		m_btnSu.setText(days[1]);

		m_btnSubmit = (Button) findViewById(R.id.scheduler_btnSubmit);
		BtnSubmitEventHandler submitHandler = new BtnSubmitEventHandler();
		m_btnSubmit.setOnClickListener(submitHandler);
		m_btnSubmit.setOnTouchListener(submitHandler);
		m_btnSubmit.setTypeface(m_resources.getThinFont());

		m_lstList = (ListView) findViewById(R.id.schedule_list);
		m_lstListAdapter = new ModifiableComplexTextAdapter<ScheduleView>(this, R.layout.adapter_scheduler_listview,
				new int[] { R.id.textView1, R.id.textView2 }, new Typeface[] { m_resources.getThinFont(),
						m_resources.getCondensedFont() });
		m_lstList.setAdapter(m_lstListAdapter);
		m_lstList.setOnItemClickListener(new LstListEventHandler());

		m_btnNew = (Button) findViewById(R.id.scheduler_btnNew);
		m_btnNew.setTypeface(m_resources.getThinFont());
		m_btnNew.setOnClickListener(new BtnNewEventHandler());
		m_btnEdit = (Button) findViewById(R.id.scheduler_btnEdit);
		m_btnEdit.setTypeface(m_resources.getThinFont());
		m_btnEdit.setOnClickListener(new BtnEditEventHandler());
		m_btnDelete = (Button) findViewById(R.id.scheduler_btnDelete);
		m_btnDelete.setTypeface(m_resources.getThinFont());
		m_btnDelete.setOnClickListener(new BtnDeleteEventHandler());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_scheduler, menu);
		return true;
	}

	@Override
	protected void onStart() {
		super.onStart();
		m_memoBaseId = getIntent().getStringExtra(MemoBaseId);		
		bindData();
	}

	private void bindData() {
		m_lstListAdapter.clear();
		BaseSchedule base = Scheduler.getSchedule(m_memoBaseId);
		ArrayList<Schedule> schedule = base.getSchedule();
		for (int i = 0; i < schedule.size(); i++) {
			m_lstListAdapter.add(new ScheduleView(schedule.get(i), this));
		}
	}

	private class BtnMoEventHandler implements OnClickListener {
		private boolean mClicked;

		public boolean isClicked() {
			return mClicked;
		}

		public void setClicked(boolean clicked) {
			mClicked = clicked;
			onClick(null);
		}

		@Override
		public void onClick(View view) {
			if (view != null) {
				mClicked = !mClicked;
			}

			Button button = m_btnMo;
			if (mClicked) {
				button.setBackgroundColor(0xFF0099cc);
			} else {
				button.setBackgroundColor(0xFF1A1A1A);
			}
		}
	}

	private class BtnTuEventHandler implements OnClickListener {
		private boolean mClicked;

		public boolean isClicked() {
			return mClicked;
		}

		public void setClicked(boolean clicked) {
			mClicked = clicked;
			onClick(null);
		}

		@Override
		public void onClick(View view) {
			if (view != null) {
				mClicked = !mClicked;
			}

			Button button = m_btnTu;
			if (mClicked) {
				button.setBackgroundColor(0xFF0099cc);
			} else {
				button.setBackgroundColor(0xFF1A1A1A);
			}

		}
	}

	private class BtnWeEventHandler implements OnClickListener {
		private boolean mClicked;

		public boolean isClicked() {
			return mClicked;
		}

		public void setClicked(boolean clicked) {
			mClicked = clicked;
			onClick(null);
		}

		@Override
		public void onClick(View view) {
			if (view != null) {
				mClicked = !mClicked;
			}

			Button button = m_btnWe;
			if (mClicked) {
				button.setBackgroundColor(0xFF0099cc);
			} else {
				button.setBackgroundColor(0xFF1A1A1A);
			}

		}
	}

	private class BtnThEventHandler implements OnClickListener {
		private boolean mClicked;

		public boolean isClicked() {
			return mClicked;
		}

		public void setClicked(boolean clicked) {
			mClicked = clicked;
			onClick(null);
		}

		@Override
		public void onClick(View view) {
			if (view != null) {
				mClicked = !mClicked;
			}

			Button button = m_btnTh;
			if (mClicked) {
				button.setBackgroundColor(0xFF0099cc);
			} else {
				button.setBackgroundColor(0xFF1A1A1A);
			}
		}
	}

	private class BtnFrEventHandler implements OnClickListener {
		private boolean mClicked;

		public boolean isClicked() {
			return mClicked;
		}

		public void setClicked(boolean clicked) {
			mClicked = clicked;
			onClick(null);
		}

		@Override
		public void onClick(View view) {
			if (view != null) {
				mClicked = !mClicked;
			}

			Button button = m_btnFr;
			if (mClicked) {
				button.setBackgroundColor(0xFF0099cc);
			} else {
				button.setBackgroundColor(0xFF1A1A1A);
			}

		}
	}

	private class BtnSaEventHandler implements OnClickListener {
		private boolean mClicked;

		public boolean isClicked() {
			return mClicked;
		}

		public void setClicked(boolean clicked) {
			mClicked = clicked;
			onClick(null);
		}

		@Override
		public void onClick(View view) {
			if (view != null) {
				mClicked = !mClicked;
			}

			Button button = m_btnSa;
			if (mClicked) {
				button.setBackgroundColor(0xFF0099cc);
			} else {
				button.setBackgroundColor(0xFF1A1A1A);
			}

		}
	}

	private class BtnSuEventHandler implements OnClickListener {
		private boolean mClicked;

		public boolean isClicked() {
			return mClicked;
		}

		public void setClicked(boolean clicked) {
			mClicked = clicked;
			onClick(null);
		}

		@Override
		public void onClick(View view) {
			if (view != null) {
				mClicked = !mClicked;
			}

			Button button = m_btnSu;
			if (mClicked) {
				button.setBackgroundColor(0xFF0099cc);
			} else {
				button.setBackgroundColor(0xFF1A1A1A);
			}
		}
	}

	private class BtnSubmitEventHandler implements OnClickListener, OnTouchListener {

		@Override
		public void onClick(View view) {

			Button button = (Button) view;
			button.setBackgroundColor(0xFF1A1A1A);

			if (!validate()) {
				return;
			}

			Schedule schedule = new Schedule();
			schedule.setHours(Integer.parseInt(m_txtHours.getText().toString()));
			schedule.setMinutes(Integer.parseInt(m_txtMinutes.getText().toString()));

			schedule.setDays(new boolean[] { m_btnMoEventHandler.isClicked(), m_btnTuEventHandler.isClicked(),
					m_btnWeEventHandler.isClicked(), m_btnThEventHandler.isClicked(), m_btnFrEventHandler.isClicked(),
					m_btnSaEventHandler.isClicked(), m_btnSuEventHandler.isClicked() });

			m_lstListAdapter.add(new ScheduleView(schedule, SchedulerActivity.this));
			updateSchedules();
			resetView();
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {

			Button button = (Button) v;
			button.setBackgroundColor(0xFF0099cc);

			return false;
		}
	}

	private class BtnNewEventHandler implements OnClickListener {
		@Override
		public void onClick(View v) {
			resetView();
		}
	}

	private class BtnEditEventHandler implements OnClickListener {
		@Override
		public void onClick(View v) {

			if (!validate()) {
				return;
			}

			Schedule schedule = m_editItem.getSchedule();
			schedule.setHours(Integer.parseInt(m_txtHours.getText().toString()));
			schedule.setMinutes(Integer.parseInt(m_txtMinutes.getText().toString()));

			schedule.setDays(new boolean[] { m_btnMoEventHandler.isClicked(), m_btnTuEventHandler.isClicked(),
					m_btnWeEventHandler.isClicked(), m_btnThEventHandler.isClicked(), m_btnFrEventHandler.isClicked(),
					m_btnSaEventHandler.isClicked(), m_btnSuEventHandler.isClicked() });

			m_lstListAdapter.notifyDataSetInvalidated();
			updateSchedules();
			resetView();
		}
	}

	private class BtnDeleteEventHandler implements OnClickListener {
		@Override
		public void onClick(View v) {
			m_lstListAdapter.remove(m_editItem);
			updateSchedules();
			resetView();
		}
	}

	private class LstListEventHandler implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

			ScheduleView item = (ScheduleView) m_lstListAdapter.getItem(position);

			boolean[] days = item.getSchedule().getDays();
			m_btnMoEventHandler.setClicked(days[0]);
			m_btnTuEventHandler.setClicked(days[1]);
			m_btnWeEventHandler.setClicked(days[2]);
			m_btnThEventHandler.setClicked(days[3]);
			m_btnFrEventHandler.setClicked(days[4]);
			m_btnSaEventHandler.setClicked(days[5]);
			m_btnSuEventHandler.setClicked(days[6]);

			m_txtHours.setText(String.format("%02d", item.getSchedule().getHours()));
			m_txtMinutes.setText(String.format("%02d", item.getSchedule().getMinutes()));

			m_btnSubmit.setVisibility(View.INVISIBLE);
			m_btnNew.setVisibility(View.VISIBLE);
			m_btnEdit.setVisibility(View.VISIBLE);
			m_btnDelete.setVisibility(View.VISIBLE);

			m_editItem = item;
		}

	}

	private void resetView() {

		m_btnNew.setVisibility(View.INVISIBLE);
		m_btnEdit.setVisibility(View.INVISIBLE);
		m_btnDelete.setVisibility(View.INVISIBLE);
		m_btnSubmit.setVisibility(View.VISIBLE);

		m_btnMoEventHandler.setClicked(false);
		m_btnTuEventHandler.setClicked(false);
		m_btnWeEventHandler.setClicked(false);
		m_btnThEventHandler.setClicked(false);
		m_btnFrEventHandler.setClicked(false);
		m_btnSaEventHandler.setClicked(false);
		m_btnSuEventHandler.setClicked(false);
	}

	private boolean validate() {
		boolean status = true;
		StringBuilder sb = new StringBuilder();

		if (!m_btnMoEventHandler.isClicked() && !m_btnTuEventHandler.isClicked() && !m_btnWeEventHandler.isClicked()
				&& !m_btnThEventHandler.isClicked() && !m_btnFrEventHandler.isClicked()
				&& !m_btnSaEventHandler.isClicked() && !m_btnSuEventHandler.isClicked()) {
			sb.append("Selected date range is invalid. ");
			status = false;
		}

		int hours = Integer.parseInt(m_txtHours.getText().toString());
		int minutes = Integer.parseInt(m_txtMinutes.getText().toString());

		if (hours < 0 || hours > 24) {
			sb.append("Selected hours range is invalid. ");
			status = false;
		}

		if (minutes < 0 || minutes > 59) {
			sb.append("Selected minutes range is invalid. ");
			status = false;
		}

		if (sb.length() > 0) {
			sb.setLength(sb.length() - 1);
			Toast.makeText(this, sb.toString(), Toast.LENGTH_LONG).show();
		}

		return status;

	}

	private void updateSchedules() {

		ArrayList<Schedule> schedules = new ArrayList<Schedule>();

		for (int i = 0; i < m_lstListAdapter.getCount(); i++) {
			schedules.add(m_lstListAdapter.getItem(i).getSchedule());
		}

		Scheduler.updateSchedule(new BaseSchedule(m_memoBaseId, schedules));
		Scheduler.updateAlarm(this);
	}
}
