public class Player implements GameObject{
    private Tank tank;
    private int points;

    public Player(){
        this.tank = null;
        this.points = 0;
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
