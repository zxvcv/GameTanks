public class Bullet extends Transformable implements GameObject, CollisionManager{
    public static final double BULLET_SPEED = 2.0;
    private Tank owner;

    public Bullet(Position position, Rotation rotation, Tank owner){
        this.position = position;
        this.rotation = rotation;
        this.owner = owner;
    }

    public Tank getOwner(){
        return owner;
    }

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
    public GameObject[] checkCollisions(Map map, Tank[] tanks, Bullet[] bullets) {
        Block[] blocks = map.getClosestBlocks(this.position);
        //...
        //podobnie jak w klasie Tank
        return null;
    }

    @Override
    public double distanceToBound(Shiftable p2){
        return 0;
    }
}
