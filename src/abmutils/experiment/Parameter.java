package abmutils.experiment;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Parameter {
	public enum DataType{
		DOUBLE, INTEGER, STRING, INFILENAME, OUTFILENAME, DATE, DAY, BOOL
	}
	public enum ValidationCode{
		VALID, UNTESTED, RANGE_LOW, RANGE_HIGH, LENGTH_LONG, LENGTH_SHORT, INVALID_DOUBLE, 
		INVALID_INTEGER, INVALID_DATE, INVALID_DAY, INVALID_BOOL, MISSING_FILE, ILLEGAL_FILE
	}
	public String name,title,defaultValue;
	public DataType type;
	public String rangeLow;
	public String rangeHigh;
	public Double dLow;
	public Double dHigh;
	public Integer iLow;
	public Integer iHigh;
	public Integer stringLow;
	public Integer stringHigh;
	public Integer displayOrder;

	public Parameter(String paramName,String defaultValue,String variableTitle,String dataType,String displayOrder, String rangeLow, String rangeHigh){
		this.name = paramName;
		this.defaultValue = defaultValue;
		this.type = this.typeFromString(dataType);
		this.rangeLow = rangeLow;
		this.rangeHigh = rangeHigh;
		this.displayOrder = Integer.parseInt(displayOrder);
		switch(this.type){
		case DOUBLE:
			this.dHigh = this.rangeHigh.equals("") ? Double.MAX_VALUE : Double.parseDouble(this.rangeHigh);
			this.dLow = this.rangeHigh.equals("") ? -Double.MAX_VALUE : Double.parseDouble(this.rangeLow);
			break;
		case INTEGER:
			this.iHigh 	= this.rangeHigh.equals("") ? Integer.MAX_VALUE : Integer.parseInt(this.rangeHigh);
			this.iLow 	= this.rangeLow.equals("") ? Integer.MIN_VALUE : Integer.parseInt(this.rangeLow);
			break;
		case INFILENAME:
		case OUTFILENAME:
			this.stringLow = this.rangeLow.equals("") ? 0 : Integer.parseInt(this.rangeLow);
			this.stringHigh = this.rangeHigh.equals("") ? 250 : Integer.parseInt(this.rangeHigh);
			break;
		case STRING:
			this.stringLow = this.rangeLow.equals("") ? 0 : Integer.parseInt(this.rangeLow);
			this.stringHigh = this.rangeHigh.equals("") ? Integer.MAX_VALUE : Integer.parseInt(this.rangeHigh);
			break;
		}
	}
	public ValidationCode validate(String value){
		ValidationCode validationCode = null;
		switch(this.type){
		case DOUBLE:
			if(value.matches("[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?")){
				Double dvalue = Double.parseDouble(value);
				if(dvalue > this.dHigh){
					validationCode = ValidationCode.RANGE_HIGH;
					break;
				}else if(dvalue < this.dLow){
					validationCode = ValidationCode.RANGE_LOW;
					break;
				}
				validationCode = ValidationCode.VALID;
			}else{
				validationCode = ValidationCode.INVALID_DOUBLE;
			}
			break;
		case INTEGER:
			if(value.matches("[-]?[0-9]+")){
				Integer ivalue = Integer.parseInt(value);
				if(ivalue > this.iHigh){
					validationCode = ValidationCode.RANGE_HIGH;
					break;
				}else if(ivalue < this.iLow){
					validationCode = ValidationCode.RANGE_LOW;
					break;
				}
				validationCode = ValidationCode.VALID;
			}else{
				validationCode = ValidationCode.INVALID_INTEGER;
			}
			break;
		case STRING:
			if(value.length() > this.stringHigh){
				validationCode = ValidationCode.LENGTH_LONG;
				break;
			}else if(value.length() < this.stringLow){
				validationCode = ValidationCode.LENGTH_SHORT;
				break;
			}
			validationCode = ValidationCode.VALID;
			break;
		case OUTFILENAME:
			if(value.length() > this.stringHigh){
				validationCode = ValidationCode.LENGTH_LONG;
				break;
			}else if(value.length() < this.stringLow){
				validationCode = ValidationCode.LENGTH_SHORT;
				break;
			}else{
				validationCode = ValidationCode.VALID;
			}
			break;
		case INFILENAME:
			if(value.length() > this.stringHigh){
				validationCode = ValidationCode.LENGTH_LONG;
				break;
			}else if(value.length() < this.stringLow){
				validationCode = ValidationCode.LENGTH_SHORT;
				break;
			}
			// Now test if the file exists
			File f = new File(value);
			if(!f.exists() || value.equals("")){
				validationCode = ValidationCode.MISSING_FILE;
			}else if(f.isDirectory()){
				validationCode = ValidationCode.ILLEGAL_FILE;
			}else{
				validationCode = ValidationCode.VALID;
			}
			break;
		case DATE:
			validationCode = ValidationCode.VALID;
			// The year must be 4 digits long
			String[] pieces = value.split("/");
			if(pieces.length<3 || pieces[2].length()<4){
				validationCode = ValidationCode.INVALID_DATE;
			}else{
				try {
					new SimpleDateFormat("yyyy-MM-dd").parse(value);
				} catch (ParseException e) {
					validationCode = ValidationCode.INVALID_DATE;
				}
			}
			break;
		case DAY:
			validationCode = ValidationCode.VALID;
			try {
				new SimpleDateFormat("MM-dd").parse(value);
			} catch (ParseException e) {
				validationCode = ValidationCode.INVALID_DAY;
			}
			break;
		case BOOL:
			if(value.equals("1") || value.equals("0")){
				validationCode = ValidationCode.VALID;
			}else{
				validationCode = ValidationCode.INVALID_BOOL;
			}
			break;
		default:
			validationCode = ValidationCode.UNTESTED;
		}
		return validationCode;
	}
	public DataType typeFromString(String strType){
		if(strType.equals("DOUBLE")){
			return DataType.DOUBLE;
		}else if(strType.equals("STRING")){
			return DataType.STRING;
		}else if(strType.equals("INFILENAME")){
			return DataType.INFILENAME;
		}else if(strType.equals("OUTFILENAME")){
			return DataType.OUTFILENAME;
		}else if(strType.equals("INTEGER")){
			return DataType.INTEGER;
		}else if(strType.equals("DATE")){
			return DataType.DATE;
		}else if(strType.equals("DAY")){
			return DataType.DAY;
		}else if(strType.equals("BOOL")){
			return DataType.BOOL;
		}
		return null;
	}
	public String typeToString(DataType type){
		switch(type){
		case DOUBLE:
			return "double";  
		case STRING:
			return "string";
		case INFILENAME:
		case OUTFILENAME:
			return "filename";
		case BOOL:
			return "BOOL";
		case INTEGER:
			return "int";
		case DATE:
			return "date";
		case DAY:
			return "day";
		}
		return null;
	}
	public String toString(){
		return "Param:"+this.name;
	}

}
