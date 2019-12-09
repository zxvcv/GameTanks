package app.data.send;

import app.abstractObjects.Sendable;

public class GameMessage implements Sendable {
    private String message;
    private int playerIndex;

    public GameMessage(String message){
        this.message = message;
        playerIndex = 0;
    }

    public GameMessage(String message, int playerIndex){
        this.message = message;
        this.playerIndex = playerIndex;
    }

    public GameMessage(GameMessage gameMessage){
        this.message = gameMessage.message;
        this.playerIndex = gameMessage.playerIndex;
    }

    public void setMessage(String message){
        this.message = message;
    }

    public String getMessage(){
        return message;
    }

    public void setOwnerIndex(int index){
        playerIndex = index;
    }

    public int getOwnerIndex(){
        return playerIndex;
    }
}
