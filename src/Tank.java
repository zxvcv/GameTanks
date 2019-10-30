import java.util.LinkedList;

public class Tank extends Transformable implements GameObject, Shootable, CollisionManager{
    static final double TANK_SPEED = 1.0;
    static final double TANK_BASIC_HP = 100;
    private double hp;
    private Player player;

    public Tank(){
        hp = TANK_BASIC_HP;
    }

    public double getHp(){
        return hp;
    }

    Player getPlayer(){
        return player;
    }

    void hit(double _dmg){
        hp -= _dmg;
        if(hp <= 0)
            this.destroy();
    }

    public Tank(Tank tank){
        this.hp = tank.hp;
        this.position = tank.position;
        this.rotation = tank.rotation;
        this.player = tank.player;
    }

    @Override
    public void destroy() {
        player.remTank();
        GameManager.tanks.remove(this);
    }

    @Override
    public void display() {

    }

    @Override
    public void firstUpdate() {
        //first update made in firstUpdate of Player class just after new data comes
    }

    @Override
    public void update() {
        LinkedList<GameObject> collisions = checkCollisions(GameManager.map, GameManager.tanks, GameManager.bullets);
        if(collisions.isEmpty())
            return;
        for(GameObject o : collisions){
            if(o instanceof Bullet)
                continue;
            if(o instanceof Block)
                player.lateUpdate();
        }
    }

    @Override
    public void lateUpdate() {
        //not used
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
