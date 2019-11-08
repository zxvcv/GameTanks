import java.util.LinkedList;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CyclicBarrier;

public class GameManager implements Updateable, Drawable{
    public static int countBull = 0; //test
    public static int countTank = 0; //test

    private volatile boolean dataReady;
    private volatile boolean collisionReady;
    private volatile boolean afterReady;
    private ConcurrentLinkedQueue<GameObject> dataQueue;
    private ConcurrentLinkedQueue<GameObject> collisionQueue;
    private ConcurrentLinkedQueue<GameObject> afterQueue;
    private CyclicBarrier barrierTaskRuntime;
    private CyclicBarrier barrierPeroidRuntime;
    private CyclicBarrier barrierTransmitters; //ilosc zalezy od ilosci graczy
    public enum BarrierNum{
        TASK_BARRIER,
        TRANSMITTER_BARRIER,
        PEROID_BARRIER
    }

    private ConcurrentLinkedQueue<Tank> tanks;
    private Map map;
    private ConcurrentLinkedQueue<Bullet> bullets;
    private ConcurrentLinkedQueue<Player> players;

    public GameManager(boolean isThisServer){
        if(isThisServer){
            dataQueue = new ConcurrentLinkedQueue<>();
            collisionQueue = new ConcurrentLinkedQueue<>();
            afterQueue = new ConcurrentLinkedQueue<>();
            barrierTaskRuntime = new CyclicBarrier(Game.SERVER_THREADS + 1);
            barrierPeroidRuntime = new CyclicBarrier(Game.SERVER_THREADS + 1);

            dataReady = false;
            collisionReady = false;
            afterReady = false;
        }
        else{
            //inicjalizacja dla wersji Klienta
        }

        tanks = new ConcurrentLinkedQueue<>();
        map = new Map();
        bullets = new ConcurrentLinkedQueue<>();
        players = new ConcurrentLinkedQueue<>();
    }

    public ConcurrentLinkedQueue<Tank> getTanks(){
        return tanks;
    }

    public Map getMap(){
        return map;
    }

    public ConcurrentLinkedQueue<Bullet> getBullets(){
        return bullets;
    }

    public ConcurrentLinkedQueue<Player> getPlayers(){
        return players;
    }

    public boolean isDataQueueFilled() {
        return dataReady;
    }

    public boolean isCollisionQueueFilled() {
        return collisionReady;
    }

    public boolean isAfterQueueFilled() {
        return afterReady;
    }

    public ConcurrentLinkedQueue<GameObject> getDataQueue() {
        return dataQueue;
    }

    public ConcurrentLinkedQueue<GameObject> getCollisionQueue() {
        return collisionQueue;
    }

    public ConcurrentLinkedQueue<GameObject> getAfterQueue() {
        return afterQueue;
    }

    public CyclicBarrier getBarrier(BarrierNum barrierNum) {
        if(barrierNum == BarrierNum.TASK_BARRIER)
            return barrierTaskRuntime;
        else if(barrierNum == BarrierNum.TRANSMITTER_BARRIER)
            return barrierTransmitters;
        else
            return barrierPeroidRuntime;
    }

    private void clearReadyFlags(){
        dataReady = false;
        collisionReady = false;
        afterReady = false;
    }

    public void prepareCycle(){
        //----do testu
        Tank t1 = new Tank(new Position(1f, 1f), new Rotation(1), new Player());
        tanks.add(t1);
        bullets.add(new Bullet(new Position(1f, 1f), new Rotation(1), t1));
        bullets.add(new Bullet(new Position(1f, 1f), new Rotation(1), t1));
        bullets.add(new Bullet(new Position(1f, 1f), new Rotation(1), t1));
        bullets.add(new Bullet(new Position(1f, 1f), new Rotation(1), t1));
        bullets.add(new Bullet(new Position(1f, 1f), new Rotation(1), t1));
        Tank t2 = new Tank(new Position(1f, 1f), new Rotation(1), new Player());
        tanks.add(t2);
        bullets.add(new Bullet(new Position(1f, 1f), new Rotation(1), t2));
        bullets.add(new Bullet(new Position(1f, 1f), new Rotation(1), t2));
        bullets.add(new Bullet(new Position(1f, 1f), new Rotation(1), t2));
        bullets.add(new Bullet(new Position(1f, 1f), new Rotation(1), t2));
        bullets.add(new Bullet(new Position(1f, 1f), new Rotation(1), t2));
        //----koniec do testu

        clearReadyFlags();
        try {
            barrierPeroidRuntime.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }finally {
            barrierPeroidRuntime.reset();
        }
    }

    public void closeCycle(){

    }

    @Override
    public void dataUpdate() {
        dataQueue.addAll(tanks);
        dataQueue.addAll(bullets);
        dataReady = true;
    }

    @Override
    public void collisionUpdate() {
        collisionQueue.addAll(tanks);
        collisionQueue.addAll(bullets);
        collisionReady = true;
    }

    @Override
    public void afterUpdate() {
        afterQueue.addAll(tanks);
        afterQueue.addAll(bullets);
        afterReady = true;
    }

    @Override
    public void display(){

    }
}
