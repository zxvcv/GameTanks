package app.data.send;

import app.Game;
import app.GameManager;
import app.abstractObjects.*;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Tank extends Transformable implements GameObject, Shootable, CollisionManager, Sendable {
    static final double TANK_SPEED = 10.0;
    static final double TANK_BASIC_HP = 100;
    private double hp;
    private Player player;
    private Position previousPosition;
    private boolean shootBlock;

    public Tank(Tank tank){
        super(tank.rotation, tank.position, tank.getIndex());
        this.hp = tank.hp;
        this.player = tank.player;
        this.previousPosition = tank.previousPosition;
        this.shootBlock = false;
        setIndex(tank.getIndex());
    }

    public Tank(Position position, Rotation rotation, Player player, int index){
        super(rotation, position, index);
        this.hp = TANK_BASIC_HP;
        this.player = player;
        this.shootBlock = false;
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

    }

    @Override
    public void dataUpdate() {
        String pressed = player.getKeysLog().getPressedKey();

        int speedDivider = 1;

        if(shootBlock && !player.getKeysLog().getKey("SPACE")){
            shootBlock = false;
        }

        switch (pressed){
            case "W":
                this.rotation.setRotation(90);
                this.move(0, TANK_SPEED/speedDivider);
                break;
            case "A":
                this.rotation.setRotation(180);
                this.move(-(TANK_SPEED/speedDivider), 0);
                break;
            case "S":
                this.rotation.setRotation(270);
                this.move(0, -(TANK_SPEED/speedDivider));
                break;
            case "D":
                this.rotation.setRotation(0);
                this.move(TANK_SPEED/speedDivider, 0);
                break;
            case "SPACE":
                if(!shootBlock){
                    this.shoot();
                    shootBlock = true;
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void collisionUpdate() {
        //System.out.println("collisionUpdate - serverApp.data.send.Tank \t\tT: " + threadNum + " \tI: " + this.getIndex());
        /*
        serverApp.GameManager gm = serverApp.Game.getGameManager();
        LinkedList<serverApp.abstractObjects.Drawable> collisions = checkCollisions(gm.getMap(), gm.getTanks(), gm.getBullets());
        if(collisions.isEmpty())
            return;
        for(serverApp.abstractObjects.Drawable o : collisions){
            if(o instanceof serverApp.abstractObjects.Block || o instanceof serverApp.data.send.Tank)
                position.setPosition(previousPosition.getX(), previousPosition.getY()); //cofniecie ruchu (nie mozna sie ruszyc w te strone)
        }
         */
    }

    @Override
    public void afterUpdate() {

    }

    @Override
    public void shoot() {
        Bullet newBullet = new Bullet(this.position, this.rotation, this, Game.getIndexer().getIndex());
        Game.getGameManager().getBullets().add(newBullet);
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
