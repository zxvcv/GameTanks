package app.data.send;

import app.Game;
import app.abstractObjects.GameObject;
import app.abstractObjects.Indexable;
import app.abstractObjects.Sendable;

public class Player extends Indexable implements GameObject, Sendable {
    private Tank tank;
    private int points;
    private KeyLog keysLog;

    public Player(int index){
        super(index);
        this.tank = null;
        this.points = 0;
        this.keysLog = new KeyLog(getIndex());
    }

    public synchronized void addPoints(int pt){
        points += pt;
    }

    public synchronized void subPoints(int pt){
        points -= pt;
    }

    public int getPoints(){
        return points;
    }

    public synchronized void setTank(Tank tank){
        this.tank = tank;
    }

    public synchronized void remTank(){
        tank = null;
    }

    public Tank getTank(){
        return tank;
    }

    public KeyLog getKeysLog() {
        return keysLog;
    }

    @Override
    public void dataUpdate() {

    }

    @Override
    public void collisionUpdate() {

    }

    @Override
    public void afterUpdate() {

    }

    @Override
    public void destroy() {
        tank.destroy();
        Game.getGameManager().getPlayers().remove(this);
    }

}
