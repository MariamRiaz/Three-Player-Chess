package jchess.controller;

import jchess.move.effects.MoveEffect;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public interface IMoveHistoryController {

    void addMove(MoveEffect moveEffects, boolean registerInHistory, boolean registerInTable);

    void clearMoveForwardStack();

    JScrollPane getScrollPane();

    List<String> getMoves();

    Queue<MoveEffect> undo();

    MoveEffect undoOne();

    Queue<MoveEffect> redo();

    MoveEffect redoOne();

    void switchColumns(boolean forward);
}
