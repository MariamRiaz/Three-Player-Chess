package jchess.pieces;

public class Orientation {
	public boolean x, y;

	public Orientation() {
		this.x = false;
		this.y = false;
	}
	
	public Orientation(boolean x, boolean y) {
		this.x = x;
		this.y = y;
	}
	
	public Orientation reverse() {
		x = !x;
		y = !y;
		return this;
	}
	
	public Orientation reverseX() {
		x = !x;
		return this;
	}
	
	public Orientation reverseY() {
		y = !y;
		return this;
	}
}
