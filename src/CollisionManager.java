import java.util.LinkedList;

public interface CollisionManager {
    public LinkedList<GameObject> checkCollisions(Map map, LinkedList<Tank> tanks, LinkedList<Bullet> bullets);
}
