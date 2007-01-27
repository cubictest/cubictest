package org.cubictest.persistence;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.model.parameterization.Parameter;
import org.cubictest.model.parameterization.ParameterList;
import org.eclipse.core.resources.IFile;


public class ParameterPersistance {

	public static void toFile(ParameterList paramList, IFile iFile) {
		try {
			FileWriter fw = new FileWriter(iFile.getLocation().toFile());
			BufferedWriter bw = new BufferedWriter(fw);
			for(Parameter param: paramList.getParameters()){
				bw.write(param.getHeader() + " ;");
			}
			bw.newLine();
			for (int i = 0; i < paramList.inputParameterSize(); i++){
				for(Parameter param: paramList.getParameters())
					bw.write(param.getParameterInput(i) + " ;");
				bw.newLine();
			}
			bw.close();
		} catch (IOException ioe) {
			ErrorHandler.logAndShowErrorDialogAndRethrow(ioe);
		}
	}

	public static ParameterList fromFile(IFile iFile) {
		ParameterList list = new ParameterList();
		list.setFileName(iFile.getFullPath().toString());
		FileReader fr;
		try {
			fr = new FileReader(iFile.getLocation().toFile());
		
			BufferedReader bw = new BufferedReader(fr);
			String line = bw.readLine();
			if (line == null)
				return list;
			
			for(String token : line.split(";")){
				Parameter param = new Parameter();
				param.setHeader(token.trim());
				list.addParameter(param);
			}
			line = bw.readLine();
			while (line != null){
				int i = 0;
				for(String token : line.split(";")){
					list.getParameters()
							.get(i++)
									.addParameterInput(token.trim());
				}
				line = bw.readLine();
			}
			bw.close();
		} catch (FileNotFoundException e) {
			ErrorHandler.logAndShowErrorDialogAndRethrow(e);
		} catch (IOException e) {
			ErrorHandler.logAndShowErrorDialogAndRethrow(e);
		}
		return list;
	}

}
