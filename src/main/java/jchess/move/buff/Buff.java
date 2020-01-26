package jchess.move.buff;

public class Buff {
    private final BuffType type;
    private int remainingTicks;

    /**
     * Creates a Buff of the given BuffType with the given amount of remaining ticks.
     *
     * @param type           The BuffType, not null.
     * @param remainingTicks The remaining ticks in turns, at least 1. Buffs tick at the end of the affected Piece's Player's turn.
     */
    public Buff(BuffType type, int remainingTicks) {
        if (type == null)
            throw new NullPointerException("'type' of Buff cannot be null.");
        this.type = type;

        if (remainingTicks < 1)
            throw new IllegalArgumentException("'remainingTicks' of Buff cannot be <1 at init.");
        this.remainingTicks = remainingTicks;
    }

    private Buff(Buff other) {
        this.type = other.type;
        this.remainingTicks = other.remainingTicks;
    }

    @Override
    public Buff clone() {
        return new Buff(this);
    }

    public BuffType getType() {
        return type;
    }

    public int getTicks() {
        return remainingTicks;
    }

    public boolean isWornOff() {
		return remainingTicks < 1;
	}

    public void tick() {
        remainingTicks -= 1;
    }
}
