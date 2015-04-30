package abmutils.experiment;

import java.util.LinkedHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import abmutils.experiment.ParameterValue;

public abstract class Model {
	public LinkedHashMap<String, ParameterValue> parameters;

	public Model() {
	}
	public void setParameters(LinkedHashMap<String, ParameterValue> params){
		this.parameters = params;
	}
	public abstract void initialize();
	public abstract void run();

}
