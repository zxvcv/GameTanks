import java.util.LinkedList;

public class Tank extends Transformable implements GameObject, Shootable, CollisionManager{
    public static final double TANK_SPEED = 1.0;
    private double hp;

    public double getHp(){
        return hp;
    }

    public void hit(double _dmg){
        hp -= _dmg;
        if(hp <= 0)
            this.destroy();
    }

    public Tank(Tank tank){
        this.hp = tank.hp;
        this.position = tank.position;
        this.rotation = tank.rotation;
    }

    @Override
    public void destroy() {

    }

    @Override
    public void display() {

    }

    @Override
    public void update(GameManager gameManager) {
        //update zalezny od danych odebranych
    }

    @Override
    public void unupdate(GameManager gameManager) {
        //unupdate zalezy od update i danych odebranych
    }

    @Override
    public Bullet shoot() {
        return new Bullet(this.position, this.rotation, this);
    }

    @Override
    public LinkedList<GameObject> checkCollisions(Map map, LinkedList<Tank> tanks, LinkedList<Bullet> bullets) {
        Block[] blocks = map.getClosestBlocks(this.position);
        LinkedList<GameObject> collisions = new LinkedList<>();

        for(Bullet b : bullets){
            if(this.distanceToObj(b) <= 0)
                collisions.add(b);
        }
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
}
