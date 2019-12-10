package jchess.controller;

import jchess.*;
import jchess.UI.board.Square;
import jchess.common.CartesianPolarConverter;
import jchess.common.PolarCell;
import jchess.common.PolarPoint;
import jchess.model.RoundChessboardModel;
import jchess.pieces.MoveHistory;
import jchess.pieces.Piece;
import jchess.pieces.PieceFactory;
import jchess.pieces.PlayedMove;
import jchess.view.ChessboardView;
import jchess.view.RoundChessboardView;

import java.awt.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RoundChessboardController {

    private RoundChessboardModel model;
    private RoundChessboardView view;
    private Square activeSquare;
    private Settings settings;

    // For undo:
    private Square undo1_sq_begin = null;
    private Square undo1_sq_end = null;
    private Piece undo1_piece_begin = null;
    private Piece undo1_piece_end = null;
    private Piece ifWasEnPassant = null;
    private Piece ifWasCastling = null;
    private boolean breakCastling = false;
    private int rows = 24;
    private int squaresPerRow = 6;
    // ----------------------------
    // For En passant:
    // |-> Pawn whose in last turn moved two square
    public Piece twoSquareMovedPawn = null;
    public Piece twoSquareMovedPawn2 = null;
    private MoveHistory moves_history;

    public static int bottom = 7;
    public static int top = 0;

    public RoundChessboardController(Settings settings) {
        this.model = new RoundChessboardModel(rows, squaresPerRow, settings);
        this.view = new RoundChessboardView(600,"3-player-board.png", rows, squaresPerRow, model.squares);
        this.settings = settings;
    }

    public RoundChessboardModel getModel() {
        return model;
    }

    public RoundChessboardView getView() {
        return view;
    }

    public void repaint() {
        this.view.repaint();
    }

//    public void resizeChessboard(int height) {
//        this.view.resizeChessboard(height);
//    }

//    public void resizeChessboard(){
//        this.view.resizeChessboard(view.get_height(settings.renderLabels));
//    }
//
    public boolean simulateMove(int beginX, int beginY, int endX, int endY) {

        try {
            select(model.getSquare(beginX, beginY));
            if (getValidTargetSquares(activeSquare.getPiece()).contains(model.getSquare(endX, endY))) // move
            {
                move(model.getSquare(beginX, beginY), model.getSquare(endX, endY), true, true);
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
        model.setActiveSquare(sq);
        view.setActiveCell(sq.getPozX(), sq.getPozY());
        Log.log("active_x: " + activeSquare + " active_y: " + activeSquare);// 4tests
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
                if (next.getPiece() == null || next.getPiece().player == piece.player)
                    add = false;
            } else if (move.conditions.contains(Piece.Move.MoveType.OnlyMove)) {
                if (next.getPiece() != null)
                    add = false;
            } else if (next.getPiece() != null && next.getPiece().player == piece.player)
                add = false;

            if (move.conditions.contains(Piece.Move.MoveType.OnlyWhenFresh) && piece.hasMoved())
                add = false;

            if (add)
                ret.add(next);

            if (!move.conditions.contains(Piece.Move.MoveType.Unblockable) && next.getPiece() != null)
                break;

            count++;
        }

        return ret;
    }

    private Square nextSquare(Square current, int x, int y) {
        return model.getSquare(current.getPozX() + x, current.getPozY() + y);
    }

    public Square getActiveSquare(){
        return model.getActiveSquare();
    }

    public void setActiveSquare(Square square){
        this.model.setActiveSquare(square);;
    }

    public Square getSquare(Piece piece) {
        return piece != null ? model.getSquare(piece): null;//TODO
    }

    public List<Square> getSquares(){
        return this.model.squares;
    }

    public Piece getKing(Player player) {
        if (player == null)
            return null;
        if (player.color == player.color.black)
            return model.kingBlack;
        return model.kingWhite;
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

        Piece tempBegin = begin.getPiece(), tempBeginState = tempBegin != null ? tempBegin.clone() : null;// 4 moves history
        Piece tempEnd = end.getPiece(), tempEndState = tempEnd != null ? tempEnd.clone() : null; // 4 moves history
        // for undo
        undo1_piece_begin = begin.getPiece();
        undo1_sq_begin = begin;
        undo1_piece_end = end.getPiece();
        undo1_sq_end = end;
        ifWasEnPassant = null;
        ifWasCastling = null;
        breakCastling = false;
        // ---

        twoSquareMovedPawn2 = twoSquareMovedPawn;

        model.setPieceOnSquare(begin.getPiece(), end);

        System.out.print(end.getPiece().type);
        if (end.getPiece().type.equals("King")) {

            if (!end.getPiece().hasMoved())
                breakCastling = true;

            end.getPiece().setHasMoved(true);// set square of piece to ending

            // Castling
            if (begin.getPozX() + 2 == end.getPozX()) {
                move(model.getSquare(7,begin.getPozY()), model.getSquare(end.getPozX() - 1,begin.getPozY()), false, false);
                ifWasCastling = end.getPiece(); // for undo
                wasCastling = MoveHistory.castling.shortCastling;
                // this.moves_history.addMove(tempBegin, tempEnd, clearForwardHistory,
                // wasCastling, wasEnPassant);
                // return;
            } else if (begin.getPozX() - 2 == end.getPozX()) {
                move(model.getSquare(0,begin.getPozY()), model.getSquare(end.getPozX() + 1,begin.getPozY()), false, false);
                ifWasCastling = end.getPiece(); // for undo
                wasCastling = MoveHistory.castling.longCastling;
                // this.moves_history.addMove(tempBegin, tempEnd, clearForwardHistory,
                // wasCastling, wasEnPassant);
                // return;
            }
            // endOf Castling
        } else if (end.getPiece().type.equals("Rook")) {
            if (!end.getPiece().hasMoved())
                breakCastling = true;
            end.getPiece().setHasMoved(true);// set square of piece to ending
        } else if (end.getPiece().type.equals("Pawn")) {
            if (twoSquareMovedPawn != null && model.getSquare(end.getPozX(),begin.getPozY()) == getSquare(twoSquareMovedPawn)) // en
            // passant
            {
                ifWasEnPassant = model.getSquare(end.getPozX(),begin.getPozY()).getPiece(); // for undo

                tempEnd = model.getSquare(end.getPozX(), begin.getPozY()).getPiece(); // ugly hack - put taken pawn in en passant plasty
                // do end square
                tempEndState = tempEnd.clone();

                model.getSquare(end.getPozX(), begin.getPozY()).setPiece(null);
                wasEnPassant = true;
            }

            if (begin.getPozY() - end.getPozY() == 2 || end.getPozY() - begin.getPozY() == 2) // moved two square
            {
                breakCastling = true;
                twoSquareMovedPawn = end.getPiece();
            } else {
                twoSquareMovedPawn = null; // erase last saved move (for En passant)
            }

            end.getPiece().setHasMoved(true);// set square of piece to ending

            if (end.getPozY() == 0 || end.getPozY() == 7) // promote Pawn
            {
                if (clearForwardHistory) {
                    String color;
                    if (end.getPiece().player.color == Player.colors.white) {
                        color = "W"; // promotionWindow was show with pieces in this color
                    } else {
                        color = "B";
                    }

                    String newPiece = JChessApp.jcv.showPawnPromotionBox(color); // return name of new piece

                    if (newPiece.equals("Queen")) // transform pawn to queen
                    {
                        Piece queen = PieceFactory.createQueen(end.getPiece().player);
                        model.setPieceOnSquare(queen, end);
                        view.setVisual(queen, end.getPozX(), end.getPozY());
                    } else if (newPiece.equals("Rook")) // transform pawn to rook
                    {
                        Piece rook = PieceFactory.createRook(end.getPiece().player);
                        model.setPieceOnSquare(rook, end);
                        view.setVisual(rook, end.getPozX(), end.getPozY());
                    } else if (newPiece.equals("Bishop")) // transform pawn to bishop
                    {
                        Piece bishop = PieceFactory.createBishop(end.getPiece().player);
                        model.setPieceOnSquare(bishop, end);
                        view.setVisual(bishop,  end.getPozX(), end.getPozY());
                    } else // transform pawn to knight
                    {
                        Piece knight = PieceFactory.createKing(end.getPiece().player);
                        model.setPieceOnSquare(knight, end);
                        view.setVisual(knight,  end.getPozX(), end.getPozY());
                    }
                    promotedPiece = end.getPiece();
                }
            }
        } else if (!end.getPiece().type.equals("Pawn")) {
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

        end.getPiece().setHasMoved(true);
    }

    /**
     * Method set variables active_x_square & active_y_square to 0 values.
     */
    public void unselect() {
        model.setActiveSquare(null);
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
                view.removeVisual(moved, end.getPozX(), end.getPozY());
                model.setPieceOnSquare(movedState, begin);
                view.setVisual(movedState, begin.getPozX(), begin.getPozY());

                Piece taken = last.getTakenPiece();
                if (last.getCastlingMove() != MoveHistory.castling.none) {
                    Piece rook = null;
                    if (last.getCastlingMove() == MoveHistory.castling.shortCastling) {
                        rook = model.getSquare(end.getPozX() - 1, end.getPozY()).getPiece();
                        model.setPieceOnSquare(rook, model.getSquare(7, begin.getPozY()));

                        view.setVisual(rook, 7, begin.getPozY());
                    } else {
                        rook = model.getSquare(end.getPozX() + 1, end.getPozY()).getPiece();
                        model.setPieceOnSquare(rook, model.getSquare(0, begin.getPozY()));
                        view.setVisual(rook, 0, begin.getPozY());
                    }
                    movedState.setHasMoved(false);
                    rook.setHasMoved(false);
                    this.breakCastling = false;
                } else if (movedState.type.equals("Rook")) {
                    movedState.setHasMoved(false);
                } else if (movedState.type.equals("Pawn") && last.wasEnPassant()) {
                    Piece pawn = last.getTakenPiece();
                    model.setPieceOnSquare(pawn, model.getSquare(end.getPozX(), begin.getPozY()));
                    view.setVisual(pawn, end.getPozX(), end.getPozY());

                } else if (movedState.type.equals("Pawn") && last.getPromotedPiece() != null) {
                    Piece promoted = model.getSquare(end.getPozX(), end.getPozY()).getPiece();
                    model.setPieceOnSquare(promoted, null);
                    view.removeVisual(promoted, end.getPozX(), end.getPozY());
                }

                // check one more move back for en passant
                PlayedMove oneMoveEarlier = this.moves_history.getLastMoveFromHistory();
                if (oneMoveEarlier != null && oneMoveEarlier.wasPawnTwoFieldsMove()) {
                    Piece canBeTakenEnPassant = model.getSquare(oneMoveEarlier.getTo().getPozX(),
                            oneMoveEarlier.getTo().getPozY()).getPiece();
                    if (canBeTakenEnPassant.type.equals("Pawn")) {
                        this.twoSquareMovedPawn = canBeTakenEnPassant;
                    }
                }

                if (taken != null && !last.wasEnPassant()) {
                    model.setPieceOnSquare(taken, null);
                    view.removeVisual(taken, last.getTo().getPozX(), last.getTo().getPozY());

                    Piece takenState = last.getTakenPieceState();
                    model.setPieceOnSquare(takenState, end);
                    view.setVisual(takenState, end.getPozX(), end.getPozY());
                } else {
                    view.removeVisual(end.getPiece(), end.getPozX(), end.getPozY());
                    model.setPieceOnSquare(end.getPiece(), null);
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
                    view.setVisual(promoted, to.getPozX(), to.getPozY());
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
        Point clickedPoint = new Point(x, y);
        CartesianPolarConverter converter = new CartesianPolarConverter();
        PolarPoint polarPoint = converter.getPolarFromCartesian(clickedPoint, view.getCircleCenter());
        for (PolarCell cell : view.getCells()) {
            double top = cell.getTopBound();
            double bottom = cell.getBottomBound();
            double left = cell.getLeftBound();
            double right = cell.getRightBound();
            if (polarPoint.getRadius() <= top && polarPoint.getRadius() > bottom) {
                if (polarPoint.getDegrees() >= left && polarPoint.getDegrees() < right) {
                    System.out.println("X: " + cell.getxIndex() + " Y: " + cell.getyIndex());
                    return getSquare(cell.getxIndex(), cell.getyIndex());
                }
            }
        }
        return null;
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
        for(Square square: model.squares) {
            if (square.getPiece().player != piece.player)
                continue;

            if (!getValidTargetSquaresToSavePiece(square.getPiece(), piece).isEmpty())
                return false;
        }
        return true;
    }

    public Square getSquare(int x, int y) {
        return model.getSquare(x, y);
    }

    public HashSet<Square> getValidTargetSquaresToSavePiece(Piece moving, Piece toSave) {
        HashSet<Square> ret = getValidTargetSquares(moving);
        if (ret.size() == 0 || toSave == null)
            return ret;

        for (Iterator<Square> it = ret.iterator(); it.hasNext(); ) {
            Square target = it.next(), start = getSquare(moving);
            Piece old = target.getPiece();

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

        for(Square square: model.squares) {
            if(square.getPiece() == null) {
                continue;
            }
            if(square.getPiece().player == piece.player) {
                continue;
            }

            HashSet<Square> validMoveSquares = getValidTargetSquares(piece);
            for (Iterator<Square> it2 = validMoveSquares.iterator(); it2.hasNext(); )
                if (it2.next() == getSquare(piece))
                    return true;
        }
        return false;   
    }

    public int getHeight(){
        return view.getHeight();
    }

    public void draw(){
        this.view.draw();
    }
}
