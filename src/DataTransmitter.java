import java.util.LinkedList;

public class DataTransmitter {
    private LinkedList<GameObject> inputData;
    private LinkedList<GameObject> outputData;

    public GameObject pullInput(){
        return inputData.pollFirst();
    }

    public void addOutput(GameObject obj){
        outputData.addLast(obj);
    }

    public void clearOutput(){
        outputData.clear();
    }
}
