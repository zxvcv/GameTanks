package app.data.send;

import app.abstractObjects.Rotatable;
import app.abstractObjects.Sendable;

public class Rotation implements Rotatable, Sendable {
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

    @Override
    public synchronized void rotate(int _rotate){
        rotation += _rotate;
        if(rotation >= 360 || rotation < 0)
            rotation %= 360;
    }
}
