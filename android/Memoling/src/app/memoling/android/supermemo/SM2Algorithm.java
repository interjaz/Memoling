package app.memoling.android.supermemo;

public class SM2Algorithm {

	enum SortType { 
		RANDOM_NEW_CARD,
		ADDED_NEW_CARD		
		}
	
	public final static int START_FACTOR_VALUE = 2500;
	public final static int FINAL_FACTOR_VALUE = 1300;

	//	5 - perfect response
	//	4 - correct response after a hesitation
	//	3 - correct response recalled with serious difficulty
	//	2 - incorrect response; where the correct one seemed easy to recall
	//	1 - incorrect response; the correct one remembered
	//	0 - complete blackout.
	public final static int PERFECT_RESPONSE = 5; 
	public final static int CORRECT_AFTER_HESITATION = 4; 
	public final static int CORRECT_WITH_DIFFICULTY = 3; 
	public final static int INCORRECT_SEEMED_EASY = 2; 
	public final static int INCORRECT_EARLIER_REMEMBERED = 1; 
	public final static int INCORRECT_BLACKOUT = 0; 
		
	// new cards settings section
	
	// number of new cards displayed for a day - specific for every deck
	private int numberOfNewCardsPerDay = 20;
	
	// steps (in minutes)
	private int[] stepsForNewCards = {1, 10};
	
	// sort type 
	// - random card
	// - added time first
	private SortType sortType = SortType.ADDED_NEW_CARD;
	
	// new learned card interval
	private int intervalOfLearnedCardInDays = 1;

	// new easily learned card interval
	private int intervalOfEasyLearnedCardInDays = 4;
	
	// new cards that are connected (back side) will be hidden until tomorrow
	private boolean isConnectedCardHidden = true;
	// ------------------
	
	
	
	// reviewed cards settings section
	
	// number of reviews displayed for a day - specific for every deck
	private int numberOfReviewsPerDay = 100;
	
	
	// ------------------
}
