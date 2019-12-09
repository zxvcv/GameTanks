package app.abstractObjects;

import app.data.send.Position;
import app.data.send.Rotation;

import java.io.Serializable;

public abstract class Transformable extends Shiftable implements Rotatable, Serializable {
    protected Rotation rotation;

    public Transformable(Rotation rotation, Position position){
        super(position);
        this.rotation = rotation;
    }

    public Rotation getRotation(){
        return rotation;
    }

    @Override
    public void rotate(int _rotate) {
        this.getRotation().rotate(_rotate);
    }
}
