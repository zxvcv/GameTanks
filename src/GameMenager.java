public class GameMenager {
    CollisionDetector collisionDetector;
    Player[] players;
    Map map;
    Bullet[] bullets;

    public void update(){
        for(Bullet b : bullets)
            b.update();

        map.update();

        for(Player p : players)
            p.update();
    }

    public void display(){
        map.display();

        for(Player p : players)
            p.display();

        for(Bullet b : bullets)
            b.display();
    }
}
