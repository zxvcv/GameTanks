public class Tank extends Transformable implements GameObject, Shootable, CollisionManager{
    public static final double TANK_SPEED = 1.0;
    private double hp;

    public double getHp(){
        return hp;
    }

    public void hit(double _dmg){
        hp -= _dmg;
        if(hp <= 0)
            this.destroy();
    }

    @Override
    public void destroy() {

    }

    @Override
    public void display() {

    }

    @Override
    public void update() {

    }

    @Override
    public void move(double _x, double _y) {
        position.move(_x, _y);
    }

    @Override
    public void rotate(double _rotate) {
        rotation.rotate(_rotate);
    }

    @Override
    public Bullet shoot(Rotation rot, Position pos) {
        return null;
    }

    @Override
    public GameObject[] checkCollisions(Map map, Tank[] tanks, Bullet[] bullets) {
        //!!!!trzeba sprawdzac kolizje na nowych danych ale starych nie usuwac w razie gdyby pojawila sie kolizja

        Block[] blocks = map.getClosestBlocks(this.position);
        for(Bullet b : bullets){
            //sprawdzenie kolizji z pociskami
            // (jezeli zniszczony to nie trzeba sprawdzac kolizji dalej)
        }
        for(Block b : blocks){
            //sprawdzenie kolizji z blokami
        }
        for(Tank t : tanks){
            //sprawdzanie kolizji z innymi czolgami
        }
        return null;
    }
}
