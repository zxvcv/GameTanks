package app.abstractObjects;

import app.data.send.Bullet;
import app.data.send.Position;
import app.data.send.Tank;

public abstract class Block extends Shiftable implements Sendable {
    public static final int BLOCK_SIZE = 50;

    public Block(Position position, int index) {
        super(position, index);
    }

    @Override
    public int distanceToObj(Shiftable point2) {
        Position p1 = this.position;
        Position p2 = point2.getPosition();
        double distanceBounds = 0.0;

        if(point2 instanceof Bullet)
            return 1; //zawsze sa oddlone (nie ma kolizji midzy pociskami)

        if(point2 instanceof Tank)
            distanceBounds = Block.BLOCK_SIZE / 2.0 + Tank.TANK_SIZE / 2.0;
        else if(point2 instanceof Block)
            distanceBounds = Block.BLOCK_SIZE;

        if(Math.abs(p1.getX() - p2.getX()) <= distanceBounds || Math.abs(p1.getY() - p2.getY()) <= distanceBounds)
            return -1;
        else
            return 1;
    }
}
