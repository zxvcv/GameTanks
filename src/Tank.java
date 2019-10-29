public class Tank extends Transformable implements GameObject, Shootable{
    public static final double TANK_SPEED = 1.0;
    private double hp;

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

    @Override
    public Bullet shoot(Rotation rot, Position pos) {
        return null;
    }

    public Position getPosition(){
        return position;
    }

    public Rotation getRotation(){
        return rotation;
    }

    public double getHp(){
        return hp;
    }

    public void hit(double _dmg){
        hp -= _dmg;
        if(hp <= 0)
            this.destroy();
    }
}
