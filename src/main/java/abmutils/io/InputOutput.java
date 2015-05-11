package abmutils.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;

import abmutils.processing.data.Table;

public class InputOutput {

	public InputOutput() {
	}
	public static Table readTable(File dataFile) throws IOException{
		return new Table(new FileInputStream(dataFile),FilenameUtils.getExtension(dataFile.getName()).toLowerCase() + ",header");
	}
	public static Table readTable(File columnFile, File dataFile) throws IOException{
		Table columnTable = new Table(new FileInputStream(columnFile),FilenameUtils.getExtension(columnFile.getName()).toLowerCase() + ",header");
		Table dataTable = columnTable.typedParse(new FileInputStream(dataFile), FilenameUtils.getExtension(dataFile.getName()).toLowerCase() + ",header");
		return dataTable;
	}
}
