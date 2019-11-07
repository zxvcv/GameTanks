import java.util.concurrent.*;

public class Game {
    static final int SERVER_THREADS = 4;

    private static GameManager gameManager;
    private static ExecutorService executorService;

    class ServerTask implements Runnable {
        @Override
        public void run() {
            //while(true){
                GameObject go;
                try {
                    gameManager.getBarrier(GameManager.BarrierNum.PEROID_BARRIER).await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }finally {

                }

                while(true){
                    if(gameManager.isDataQueueFilled() && gameManager.getDataQueue().isEmpty())
                       break;
                    else
                        go = gameManager.getDataQueue().poll();
                    if(go != null)
                        go.dataUpdate();
                }

                try {
                    gameManager.getBarrier(GameManager.BarrierNum.TASK_BARRIER).await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }finally {
                    gameManager.getBarrier(GameManager.BarrierNum.TASK_BARRIER).reset();
                }

                while(true){
                    if(gameManager.isCollisionQueueFilled() && gameManager.getCollisionQueue().isEmpty())
                        break;
                    else
                        go = gameManager.getCollisionQueue().poll();
                        if(go != null)
                            go.collisionUpdate();
                }

                try {
                    gameManager.getBarrier(GameManager.BarrierNum.TASK_BARRIER).await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }finally {
                    gameManager.getBarrier(GameManager.BarrierNum.TASK_BARRIER).reset();
                }

                while(true){
                    if(gameManager.isAfterQueueFilled() && gameManager.getAfterQueue().isEmpty())
                        break;
                    else
                        go = gameManager.getAfterQueue().poll();
                    if(go != null)
                        go.afterUpdate();
                }

                try {
                    gameManager.getBarrier(GameManager.BarrierNum.TASK_BARRIER).await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }finally {
                    gameManager.getBarrier(GameManager.BarrierNum.TASK_BARRIER).reset();
                }
            //}
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

        //while(true){
            //bariera czasowa i transmitera danych
            /*try {
                //??
                gameManager.getBarrier(GameManager.BarrierNum.TRANSMITTER_BARRIER).await(100, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {

            } finally {
                gameManager.getBarrier(GameManager.BarrierNum.TRANSMITTER_BARRIER).reset();
            }*/

            gameManager.prepareCycle();
            gameManager.dataUpdate();
            gameManager.collisionUpdate();
            gameManager.afterUpdate();
            gameManager.closeCycle();
        //}
    }

    private void runClientMode(){
        gameManager = new GameManager(false);

        while(true){
            //bariera czasowa i transmitera danych

        }
    }

    public static void main(String[] args){
        Game game = new Game();
        //wybór trybu aplikacji (client/server)

        //uruchomienie odpowiedzniego trybu
        game.runServerMode();
    }
}
