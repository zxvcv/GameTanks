package app.data.send;

public class Indexer {
    private int actualIndex;

    public Indexer(){
        this.actualIndex = 1;
    }

    public int getIndex(){
        return ++actualIndex;
    }
}
