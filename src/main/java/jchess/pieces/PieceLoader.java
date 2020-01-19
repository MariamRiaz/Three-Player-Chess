package jchess.pieces;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;

import com.google.gson.JsonParser;

import jchess.JChessApp;
import jchess.helper.Log;

/*
* A factory class to generate specific pre-defined Pieces.
* */
public class PieceLoader {
	private static final String piecesFolder = "pieces", masterFile = "pieces.ls";
	private static final HashMap<String, PieceDefinition> pieces = loadPieceDefinitions(JChessApp.class.getClassLoader().getResource(
			piecesFolder + "/" + masterFile));
	
	private static HashSet<String> getPieceFiles(URL master) {
		try {
			Scanner sc = new Scanner(master.openStream());
			final String content = sc.useDelimiter("\\Z").next();
			sc.close();
			
			return new HashSet<String>(Arrays.asList(content.split("\\|")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return new HashSet<String>();
	}
	
	private static HashMap<String, PieceDefinition> loadPieceDefinitions(URL path) {
		if (path == null) {
			Log.log(Level.SEVERE, "Fatal: Could not load master pieces file.");
			return null;
		}
		
		HashMap<String, PieceDefinition> retVal = new HashMap<String, PieceDefinition>();
		
		Set<String> pieceFiles = getPieceFiles(path);
		
		try {
			for (String fileName : pieceFiles) {
				URL file = JChessApp.class.getClassLoader()
						.getResource(piecesFolder + "/" + fileName);
				if (file == null)
					continue;
				
				PieceDefinition pd = PieceDefinition.loadFromJSON(
						new JsonParser().parse(new BufferedReader(new InputStreamReader(file.openStream()))));
				retVal.put(pd.type, pd);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return retVal;
	}
	
	public static PieceDefinition getPieceDefinition(String type) {
		return pieces.get(type);
	}
	
	public static Set<String> getPieceTypes() {
		return pieces.keySet();
	}
}
