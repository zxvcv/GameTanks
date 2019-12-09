package app.abstractObjects;

import app.data.send.Position;

public abstract class Block extends Shiftable implements Drawable, Sendable {
    static final int BLOCK_SIZE = 40;

    public Block(Position position) {
        super(position);
    }
}
