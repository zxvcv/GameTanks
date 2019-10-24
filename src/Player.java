public class Player implements Drawable, Changeable, Movable, Rotatable{
    Position position;
    Rotation rotation;
    double speed;

    public Player(Position _position, double _speed){
        position = _position;
        speed = _speed;
    }

    public Bullet shoot(){
        return new Bullet(new Position(position), this, Bullet.SPEED);
    }

    @Override
    public void rotate(double _rotate){
        rotation.rotate(_rotate);
    }

    @Override
    public Position getPosition(){
        return position;
    }

    @Override
    public void move(double _x, double _y){
        position.absoluteMovement(_x, _y);
    }

    @Override
    public void update(){

    }

    @Override
    public void display(){

    }
}
