package serverApp.data;

import serverApp.Game;
import serverApp.abstractObjects.GameObject;
import serverApp.abstractObjects.Indexable;
import serverApp.abstractObjects.Sendable;

public class Player extends Indexable implements GameObject, Sendable {
    private Tank tank;
    private int points;
    private PlayerState playerState;
    private int threadNum; //test

    public enum PlayerState{
        ACTIVE,
        UNACTIVE,
        EXIT
    }

    public Player(){
        this.tank = null;
        this.points = 0;
        this.playerState = PlayerState.ACTIVE;
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

    public PlayerState getState() {
        return playerState;
    }

    public void setState(PlayerState playerState) {
        this.playerState = playerState;
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

    @Override
    public void display() {
        //...
    }

    @Override
    public void dataUpdate() {
        //System.out.println("dataUpdate - serverApp.data.Player \t\tT: " + threadNum + " \tI: " + this.getIndex());
        //...
    }

    @Override
    public void collisionUpdate() {
        //System.out.println("collisionUpdate - serverApp.data.Player \tT: " + threadNum + " \tI: " + this.getIndex());
        //...
    }

    @Override
    public void afterUpdate() {
        //System.out.println("afterUpdate - serverApp.data.Player \t\tT: " + threadNum + " \tI: " + this.getIndex());
        //...
    }

    @Override
    public void destroy() {
        tank.destroy();
        Game.getGameManager().getPlayers().remove(this);
    }

    @Override
    public void setThread(int th) { //test
        threadNum = th;
    }

    @Override
    public String toString(){
        String str;
        str = "serverApp.data.Player" + this.getIndex();
        return str;
    }
}
