public abstract class Transformable extends Shiftable implements Rotatable{
    protected Rotation rotation;

    public Rotation getRotation(){
        return rotation;
    }

    @Override
    public void rotate(int _rotate) {
        this.getRotation().rotate(_rotate);
    }
}
