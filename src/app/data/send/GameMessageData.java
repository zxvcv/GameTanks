package app.data.send;

import app.abstractObjects.Sendable;

public class GameMessageData extends GameMessage implements Sendable {
    private Position position;
    private Rotation rotation;
    private int objectIndex;

    public GameMessageData(Tank tank, int playerIndex) {
        super("TANK", playerIndex);
        this.position = new Position(tank.getPosition());
        this.rotation = new Rotation(tank.getRotation());
        this.objectIndex = tank.getIndex();
    }

    public GameMessageData(Bullet bullet, int playerIndex) {
        super("BULLET", playerIndex);
        this.position = new Position(bullet.getPosition());
        this.rotation = new Rotation(bullet.getRotation());
        this.objectIndex = bullet.getIndex();
    }

    public Position getPosition() {
        return position;
    }

    public Rotation getRotation() {
        return rotation;
    }

    public int getObjectIndex() {
        return objectIndex;
    }
}
