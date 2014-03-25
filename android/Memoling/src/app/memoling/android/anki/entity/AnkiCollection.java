package app.memoling.android.anki.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AnkiCollection {

	// Database columns
	
	// database column: id
	private int m_collectionId;
	
	// database column: crt
	private Date m_crt;
	
	// database column: mod
	private Date m_lastModification;
	
	// database column: scm
	private Date m_scm;
	
	// database column: ver
	private int m_version;
	
	// database column: dty
	private int m_dty;
	
	// database column: usn
	private int m_universalSerialNumber;
	
	// database column: ls
	private Date m_lastSync;
	
	// database column: conf
	private String m_configuration;
	
	// database column: models
	private String m_models;
	
	// database column: decks
	private String m_decks;
	
	// database column: dconf
	private String m_defaultConfiguration;
	
	// database column: tags
	private String m_tags;

	public int getCollectionId() {
		return m_collectionId;
	}

	public void setCollectionId(int m_collectionId) {
		this.m_collectionId = m_collectionId;
	}

	public Date getCrt() {
		return m_crt;
	}

	public void setCrt(Date m_crt) {
		this.m_crt = m_crt;
	}

	public Date getLastModification() {
		return m_lastModification;
	}

	public void setLastModification(Date m_lastModification) {
		this.m_lastModification = m_lastModification;
	}

	public Date getScm() {
		return m_scm;
	}

	public void setScm(Date m_scm) {
		this.m_scm = m_scm;
	}

	public int getVersion() {
		return m_version;
	}

	public void setVersion(int m_version) {
		this.m_version = m_version;
	}

	public int getDty() {
		return m_dty;
	}

	public void setDty(int m_dty) {
		this.m_dty = m_dty;
	}

	public int getUniversalSerialNumber() {
		return m_universalSerialNumber;
	}

	public void setUniversalSerialNumber(int m_universalSerialNumber) {
		this.m_universalSerialNumber = m_universalSerialNumber;
	}

	public Date getLastSync() {
		return m_lastSync;
	}

	public void setLastSync(Date m_lastSync) {
		this.m_lastSync = m_lastSync;
	}

	public String getConfiguration() {
		return m_configuration;
	}

	public void setConfiguration(String m_configuration) {
		this.m_configuration = m_configuration;
	}

	public String getModels() {
		return m_models;
	}

	public void setModels(String m_models) {
		this.m_models = m_models;
	}

	public String getDecks() {
		return m_decks;
	}

	public void setDecks(String m_decks) {
		this.m_decks = m_decks;
	}

	public String getDefaultConfiguration() {
		return m_defaultConfiguration;
	}

	public void setDefaultConfiguration(String m_defaultConfiguration) {
		this.m_defaultConfiguration = m_defaultConfiguration;
	}

	public String getTags() {
		return m_tags;
	}

	public void setTags(String m_tags) {
		this.m_tags = m_tags;
	}
	
	public static List<AnkiDeck> getDecksDescription(String decksDescription) {
		List<AnkiDeck> ankiDeckList = new ArrayList<AnkiDeck>();
		
		try {
			JSONObject ankiDecks = new JSONObject(decksDescription);
			JSONArray ankiDecksNames = ankiDecks.names();
			
			for (int i = 0; i < ankiDecksNames.length(); i++) {
				JSONObject newObject = ankiDecks.getJSONObject(ankiDecksNames.getString(i));
				AnkiDeck ankiDeck = new AnkiDeck().deserialize(newObject);
				ankiDeckList.add(ankiDeck);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ankiDeckList;
	}

	public static List<AnkiModel> getModelsDescription(String modelsDescription) {
		return null;

	}

	public static AnkiConfiguration getConfigurationDescription(String configurationDescription) {
		try {
			JSONObject ankiConfigurationJSON = new JSONObject(configurationDescription);
			AnkiConfiguration ankiConfiguration = new AnkiConfiguration().deserialize(ankiConfigurationJSON);
			return ankiConfiguration;
				
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}