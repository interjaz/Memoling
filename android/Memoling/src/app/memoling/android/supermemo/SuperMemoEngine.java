package app.memoling.android.supermemo;

import java.util.ArrayList;
import java.util.List;

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
	
	private int reviewCount;
	private int learnCount;
	private int newCount;
	private int learnDayCount; 
	
	List reviewQueue = new ArrayList();
	List learnQueue = new ArrayList();
	List newQueue = new ArrayList();
	List learnDayQueue = new ArrayList();
	
	
	// reviewed cards settings section
	
	// number of reviews displayed for a day - specific for every deck
	private int numberOfReviewsPerDay = 100;
	// ------------------
	
	// DONE
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

	// DONE
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

	// DONE
	private void rescheduleReview(Easiness easiness) {
		// assign to last interval field interval field value
		
		updateReviewInterval(easiness);
		
		updateDifficulty(easiness);
		
		updateDue();
		
	}

	// DONE
	private void updateDue() {
		// card.due = self.today + card.ivl
	}

	//DONE
	private void updateDifficulty(Easiness easiness) {
		// card.factor = max(1300, card.factor+[-150, 0, 150][ease-2])
	}

	// DONE
	private void updateReviewInterval(Easiness easiness) {
		// get delay from _daysLate()
		// get configuration from _revConf()
		
		// fct = card.factor / 1000
		double difficulty;
		
		int interval;
		
		switch(easiness) {
		case INCORRECT_SEEMED_EASY: 	/** 2 */
			interval = constrainedInterval(0, 0);
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

	// DONE
	private int constrainedInterval(int i, int j) {
		// "Integer interval after interval factor and prev+1 constraints applied."
		// new = ivl * conf.get('ivlFct', 1)
		// return int(max(new, prev+1))
		return 0;
	}

	// DONE
	private int rescheduleLapse(Easiness easiness) {
		// assign to last interval field interval field value
		
		// increment number of lapses
		
		// new interval is 
		nextLapseInterval();
		
		// update difficulty
		decrementDifficulty();
		
		updateDue();
		
		int delay = 0;
		
		// check if the card is a leech
		// _checkLeech()
		
		if(checkLeech()) { // && card queue type is SUSPENDED /** -1 */
			return delay;
		}
		
		// card odue has now a value of card due
		
		delay = delayForGrade(0); 
		
		// set new value of due
		// card.due = int(delay + time.time())
		
		// set new value of "left"
		// _startingLeft()
		
		// if card.due < self.dayCutoff:
			// increment lrnCount by "left" / 1000
			// change card queue to LEARN /** 1 */
			// heappush(self._lrnQueue, (card.due, card.id))
		
		// else
			// compute ahead
			// ahead = ((card.due - self.dayCutoff) // 86400) + 1
			
			// set new value of due
			// card.due = self.today + ahead 	
		
			// change card queue to DAY_LEARN /** 3 */
		
		return delay;
	}

	// DONE
	private int delayForGrade(int left) {
		// left = left % 1000
		// this should return 1 or 2 so we select one of two delays
		int delay = 0;
		
		return delay * 60;
	}

	// UGLY
	private boolean checkLeech() {
		// TODO Auto-generated method stub
		return false;
	}

	// DONE
	private void decrementDifficulty() {
		// card.factor = max(1300, card.factor-200)
	}

	// DONE
	private void nextLapseInterval() {
		// return max(conf['minInt'], int(card.ivl*conf['mult']))
	}

	// DONE
	private void answerLearnCard(Easiness easiness) {
		// if card type is DUE /** 2 */ then conf is 'lapse' conf
		// else conf is 'new' conf
		
		int type;
		// if card type is DUE /** 2 */ then 
		type = 2;
		// else
		type = 0;
		
		boolean leaving = false;
		
		if(easiness == Easiness.CORRECT_AFTER_HESITATION) {
			rescheduleAsReview(true);
			leaving = true;
		} else if(easiness == Easiness.CORRECT_WITH_DIFFICULTY) { // && (card.left%1000)-1 <= 0
			rescheduleAsReview(false);
			leaving = true;
		} else {
			if(easiness == Easiness.CORRECT_WITH_DIFFICULTY) {
				// decrement real left count and recalculate left today
				// card.left = self._leftToday(conf['delays'], left)*1000 + left
			} else {
				// card.left = self._startingLeft(card)
				// card.ivl = 1;
			}
			int delay = delayForGrade(0); // card.left
			
			//	if card.due < time.time():
			//  	# not collapsed; add some randomness
			//      delay *= random.uniform(1, 1.25)
			
			// card.due = int(time.time() + delay)
			
			// if card.due < self.dayCutoff:
			
//            if card.due < self.dayCutoff:
//                self.lrnCount += card.left // 1000
//                # if the queue is not empty and there's nothing else to do, make
//                # sure we don't put it at the head of the queue and end up showing
//                # it twice in a row
//                card.queue = 1
//                if self._lrnQueue and not self.revCount and not self.newCount:
//                    smallestDue = self._lrnQueue[0][0]
//                    card.due = max(card.due, smallestDue+1)
//                heappush(self._lrnQueue, (card.due, card.id))
//            else:
//                # the card is due in one or more days, so we need to use the
//                # day learn queue
//                ahead = ((card.due - self.dayCutoff) // 86400) + 1
//                card.due = self.today + ahead
//                card.queue = 3
//        self._logLrn(card, ease, conf, leaving, type, lastLeft)
		}
	}
	
	// DONE
	private int leftToday(int left) {
		// "The number of steps that can be completed by the day cutoff."
		
		// now = intTime()
		// delays = delays[-left:]
		int ok = 0;
		
//        for i in range(len(delays)):
//            now += delays[i]*60
//            if now > self.dayCutoff:
//                break
//            ok = i
        return ok+1;
	}

	// DONE
	private void rescheduleAsReview(boolean early) {
		boolean lapse = false; // lapse = card.type == 2
		
		if(lapse) {
			// card.due = max(self.today+1, card.odue)
			// card.odue = 0
		} else {
			rescheduleNew(early);
		}
        // card.queue = 2
        // card.type = 2
	}

	// DONE
	private void rescheduleNew(boolean early) {
//        "Reschedule a new card that's graduated for the first time."
//        card.ivl = self._graduatingIvl(card, conf, early)
		graduatingInterval(early);
//        card.due = self.today+card.ivl
//        card.factor = conf['initialFactor']
	}
	
	// DONE
	private int graduatingInterval(boolean early) {
//        if card.type == 2:
//            # lapsed card being relearnt
//            return card.ivl
		int ideal = 0;
		if(early) {
			// ideal = conf['ints'][1]
		} else {
			// ideal = conf['ints'][0]
		}
		return ideal;
	}
	
	// DONE
	private int startingLeft() {
//        if card.type == 2:
//            conf = self._lapseConf(card)
//        else:
//            conf = self._lrnConf(card)
        int tot = 0; // = len(conf['delays'])
        int tod = leftToday(tot);
        return tot + tod*1000;
	}
	
	private void getCard() {
		// "Return the next due card id, or None."
	}
	
	// DONE
	private void getLearnCard() {
		if(fillLearn()) {
			// cutoff = time.time()
			
			// cutoff += self.col.conf['collapseTime']
			
            // if self._lrnQueue[0][0] < cutoff:
            //    id = heappop(self._lrnQueue)[1]
            //    card = self.col.getCard(id)
            //    self.lrnCount -= card.left // 1000
            //    return card	
		}
	}
	
	// DONE
	private boolean fillLearn() {
		if(learnCount == 0) {
			return false;
		}
		if(learnQueue.size() > 0) {
			return true;
		}
		
		// ("""select due, id from cards where did in %s and queue = 1 and due < :lim limit %d""" % (self._deckLimit(), self.reportLimit), lim=self.dayCutoff)
		
        // # as it arrives sorted by did first, we need to sort it
        // self._lrnQueue.sort()
		
		return (learnQueue.size() > 0);
	}

	// DONE
	private boolean timeForNewCard() {
	     // "True if it's time to display a new card when distributing."
		if(newCount == 0) {
			return false;
		}
        // if self.col.conf['newSpread'] == NEW_CARDS_LAST:
        //    return False
        // elif self.col.conf['newSpread'] == NEW_CARDS_FIRST:
        //    return True
        // elif self.newCardModulus:
        //    return self.reps and self.reps % self.newCardModulus == 0
		return false;
	}
	
	// DONE
	private void getNewCard() {
		if(fillNew()) {
			newCount -= 1;
			// return single card
		}
	}
	
	// DONE
	private boolean fillNew() {
		if(newQueue.size() > 0) {
			return true;
		}
		if(newCount == 0) {
			return false;
		}
		// while loop to fill new queues for all decks
		// in my opinion we should have only current deck
		// but the future will show
		
		// get did
		int limit = 0;
		// limit = min(self.queueLimit, self._deckNewLimit(did))
		if(limit > 0) {
			// use the database adapter to fill the queue
			
			// ("""select id from cards where did = ? and queue = 0 order by due limit ?""", did, lim)
			if(newQueue.size() > 0) {
				// self._newQueue.reverse()
				
				return true;
			}
		}
		if(newCount > 0) {
            // # if we didn't get a card but the count is non-zero,
            // # we need to check again for any cards that were
            // # removed from the queue but not buried
			resetNew();
			return fillNew();
		}
		
		return false;
	}

	// DONE
	private void resetNew() {
		// ("""select count() from (select 1 from cards where did = ? and queue = 0 limit ?)""", did, lim)
		
		// self.newCount = self._walkingCount(self._deckNewLimitSingle, cntFn)
		
		newQueue.clear();
		updateNewCardRatio();
	}

	// DONE
	private void updateNewCardRatio() {
		// if self.col.conf['newSpread'] == NEW_CARDS_DISTRIBUTE:
        // if self.newCount:
        //    self.newCardModulus = (
        //        (self.newCount + self.revCount) // self.newCount)
        //    # if there are cards to review, ensure modulo >= 2
        //    if self.revCount:
        //        self.newCardModulus = max(2, self.newCardModulus)
        //    return
        // self.newCardModulus = 0
	}

	// DONE
	private void getReviewCard() {
		if(fillReview()) {
			reviewCount -= 1;
			// return single card
		}
		
	}
	
	// DONE
	private boolean fillReview() {
		if(reviewQueue.size() > 0) {
			return true;
		}
		if(reviewCount == 0) {
			return false;
		}
		// while loop to fill review queues for all decks
		// in my opinion we should have only current deck
		// but the future will show
		
		// get did
		int limit = 0;
		// limit  = min(self.queueLimit, self._deckRevLimit(did))
		if(limit > 0) {
			// use the database adapter to fill the queue
			
			// ("""select id from cards where did = ? and queue = 2 and due <= ? limit ?""", did, self.today, lim)
			if(reviewQueue.size() > 0) {
				// # random order for regular reviews
                // r = random.Random()
                // r.seed(self.today)
                // r.shuffle(self._revQueue)
				
				return true;
			}
		}
		if(reviewCount > 0) {
            // # if we didn't get a card but the count is non-zero,
            // # we need to check again for any cards that were
            // # removed from the queue but not buried
			resetReview();
			return fillReview();
		}
		
		return false;
	}

	// DONE
	private void resetReview() {
		reviewQueue.clear();
		
		// ("""select count() from (select id from cards where did = ? and queue = 2 and due <= ? limit %d)""" % lim, did, self.today)
		
		// self.revCount = self._walkingCount(self._deckRevLimitSingle, cntFn)
		
	}

	// DONE
	private void getLearnDayCard() {
		if(fillLearnDay()) {
			learnDayCount -= 1;
			// return single card
		}
	}

	// DONE
	private boolean fillLearnDay() {
		if(learnDayCount == 0) {
			return false;
		}
		if(learnDayQueue.size() > 0) {
			return true;
		}
		// while loop to fill review queues for all decks
		// in my opinion we should have only current deck
		// but the future will show
		
		// get did
		
		// ("""select id from cards where did = ? and queue = 3 and due <= ? limit ?""", did, self.today, self.queueLimit)
		if(reviewQueue.size() > 0) {
            // # order
            // r = random.Random()
            // r.seed(self.today)
            // r.shuffle(self._lrnDayQueue)
            
            return true;
		}
		
		return false;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
