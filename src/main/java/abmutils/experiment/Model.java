package abmutils.experiment;

import java.util.LinkedHashMap;

import abmutils.experiment.ParameterValue;

public abstract class Model {
	public LinkedHashMap<String, ParameterValue> parameters;
	public ExperimentalGroup experimentalGroup;

	public Model() {
	}
	public void setParameters(LinkedHashMap<String, ParameterValue> params){
		this.parameters = params;
	}
	public void setExperimentalGroup(ExperimentalGroup experimentalGroup) {
		this.experimentalGroup = experimentalGroup;
	}
	public abstract void initialize();
	public abstract void run();

}
