package abmutils.experiment;

import java.util.ArrayList;

public class ExperimentalGroup {
	protected ArrayList<Level> levels = new ArrayList<Level>();
	private Integer replicate = 0;
	private String experGroupBrief,experGroupVerbose;
	private boolean repChanged = true;
	
	public ExperimentalGroup() {
	}
	public ExperimentalGroup(ArrayList<Level> levels){
		this.levels = levels;
	}

	public void addLevel(Level level) {
		levels.add(level);
	}
	@SuppressWarnings("unchecked")
	public ExperimentalGroup clone(){
		return new ExperimentalGroup((ArrayList<Level>)this.levels.clone());
	}
	public String toString(){
		return toBrief();
	}
	public String toBrief() {
		if(repChanged){
			this.repChanged = false;
			updateGroupStrings();
		}
		return this.experGroupBrief;
	}
	public String toVerbose() {
		if(repChanged){
			this.repChanged = false;
			updateGroupStrings();
		}
		return this.experGroupVerbose;
	}
	private void updateGroupStrings(){
		experGroupBrief = "";
		experGroupVerbose = "";
		for(Level level : levels){
			experGroupBrief += level.factor.code + ":" + level.code + "  ";
			experGroupVerbose += level.factor.code + ":" + level.code + "  ";
		}
		this.experGroupBrief += "Rep:"+replicate;
		this.experGroupVerbose += "Replicate:"+replicate;
	}
	public void setReplicate(int replicate) {
		this.repChanged = true;
		this.replicate = replicate;
	}

}
