package app.data.send;

import app.abstractObjects.Block;
import app.abstractObjects.Sendable;

public class GrassBlock extends Block implements Sendable {
    public GrassBlock(Position position, int index) {
        super(position, index);
    }

}
