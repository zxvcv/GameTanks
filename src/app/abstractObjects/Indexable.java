package app.abstractObjects;

public abstract class Indexable implements Comparable<Indexable>{
    private static int objectsIndex = 1;
    private int index;

    public Indexable(){
        index = objectsIndex;
        ++objectsIndex;
    }

    public int getIndex(){
        return index;
    }

    public void setIndex(Indexable obj){
        index = obj.getIndex();
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
