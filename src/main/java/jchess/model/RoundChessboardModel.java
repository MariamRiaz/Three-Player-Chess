package jchess.model;

import jchess.helper.Log;
import jchess.entities.Player;
import jchess.Settings;
import jchess.entities.Square;
import jchess.pieces.Orientation;
import jchess.pieces.Piece;
import jchess.pieces.PieceDefinition;
import jchess.pieces.PieceLoader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

/**
 * Class that holds the state of the RoundChessboard component
 */
public class RoundChessboardModel {
	private static final String boardPath = System.getProperty("user.dir") + File.separator + "boards" + File.separator + "circle_rim.bd";
	
    public List<Square> squares;
    public Piece kingWhite;
    public Piece kingBlack;
    public Piece kingGray;
    private int squaresPerRow;
    private int rows;
    private boolean hasContinuousRows, innerRimConnected;

    /**
     * Constructor
     * @param rows              int     row count of the chessboard
     * @param squaresPerRow     int     count of squares per row of the chessboard
     * @param continuousRows    boolean
     * @param settings          Settings    settings of the application
     */
    public RoundChessboardModel(int rows, int squaresPerRow, boolean continuousRows, boolean connectedInnerRim, Settings settings) {
        this.squares = new ArrayList<Square>();
        
        try {
			initializeFromJSON(new JsonParser().parse(new BufferedReader(new FileReader(boardPath))), settings);
		} catch (JsonIOException e) {
			e.printStackTrace();
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
    }

    private void populateSquares() {
    	squares.clear();
    	
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < squaresPerRow; j++) {
                squares.add(new Square(j, i, null));
            }
        }
    }
    
    private int normalizeY(int y) {
    	return y % rows < 0 ? (y % rows) + rows : y % rows;
    }

    /**
     * gets the Square corresponding to the given x and y index
     * @param x     int x index of the desired square
     * @param y     int y index of the desired square
     * @return      Square corresponding to the given x and y index
     */
    public Square getSquare(int x, int y) {
    	if (hasContinuousRows) {
	    	y = normalizeY(y);
	    	
	    	if (innerRimConnected) {
	    		if (x < 0) {
	    			x = -x - 1;
	    			y = normalizeY(y + rows / 2);
	    		}
	    	}
    	}
    	
    	final int newX = x, newY = y;
        Optional<Square> optionalSquare = squares.stream().filter(s ->
        	s.getPozX() == newX && s.getPozY() == newY).findFirst();
        if(optionalSquare.equals(Optional.empty()))
            return null;
            
        return optionalSquare.get();//TODO
    }

    /**
     * @return Whether or not the board has continuous rows, i.e. is circular.
     */
    public boolean getHasContinuousRows() {
    	return hasContinuousRows;
    }
    
    /**
     * @return Whether or not the board inner rim is connected, if circular. I.e. whether jumps across the middle are possible.
     */
    public boolean getInnerRimConnected() {
    	return hasContinuousRows && innerRimConnected;
    }

    /**
     * gets the Square where the given Piece is located on
     * @param piece     Piece to get the Square from
     * @return          Square where the given Piece is located on
     */
    public Square getSquare(Piece piece) {
        Optional<Square> optionalSquare = squares.stream().filter(s -> s.getPiece() == piece).findFirst();
        if(optionalSquare.equals(Optional.empty()))
            return null;
        
        return optionalSquare.get();//TODO
    }

    /**
     * sets the given Piece on the given Square
     * @param piece     Piece   piece to put on the Square
     * @param square    Square  square where to put the piece on
     * @return          Piece   after setting it on the Square
     */
    public Piece setPieceOnSquare(Piece piece, Square square) {
        if (square == null) {
            Log.log(Level.WARNING, "Piece was not set on square because square is null");
            return null;
        }
        
        Square prev = getSquare(piece);
        if (prev != null)
        	prev.setPiece(null);
        
        square.setPiece(piece);
        return piece;
    }
    
    private void initializeFromJSON(JsonElement jsonBody, Settings settings) {
		if (jsonBody == null || !jsonBody.isJsonObject())
			return;
		
		JsonObject json = jsonBody.getAsJsonObject();
		HashMap<String, Player> players = new HashMap<String, Player>();
		
		if (json.get("rows") != null && json.get("rows").isJsonPrimitive())
			rows = json.get("rows").getAsInt();
			
		if (json.get("columns") != null && json.get("columns").isJsonPrimitive())
			squaresPerRow = json.get("columns").getAsInt();
			
		if (json.get("continuous-rows") != null && json.get("continuous-rows").isJsonPrimitive())
			hasContinuousRows = json.get("continuous-rows").getAsBoolean();
			
		if (json.get("connected-inner-rim") != null && json.get("connected-inner-rim").isJsonPrimitive())
			innerRimConnected = json.get("connected-inner-rim").getAsBoolean();
		
		populateSquares();

		if (json.get("players") != null && json.get("players").isJsonArray())
			for (JsonElement element : json.get("players").getAsJsonArray()) 
				if (element.isJsonPrimitive())
					switch (element.getAsString()) {
					case "WH":
						players.put("WH", settings.getPlayerWhite());
						break;
					case "BL":
						players.put("BL", settings.getPlayerBlack());
						break;
					case "GR":
						players.put("GR", settings.getPlayerGray());
						break;
					}
		
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
    			
    			square = getSquare(x, y);
    		}
    		
    		if (piece.get("type") != null && piece.get("type").isJsonPrimitive())
    			type = PieceLoader.getPieceDefinition(piece.get("type").getAsString());
    		
    		if (piece.get("player") != null && piece.get("player").isJsonPrimitive())
    			player = players.get(piece.get("player").getAsString());
    		
    		if (square != null && type != null && player != null)
    			setPieceOnSquare(new Piece(type, player, new Orientation()), square);
    	}
    }
}
