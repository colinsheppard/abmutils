package abmutils.experiment;

import java.util.ArrayList;

public class ExperimentalGroup implements Cloneable {
	protected ArrayList<Level> levels = new ArrayList<Level>();
	private Integer replicate = 0;
	private String experGroupBrief,experGroupVerbose;
	private boolean repChanged = true;
	
	public ExperimentalGroup() {
	}
	public ExperimentalGroup(ArrayList<Level> levels){
		this.levels = levels;
	}
	@SuppressWarnings("unchecked")
	@Override
    protected Object clone() throws CloneNotSupportedException {
		ExperimentalGroup cloned = (ExperimentalGroup) super.clone();
		cloned.repChanged = true;
		cloned.levels = (ArrayList<Level>)cloned.levels.clone();
		return cloned;
	}

	public void addLevel(Level level) {
		levels.add(level);
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
