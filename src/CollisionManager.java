import java.util.LinkedList;

public interface CollisionManager {
    LinkedList<GameObject> checkCollisions(Map map, LinkedList<Tank> tanks, LinkedList<Bullet> bullets);
}
