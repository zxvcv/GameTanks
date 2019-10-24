public class Position {
    private double x;
    private double y;

    public Position(double _x, double _y){
        x = _x;
        y = _y;
    }

    public Position(Position _position){
        x = _position.x;
        y = _position.y;
    }

    public void relativeMovement(double _x, double _y, GameObject _position){

    }

    public void absoluteMovement(double _x, double _y){

    }
}
