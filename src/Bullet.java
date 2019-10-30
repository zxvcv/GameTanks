import java.util.LinkedList;

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
    public void update(GameManager gameManager) {
        if(rotation.getRotation() == 0)
            move(GameTime.deltaTime() * BULLET_SPEED, 0);
        if(rotation.getRotation() == 90)
            move(0, GameTime.deltaTime() * BULLET_SPEED);
        if(rotation.getRotation() == 180)
            move(GameTime.deltaTime() * BULLET_SPEED * (-1), 0);
        if(rotation.getRotation() == 270)
            move(0, GameTime.deltaTime() * BULLET_SPEED * (-1));
    }

    @Override
    public void unupdate(GameManager gameManager) {
        if(rotation.getRotation() == 0)
            move(GameTime.deltaTime() * BULLET_SPEED * (-1), 0);
        if(rotation.getRotation() == 90)
            move(0, GameTime.deltaTime() * BULLET_SPEED * (-1));
        if(rotation.getRotation() == 180)
            move(GameTime.deltaTime() * BULLET_SPEED, 0);
        if(rotation.getRotation() == 270)
            move(0, GameTime.deltaTime() * BULLET_SPEED);
    }

    @Override
    public LinkedList<GameObject> checkCollisions(Map map, LinkedList<Tank> tanks, LinkedList<Bullet> bullets) {
        Block[] blocks = map.getClosestBlocks(this.position);
        LinkedList<GameObject> collisions = new LinkedList<>();

        for(Block b : blocks){
            if(this.distanceToObj(b) <= 0)
                collisions.add(b);
        }
        for(Tank t : tanks){
            if(this.distanceToObj(t) <= 0)
                collisions.add(t);
        }
        return null;
    }

    @Override
    public double distanceToBound(Shiftable p2){
        return 0;
    }
}
