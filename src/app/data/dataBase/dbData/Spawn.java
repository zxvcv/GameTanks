package app.data.dataBase.dbData;

import app.data.send.Position;

public class Spawn {
    private Position position;
    private String color;

    public Spawn(int x, int y, String color){
        this.position = new Position(x,y);
        this.color = color;
    }

    public Position getPosition() {
        return position;
    }

    public String getColor() {
        return color;
    }
}
