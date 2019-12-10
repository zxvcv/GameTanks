package app.data.send;

import java.io.Serializable;
import java.util.LinkedList;

public class KeyLog implements Serializable {
    private int playerIndex;
    private LinkedList<Boolean> values;
    private LinkedList<String> keys;
    private int countPressed;

    public KeyLog(int playerIndex){
        this.playerIndex = playerIndex;
        this.values = new LinkedList<>();
        this.keys = new LinkedList<>();
        keys.add("W"); values.add(false);
        keys.add("A"); values.add(false);
        keys.add("S"); values.add(false);
        keys.add("D"); values.add(false);
        keys.add("SPACE"); values.add(false);
        this.countPressed = 0;
    }

    public void setKeyState(String key, boolean state) {
        int i;

        for (i = 0; i < keys.size(); ++i){
            if (keys.get(i).matches(key))
                break;
        }
        if(i == keys.size())
            return;

        boolean val = values.get(i);
        values.set(i, state);

        if(state && !val)
            this.countPressed++;
        else if(!state && val)
            this.countPressed--;
    }

    public int getPlayerIndex() {
        return playerIndex;
    }

    public String getPressedKey(){
        if(this.countPressed != 1)
            return "";
        else{
            int i;
            for(i = 0; i<values.size(); ++i) {
                if (values.get(i))
                    break;
            }
            return keys.get(i);
        }
    }
}
