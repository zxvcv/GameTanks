public abstract class Indexable {
    private static int objectsIndex = 0;
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
}
