public abstract class Block extends Shiftable implements GameObject{
    static final int BLOCK_SIZE = 40;
    private double hp;

    void hit(double _dmg){
        hp -= _dmg;
        if(hp <= 0)
            this.destroy();
    }

    @Override
    public void update() {
        //nothing
    }
}
