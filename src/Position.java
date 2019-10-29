public class Position implements Movable{
    private double x;
    private double y;

    public Position(double _x, double _y){
        x = _x;
        y = _y;
    }

    public Position(final Position _position){
        x = _position.x;
        y = _position.y;
    }

    public double getX(){
        return x;
    }

    public double getY(){
        return y;
    }

    @Override
    public void move(double _x, double _y){
        x += _x;
        y += _y;
    }
}
