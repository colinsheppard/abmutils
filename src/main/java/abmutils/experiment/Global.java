package abmutils.experiment;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedHashMap;

import abmutils.experiment.ParameterValue;

public abstract class Global {
	public static Global instance = null;
	public File experimentDirectory = null;
	public File inputsDirectory = null,outputsDirectory = null;
	public LinkedHashMap<String, ParameterValue> baseParams;
	public LinkedHashMap<String, Parameter> parameterDefinitions;

	public LinkedHashMap<String, Parameter> getParameterDefinitions() {
		return parameterDefinitions;
	}
	public void setParameterDefinitions(LinkedHashMap<String, Parameter> parameterDefinitions) {
		this.parameterDefinitions = parameterDefinitions;
	}
	public void setBaseParams(LinkedHashMap<String, ParameterValue> baseParams) {
		this.baseParams = baseParams;
	}
	public LinkedHashMap<String, ParameterValue> getBaseParams() {
		return baseParams;
	}
	public File findFile(File tempfile) throws FileNotFoundException{
		if(tempfile.exists())return tempfile;
		tempfile = new File(inputsDirectory.getAbsolutePath() + "/" + tempfile.getName());
		if(tempfile.exists())return tempfile;
		tempfile = new File(experimentDirectory.getAbsolutePath() + "/" + tempfile.getName());
		if(tempfile.exists())return tempfile;
		throw new FileNotFoundException("Cannot find file "+tempfile.getName()+" in the ternpop experiment directory or the model inputs directory");
	}
	public File getInputsDirectory() {
		return inputsDirectory;
	}
	public void setInputsDirectory(File inputsDirectory) {
		this.inputsDirectory = inputsDirectory;
	}
	public void setOutputsDirectory(File outputsDirectory) {
		this.outputsDirectory = outputsDirectory;
	}
	public File getOutputsDirectory() {
		return outputsDirectory;
	}
	public File getExperimentDirectory() {
		return experimentDirectory;
	}
	public void setExperimentDirectory(File experimentDirectory) {
		this.experimentDirectory = experimentDirectory;
	}
	public static Global getInstance(){ 
		if(instance == null){
			throw(new RuntimeException("The subclass of Global for this model must be called before any abmutils class are created or used."));
		}
		return (instance);
	}
	protected Global() {
	}
}
