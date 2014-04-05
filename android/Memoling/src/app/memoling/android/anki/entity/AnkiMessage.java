package app.memoling.android.anki.entity;

import java.util.List;
import java.util.concurrent.locks.Lock;

import app.memoling.android.entity.Memo;

public class AnkiMessage {

	private final List<Memo> internalMemos;
	
	private final List<Memo> externalMemos;
	
	private final String destinationMemoBaseId;
	
	private final Lock publishingLock;
	
	public AnkiMessage(String destinationMemoBaseId, List<Memo> internalMemos, List<Memo> externalMemos, Lock publishingLock){
		this.internalMemos = internalMemos;
		this.externalMemos = externalMemos;
		this.destinationMemoBaseId = destinationMemoBaseId;
		this.publishingLock = publishingLock;
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
}
