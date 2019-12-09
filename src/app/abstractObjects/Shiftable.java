package app.abstractObjects;

import app.data.send.Position;

import java.io.Serializable;

import static java.lang.Math.*;

public abstract class Shiftable extends Indexable implements Movable, Serializable {
    protected Position position;

    public Shiftable(Position position){
        super();
        this.position = position;
    }

    public Position getPosition(){
        return position;
    }

    public double distanceToObj(Shiftable p2){
        Position posP1 = this.getPosition();
        Position posP2 = p2.getPosition();

        return sqrt(pow(posP2.getX() - posP1.getX(), 2) + pow(posP2.getY() - posP1.getY(), 2))
                - this.distanceToBound(p2) - p2.distanceToBound(this);
    }

    public double distanceToBound(Shiftable p2){
        Position posP1 = this.getPosition();
        Position posP2 = p2.getPosition();
        double alfa = atan((posP2.getY() - posP1.getY()) / (posP2.getX() - posP1.getX()));
        double beta = 0;

        return ((Block.BLOCK_SIZE / 2) / (cos(alfa - beta)));
    }

    @Override
    public synchronized void move(double _x, double _y) {
        this.getPosition().move(_x, _y);
    }
}
