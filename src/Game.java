import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;
import java.util.concurrent.*;

public class Game {
    static final int SERVER_THREADS = 4;
    static final int MAX_PLAYERS = 4;
    static final int SERVER_SOCKET_NUM = 8100;
    static int threadnum = 0; //test

    private static GameManager gameManager;
    private ServerSocket serverSocket;

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

            }
        }
    }

    class ServerDataTransmitter implements Runnable {
        private Player player;
        private Socket socket;

        public ServerDataTransmitter(Player player, Socket socket){
            this.player = player;
            this.socket = socket;
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
        ExecutorService executorService = Executors.newFixedThreadPool(SERVER_THREADS + MAX_PLAYERS);

        for (int i = 0; i < SERVER_THREADS; ++i)
            executorService.execute(new ServerTask());

        int playersNum;
        Scanner consoleIn = new Scanner(System.in);
        do{
            System.out.print("set number of players [1-4]: ");
            playersNum = consoleIn.nextInt();
            if(playersNum < 1 || playersNum > 4)
                System.out.println("[I] incorrect number of players");
            else if(playersNum == 0) {
                System.out.println("[I] server shutdown");
                return;
            }
            else{
                break;
            }
        }while(true);

        try {
            serverSocket = new ServerSocket(SERVER_SOCKET_NUM);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        //dołączanie graczy do gry
        Socket incoming;
        Player newPlayer;
        for(int i=playersNum; i>0; --i){
            try {
                incoming = serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }

            //jezeli odebrano polaczenie to stwórz nowego gracza i transmiter danych
            newPlayer = new Player();
            gameManager.getPlayers().add(newPlayer);
            executorService.execute(new ServerDataTransmitter(newPlayer, incoming));
        }

        //ustawienie bariery dla transmiterów
        gameManager.setBarrier(GameManager.BarrierNum.TRANSMITTER_BARRIER, new CyclicBarrier(gameManager.getPlayers().size()+1));

        //tworzenie czołgów dla graczy (plus dla testu kilka pocisków)
        for(Player player : gameManager.getPlayers()){
            Tank tank = new Tank(new Position(1f, 1f), new Rotation(1), player);
            gameManager.getTanks().add(tank);
            player.setTank(tank);
            for(int i=0; i<5; ++i)
                gameManager.getBullets().add(new Bullet(new Position(1f, 1f), new Rotation(1), tank));
        }

        //wyslanie danych poczatkowych gry do kazdego z graczy i oczekiwanie na potwierdzenie rozpoczecia
        //rozgrywki przez każdego z nich
        //...

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

        //łączenie z serverem
        Socket socket  = new Socket();
        Scanner consoleIn = new Scanner(System.in);
        String hostName;
        do{
            System.out.print("set host name, or 0 to exit: ");
            hostName = consoleIn.nextLine();
            if(hostName.matches("0")) {
                System.out.println("[I] server shutdown");
                return;
            }

            try {
                socket.connect(new InetSocketAddress(hostName, SERVER_SOCKET_NUM), 5000);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("[I] unable to connect to the server");
                continue;
            }
            break;
        } while(true);

        //oczekiwanie na otrzymanie danych poczatkowych gry
        //...

        //potwierdzenie otrzymania danych poczatkowych i gotowosci do gry
        //...

        while(true){
            //bariera czasowa i transmitera danych
            //...

            //cykl gry
            //...
        }
    }

    public static void main(String[] args){
        Game game = new Game();

        //wybór trybu aplikacji (client/server)
        if(args[0].matches("server"))
            game.runServerMode();
        else if(args[0].matches("client"))
            game.runClientMode();
        else {
            System.out.println("[I] unknown parameter");
            System.out.println("[I] program shutdown");
        }
    }
}
