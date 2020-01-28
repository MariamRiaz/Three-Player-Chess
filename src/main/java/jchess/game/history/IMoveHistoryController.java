package jchess.game.history;

import jchess.move.effects.BoardTransition;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public interface IMoveHistoryController {

    void addMove(BoardTransition moveEffects);

    void clearMoveForwardStack();

    JScrollPane getScrollPane();

    List<String> getMoves();

    Queue<BoardTransition> undo();

    BoardTransition undoOne();

    Queue<BoardTransition> redo();

    BoardTransition redoOne();

    void switchColumns(boolean forward);
}
