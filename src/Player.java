public class Player extends Indexable implements GameObject, Sendable{
    private Tank tank;
    private int points;
    private PlayerState playerState;

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
        //...
    }

    @Override
    public void collisionUpdate() {
        //...
    }

    @Override
    public void afterUpdate() {
        //...
    }

    @Override
    public void destroy() {
        tank.destroy();
        Game.getGameManager().getPlayers().remove(this);
    }

    @Override
    public void setThread(int th) { //test

    }
}
