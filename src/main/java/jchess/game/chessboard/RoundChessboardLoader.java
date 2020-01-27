package jchess.game.chessboard;

import com.google.gson.*;
import jchess.JChessApp;
import jchess.game.player.Player;
import jchess.game.IGameModel;
import jchess.game.chessboard.model.RoundChessboardModel;
import jchess.game.chessboard.model.Square;
import jchess.move.Orientation;
import jchess.pieces.Piece;
import jchess.pieces.PieceDefinition;
import jchess.pieces.PieceLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class RoundChessboardLoader {
    private static final String boardsFolder = "boards", defaultBoardFile = "circle_rim.json";
    private static final URL defaultBoardPath = JChessApp.class.getClassLoader().getResource(boardsFolder + "/" + defaultBoardFile);

    private RoundChessboardModel model = null;
    private ArrayList<Character> columnNames = new ArrayList<>();

    /**
     * Loads the default RoundChessboardModel. See loadFromJSON()
     *
     * @param gameModel The GameModel with the Players to use.
     * @return The loaded model or null if loading failed.
     */
    public RoundChessboardModel loadDefaultFromJSON(IGameModel gameModel) {
        return loadFromJSON(defaultBoardPath, gameModel);
    }

    /**
     * Loads the RoundChessboardModel from the given JSON file with the given GameModel to reference Players.
     *
     * @param gameModel The GameModel with the Players to use.
     * @return The loaded model or null if loading failed.
     */
    private RoundChessboardModel loadFromJSON(URL boardPath, IGameModel gameModel) {
        model = null;

        try {
            if (boardPath != null)
                initializeFromJSON(new JsonParser().parse(new BufferedReader(new InputStreamReader(boardPath.openStream()))), gameModel);
        } catch (JsonIOException | JsonSyntaxException | IOException e) {
            e.printStackTrace();
        }
        return model;
    }

    private Player parsePlayerCode(String code, IGameModel gameModel) {
        switch (code) {
            case "WH":
                return gameModel.getPlayerWhite();
            case "BL":
                return gameModel.getPlayerBlack();
            case "GR":
                return gameModel.getPlayerGray();
        }
        return null;
    }

    private void initializeFromJSON(JsonElement jsonBody, IGameModel gameModel) {
        if (jsonBody == null || !jsonBody.isJsonObject())
            return;

        JsonObject json = jsonBody.getAsJsonObject();
        int rows = 0;
        boolean hasContinuousRows = false, innerRimConnected = false;
        HashMap<String, Player> players = new HashMap<String, Player>();

        if (json.get("rows") != null && json.get("rows").isJsonPrimitive())
            rows = json.get("rows").getAsInt();

        if (json.get("columns") != null && json.get("columns").isJsonArray()) {
            json.get("columns").getAsJsonArray()
                    .forEach(jsonElement -> columnNames.add(jsonElement.getAsCharacter()));
        }
        if (json.get("continuous-rows") != null && json.get("continuous-rows").isJsonPrimitive())
            hasContinuousRows = json.get("continuous-rows").getAsBoolean();

        if (json.get("connected-inner-rim") != null && json.get("connected-inner-rim").isJsonPrimitive())
            innerRimConnected = json.get("connected-inner-rim").getAsBoolean();

        model = new RoundChessboardModel(rows, columnNames.size(), hasContinuousRows, innerRimConnected);

        if (json.get("players") != null && json.get("players").isJsonArray())
            for (JsonElement element : json.get("players").getAsJsonArray())
                if (element.isJsonPrimitive())
                    players.put(element.getAsString(), parsePlayerCode(element.getAsString(), gameModel));

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

    public ArrayList<Character> getColumnNames() {
        return columnNames;
    }
}
