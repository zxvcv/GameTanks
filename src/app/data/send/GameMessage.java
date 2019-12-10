package app.data.send;

import app.abstractObjects.Sendable;

public class GameMessage implements Sendable {
    protected String message;
    protected int index;

    public GameMessage(String message){
        this.message = message;
        index = 0;
    }

    public GameMessage(String message, int playerIndex){
        this.message = message;
        this.index = playerIndex;
    }

    public GameMessage(GameMessage gameMessage){
        this.message = gameMessage.message;
        this.index = gameMessage.index;
    }

    public void setMessage(String message){
        this.message = message;
    }

    public String getMessage(){
        return message;
    }

    public void setIndex(int index){
        index = index;
    }

    public int getIndex(){
        return index;
    }
}
