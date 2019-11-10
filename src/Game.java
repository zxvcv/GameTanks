import java.util.concurrent.*;

public class Game {
    static final int SERVER_THREADS = 4;
    static final int MAX_PLAYERS = 4;
    static int threadnum = 0; //test

    private static GameManager gameManager;
    private static ExecutorService executorService;

    class ServerTask implements Runnable {
        int num = 0; //test

        public ServerTask(){ //test
            num = threadnum++;
        }

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
                }

                while(true){
                    if(gameManager.isDataQueueFilled() && gameManager.getDataQueue().isEmpty())
                       break;
                    else
                        go = gameManager.getDataQueue().poll();
                    if(go != null) {
                        go.setThread(num); //test
                        go.dataUpdate();
                    }
                }

                try {
                    gameManager.getBarrier(GameManager.BarrierNum.TASK_BARRIER).await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }

                while(true){
                    if(gameManager.isCollisionQueueFilled() && gameManager.getCollisionQueue().isEmpty())
                        break;
                    else
                        go = gameManager.getCollisionQueue().poll();
                        if(go != null){
                            go.setThread(num); //test
                            go.collisionUpdate();
                        }
                }

                try {
                    gameManager.getBarrier(GameManager.BarrierNum.TASK_BARRIER).await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }

                while(true){
                    if(gameManager.isAfterQueueFilled() && gameManager.getAfterQueue().isEmpty())
                        break;
                    else
                        go = gameManager.getAfterQueue().poll();
                    if(go != null){
                        go.setThread(num); //test
                        go.afterUpdate();
                    }
                }

                try {
                    gameManager.getBarrier(GameManager.BarrierNum.TASK_BARRIER).await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
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

    class ServerDataTransmitter implements Runnable {
        private Player player;

        public ServerDataTransmitter(Player player){
            this.player = player;
        }

        @Override
        public void run() {
            //watek wymiany danych z graczem player
            //while(true){
            try {
                gameManager.getBarrier(GameManager.BarrierNum.TRANSMITTER_BARRIER).await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
            //}
        }
    }

    public static GameManager getGameManager(){
        return gameManager;
    }

    private void runServerMode() {
        gameManager = new GameManager(true);
        executorService = Executors.newFixedThreadPool(SERVER_THREADS + MAX_PLAYERS);

        for (int i = 0; i < SERVER_THREADS; ++i)
            executorService.execute(new ServerTask());

        //dołączanie graczy do gry
        Player p = new Player();
        gameManager.getPlayers().add(p);
        p = new Player();
        gameManager.getPlayers().add(p);

        //ustawienie bariery dla transmiterów
        gameManager.setBarrier(GameManager.BarrierNum.TRANSMITTER_BARRIER, new CyclicBarrier(gameManager.getPlayers().size()+1));

        //tworzenie czołgów i transmiterów danych dla graczy (plus dla testu kilka pocisków)
        for(Player player : gameManager.getPlayers()){
            executorService.execute(new ServerDataTransmitter(player));
            Tank tank = new Tank(new Position(1f, 1f), new Rotation(1), player);
            gameManager.getTanks().add(tank);
            player.setTank(tank);
            for(int i=0; i<5; ++i)
                gameManager.getBullets().add(new Bullet(new Position(1f, 1f), new Rotation(1), tank));
        }


        //while(true){

        //bariera transmitera danych
        try {
            gameManager.getBarrier(GameManager.BarrierNum.TRANSMITTER_BARRIER).await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }finally {
            gameManager.getBarrier(GameManager.BarrierNum.TRANSMITTER_BARRIER).reset();
        }

        gameManager.prepareCycle();

        try {
            gameManager.getBarrier(GameManager.BarrierNum.PEROID_BARRIER).await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }finally {
            gameManager.getBarrier(GameManager.BarrierNum.PEROID_BARRIER).reset();
        }

        gameManager.dataUpdate();

        try {
            gameManager.getBarrier(GameManager.BarrierNum.TASK_BARRIER).await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        } finally {
            gameManager.getBarrier(GameManager.BarrierNum.TASK_BARRIER).reset();
        }

        gameManager.collisionUpdate();

        try {
            gameManager.getBarrier(GameManager.BarrierNum.TASK_BARRIER).await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        } finally {
            gameManager.getBarrier(GameManager.BarrierNum.TASK_BARRIER).reset();
        }

        gameManager.afterUpdate();

        try {
            gameManager.getBarrier(GameManager.BarrierNum.TASK_BARRIER).await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        } finally {
            gameManager.getBarrier(GameManager.BarrierNum.TASK_BARRIER).reset();
        }

        gameManager.closeCycle();
        //}

        executorService.shutdown();
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
