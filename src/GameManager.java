import java.util.LinkedList;

public class GameManager {
    public static CollisionManager collisionDetector;
    static LinkedList<Tank> tanks;
    static Map map;
    static LinkedList<Bullet> bullets;
    static LinkedList<Player> players;

    public void update(){
        //firstUpdate
        for(Bullet b: bullets)
            b.firstUpdate();
        //tanks firstUpdate just after data comes


        //update
        for(Bullet b: bullets)
            b.update();
        for(Tank t : tanks)
            t.update();


        //lateUpdate
        //nothing
    }

    public void display(){
        map.display();

        for(Tank p : tanks)
            p.display();

        for(Bullet b : bullets)
            b.display();
    }

    public void send(){
        //send data to players
    }
}
