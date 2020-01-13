package jchess.pieces;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import com.google.gson.JsonParser;

/*
* A factory class to generate specific pre-defined Pieces.
* */
public class PieceLoader {
	public static PieceDefinition getPieceDefinition(String type) {
		return pieces.get(type);
	}
	
	public static Set<String> getPieceTypes() {
		return pieces.keySet();
	}
	
	private static final HashMap<String, PieceDefinition> pieces = loadPieceDefinitions(System.getProperty("user.dir") + File.separator + "pieces");
	
	private static HashMap<String, PieceDefinition> loadPieceDefinitions(String path) {
		System.out.print(path);
		
		HashMap<String, PieceDefinition> retVal = new HashMap<String, PieceDefinition>();
		File folder = new File(path);
		
		if (!folder.exists())
			return retVal;

		try {
			for (File file : folder.listFiles()) { 
				PieceDefinition pd = PieceDefinition.loadFromJSON(
						new JsonParser().parse(new BufferedReader(new FileReader(file.getAbsolutePath()))));
				retVal.put(pd.type, pd);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return retVal;
	}
}
