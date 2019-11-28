package control;

import data.Bullet;
import data.Drawable;
import data.Map;
import data.Tank;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

public interface CollisionManager {
    LinkedList<Drawable> checkCollisions(Map map, ConcurrentLinkedQueue<Tank> tanks, ConcurrentLinkedQueue<Bullet> bullets);
}
