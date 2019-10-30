import static java.lang.Math.*;

public abstract class Transformable extends Shiftable implements Rotatable{
    protected Rotation rotation;

    public Rotation getRotation(){
        return rotation;
    }
}
