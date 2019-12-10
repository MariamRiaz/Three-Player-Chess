package jchess.model;

import jchess.Player;
import jchess.Settings;
import jchess.UI.board.Square;
import jchess.pieces.Piece;
import jchess.pieces.PieceFactory;

import java.util.ArrayList;
import java.util.List;

public class RoundChessboardModel {

    public List<Square> squares;
    public Piece kingWhite;
    public Piece kingBlack;
    private int squaresPerRow;
    private Square activeSquare;
    private int rows;



    public RoundChessboardModel(int rows, int squaresPerRow, Settings settings) {
        this.squares = new ArrayList<Square>();
        this.squaresPerRow = squaresPerRow;
        this.rows = rows;
        for(int i=0; i<rows; i++) {
            for(int j=0; j<squaresPerRow; j++) {
                squares.add(new Square(j, i, null));
            }
        }
        intializePieces(settings.playerWhite, settings.playerBlack);
    }

    public Square getSquare(int x, int y) { // duplicate method with GUI-related getSquare
        return squares.stream().filter(s -> s.getPozX() == x && s.getPozY() == y).findFirst().get();
    }

    public Square getSquare(Piece piece) {
        return squares.stream().filter(s -> s.getPiece() == piece).findFirst().get();
    }

    public void setActiveSquare(Square square) {
        activeSquare = square;
    }

    public Square getActiveSquare() {
        return this.activeSquare;
    }

    public Piece getPiece(int x, int y) {
        return getPiece(getSquare(x, y));
    }

    public Piece getPiece(Square square) {
        return square.getPiece();
    }



    public Piece setPieceOnSquare(Piece piece, Square square) {
        if (piece == null)
            return null;//TODO
        if (square == null)
            return null;//TODO
        square.setPiece(piece);
        return piece;
    }

    private void intializePiecesForPlayer(Player player, int row) {
        for(int i=0; i<squaresPerRow; i++) {
            getSquare(i, row).setPiece(PieceFactory.createPawn(player, true));
            getSquare(i, row + 2).setPiece(PieceFactory.createPawn(player, false));
        }
        Piece king = PieceFactory.createKing(player);
        getSquare(0, row+1).setPiece(PieceFactory.createRook(player));
        getSquare(1, row+1).setPiece(PieceFactory.createKnight(player));
        getSquare(2, row+1).setPiece(PieceFactory.createQueen(player));
        getSquare(3, row+1).setPiece(king);
        getSquare(4, row+1).setPiece(PieceFactory.createKnight(player));
        getSquare(5, row+1).setPiece(PieceFactory.createRook(player));

        if(player.color == Player.colors.white) {
            kingWhite = king;
        } else{
            kingBlack = king;
        }
    }

    public void intializePieces(Player plWhite, Player plBlack) {
        Player player1 = plBlack;
        Player player2 = plWhite;

        intializePiecesForPlayer(player1, 0);
        intializePiecesForPlayer(player2, 12);
    }




}
