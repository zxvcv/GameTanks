package app.data.send;

import app.abstractObjects.Block;
import app.abstractObjects.Drawable;
import app.abstractObjects.Indexable;
import app.abstractObjects.Sendable;

import java.util.LinkedList;

public class Map extends Indexable implements Drawable, Sendable {
    public static final int MAP_SIZE = 25; //always square
    private Block[][] mapBlocks;

    public Map(int index) {
        super(index);
        mapBlocks = new Block[MAP_SIZE][MAP_SIZE];
        //always the same map
    }

    public Block getBlock(int x, int y){
        return mapBlocks[x][y];
    }

    Block[] getClosestBlocks(Position position){
        LinkedList<Block> blocks = new LinkedList<>();

        //...

        return (Block[])blocks.toArray();
    }

    @Override
    public void display(){

    }
}
