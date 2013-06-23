package app.memoling.android.helper;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

@SuppressLint("HandlerLeak")
public class HaltableThread<T> extends Thread {

	protected T m_object;
	protected Handler m_uiHandler;
	protected Handler m_handler;
	
	public interface OnHandleMessageHaltable<T> {
		void onHandleMessage(Message message, T waitObject);
	}

	public T getObject() {
		return m_object;
	}
	
	public void setObject(T state) {
		m_object = state;
	}
	
	public Handler getUiHandler() {
		return m_uiHandler;
	}
	
	public Handler getHandler() {
		return m_handler;
	}
	
	public void prepare(final OnHandleMessageHaltable<T> onHandleMessage) {
		m_uiHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				onHandleMessage.onHandleMessage(msg, m_object);
			}
		};
	}

	@Override
	public void run() {
		Looper.prepare();
		m_handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);

			}
		};
	}
	
	public void halt() throws InterruptedException {
		synchronized(m_object) {
			m_object.wait();
		}
	}

	public void clean() {
		Looper.myLooper().quit();
	}

}