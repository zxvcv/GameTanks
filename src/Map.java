import java.util.LinkedList;

public class Map implements Drawable {
    public static final int MAP_SIZE_X = 25;
    public static final int MAP_SIZE_Y = 25;
    private Block[][] mapBlocks;

    public Block getBlock(int x, int y){
        return mapBlocks[x][y];
    }

    Block[] getClosestBlocks(Position position){
        LinkedList<Block> blocks = new LinkedList<>();

        //...

        return (Block[])blocks.toArray();
    }

    public Map() {

    }

    @Override
    public void update(){

    }

    @Override
    public void lateUpdate() {

    }

    @Override
    public void display(){

    }

    @Override
    public void firstUpdate() {

    }
}
