package app.data.send;

import app.Game;
import app.GameManager;
import app.abstractObjects.*;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Bullet extends Transformable implements GameObject, CollisionManager, Sendable {
    static final double BULLET_SPEED = 10.0;
    static final double BULLET_DMG = 10;
    private Tank owner;

    public Bullet(Bullet bullet){
        super(bullet.rotation, bullet.position, bullet.getIndex());
        this.owner = bullet.owner;
    }

    public Bullet(Position position, Rotation rotation, Tank owner, int index){
        super(rotation, position, index);
        this.owner = owner;
    }

    public Tank getOwner(){
        return owner;
    }

    @Override
    public void destroy() {
        GameManager gameManager = Game.getGameManager();
        gameManager.getMessageQueueToSend().add(new GameMessage("DESTROY BULLET", getIndex()));
        gameManager.getBullets().remove(this);
        System.out.println("destroy bullet");
    }

    @Override
    public void dataUpdate() {
        int speedDivider = 1;

        switch (this.rotation.getRotation()){
            case 0:
                this.move(BULLET_SPEED/speedDivider, 0);
                break;
            case 90:
                this.move(0, BULLET_SPEED/speedDivider);
                break;
            case 180:
                this.move(-(BULLET_SPEED/speedDivider), 0);
                break;
            case 270:
                this.move(0, -(BULLET_SPEED/speedDivider));
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
            if(o instanceof Tank){
                owner.getPlayer().addPoints(20);
                ((Tank) o).hit(BULLET_DMG);
            }
            if(o instanceof StoneBlock) {
                this.destroy();
            }
        }
    }

    @Override
    public void afterUpdate() {

    }

    @Override
    public LinkedList<Shiftable> checkCollisions(Map map, ConcurrentLinkedQueue<Tank> tanks, ConcurrentLinkedQueue<Bullet> bullets) {
        LinkedList<Block> blocks = map.getClosestBlocks(this.position);
        LinkedList<Shiftable> collisions = new LinkedList<>();

        for(Block b : blocks){
            if(this.distanceToObj(b) <= 0)
                if(b instanceof StoneBlock)
                    collisions.add(b);
        }
        for(Tank t : tanks){
            if(this.distanceToObj(t) <= 0)
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
            distanceBounds = Tank.TANK_SIZE / 2.0;
        else if(point2 instanceof Block)
            distanceBounds = Block.BLOCK_SIZE / 2.0;

        if(Math.abs(p1.getX() - p2.getX()) <= distanceBounds && Math.abs(p1.getY() - p2.getY()) <= distanceBounds)
            return -1;
        else
            return 1;
    }
}
