package abmutils.schedule;

import java.util.Comparator;

/*
 * The EventComparator first compares based on tick (which is a Double) and then on id (which is a long)
 * so if there is a tie for tick, the event that was created first get's executed first allowing
 * for a more intuitive execution.
 */
public class EventComparator implements Comparator<Event> {
	@Override
	public int compare(Event a, Event b) {
		if(a.tick < b.tick){
			return -1;
		}else if(a.tick > b.tick){
			return 1;
		}else if(a.priority < b.priority){
			return -1;
		}else if(a.priority > b.priority){
			return 1;
		}else if(a.id < b.id){
			return -1;
		}else if(a.id > b.id){
			return 1;
		}else{
			return 0;
		}
	}

}