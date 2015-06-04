package abmutils.experiment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.*;
import org.yaml.snakeyaml.Yaml;

import abmutils.experiment.Parameter;
import abmutils.experiment.ParameterValue;
import abmutils.io.InputOutput;
import abmutils.processing.data.Table;

public class Experiment {
	protected static final Logger log = LogManager.getLogger(Experiment.class.getName());
	protected String title, author;
	protected int replicates; 
	protected File experimentFile; 
	protected ArrayList<Factor> factors = new ArrayList<Factor>();
	protected LinkedHashMap<String, ParameterValue> baseParams = new LinkedHashMap <String, ParameterValue>();
	protected Map<String,Object> exp;
	protected static ExecutorService executer = null;
	
	public Experiment(File parameterDefinitionFile, String experimentConfigurationFile) throws Exception,FileNotFoundException{
		try {
			this.loadParameterDefinitions(parameterDefinitionFile);
			this.loadExperiment(experimentConfigurationFile);
			Experiment.executer = Executors.newFixedThreadPool(this.baseParams.get("NumThreads").valueInt);
		} catch (FileNotFoundException e) {
			log.error(e);
			e.printStackTrace();
		} catch (IOException e) {
			log.error(e);
			e.printStackTrace();
		} catch (ParseException e) {
			log.error(e);
			e.printStackTrace();
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
	}
	
	private void loadParameterDefinitions(File parameterDefinitionFile) throws FileNotFoundException, IOException, ParseException{
		LinkedHashMap<String, Parameter> parameterDefinitions = new LinkedHashMap<String, Parameter>();
		Table paramData = InputOutput.readTable(parameterDefinitionFile);
		for (int i = 0; i < paramData.getRowCount(); i++) {
			Parameter newParam = new Parameter(paramData.getString(i,"Name").trim(), 
					paramData.getString(i, "DefaultValue"),
					paramData.getString(i, "Title"),
					paramData.getString(i, "DataType"),
					paramData.getString(i, "DisplayOrder"),
					paramData.getString(i, "LowerBound"),
					paramData.getString(i, "UpperBound"));
			parameterDefinitions.put(paramData.getString(i,"Name").trim(),newParam);
		}
		Global.getInstance().setParameterDefinitions(parameterDefinitions);
	}
	@SuppressWarnings("unchecked")
	private void loadExperiment(String fileName) throws Exception{
		log.info("Loading experiment from: "+fileName);
		this.experimentFile = new File(fileName);
		Global.getInstance().setExperimentDirectory(this.experimentFile.getParentFile());
		InputStream inStream = new FileInputStream(this.experimentFile);
		Yaml yaml = new Yaml();
		exp = (Map<String, Object>) yaml.load(inStream);
		
		// Assign experiment properties and setup inputs/outputs directory paths
		this.title = (String)exp.get("Title");
		this.author = (String)exp.get("Author");
		this.replicates = (int)exp.get("Replicates");
		String inputsDirectoryPath = (String)exp.get("InputsDirectory");
		File inputsDirectory = new File(inputsDirectoryPath);
		String outputsDirectoryPath = (String)exp.get("OutputsDirectory");
		File outputsDirectory = new File(outputsDirectoryPath);
		
		// Allow for an absolute path first, then assume it's relative to the experiment directory
		if(!inputsDirectory.exists())inputsDirectory = new File(Global.getInstance().getExperimentDirectory().getAbsolutePath()+"/"+inputsDirectoryPath);
		Global.getInstance().setInputsDirectory(inputsDirectory);
		if(!outputsDirectory.exists())outputsDirectory = new File(Global.getInstance().getExperimentDirectory().getAbsolutePath()+"/"+outputsDirectoryPath);
		Global.getInstance().setOutputsDirectory(outputsDirectory);
		
		// Build the list of factors, creating a default if none specified
		if(exp.get("Factors") == null){
			LinkedHashMap<String, Object> defaultFactor = new LinkedHashMap<String, Object>();
			LinkedHashMap<String, Object> defaultLevel = new LinkedHashMap<String, Object>();
			ArrayList<Object> defaultLevels = new ArrayList<Object>();
			defaultLevel.put("Title", "NA");
			defaultLevel.put("Code", "NA");
			defaultLevel.put("Params", null);
			defaultLevels.add(defaultLevel);
			defaultFactor.put("Title", "NA");
			defaultFactor.put("Code", "NA");
			defaultFactor.put("Levels", defaultLevels);
			factors.add(new Factor(defaultFactor));
		}else{
			for(LinkedHashMap<String, Object> factor : (ArrayList<LinkedHashMap<String,Object>>)exp.get("Factors")){
				factors.add(new Factor(factor));
			}
		}

		// Initialize our baseline hash of parameter values
		for(Parameter param : Global.getInstance().parameterDefinitions.values()){
			baseParams.put(param.name,new ParameterValue(param,param.defaultValue));
		}
		// Replace specific default parameters with new values under "nonDefaults"
		for(LinkedHashMap<String,Object> nonDefault : ((ArrayList<LinkedHashMap<String,Object>>)exp.get("NonDefaults"))){
			String paramName = (String)nonDefault.keySet().toArray()[0];
			if(!baseParams.containsKey(paramName))throw new Exception("No such parameter name '"+paramName+"' under 'nonDefaults' in "+fileName);
			baseParams.remove(paramName);
			baseParams.put(paramName,new ParameterValue(Global.getInstance().parameterDefinitions.get(paramName), nonDefault.get(paramName).toString()));
		}
		Global.getInstance().setBaseParams(baseParams);
		log.info(baseParams);
	}
	@SuppressWarnings("unchecked")
	public void run(Class<?> modelRunClass) throws ParseException, Exception{
		// levelGroups will be a table where each row is a unique combination
		// of levels from all the factors to use in a model run
		ArrayList<ExperimentalGroup> experimentalGroups = new ArrayList<ExperimentalGroup>();
		combinationsOfLevels(experimentalGroups);
		
		int baseSeed = baseParams.get("Seed").valueInt;
		
		for(ExperimentalGroup experimentalGroup : experimentalGroups){
			LinkedHashMap<String, ParameterValue> runParams = new LinkedHashMap<String, ParameterValue>();
			for(String key : baseParams.keySet()){
				runParams.put(key, (ParameterValue)baseParams.get(key).clone());
			}
			for(Level level : experimentalGroup.levels){
				for(ParameterValue paramValue : level.values){
					runParams.remove(paramValue.parameter.name);
					runParams.put(paramValue.parameter.name,paramValue);
				}
			}
			for (int i = 0; i < this.replicates; i++) {
				LinkedHashMap<String, ParameterValue> runParamsWithRep = (LinkedHashMap<String, ParameterValue>) runParams.clone();
				ExperimentalGroup experimentalGroupWithRep = (ExperimentalGroup) experimentalGroup.clone();
				experimentalGroupWithRep.setReplicate(i);
				Object modelRun = null;
				if(baseSeed != 0){
					runParamsWithRep.remove("Seed");
					runParamsWithRep.put("Seed",new ParameterValue(baseParams.get("Seed").parameter, (new Integer(baseSeed * (i+1))).toString()));
				}
				try {
		            modelRun = modelRunClass.newInstance();
		        } catch (InstantiationException ex) {
		            log.error(ex);
		        } catch (IllegalAccessException ex) {
		            log.error(ex);
		        }
				((Model)modelRun).setParameters(runParamsWithRep);
				((Model)modelRun).setExperimentalGroup(experimentalGroupWithRep);
				executer.execute((Runnable) modelRun);
			}
		}
		executer.shutdown();
	}
	public void combinationsOfLevels(ArrayList<ExperimentalGroup> levelGroups) throws CloneNotSupportedException{
		// Initialize "workingGroup" which will be a row of levels
		ExperimentalGroup workingGroup = new ExperimentalGroup();
		for(int i = 0; i < this.factors.size(); i++){
			workingGroup.addLevel(this.factors.get(i).levels.get(0));
		}
		combinationsOfLevels(levelGroups,workingGroup,0);
	}
	// combinationsOfLevels is a recursive method which replaces the appropriate element in 
	// working group and then passes off to the next factor or it adds the final ExperimentalGroup to
	// levelGroups when all factors have been visited
	public void combinationsOfLevels(ArrayList<ExperimentalGroup> levelGroups, ExperimentalGroup workingGroup, int factorIndex) throws CloneNotSupportedException{
		if(factorIndex >= this.factors.size()){
			levelGroups.add((ExperimentalGroup)workingGroup.clone());
			return;
		}
		for(int i = 0; i < this.factors.get(factorIndex).levels.size(); i++){
			workingGroup.levels.remove(factorIndex);
			workingGroup.levels.add(factorIndex,this.factors.get(factorIndex).levels.get(i));
			combinationsOfLevels(levelGroups, workingGroup, factorIndex + 1);
		}
	}
	public String toString(){
		return "Experiment:"+this.title+" = " + this.factors.toString();
	}
}
