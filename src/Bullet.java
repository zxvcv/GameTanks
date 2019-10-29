public class Bullet extends Transformable implements GameObject{
    public static final double BULLET_SPEED = 2.0;

    Tank owner;

    @Override
    public void destroy() {

    }

    @Override
    public void display() {

    }

    @Override
    public void update() {

    }

    @Override
    public void move(double _x, double _y) {
        position.move(_x, _y);
    }

    @Override
    public void rotate(double _rotate) {
        rotation.rotate(_rotate);
    }

    public Position getPosition(){
        return position;
    }

    public Rotation getRotation(){
        return rotation;
    }

    public Tank getOwner(){
        return owner;
    }
}