package app.data.send;

import app.abstractObjects.Sendable;

public class GameMessage implements Sendable {
    protected String message;
    protected int playerIndex;

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

    public int getPlayerIndex(){
        return playerIndex;
    }
}
