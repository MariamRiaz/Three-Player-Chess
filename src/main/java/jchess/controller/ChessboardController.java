package jchess.controller;

import jchess.*;
import jchess.UI.board.Square;
import jchess.model.ChessboardModel;
import jchess.pieces.*;
import jchess.view.ChessboardView;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChessboardController {

    public ChessboardModel model;
    public ChessboardView view;
    public Settings settings;

    // For undo:
    private Square undo1_sq_begin = null;
    private Square undo1_sq_end = null;
    private Piece undo1_piece_begin = null;
    private Piece undo1_piece_end = null;
    private Piece ifWasEnPassant = null;
    private Piece ifWasCastling = null;
    private boolean breakCastling = false;
    // ----------------------------
    // For En passant:
    // |-> Pawn whose in last turn moved two square
    public Piece twoSquareMovedPawn = null;
    public Piece twoSquareMovedPawn2 = null;
    private MoveHistory moves_history;

    public ChessboardController(ChessboardModel model, ChessboardView view, Settings settings) {
        this.model = model;
        this.view = view;
        this.settings = settings;
    }

    public void initView() {
        this.view.initView("chessboard.png", settings);
    }

    public void repaint() {
        this.view.repaint();
    }

    public void setPieces4NewGame() {
        this.model.setPieces4NewGame(settings.upsideDown, settings.playerWhite, settings.playerBlack);
    }

    public void resizeChessboard() {
        this.view.resizeChessboard();
    }

    public boolean simulateMove(int beginX, int beginY, int endX, int endY) {

        try {
            select(model.squares[beginX][beginY]);
            if (getValidTargetSquares(model.activeSquare.getPiece()).contains(model.squares[endX][endY])) // move
            {
                move(model.squares[beginX][beginY], model.squares[endX][endY], true, true);
            } else {
                Log.log("Bad move");
                return false;
            }
            unselect();
            return true;

        } catch (StringIndexOutOfBoundsException exc) {
            return false;
        } catch (ArrayIndexOutOfBoundsException exc) {
            return false;
        } catch (NullPointerException exc) {
            return false;
        } finally {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, "ERROR");
        }


    }

    public void select(Square sq) {
        model.activeSquare = sq;
        model.active_x_square = sq.pozX + 1;
        model.active_y_square = sq.pozY + 1;

        Log.log("active_x: " + model.active_x_square + " active_y: " + model.active_y_square);// 4tests
        repaint();

    }

    public HashSet<Square> getValidTargetSquares(Piece piece) {
        HashSet<Square> ret = new HashSet<Square>();

        if (piece == null)
            return ret;

        HashSet<Piece.Move> moves = piece.getMoves();
        for (Iterator<Piece.Move> it = moves.iterator(); it.hasNext(); )
            ret.addAll(evaluateMoveToTargetSquares(it.next(), piece));

        return ret;
    }

    private HashSet<Square> evaluateMoveToTargetSquares(Piece.Move move, Piece piece) {
        HashSet<Square> ret = new HashSet<Square>();

        if (move == null || piece == null)
            return ret;

        int count = 0;
        for (Square next = nextSquare(getSquare(piece), move.x, move.y); next != null
                && (move.limit == null || count < move.limit); next = nextSquare(next, move.x, move.y)) {
            boolean add = true;

            if (move.conditions.contains(Piece.Move.MoveType.OnlyAttack)) {
                if (next.piece == null || next.piece.player == piece.player)
                    add = false;
            } else if (move.conditions.contains(Piece.Move.MoveType.OnlyMove)) {
                if (next.piece != null)
                    add = false;
            } else if (next.piece != null && next.piece.player == piece.player)
                add = false;

            if (move.conditions.contains(Piece.Move.MoveType.OnlyWhenFresh) && piece.hasMoved())
                add = false;

            if (add)
                ret.add(next);

            if (!move.conditions.contains(Piece.Move.MoveType.Unblockable) && next.piece != null)
                break;

            count++;
        }

        return ret;
    }

    private Square nextSquare(Square current, int x, int y) {
        return getSquare(current.pozX + x, current.pozY + y);
    }

    public Square getActiveSquare(){
        return model.activeSquare;
    }

    public Square getSquare(Piece piece) {
        return piece != null && model.pieceToSquare.containsKey(piece) ? model.pieceToSquare.get(piece) : null;
    }

    public Square getSquare(int x, int y) { // duplicate method with GUI-related getSquare
        return x < 0 || y < 0 || x >= model.squares.length || y >= model.squares[x].length ? null : model.squares[x][y];
    }

    /**
     * Method move piece from square to square
     *
     * @param begin   square from which move piece
     * @param end     square where we want to move piece *
     * @param refresh chessboard, default: true
     */
    public void move(Square begin, Square end, boolean refresh, boolean clearForwardHistory) {

        MoveHistory.castling wasCastling = MoveHistory.castling.none;
        Piece promotedPiece = null;
        boolean wasEnPassant = false;
        /*
         * if (end.piece != null) { end.piece.setSquare(null); }
         */

        Piece tempBegin = begin.piece, tempBeginState = tempBegin != null ? tempBegin.clone() : null;// 4 moves history
        Piece tempEnd = end.piece, tempEndState = tempEnd != null ? tempEnd.clone() : null; // 4 moves history
        // for undo
        undo1_piece_begin = begin.piece;
        undo1_sq_begin = begin;
        undo1_piece_end = end.piece;
        undo1_sq_end = end;
        ifWasEnPassant = null;
        ifWasCastling = null;
        breakCastling = false;
        // ---

        twoSquareMovedPawn2 = twoSquareMovedPawn;

        model.setPieceOnSquare(begin.piece, end);

        System.out.print(end.piece.type);
        if (end.piece.type.equals("King")) {

            if (!end.piece.hasMoved())
                breakCastling = true;

            end.piece.setHasMoved(true);// set square of piece to ending

            // Castling
            if (begin.pozX + 2 == end.pozX) {
                move(model.squares[7][begin.pozY], model.squares[end.pozX - 1][begin.pozY], false, false);
                ifWasCastling = end.piece; // for undo
                wasCastling = MoveHistory.castling.shortCastling;
                // this.moves_history.addMove(tempBegin, tempEnd, clearForwardHistory,
                // wasCastling, wasEnPassant);
                // return;
            } else if (begin.pozX - 2 == end.pozX) {
                move(model.squares[0][begin.pozY], model.squares[end.pozX + 1][begin.pozY], false, false);
                ifWasCastling = end.piece; // for undo
                wasCastling = MoveHistory.castling.longCastling;
                // this.moves_history.addMove(tempBegin, tempEnd, clearForwardHistory,
                // wasCastling, wasEnPassant);
                // return;
            }
            // endOf Castling
        } else if (end.piece.type.equals("Rook")) {
            if (!end.piece.hasMoved())
                breakCastling = true;
            end.piece.setHasMoved(true);// set square of piece to ending
        } else if (end.piece.type.equals("Pawn")) {
            if (twoSquareMovedPawn != null && model.squares[end.pozX][begin.pozY] == getSquare(twoSquareMovedPawn)) // en
            // passant
            {
                ifWasEnPassant = model.squares[end.pozX][begin.pozY].piece; // for undo

                tempEnd = model.squares[end.pozX][begin.pozY].piece; // ugly hack - put taken pawn in en passant plasty
                // do end square
                tempEndState = tempEnd.clone();

                model.squares[end.pozX][begin.pozY].piece = null;
                wasEnPassant = true;
            }

            if (begin.pozY - end.pozY == 2 || end.pozY - begin.pozY == 2) // moved two square
            {
                breakCastling = true;
                twoSquareMovedPawn = end.piece;
            } else {
                twoSquareMovedPawn = null; // erase last saved move (for En passant)
            }

            end.piece.setHasMoved(true);// set square of piece to ending

            if (end.pozY == 0 || end.pozY == 7) // promote Pawn
            {
                if (clearForwardHistory) {
                    String color;
                    if (end.piece.player.color == Player.colors.white) {
                        color = "W"; // promotionWindow was show with pieces in this color
                    } else {
                        color = "B";
                    }

                    String newPiece = JChessApp.jcv.showPawnPromotionBox(color); // return name of new piece

                    if (newPiece.equals("Queen")) // transform pawn to queen
                    {
                        Piece queen = PieceFactory.createQueen(end.piece.player);
                        model.setPieceOnSquare(queen, end);
                        view.setVisual(queen);
                    } else if (newPiece.equals("Rook")) // transform pawn to rook
                    {
                        Piece rook = PieceFactory.createRook(end.piece.player);
                        model.setPieceOnSquare(rook, end);
                        view.setVisual(rook);
                    } else if (newPiece.equals("Bishop")) // transform pawn to bishop
                    {
                        Piece bishop = PieceFactory.createBishop(end.piece.player);
                        model.setPieceOnSquare(bishop, end);
                        view.setVisual(bishop);
                    } else // transform pawn to knight
                    {
                        Piece knight = PieceFactory.createKing(end.piece.player);
                        model.setPieceOnSquare(knight, end);
                        view.setVisual(knight);
                    }
                    promotedPiece = end.piece;
                }
            }
        } else if (!end.piece.type.equals("Pawn")) {
            twoSquareMovedPawn = null; // erase last saved move (for En passant)
        }
        // }

        if (refresh) {
            this.unselect();// unselect square
            repaint();
        }

        if (clearForwardHistory) {
            this.moves_history.clearMoveForwardStack();
            this.moves_history.addMove(begin, end, tempBegin, tempBeginState, tempEnd, tempEndState, true, wasCastling, wasEnPassant, promotedPiece);
        } else {
            this.moves_history.addMove(begin, end, tempBegin, tempBeginState, tempEnd, tempEndState, false, wasCastling, wasEnPassant, promotedPiece);
        }

        end.piece.setHasMoved(true);
    }

    /**
     * Method set variables active_x_square & active_y_square to 0 values.
     */
    public void unselect() {
        model.active_x_square = 0;
        model.active_y_square = 0;
        model.activeSquare = null;
        repaint();
    }

    public synchronized boolean undo(boolean refresh) // undo last move
    {
        PlayedMove last = this.moves_history.undo();

        System.out.print(last != null);
        if (last != null && last.getFrom() != null) {
            System.out.print("1");
            Square begin = last.getFrom();
            Square end = last.getTo();
            try {
                Piece moved = last.getMovedPiece(), movedState = last.getMovedPieceState();
                model.setPieceOnSquare(moved, null);
                view.removeVisual(moved);
                model.setPieceOnSquare(movedState, begin);
                view.setVisual(movedState);

                Piece taken = last.getTakenPiece();
                if (last.getCastlingMove() != MoveHistory.castling.none) {
                    Piece rook = null;
                    if (last.getCastlingMove() == MoveHistory.castling.shortCastling) {
                        rook = getSquare(end.pozX - 1, end.pozY).piece;
                        model.setPieceOnSquare(rook, getSquare(7, begin.pozY));

                        view.setVisual(rook);
                    } else {
                        rook = getSquare(end.pozX + 1, end.pozY).piece;
                        model.setPieceOnSquare(rook, getSquare(0, begin.pozY));
                        view.setVisual(rook);
                    }
                    movedState.setHasMoved(false);
                    rook.setHasMoved(false);
                    this.breakCastling = false;
                } else if (movedState.type.equals("Rook")) {
                    movedState.setHasMoved(false);
                } else if (movedState.type.equals("Pawn") && last.wasEnPassant()) {
                    Piece pawn = last.getTakenPiece();
                    model.setPieceOnSquare(pawn, getSquare(end.pozX, begin.pozY));
                    view.setVisual(pawn);

                } else if (movedState.type.equals("Pawn") && last.getPromotedPiece() != null) {
                    Piece promoted = getSquare(end.pozX, end.pozY).piece;
                    model.setPieceOnSquare(promoted, null);
                    view.removeVisual(promoted);
                }

                // check one more move back for en passant
                PlayedMove oneMoveEarlier = this.moves_history.getLastMoveFromHistory();
                if (oneMoveEarlier != null && oneMoveEarlier.wasPawnTwoFieldsMove()) {
                    Piece canBeTakenEnPassant = getSquare(oneMoveEarlier.getTo().pozX,
                            oneMoveEarlier.getTo().pozY).piece;
                    if (canBeTakenEnPassant.type.equals("Pawn")) {
                        this.twoSquareMovedPawn = canBeTakenEnPassant;
                    }
                }

                if (taken != null && !last.wasEnPassant()) {
                    model.setPieceOnSquare(taken, null);
                    view.removeVisual(taken);

                    Piece takenState = last.getTakenPieceState();
                    model.setPieceOnSquare(takenState, end);
                    view.setVisual(takenState);
                } else {
                    view.removeVisual(end.piece);
                    model.setPieceOnSquare(end.piece, null);
                }

                if (refresh) {
                    this.unselect();// unselect square
                    repaint();
                }

            } catch (java.lang.ArrayIndexOutOfBoundsException exc) {
                System.out.print("2");
                return false;
            } catch (java.lang.NullPointerException exc) {
                System.out.print("3");
                return false;
            }

            return true;
        } else {
            System.out.print("4");
            return false;
        }
    }

    public boolean redo(boolean refresh) {
        if (this.settings.gameType == Settings.gameTypes.local) // redo only for local game
        {
            PlayedMove first = this.moves_history.redo();

            Square from = null;
            Square to = null;

            if (first != null) {
                from = first.getFrom();
                to = first.getTo();

                this.move(from, to, true, false);
                if (first.getPromotedPiece() != null) {
                    Piece promoted = model.setPieceOnSquare(first.getPromotedPiece(), to);
                    view.setVisual(promoted);
                }
                return true;
            }

        }
        return false;
    }

    /**
     * method to get reference to square from given x and y integeres
     *
     * @param x x position on chessboard
     * @param y y position on chessboard
     * @return reference to searched square
     */
    public Square getSquareFromClick(int x, int y) {
        if ((x > view.get_height(settings.renderLabels)) || (y > view.getWidth())) // test if click is out of chessboard
        {
            Log.log("click out of chessboard.");
            return null;
        }
        if (this.settings.renderLabels) {
            x -= view.upDownLabel.getHeight(null);
            y -= view.upDownLabel.getHeight(null);
        }
        double square_x = x / view.square_height;// count which field in X was clicked
        double square_y = y / view.square_height;// count which field in Y was clicked

        if (square_x > (int) square_x) // if X is more than X parsed to Integer
        {
            square_x = (int) square_x + 1;// parse to integer and increment
        }
        if (square_y > (int) square_y) // if X is more than X parsed to Integer
        {
            square_y = (int) square_y + 1;// parse to integer and increment
        }
        // Square newActiveSquare =
        // getSquare((int)square_x-1][(int)square_y-1];//4test
        Log.log("square_x: " + square_x + " square_y: " + square_y + " \n"); // 4tests
        Square result;
        try {
            result = getSquare((int) square_x - 1, (int) square_y - 1);
        } catch (java.lang.ArrayIndexOutOfBoundsException exc) {
            Log.log(Level.SEVERE,
                    "!!Array out of bounds when getting Square with Chessboard.getSquare(int,int) : " + exc);
            return null;
        }
        return getSquare((int) square_x - 1, (int) square_y - 1);
    }

    public Piece getKingWhite(){
        return model.kingWhite;
    }

    public Piece getKingBlack(){
        return model.kingBlack;
    }

    public boolean pieceUnsavable(Piece piece) {
        if (piece == null)
            return false;

        for (int x = 0; x < model.squares.length; x++)
            for (int y = 0; y < this.model.squares[x].length; y++) {
                Square sq = getSquare(x, y);

                if (sq.piece.player != piece.player)
                    continue;

                if (!getValidTargetSquaresToSavePiece(sq.piece, piece).isEmpty())
                    return false;
            }
        return true;
    }

    public HashSet<Square> getValidTargetSquaresToSavePiece(Piece moving, Piece toSave) {
        HashSet<Square> ret = getValidTargetSquares(moving);
        if (ret.size() == 0 || toSave == null)
            return ret;

        for (Iterator<Square> it = ret.iterator(); it.hasNext(); ) {
            Square target = it.next(), start = getSquare(moving);
            Piece old = target.piece;

            model.setPieceOnSquare(moving, target);
            if (pieceThreatened(toSave))
                it.remove();

            model.setPieceOnSquare(old, target);
            model.setPieceOnSquare(moving, start);
        }

        return ret;
    }

    public boolean pieceThreatened(Piece piece) {
        if (piece == null)
            return false;

        for (Iterator<Map.Entry<Piece, Square>> it = model.pieceToSquare.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Piece, Square> ent = it.next();

            if (ent.getKey().player == piece.player)
                continue;

            HashSet<Square> validMoveSquares = getValidTargetSquares(ent.getKey());
            for (Iterator<Square> it2 = validMoveSquares.iterator(); it2.hasNext(); )
                if (it2.next() == getSquare(piece))
                    return true;
        }

        return false;
    }
}
