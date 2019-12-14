package app.abstractObjects;

import app.data.send.Position;
import app.data.send.Tank;

import java.io.Serializable;

import static java.lang.Math.*;

public abstract class Shiftable extends Indexable implements Movable, Serializable {
    protected Position position;

    public Shiftable(Position position, int index){
        super(index);
        this.position = new Position(position);
    }

    public Position getPosition(){
        return position;
    }

    /*
    public double distanceToObj(Shiftable point2){
        Position p1 = this.getPosition();
        Position p2 = point2.getPosition();

        return sqrt(pow(p2.getX() - p1.getX(), 2) + pow(p2.getY() - p1.getY(), 2))
                - this.distanceToBound(point2) - point2.distanceToBound(this);
    }

    public double distanceToBound(Shiftable point2){
        Position p1 = this.getPosition();
        Position p2 = point2.getPosition();
        double alfa = atan((p2.getY() - p1.getY()) / (p2.getX() - p1.getX()));

        double len = 0;
        if(this instanceof Tank)
            len = Tank.TANK_SIZE;
        else if(point2 instanceof Block)
            len = Block.BLOCK_SIZE;

        return (len / (2.0 * cos(alfa)));
    }
    */

    public abstract int distanceToObj(Shiftable point2);

    @Override
    public synchronized void move(double _x, double _y) {
        this.getPosition().move(_x, _y);
    }
}
