public class Player implements GameObject{
    private DataTransmitter transmitter;
    private Tank tank;
    private int points;

    public synchronized void addPoints(int pt){
        points += pt;
    }

    public synchronized void subPoints(int pt){
        points -= pt;
    }

    public int getPoints(){
        return points;
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
}
