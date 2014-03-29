package app.memoling.android.anki.entity;

import java.util.List;

import app.memoling.android.entity.Memo;

public class AnkiMessage {

	private final List<Memo> internalMemos;
	
	private final List<Memo> externalMemos;
	
	private final String destinationMemoBaseId;
	
	public AnkiMessage(String destinationMemoBaseId, List<Memo> internalMemos, List<Memo> externalMemos){
		this.internalMemos = internalMemos;
		this.externalMemos = externalMemos;
		this.destinationMemoBaseId = destinationMemoBaseId;
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
}
