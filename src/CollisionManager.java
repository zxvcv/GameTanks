import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

public interface CollisionManager {
    LinkedList<Drawable> checkCollisions(Map map, ConcurrentLinkedQueue<Tank> tanks, ConcurrentLinkedQueue<Bullet> bullets);
}
