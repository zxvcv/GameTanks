public class Map implements Drawable {
    public static final int MAP_SIZE_X = 25;
    public static final int MAP_SIZE_Y = 25;
    Block[][] mapBlocks;

    public Block getBlock(int x, int y){
        return mapBlocks[x][y];
    }

    public Block[] getClosestBlocks(Position position){
        return null;
    }

    public Map() {

    }

    @Override
    public void update(GameManager gameManager){
        //nothing
    }

    @Override
    public void display(){

    }
}
