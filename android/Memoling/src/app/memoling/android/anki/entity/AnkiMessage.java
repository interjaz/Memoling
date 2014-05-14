package app.memoling.android.anki.entity;

import java.util.List;
import java.util.concurrent.locks.Lock;

import app.memoling.android.entity.Memo;


public class AnkiMessage {

	private List<Memo> internalMemos;
	
	private List<Memo> externalMemos;
	
	private String destinationMemoBaseId;
	
	private Lock publishingLock;
	
	private Integer messageType;
	
	private Integer progressBarValue;
	
	private String leftWord;
	
	private String rightWord;

	private String progressInfo;
	
	public AnkiMessage(Integer messageType, String destinationMemoBaseId, List<Memo> internalMemos, List<Memo> externalMemos, Lock publishingLock){
		this.messageType = messageType;
		this.internalMemos = internalMemos;
		this.externalMemos = externalMemos;
		this.destinationMemoBaseId = destinationMemoBaseId;
		this.publishingLock = publishingLock;
	}
	
	public AnkiMessage(Integer messageType, Lock publishingLock, String leftWord, String rightWord) {
		this.messageType = messageType;
		this.publishingLock = publishingLock;
		this.leftWord = leftWord;
		this.rightWord = rightWord;
	}
	
	public AnkiMessage(Integer messageType, Integer progressBarValue, String progressInfo) {
		this.messageType = messageType;
		this.progressBarValue = progressBarValue;
		this.progressInfo = progressInfo;
	}

	public List<Memo> getInternalMemos() {
		return internalMemos;
	}

	public List<Memo> getExternalMemos() {
		return externalMemos;
	}

	public String getDestinationMemoBaseId() {
		return destinationMemoBaseId;
	}

	public Lock getPublishingLock() {
		return publishingLock;
	}

	public Integer getMessageType() {
		return messageType;
	}
	
	public void setMessageType(Integer messageType) {
		this.messageType = messageType;
	}

	public Integer getProgressBarValue() {
		return progressBarValue;
	}
	
	public void setProgressBarValue(Integer progressBarValue) {
		this.progressBarValue = progressBarValue;
	}

	public String getLeftWord() {
		return leftWord;
	}

	public String getRightWord() {
		return rightWord;
	}

	public String getProgressInfo() {
		return progressInfo;
	}

	public void setProgressInfo(String progressInfo) {
		this.progressInfo = progressInfo;
	}
}