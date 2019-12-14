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

    LinkedList<Block> getClosestBlocks(Position position){
        LinkedList<Block> blocks = new LinkedList<>();

        int posX = (int)Math.floor(position.getX()/Block.BLOCK_SIZE);
        int posY = (int)Math.floor(position.getY()/Block.BLOCK_SIZE);
        int xx, yy;
        for(int x = -1; x <= 1; ++x){
            for(int y = -1; y <= 1; ++y){
                xx = posX + x;
                yy = posY + y;
                if(xx < 0 || xx >=20 || yy < 0 || yy >= 20)
                    continue;
                blocks.add(mapBlocks[posX + x][posY + y]);
            }
        }

        return blocks;
    }
}
