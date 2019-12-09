package app.data.send;

import app.Game;
import app.abstractObjects.*;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Bullet extends Transformable implements GameObject, CollisionManager, Sendable {
    static final double BULLET_SPEED = 2.0;
    static final double BULLET_DMG = 10;
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
        Game.getGameManager().getBullets().remove(this);
    }

    @Override
    public void display() {

    }

    @Override
    public void dataUpdate() {
        //System.out.println("dataUpdate - serverApp.data.send.Bullet \t\tT: " + threadNum + " \tI: " + this.getIndex());
        /*
        if(rotation.getRotation() == 0)
            move(serverApp.GameTime.deltaTime() * BULLET_SPEED, 0);
        if(rotation.getRotation() == 90)
            move(0, serverApp.GameTime.deltaTime() * BULLET_SPEED);
        if(rotation.getRotation() == 180)
            move(serverApp.GameTime.deltaTime() * BULLET_SPEED * (-1), 0);
        if(rotation.getRotation() == 270)
            move(0, serverApp.GameTime.deltaTime() * BULLET_SPEED * (-1));
         */
    }

    @Override
    public void collisionUpdate() {
        //System.out.println("collisionUpdate - serverApp.data.send.Bullet \tT: " + threadNum + " \tI: " + this.getIndex());
        /*
        serverApp.GameManager gm = serverApp.Game.getGameManager();
        LinkedList<serverApp.abstractObjects.Drawable> collisions = checkCollisions(gm.getMap(), gm.getTanks(), gm.getBullets());
        if(collisions.isEmpty())
            return;
        for(serverApp.abstractObjects.Drawable o : collisions){
            if(o instanceof serverApp.data.send.Tank){
                owner.getPlayer().addPoints(20);
                ((serverApp.data.send.Tank) o).hit(BULLET_DMG);
            }
        }
        this.destroy();
         */
    }

    @Override
    public void afterUpdate() {

    }

    @Override
    public LinkedList<Drawable> checkCollisions(Map map, ConcurrentLinkedQueue<Tank> tanks, ConcurrentLinkedQueue<Bullet> bullets) {
        Block[] blocks = map.getClosestBlocks(this.position);
        LinkedList<Drawable> collisions = new LinkedList<>();

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

    @Override
    public double distanceToBound(Shiftable p2){
        return 0;
    }
}
