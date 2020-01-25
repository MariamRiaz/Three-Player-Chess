package jchess.helper;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import jchess.JChessApp;
import jchess.Settings;
import jchess.entities.Player;
import jchess.entities.Square;
import jchess.model.RoundChessboardModel;
import jchess.move.Orientation;
import jchess.pieces.Piece;
import jchess.pieces.PieceDefinition;
import jchess.pieces.PieceLoader;

public class RoundChessboardLoader {
	public static final String boardsFolder = "boards", defaultBoardFile = "circle_rim.json";
	private static final URL defaultBoardPath = JChessApp.class.getClassLoader().getResource(boardsFolder + "/" + defaultBoardFile);
	
	private RoundChessboardModel model = null;
	
	/**
	 * Loads the default RoundChessboardModel. See loadFromJSON()
	 * @param settings The Settings with the Players to use.
	 * @return The loaded model or null if loading failed.
	 */
	public RoundChessboardModel loadDefaultFromJSON(Settings settings) {
		return loadFromJSON(defaultBoardPath, settings);
	}
	
	/**
	 * Loads the RoundChessboardModel from the given JSON file with the given Settings to reference Players.
	 * @param settings The Settings with the Players to use.
	 * @return The loaded model or null if loading failed.
	 */
	public RoundChessboardModel loadFromJSON(URL boardPath, Settings settings) {
		model = null;
		
		try {
        	if (boardPath != null)
        		initializeFromJSON(new JsonParser().parse(new BufferedReader(new InputStreamReader(boardPath.openStream()))), settings);
		} catch (JsonIOException e) {
			e.printStackTrace();
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return model;
	}
	
    private Player parsePlayerCode(String code, Settings settings) {
    	switch (code) {
		case "WH":
			return settings.getPlayerWhite();
		case "BL":
			return settings.getPlayerBlack();
		case "GR":
			return settings.getPlayerGray();
		}
    	return null;
    }
    
    private void initializeFromJSON(JsonElement jsonBody, Settings settings) {
		if (jsonBody == null || !jsonBody.isJsonObject())
			return;
		
		JsonObject json = jsonBody.getAsJsonObject();
		int rows = 0, columns = 0;
		boolean hasContinuousRows = false, innerRimConnected = false;
		HashMap<String, Player> players = new HashMap<String, Player>();
		
		if (json.get("rows") != null && json.get("rows").isJsonPrimitive())
			rows = json.get("rows").getAsInt();
			
		if (json.get("columns") != null && json.get("columns").isJsonPrimitive())
			columns = json.get("columns").getAsInt();
			
		if (json.get("continuous-rows") != null && json.get("continuous-rows").isJsonPrimitive())
			hasContinuousRows = json.get("continuous-rows").getAsBoolean();
			
		if (json.get("connected-inner-rim") != null && json.get("connected-inner-rim").isJsonPrimitive())
			innerRimConnected = json.get("connected-inner-rim").getAsBoolean();
		
		model = new RoundChessboardModel(rows, columns, hasContinuousRows, innerRimConnected);
		
		if (json.get("players") != null && json.get("players").isJsonArray())
			for (JsonElement element : json.get("players").getAsJsonArray()) 
				if (element.isJsonPrimitive())
					players.put(element.getAsString(), parsePlayerCode(element.getAsString(), settings));
		
		if (json.get("pieces") != null && json.get("pieces").isJsonArray())
			loadPieces(json.get("pieces").getAsJsonArray(), players);
    }
    
    private void loadPieces(JsonArray pieces, HashMap<String, Player> players) {
    	for (JsonElement el : pieces) {
    		if (!el.isJsonObject())
    			continue;
    		
    		JsonObject piece = el.getAsJsonObject();
    		Square square = null;
    		PieceDefinition type = null;
    		Player player = null;
    		
    		if (piece.get("square") != null && piece.get("square").isJsonArray() && piece.get("square").getAsJsonArray().size() == 2) {
    			int x = piece.get("square").getAsJsonArray().get(0).getAsInt(),
    					y = piece.get("square").getAsJsonArray().get(1).getAsInt();
    			
    			square = model.getSquare(x, y);
    		}
    		
    		if (piece.get("type") != null && piece.get("type").isJsonPrimitive())
    			type = PieceLoader.getPieceDefinition(piece.get("type").getAsString());
    		
    		if (piece.get("player") != null && piece.get("player").isJsonPrimitive())
    			player = players.get(piece.get("player").getAsString());
    		
    		if (square != null && type != null && player != null) {
    			final Piece toAdd = new Piece(type, player, new Orientation());
    			model.setPieceOnSquare(toAdd, square);
    			
    			if (piece.get("crucial") != null && piece.get("crucial").isJsonPrimitive() && piece.get("crucial").getAsBoolean() == true)
    				model.addCrucialPiece(toAdd);
    		}
    	}
    }
}
