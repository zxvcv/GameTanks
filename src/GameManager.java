public class GameManager {
    CollisionManager collisionDetector;
    DataTransmitter dataTransmitter; //odbiera dane i wysyla
    Tank[] tanks;
    Map map;
    Bullet[] bullets;

    public void update(){
        for(Bullet b : bullets)
            b.update();

        map.update();

        for(Tank p : tanks)
            p.update();
    }

    public void display(){
        map.display();

        for(Tank p : tanks)
            p.display();

        for(Bullet b : bullets)
            b.display();
    }
}
