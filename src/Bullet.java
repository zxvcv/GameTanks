public class Bullet implements Drawable, Changeable, Movable{
    public static final double SPEED = 2.0;

    Position position;
    Player owner;
    double speed;

    public Bullet(Position _position, Player _owner, double _speed){
        position = _position;
        owner = _owner;
        speed = _speed;
    }

    @Override
    public Position getPosition(){
        return position;
    }

    @Override
    public void move(double _x, double _y){

    }

    @Override
    public void update(){

    }

    @Override
    public void display(){

    }
}
