package app.data.send;

import app.Game;
import app.GameManager;
import app.abstractObjects.*;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Tank extends Transformable implements GameObject, Shootable, CollisionManager, Sendable {
    public static final int TANK_SIZE = 40;
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
    }

    public Tank(Position position, Rotation rotation, Player player, int index){
        super(rotation, position, index);
        this.hp = TANK_BASIC_HP;
        this.player = player;
        this.previousPosition = new Position(position);
        this.shootBlock = false;
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
        GameManager gameManager = Game.getGameManager();
        player.remTank();
        gameManager.getMessageQueueToSend().add(new GameMessage("DESTROY TANK", getIndex()));
        gameManager.getTanks().remove(this);
    }

    @Override
    public void dataUpdate() {
        String pressed = player.getKeysLog().getPressedKey();

        if(pressed == ""){
            if(player.getKeysLog().getKey("SPACE"))
                pressed = "SPACE";
        }

        int speedDivider = 1;

        if(shootBlock && !player.getKeysLog().getKey("SPACE")){
            shootBlock = false;
        }

        switch (pressed){
            case "W":
                this.rotation.setRotation(270);
                this.move(0, -(TANK_SPEED/speedDivider));
                break;
            case "A":
                this.rotation.setRotation(180);
                this.move(-(TANK_SPEED/speedDivider), 0);
                break;
            case "S":
                this.rotation.setRotation(90);
                this.move(0, TANK_SPEED/speedDivider);
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
        GameManager gm = Game.getGameManager();
        LinkedList<Shiftable> collisions = checkCollisions(gm.getMap(), gm.getTanks(), gm.getBullets());
        if(collisions.isEmpty())
            return;

        for(Shiftable o : collisions){
            if(o instanceof StoneBlock || o instanceof Tank) {
                position.setPosition(previousPosition.getX(), previousPosition.getY()); //cofniecie ruchu (nie mozna sie ruszyc w te strone)
            }
        }
    }

    @Override
    public void afterUpdate() {

    }

    @Override
    public void shoot() {
        Position pos = new Position(this.position.getX(), this.position.getY());

        if(rotation.getRotation() == 0) //w prawo
            pos = new Position(this.position.getX() + 25, this.position.getY());
        if(rotation.getRotation() == 90) // w dol
            pos = new Position(this.position.getX(), this.position.getY() + 25);
        if(rotation.getRotation() == 180) //w lewo
            pos = new Position(this.position.getX() - 25, this.position.getY());
        if(rotation.getRotation() == 270) //do gory
            pos = new Position(this.position.getX(), this.position.getY() - 25);

        Bullet newBullet = new Bullet(pos, this.rotation, this, Game.getIndexer().getIndex());
        Game.getGameManager().getBullets().add(newBullet);
    }

    @Override
    public synchronized void move(double _x, double _y) {
        previousPosition.setPosition(position.getX(), position.getY());
        this.getPosition().move(_x, _y);
    }

    @Override
    public LinkedList<Shiftable> checkCollisions(Map map, ConcurrentLinkedQueue<Tank> tanks, ConcurrentLinkedQueue<Bullet> bullets) {
        GameManager gm = Game.getGameManager();
        LinkedList<Block> blocks = gm.getMap().getClosestBlocks(this.position);
        LinkedList<Shiftable> collisions = new LinkedList<>();

        for(Block b : blocks){
            if(this.distanceToObj(b) <= 0)
                collisions.add(b);
        }

        for(Tank t : tanks){
            if(this.distanceToObj(t) <= 0 && t != this)
                collisions.add(t);
        }

        return collisions;
    }

    @Override
    public int distanceToObj(Shiftable point2) {
        Position p1 = this.position;
        Position p2 = point2.getPosition();
        double distanceBounds = 0.0;

        if(point2 instanceof Bullet)
            return 1; //zawsze sa oddlone (nie ma kolizji midzy pociskami)

        if(point2 instanceof Tank)
            distanceBounds = Tank.TANK_SIZE;
        else if(point2 instanceof Block)
            distanceBounds = TANK_SIZE / 2.0 + Block.BLOCK_SIZE / 2.0 - 10;

        if(Math.abs(p1.getX() - p2.getX()) <= distanceBounds && Math.abs(p1.getY() - p2.getY()) <= distanceBounds)
            return -1;
        else
            return 1;
    }
}
