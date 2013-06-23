package app.memoling.android.wordlist;

public class WordsProviderException extends Exception {

	private static final long serialVersionUID = 7360693681215290845L;

	public WordsProviderException(Throwable inner) 
	{
		this.initCause(inner);		
	}
}
