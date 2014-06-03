package abmutils.io;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FilenameUtils;

import abmutils.processing.data.Table;
import au.com.bytecode.opencsv.CSVReader;

public class InputOutput {

	public InputOutput() {
	}
	public static ArrayList<ArrayList> readTable(String filename, Integer skipRows) throws FileNotFoundException,IOException,ParseException{
		return readTable(new File(filename), skipRows, false, new ArrayList<Integer>(), new ArrayList<Integer>(), 0);
	}
	public static ArrayList<ArrayList> readTable(File file, Integer skipRows) throws FileNotFoundException,IOException,ParseException{
		return readTable(file, skipRows, false, new ArrayList<Integer>(), new ArrayList<Integer>(), 0);
	}
	public static ArrayList<ArrayList> readTable(String filename, Integer skipRows, Boolean hasHeader) throws FileNotFoundException,IOException,ParseException{
		return readTable(new File(filename), skipRows, hasHeader, new ArrayList<Integer>(), new ArrayList<Integer>(), 0);
	}
	public static ArrayList<ArrayList> readTable(File file, Integer skipRows, Boolean hasHeader) throws FileNotFoundException,IOException,ParseException{
		return readTable(file, skipRows, hasHeader, new ArrayList<Integer>(), new ArrayList<Integer>(), 0);
	}
	public static ArrayList<ArrayList> readTable(String filename, Integer skipRows, Boolean hasHeader, ArrayList<Integer> doubleColumnInds, ArrayList<Integer> integerColumnInds) throws FileNotFoundException,IOException,ParseException{
		return readTable(new File(filename), skipRows, hasHeader, doubleColumnInds, integerColumnInds, 0);
	}
	public static ArrayList<ArrayList> readTable(File file, Integer skipRows, Boolean hasHeader, ArrayList<Integer> doubleColumnInds, ArrayList<Integer> integerColumnInds) throws FileNotFoundException,IOException,ParseException{
		return readTable(file, skipRows, hasHeader, doubleColumnInds, integerColumnInds, 0);
	}
	public static ArrayList<ArrayList> readTable(String filename, Integer skipRows, Boolean hasHeader, ArrayList<Integer> doubleColumnInds, ArrayList<Integer> integerColumnInds, Integer numColumnsExpected) throws FileNotFoundException,IOException,ParseException{
		return readTable(new File(filename), skipRows, hasHeader, doubleColumnInds, integerColumnInds, numColumnsExpected);
	}
	public static ArrayList<ArrayList> readTable(File file, Integer skipRows, Boolean hasHeader, ArrayList<Integer> doubleColumnInds, ArrayList<Integer> integerColumnInds, Integer numColumnsExpected) throws FileNotFoundException,IOException,ParseException{
		ArrayList<ArrayList> result = new ArrayList<ArrayList>();
		FileInputStream fstream = new FileInputStream(file);
		BufferedReader br;
		if(FilenameUtils.getExtension(file.getName()).toLowerCase().equals("zip")){
			ZipInputStream zin = new ZipInputStream(fstream);
			zin.getNextEntry();
			br = new BufferedReader(new InputStreamReader(zin));
		}else{
			br = new BufferedReader(new InputStreamReader(new DataInputStream(fstream)));
		}
		int lineCount = 0;
		String delim = null, strLine = null;
		String[] lineData;

		// First figure out what the delimiter is and initialize the result
		while(delim == null){
			strLine = br.readLine();
			if(lineCount++<skipRows)continue;
			Boolean hasTab = strLine.contains("\t");
			Boolean hasCom = strLine.contains(",");
			if(hasTab && hasCom){
				throw new IOException("Ambiguous file format in file "+file.getName()+", line "+lineCount+" contains both a tab and a comma character, expecting one or the other.");
			}else if(hasTab){
				delim = "\t";
			}else if(hasCom){
				delim = ",";
			}else{
				throw new IOException("Illegal file format in file "+file.getName()+", line "+lineCount+" does not contain a tab or a comma character, expecting one or the other.");
			}
			if(numColumnsExpected<1){
				numColumnsExpected = strLine.split(delim).length;
			}
			for(Integer i=0; i<numColumnsExpected; i++){
				if(doubleColumnInds.contains(i)){
					result.add(new ArrayList<Double>());
				}else if(integerColumnInds.contains(i)){
					result.add(new ArrayList<Integer>());
				}else{
					result.add(new ArrayList<String>());
				}	
			}
		}
		br.close();
		fstream.close();
		fstream = new FileInputStream(file);
		if(FilenameUtils.getExtension(file.getName()).toLowerCase().equals("zip")){
			ZipInputStream zin = new ZipInputStream(fstream);
			zin.getNextEntry();
			br = new BufferedReader(new InputStreamReader(zin));
		}else{
			br = new BufferedReader(new InputStreamReader(new DataInputStream(fstream)));
		}
		
		// Now use CSVReader to parse the file and load up the result arrays
		CSVReader reader = new CSVReader(br,delim.charAt(0),'"',skipRows.intValue());
		while ((lineData = reader.readNext()) != null) {
			if(lineData.length != numColumnsExpected){
				throw new IOException("Unexpected or missing data in file "+file.getName()+", line "+lineCount+" contains "+lineData.length+" values, expecting "+numColumnsExpected+".");
			}
			for(Integer i=0; i<numColumnsExpected; i++){
				if(doubleColumnInds.contains(i)){
					result.get(i).add(Double.parseDouble(lineData[i]));
				}else if(integerColumnInds.contains(i)){
					result.get(i).add(Integer.parseInt(lineData[i]));
				}else{
					result.get(i).add(lineData[i]);
				}	
			}
		}
		return result;
	}
	public static Table readTable(File columnFile, File dataFile) throws IOException{
		Table columnTable = new Table(new FileInputStream(columnFile),FilenameUtils.getExtension(columnFile.getName()).toLowerCase() + ",header");
		Table dataTable = columnTable.typedParse(new FileInputStream(dataFile), FilenameUtils.getExtension(dataFile.getName()).toLowerCase() + ",header");
		return dataTable;
	}
}
