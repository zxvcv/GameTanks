public class Map implements Drawable, Changeable {
    //private static final MAP_SIZE_X =25;
    //private static final MAP_SIZE_Y =25;
    Block[][] mapGrid;
    Position position;

    public Map() {

    }

    @Override
    public Position getPosition(){
        return position;
    }

    @Override
    public void update(){

    }

    @Override
    public void display(){

    }

    public Block getBlock(int x, int y){
        return mapGrid[x][y];
    }
}
