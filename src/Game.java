import java.util.concurrent.*;

public abstract class Game {
    static final int SERVER_THREADS = 4;

    private static GameManager gameManager;
    private static ExecutorService executorService;

    class ServerTask implements Runnable {
        @Override
        public void run() {
            while(true){
                try{
                    gameManager.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                while(true){
                    if(gameManager.isDataQueueFilled() && gameManager.getDataQueue().isEmpty())
                       break;
                    else
                        gameManager.getDataQueue().poll().dataUpdate();
                }

                try {
                    gameManager.getBarrier().await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }finally {
                    gameManager.getBarrier().reset();
                }

                while(true){
                    if(gameManager.isCollisionQueueFilled() && gameManager.getCollisionQueue().isEmpty())
                        break;
                    else
                        gameManager.getCollisionQueue().poll().dataUpdate();
                }

                try {
                    gameManager.getBarrier().await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }finally {
                    gameManager.getBarrier().reset();
                }

                while(true){
                    if(gameManager.isAfterQueueFilled() && gameManager.getAfterQueue().isEmpty())
                        break;
                    else
                        gameManager.getAfterQueue().poll().dataUpdate();
                }

                try {
                    gameManager.getBarrier().await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }finally {
                    gameManager.getBarrier().reset();
                }
            }
        }
    }

    class ClientTask implements Runnable {
        @Override
        public void run() {
            while(true){
                try{
                    gameManager.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //...
            }
        }
    }

    public static GameManager getGameManager(){
        return gameManager;
    }

    private void runServerMode(){
        gameManager = new GameManager(true);
        executorService = Executors.newFixedThreadPool(SERVER_THREADS);

        for(int i=0; i<SERVER_THREADS; ++i)
            executorService.execute(new ServerTask());

        while(true){
            //bariera czasowa i transmitera danych
            gameManager.prepareCycle();
            gameManager.dataUpdate();
            gameManager.collisionUpdate();
            gameManager.afterUpdate();
            gameManager.closeCycle();
        }
    }

    private void runClientMode(){
        gameManager = new GameManager(false);

        while(true){
            //bariera czasowa i transmitera danych

        }
    }

    public static void main(String[] args){

        //wybór trybu aplikacji (client/server)
        //uruchomienie odpowiedzniego trybu
    }
}
