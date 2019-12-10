package app.data.send;

import app.abstractObjects.Rotatable;
import app.abstractObjects.Sendable;

import java.io.Serializable;

public class Rotation implements Rotatable, Serializable {
    int rotation;

    public Rotation(int _rotation){
        rotation = _rotation;
    }

    public Rotation(final Rotation _rotation){
        rotation = _rotation.rotation;
    }

    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rotation){
        this.rotation = rotation;
    }

    @Override
    public synchronized void rotate(int _rotate){
        rotation += _rotate;
        if(rotation >= 360 || rotation < 0)
            rotation %= 360;
    }
}
