package abmutils.experiment;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import abmutils.experiment.Parameter;
import abmutils.experiment.ParameterValue;

public class Factor {
	public String 	title, code;
	public ArrayList<Level> levels = new ArrayList<Level>();
	
	public Factor(String title, String code){
		this.title = title;
		this.code = code;
	}
	public void addLevel(Level level){
		this.levels.add(level);
	}
	public Factor(LinkedHashMap<String, Object> factor) throws Exception{
		this.title = (String)factor.get("Title");
		this.code = (String)factor.get("Code");
		
		for(LinkedHashMap<String, Object> level : (ArrayList<LinkedHashMap>)factor.get("Levels")){
			levels.add(new Level(level));
		}
	}
	public String toString(){
		String result = "Factor:"+this.code+" = [";
		for(Level level : levels){
			result += level.toString() + ", ";
		}
		return result.substring(0,result.length()-2) + "]";
	}
}
