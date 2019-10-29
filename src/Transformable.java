public abstract class Transformable implements Movable, Rotatable{
    protected Position position;
    protected Rotation rotation;

    public Position getPosition(){
        return position;
    }

    public Rotation getRotation(){
        return rotation;
    }
}
