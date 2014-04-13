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
	
	public AnkiMessage(Integer messageType, String destinationMemoBaseId, List<Memo> internalMemos, List<Memo> externalMemos, Lock publishingLock){
		this.messageType = messageType;
		this.internalMemos = internalMemos;
		this.externalMemos = externalMemos;
		this.destinationMemoBaseId = destinationMemoBaseId;
		this.publishingLock = publishingLock;
	}
	
	public AnkiMessage(Integer messageType, Lock publishingLock) {
		this.messageType = messageType;
		this.publishingLock = publishingLock;
	}
	
	public AnkiMessage(Integer messageType, Integer progressBarValue) {
		this.messageType = messageType;
		this.progressBarValue = progressBarValue;
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
}