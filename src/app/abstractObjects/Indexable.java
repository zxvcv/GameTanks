package app.abstractObjects;

import java.io.Serializable;

public abstract class Indexable implements Comparable<Indexable>, Serializable {
    private int index;

    public Indexable(int index){
        this.index = index;
    }

    public int getIndex(){
        return index;
    }

    public void setIndex(int index){
        this.index = index;
    }

    public static boolean compareIndex(Indexable obj1, Indexable obj2){
        if(obj1.getIndex() == obj2.getIndex())
            return true;
        else
            return false;
    }

    @Override
    public int compareTo(Indexable o) {
        if(this.getIndex() == o.getIndex())
            return 0;
        else if(this.getIndex() > o.getIndex())
            return 1;
        else
            return -1;
    }
}
