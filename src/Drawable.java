import java.util.LinkedList;

public interface Drawable<T extends GameObject>{
    void display();
    void update(GameManager gameManager);
    void unupdate(GameManager gameManager);
}
