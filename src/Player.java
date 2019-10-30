public class Player implements GameObject{
    private DataTransmitter transmitter;
    private Tank tank;
    private int points;

    void addPoints(int pt){
        points += pt;
    }

    public void subPoints(int pt){
        points -= pt;
    }

    public int getPoints(){
        return points;
    }

    void remTank(){
        tank = null;
    }

    @Override
    public void display() {
        //unused in Player
    }

    @Override
    public void firstUpdate() {
        //update swojego czolgu po otrzymaniu danych o dzialaniach gracza
        //wywolanie metody shoot na tanku jezeli zostal wykonany strzal
    }

    @Override
    public void update(){
        //not used
    }

    @Override
    public void lateUpdate() {
        //cofniecie zmian jezeli kolizja nie z pociskiem
    }

    @Override
    public void destroy() {
        tank.destroy();
        GameManager.players.remove(this);
    }
}
