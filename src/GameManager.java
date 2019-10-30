import java.util.LinkedList;

public class GameManager {
    CollisionManager collisionDetector;
    DataTransmitter dataTransmitter; //odbiera dane i wysyla
    LinkedList<Tank> tanks;
    Map map;
    LinkedList<Bullet> bullets;
    LinkedList<Player> players;

    public void update(){
        //update changes
        for(Bullet b : bullets)
            b.update(this);

        map.update(this);

        for(Tank p : tanks)
            p.update(this);

        //calculate
        LinkedList<GameObject> collisions;
        for(Bullet b : bullets) {
            collisions = b.checkCollisions(map, tanks, bullets);
            if(collisions.isEmpty())
                break;
            else
                //collisionsResolve(collisions);
        }
        for(Tank t : tanks){
            collisions = t.checkCollisions(map, tanks, bullets);
            if(collisions.isEmpty())
                break;
            else
                //collisionsResolve(collisions);
        }
    }

    public void display(){
        map.display();

        for(Tank p : tanks)
            p.display();

        for(Bullet b : bullets)
            b.display();
    }

    public void send(){

    }
}
