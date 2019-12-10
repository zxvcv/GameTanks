package app.data.send;

import app.abstractObjects.Sendable;

public class GameMessageData extends GameMessage implements Sendable {
    private Position position;
    private Rotation rotation;

    public GameMessageData(Tank tank) {
        super("TANK");
        this.position = tank.getPosition();
        this.rotation = tank.getRotation();
        this.index = tank.getIndex();
    }

    public GameMessageData(Bullet bullet) {
        super("BULLET");
        this.position = bullet.getPosition();
        this.rotation = bullet.getRotation();
        this.index = bullet.getIndex();
    }

    public Position getPosition() {
        return position;
    }

    public Rotation getRotation() {
        return rotation;
    }
}
