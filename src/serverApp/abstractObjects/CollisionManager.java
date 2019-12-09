package serverApp.abstractObjects;

import serverApp.data.Bullet;
import serverApp.data.Map;
import serverApp.data.Tank;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

public interface CollisionManager {
    LinkedList<Drawable> checkCollisions(Map map, ConcurrentLinkedQueue<Tank> tanks, ConcurrentLinkedQueue<Bullet> bullets);
}
