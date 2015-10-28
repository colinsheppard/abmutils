package abmutils.experiment;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import abmutils.experiment.Parameter.ValidationCode;

import org.apache.logging.log4j.*;

public class ParameterValue implements Cloneable{
	private static final Logger log = LogManager.getLogger(ParameterValue.class.getName());
	
	public File		valueFile;
	public Integer 	valueInt;
	public Double	valueDbl;
	public boolean	valueBoo;
	public String	valueStr;
	public Date		valueDat;
	public Date		valueDay;
	public Parameter parameter;
	
	public ParameterValue(Parameter param, String value) throws Exception, ParseException{
		this.parameter = param;
		
		value = value.trim();
		validateParam(value);
		setValue(value);
	}
	@Override
    protected Object clone() throws CloneNotSupportedException {
		ParameterValue cloned = (ParameterValue) super.clone();
		return cloned;
	}
	
	public void setValue(String value) throws ParseException{
		switch(this.parameter.type){
		case DOUBLE:
			this.valueDbl = Double.parseDouble(value);
			break;
		case STRING:
			this.valueStr = value;
			break;
		case INFILENAME:
		case OUTFILENAME:
			this.valueFile = new File(value);
			break;
		case BOOL:
			this.valueBoo = Boolean.parseBoolean(value);
			break;
		case INTEGER:
			this.valueInt = Integer.parseInt(value.replaceAll("\\.0*$", ""));
			break;
		case DATE:
			this.valueDat = new SimpleDateFormat("yyyy-MM-dd").parse(value);
			break;
		case DAY:
			this.valueDay = new SimpleDateFormat("MM-dd").parse(value);
			break;
		}
	}
	
	public void validateParam(String value) throws Exception,FileNotFoundException{
		ValidationCode validationCode = this.parameter.validate(value); 
		switch(validationCode){
		case UNTESTED:
			log.warn("Parameter "+parameter.name+" value '"+value+"' could not be validated");
			break;
		case RANGE_LOW:
			log.warn("Parameter "+parameter.name+" value '"+value+"' is below the recommended range");
			break;
		case RANGE_HIGH:
			log.warn("Parameter "+parameter.name+" value '"+value+"' is above the recommended range");
			break;
		case LENGTH_LONG:
			log.warn("Parameter "+parameter.name+" value '"+value+"' has more characters than recommended");
			break;
		case LENGTH_SHORT:
			log.warn("Parameter "+parameter.name+" value '"+value+"' has fewer characters than recommended");
			break;
		case INVALID_DOUBLE:
			throw new Exception("Parameter "+parameter.name+" value '"+value+"' is not a valid double");
		case INVALID_INTEGER:
			throw new Exception("Parameter "+parameter.name+" value '"+value+"' is not a valid integer");
		case INVALID_DATE:
			throw new Exception("Parameter "+parameter.name+" value '"+value+"' is not a valid date string");
		case INVALID_DAY:
			throw new Exception("Parameter "+parameter.name+" value '"+value+"' is not a valid day string");
		case INVALID_BOOL:
			throw new Exception("Parameter "+parameter.name+" value '"+value+"' is not a valid boolean");
		case ILLEGAL_FILE:
			throw new Exception("Parameter "+parameter.name+" value '"+value+"' refers to an illegal file");
		default:
		}
	}
	
	public String toString(){
		String result = "Value:";
		switch(this.parameter.type){
			case DOUBLE:
				result += this.valueDbl;
				break;
			case STRING:
				result += this.valueStr.toString();
				break;
			case INFILENAME:
			case OUTFILENAME:
				result += this.valueFile.toString();
				break;
			case BOOL:
				result += this.valueBoo;
				break;
			case INTEGER:
				result += this.valueInt;
				break;
			case DATE:
				result += this.valueDat.toString();
				break;
			case DAY:
				result += this.valueDay.toString();
				break;
			default:
				result += "";
				break;
		}
		return result;
	}
}
