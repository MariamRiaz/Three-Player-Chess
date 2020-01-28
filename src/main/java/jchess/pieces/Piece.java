package jchess.pieces;

import java.util.ArrayList;
import java.util.List;

import jchess.game.player.Player;
import jchess.move.Orientation;
import jchess.move.buff.Buff;

/**
 * Class to represent a Piece of any kind. Each Piece is defined by specific values for its member attributes.
 */
public class Piece {
    private static int idIncrement = 0;

    private PieceDefinition definition;
    private boolean hasMoved = false;
    private Orientation orientation;
    private List<Buff> buffs = new ArrayList<>();

    private final int id;
    private final Player player;

    /**
     * Creates a new Piece based on the given parameters.
     * Piece attributes cannot be changed after initialization.
     * @param definition The piece definition
     * @param player The player that owns the piece.
     * @param orientation The orientation of the piece
     */
    public Piece(PieceDefinition definition, Player player, Orientation orientation) {
        if (definition == null)
            throw new NullPointerException("Argument 'definition' is null.");
        this.definition = definition;

        if (player == null)
            throw new NullPointerException("Argument 'player' is null.");
        this.player = player;

        if (orientation == null)
            throw new NullPointerException("Argument 'orientation' is null.");
        this.orientation = orientation;

        id = idIncrement++;
    }

    /**
     * Creates a new Piece with the same attributes as those of the Piece other.
     *
     * @param other Piece whose attributes to copy. Must be non-null.
     */
    public Piece(Piece other) {
        if (other == null)
            throw new NullPointerException("Argument 'other' is null.");

        this.player = other.player;
        this.definition = other.definition;
        this.hasMoved = other.hasMoved;
        this.orientation = other.orientation;
        this.id = other.id;

        for (Buff buff : other.buffs)
            this.addBuff(buff);
    }

    /**
     * Returns a deep copy of this Piece. ID of the copy will be the same.
     */
    public Piece clone() {
        return new Piece(this);
    }

    /**
     * @return The unique ID of this Piece. Clones of this Piece will share ID.
     */
    public int getID() {
        return id;
    }

    /**
     * @return The Player owning this Piece.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * @param val Whether the Piece has moved since its creation or not.
     * @return This Piece.
     */
    public Piece setHasMoved(boolean val) {
        hasMoved = val;
        return this;
    }

    /**
     * @return Whether this Piece has moved since its creation or not.
     */
    public boolean hasMoved() {
        return hasMoved;
    }

    /**
     * @return This Piece's orientation on the board.
     */
    public Orientation getOrientation() {
        return orientation;
    }

    /**
     * Changes this Piece's Orientation to the given non-null Orientation.
     *
     * @param orientation The new Orientation.
     * @return This Piece.
     */
    public Piece reorient(Orientation orientation) {
        if (orientation != null)
            this.orientation = orientation;
        return this;
    }

    /**
     * @return The PieceDefinition of this Piece.
     */
    public PieceDefinition getDefinition() {
        return definition;
    }

    /**
     * @param def This Piece's new PieceDefinition. Cannot be null.
     * @return This Piece.
     */
    public Piece setDefinition(PieceDefinition def) {
        if (def == null)
            throw new NullPointerException("Argument 'definition' is null.");
        this.definition = def;
        return this;
    }

    /**
     * Adds a Buff to this Piece.
     *
     * @param buff The Buff to add.
     * @return This Piece.
     */
    public Piece addBuff(Buff buff) {
        if (buff != null)
            buffs.add(buff.clone());
        return this;
    }

    /**
     * Ticks the Buffs on this Piece and removes expiring ones.
     */
    public void tickBuffs() {
        buffs.forEach(Buff::tick);
        buffs.removeIf(Buff::isWornOff);
    }

    /**
     * @return The active Buffs on this Piece.
     */
    public List<Buff> getActiveBuffs() {
        ArrayList<Buff> retVal = new ArrayList<>();
        retVal.addAll(this.buffs);

        return retVal;
    }
}
