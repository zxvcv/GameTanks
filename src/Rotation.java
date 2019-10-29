public class Rotation implements Rotatable{
    double rotation;

    public Rotation(double _rotation){
        rotation = _rotation;
    }

    public Rotation(final Rotation _rotation){ rotation = _rotation.rotation; }

    @Override
    public void rotate(double _rotate){
        rotation += _rotate;
        if(rotation>=360 || rotation < 0)
            rotation %= 360;
    }
}
