package app;

import app.abstractObjects.Drawable;
import app.abstractObjects.GameObject;
import app.abstractObjects.Updateable;
import app.data.send.*;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CyclicBarrier;

public class GameManager implements Updateable, Drawable {
    private volatile boolean dataReady;
    private volatile boolean collisionReady;
    private volatile boolean afterReady;
    private ConcurrentLinkedQueue<GameObject> dataQueue;
    private ConcurrentLinkedQueue<GameObject> collisionQueue;
    private ConcurrentLinkedQueue<GameObject> afterQueue;
    private ConcurrentLinkedQueue<GameMessage> messageQueueReceived;
    private ConcurrentLinkedQueue<GameMessage> messageQueueToSend;
    private boolean sendPermit;
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

    public GameManager(){
        dataQueue = new ConcurrentLinkedQueue<>();
        collisionQueue = new ConcurrentLinkedQueue<>();
        afterQueue = new ConcurrentLinkedQueue<>();
        barrierTaskRuntime = new CyclicBarrier(Game.SERVER_THREADS + 1);
        barrierPeroidRuntime = new CyclicBarrier(Game.SERVER_THREADS + 1);

        dataReady = false;
        collisionReady = false;
        afterReady = false;

        tanks = new ConcurrentLinkedQueue<>();
        map = new Map();
        bullets = new ConcurrentLinkedQueue<>();
        players = new ConcurrentLinkedQueue<>();
        messageQueueReceived = new ConcurrentLinkedQueue<>();
        messageQueueToSend = new ConcurrentLinkedQueue<>();

        sendPermit = false;
    }

    public ConcurrentLinkedQueue<Tank> getTanks(){
        return tanks;
    }

    public Map getMap(){
        return map;
    }

    public void setMap(Map map){
        this.map = map;
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

    public ConcurrentLinkedQueue<GameMessage> getMessageQueueToSend() {
        return messageQueueToSend;
    }

    public ConcurrentLinkedQueue<GameMessage> getMessageQueueReceived() {
        return messageQueueReceived;
    }

    public CyclicBarrier getBarrier(BarrierNum barrierNum) {
        switch (barrierNum){
            case TASK_BARRIER: return barrierTaskRuntime;
            case TRANSMITTER_BARRIER: return barrierTransmitters;
            case PEROID_BARRIER: return barrierPeroidRuntime;
            default: return barrierTaskRuntime;
        }
    }

    public void setBarrier(BarrierNum barrierNum, CyclicBarrier barrier){
        switch (barrierNum){
            case TASK_BARRIER:
                barrierTaskRuntime = barrier;
                break;
            case TRANSMITTER_BARRIER:
                barrierTransmitters = barrier;
                break;
            case PEROID_BARRIER:
                barrierPeroidRuntime = barrier;
                break;
            default:
                break;
        }
    }

    public synchronized void resetBarrier(BarrierNum barrierNum){
        switch (barrierNum){
            case TASK_BARRIER:
                if(barrierTaskRuntime.isBroken())
                    barrierTaskRuntime.reset();
                break;
            case TRANSMITTER_BARRIER:
                if(barrierTransmitters.isBroken())
                    barrierTransmitters.reset();
                break;
            case PEROID_BARRIER:
                if(barrierPeroidRuntime.isBroken())
                    barrierPeroidRuntime.reset();
                break;
            default:
        }
    }

    public void setSendPermit(boolean sendPermit) {
        this.sendPermit = sendPermit;
    }

    public boolean isSendPermit() {
        return sendPermit;
    }

    private void clearReadyFlags(){
        dataReady = false;
        collisionReady = false;
        afterReady = false;
    }

    public void prepareCycle(){
        clearReadyFlags();
        sendPermit = false;
    }

    public void closeCycle(){
        sendPermit = true;
    }

    @Override
    public void dataUpdate() {
        dataQueue.addAll(players);
        dataQueue.addAll(tanks);
        dataQueue.addAll(bullets);
        dataReady = true;
    }

    @Override
    public void collisionUpdate() {
        collisionQueue.addAll(players);
        collisionQueue.addAll(tanks);
        collisionQueue.addAll(bullets);
        collisionReady = true;
    }

    @Override
    public void afterUpdate() {
        afterQueue.addAll(players);
        afterQueue.addAll(tanks);
        afterQueue.addAll(bullets);
        afterReady = true;
    }

    @Override
    public void display(){

    }
}