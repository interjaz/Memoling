package app.memoling.android.supermemo;

public class SuperMemoEngine {

	enum SortType { 
		RANDOM_NEW_CARD,
		ADDED_NEW_CARD		
		}
	
	enum Easiness {
		INCORRECT_EARLIER_REMEMBERED,	/** 1 */
		INCORRECT_SEEMED_EASY,			/** 2 */
		CORRECT_WITH_DIFFICULTY,		/** 3 */
		CORRECT_AFTER_HESITATION		/** 4 */
	}
	
	enum QueueType {
		NEW, 				/** 0 */
		LEARN, 				/** 1 */ 
		DUE,  				/** 2 */
		DAY_LEARN, 			/** 3 */
		SUSPENDED, 			/** -1 */
		USER_BURIED, 		/** -2 */
		SCHEDULER_BURIED	/** -3 */
	}
	
	enum CardType {
		NEW, 				/** 0 */
		LEARN, 				/** 1 */ 
		DUE,  				/** 2 */
		DAY_LEARN, 			/** 3 */
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
	
	private void answeredCard(Easiness easiness) {
		
		// this card is displayed
		
		// if there was an option to bury the sibling card then bury the card
	
		// increment the repetitions counter of the card
		
		// check the type of card, if the card was new
		boolean isNewCard = false; // TODO read the type of card
		
		// check the type of queue that card came from, if the card was in new queue
		boolean fromNewQueue = false; // TODO read the type of card queue
		
		if(fromNewQueue) {
			// move the card to learning queue
			
			if(isNewCard) {
				// change the type of card to learning card
			}
			
			// "init reps to graduation" TODO figure out what is going here
			// there will be "left" column updated	
		}
		
		// check the type of card queue type
		
		// if the card queue type is LEARN or DAY_LEARN then
		answerLearnCard(easiness);
		
		// if the card queue type is DUE then
		answerReviewedCard(easiness);
		
		// set the last modification time of card
		
		// write all needed information into the card
	}

	private void answerReviewedCard(Easiness easiness) {
		int delay = 0;
		
		if(easiness == Easiness.INCORRECT_EARLIER_REMEMBERED) {
			delay = rescheduleLapse(easiness);
		} else {
			rescheduleReview(easiness);
		}
		
		addMemoReviewLog(easiness, delay);
		// self._logRev(card, ease, delay)
	}

	private void addMemoReviewLog(Easiness easiness, int delay) {
		// TODO Auto-generated method stub
		
	}

	private void rescheduleReview(Easiness easiness) {
		// assign to last interval field interval field value
		
		updateReviewInterval(easiness);
		
		updateDifficulty(easiness);
		
		updateDue();
		
	}

	private void updateDue() {
		// card.due = self.today + card.ivl
	}

	private void updateDifficulty(Easiness easiness) {
		// card.factor = max(1300, card.factor+[-150, 0, 150][ease-2])
	}

	private void updateReviewInterval(Easiness easiness) {
		// get delay from _daysLate()
		// get configuration from _revConf()
		
		// fct = card.factor / 1000
		double difficulty;
		
		int interval;
		
		switch(easiness) {
		case INCORRECT_SEEMED_EASY: 	/** 2 */
//			interval = constrainedInterval();
			break;
		case CORRECT_WITH_DIFFICULTY:	/** 3 */
//			interval = constrainedInterval();
			break;
		case CORRECT_AFTER_HESITATION:	/** 4 */
//			interval = constrainedInterval();
			break;
		}
		
		// return min(interval, conf['maxIvl'])
	}

	private int rescheduleLapse(Easiness easiness) {
		// TODO Auto-generated method stub
		
		
		return 0;
	}

	private void answerLearnCard(Easiness easiness) {
		// TODO Auto-generated method stub
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
