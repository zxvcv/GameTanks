package app.data.send;

import app.abstractObjects.Block;
import app.abstractObjects.Sendable;
import app.abstractObjects.Shiftable;

public class StoneBlock extends Block implements Sendable {
    public StoneBlock(Position position, int index) {
        super(position, index);
    }

}
