package abmutils.experiment;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import abmutils.experiment.Parameter;
import abmutils.experiment.ParameterValue;

public class Level {
	public String 	title, code;
	public ArrayList<ParameterValue> values = new ArrayList<ParameterValue>();
	
	public Level(String title, String code){
		this.title = title;
		this.code = code;
	}
	public void addValue(ParameterValue value){
		this.values.add(value);
	}
	public Level(LinkedHashMap<String, Object> level) throws Exception {
		this.title = (String)level.get("Title");
		this.code = (String)level.get("Code");
		for(LinkedHashMap<String, Object> param : (ArrayList<LinkedHashMap>)level.get("Params")){
			String paramName = (String)param.keySet().toArray()[0]; 
			Parameter theParam = Global.getInstance().getParameterDefinitions().get(paramName);
			if(theParam == null)throw new Exception("Undefined parameter '"+paramName+"' see TernPOP/src/ternpop/resources/parameterDefinitions.csv for valid parameter names.");
			this.values.add(new ParameterValue(theParam, param.get(paramName).toString()));
		}
	}
	public String toString(){
		String result = "Level:"+this.code+" = {";
		for (int i = 0; i < values.size(); i++) {
			result += values.get(i).parameter.toString() + "=" + values.get(i).toString() + ", ";
		}
		return result.substring(0,result.length()-2) + "}";
	}
}
