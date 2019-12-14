package app.abstractObjects;

import app.data.send.Bullet;
import app.data.send.Map;
import app.data.send.Tank;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

public interface CollisionManager {
    LinkedList<Shiftable> checkCollisions(Map map, ConcurrentLinkedQueue<Tank> tanks, ConcurrentLinkedQueue<Bullet> bullets);
}
