package app.data.send;

import app.abstractObjects.Block;
import app.abstractObjects.Indexable;
import app.abstractObjects.Sendable;

import java.util.LinkedList;

public class Map extends Indexable implements Sendable {
    protected Block[][] mapBlocks;

    public Map(Block[][] blocks, int index) {
        super(index);
        this.mapBlocks = blocks;
    }

    public Block getBlock(int x, int y){
        return mapBlocks[x][y];
    }

    public Block[][] getBlocks(){
        return mapBlocks;
    }

    Block[] getClosestBlocks(Position position){
        LinkedList<Block> blocks = new LinkedList<>();

        //...

        return (Block[])blocks.toArray();
    }
}
