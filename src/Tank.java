import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Tank extends Transformable implements GameObject, Shootable, CollisionManager{
    static final double TANK_SPEED = 1.0;
    static final double TANK_BASIC_HP = 100;
    private double hp;
    private Player player;
    private Position previousPosition;

    public Tank(Tank tank){
        this.hp = tank.hp;
        this.position = tank.position;
        this.rotation = tank.rotation;
        this.player = tank.player;
        this.previousPosition = tank.previousPosition;
    }

    public Tank(Position position, Rotation rotation, Player player){
        this.hp = TANK_BASIC_HP;
        this.position = position;
        this.rotation = rotation;
        this.player = player;
        this.previousPosition = new Position(position);
    }

    public double getHp(){
        return hp;
    }

    public Player getPlayer(){
        return player;
    }

    public synchronized void hit(double dmg){
        hp -= dmg;
        if(hp <= 0)
            this.destroy();
    }

    @Override
    public synchronized void destroy() {
        player.remTank();
        Game.getGameManager().getTanks().remove(this);
    }

    @Override
    public void display() {
        //...
    }

    @Override
    public void dataUpdate() {
        //zmiana watosci po otrzymaniu danych od gracza
    }

    @Override
    public void collisionUpdate() {
        GameManager gm = Game.getGameManager();
        LinkedList<Drawable> collisions = checkCollisions(gm.getMap(), gm.getTanks(), gm.getBullets());
        if(collisions.isEmpty())
            return;
        for(Drawable o : collisions){
            if(o instanceof Block || o instanceof Tank)
                position.setPosition(previousPosition.getX(), previousPosition.getY()); //cofniecie ruchu (nie mozna sie ruszyc w te strone)
        }
    }

    @Override
    public void afterUpdate() {
        //---
    }

    @Override
    public void shoot() {
        Game.getGameManager().getBullets().add(new Bullet(this.position, this.rotation, this));
    }

    @Override
    public synchronized void move(double _x, double _y) {
        previousPosition.setPosition(position.getX(), position.getY());
        this.getPosition().move(_x, _y);
    }

    @Override
    public LinkedList<Drawable> checkCollisions(Map map, ConcurrentLinkedQueue<Tank> tanks, ConcurrentLinkedQueue<Bullet> bullets) {
        GameManager gm = Game.getGameManager();
        Block[] blocks = gm.getMap().getClosestBlocks(this.position);
        LinkedList<Drawable> collisions = new LinkedList<>();

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
        return collisions;
    }
}
