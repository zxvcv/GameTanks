package app;

import app.abstractObjects.GameObject;
import app.data.send.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.*;

public class Game {
    static final int SERVER_THREADS = 4;
    static final int MAX_PLAYERS = 4;
    static final int SERVER_SOCKET_NUM = 8100;
    static final int SERVER_CYCLE_TIME = 50;

    private static ExecutorService executorService = Executors.newFixedThreadPool(SERVER_THREADS + MAX_PLAYERS * 3 + 1);
    private static GameManager gameManager;
    private ServerSocket serverSocket;
    private Object timeLock = new Object();
    private static Indexer indexer = new Indexer();

    class ServerTask implements Runnable {

        @Override
        public void run() {
            Object data;

            while(true){
                GameObject go;
                try {
                    gameManager.getBarrier(GameManager.BarrierNum.PEROID_BARRIER).await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }

                try {
                    gameManager.getBarrier(GameManager.BarrierNum.TASK_BARRIER).await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }

                while(true){
                    if(gameManager.isDataQueueFilled() && gameManager.getDataQueue().isEmpty())
                       break;
                    else
                        go = gameManager.getDataQueue().poll();
                    if(go != null) {
                        go.dataUpdate();
                    }
                }

                try {
                    gameManager.getBarrier(GameManager.BarrierNum.TASK_BARRIER).await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }

                while(true){
                    if(gameManager.isCollisionQueueFilled() && gameManager.getCollisionQueue().isEmpty())
                        break;
                    else
                        go = gameManager.getCollisionQueue().poll();
                        if(go != null){
                            go.collisionUpdate();
                        }
                }

                try {
                    gameManager.getBarrier(GameManager.BarrierNum.TASK_BARRIER).await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }

                while(true){
                    if(gameManager.isAfterQueueFilled() && gameManager.getAfterQueue().isEmpty())
                        break;
                    else
                        go = gameManager.getAfterQueue().poll();
                    if(go != null){
                        go.afterUpdate();
                    }
                }

                try {
                    gameManager.getBarrier(GameManager.BarrierNum.TASK_BARRIER).await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class ServerConnector implements Runnable {
        private Player player;
        private Socket socket;
        private ObjectOutputStream outputStream;
        private ObjectInputStream inputStream;

        public ServerConnector(Player player, Socket socket) throws IOException {
            this.player = player;
            this.socket = socket;
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());
        }

        @Override
        public void run() {
            GameMessage message;

            //wysłanie obiektu gracza w celu uzgodnienia indeksu gracza
            try {
                outputStream.writeObject(player);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                //bariera transmiterów (T1) - oczekiwanie na dolaczenie wszystkich graczy
                gameManager.getBarrier(GameManager.BarrierNum.TRANSMITTER_BARRIER).await();
                gameManager.resetBarrier(GameManager.BarrierNum.TRANSMITTER_BARRIER);

                //bariera transmiterów (T2) - oczekiwanie na przygotowanie danych poczatkowych gry
                gameManager.getBarrier(GameManager.BarrierNum.TRANSMITTER_BARRIER).await();
                gameManager.resetBarrier(GameManager.BarrierNum.TRANSMITTER_BARRIER);
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace(); return;
            }

            //wyslanie danych poczatkowych gry
            try {
                for(Player p : gameManager.getPlayers())
                    outputStream.writeObject(p);
                for(Tank t : gameManager.getTanks())
                    outputStream.writeObject(t);
                for(Bullet b : gameManager.getBullets())
                    outputStream.writeObject(b);
                outputStream.writeObject(gameManager.getMap());
                outputStream.writeObject(new GameMessage("DATA_END", 0));
            } catch (IOException e) {
                e.printStackTrace(); return;
            }

            //oczekiwanie na potwierdzenie gotowości do gry
            try {
                do {
                    message = (GameMessage)inputStream.readObject();
                }while (!message.getMessage().matches("READY"));
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace(); return;
            }

            //bariera transmiterów (T3) - oczekiwanie na gotowość klientów
            try {
                gameManager.getBarrier(GameManager.BarrierNum.TRANSMITTER_BARRIER).await();
                gameManager.resetBarrier(GameManager.BarrierNum.TRANSMITTER_BARRIER);
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace(); return;
            }

            //wysłanie sygnału startu gry do wszystkich klientów
            try {
                outputStream.writeObject(new GameMessage("START", 0));
            } catch (IOException e) {
                e.printStackTrace(); return;
            }

            ExecutorService executorService = Game.getExecutorService();
            executorService.execute(new ServerDataTransmitterIn(player, inputStream));
            executorService.execute(new ServerDataTransmitterOut(player, outputStream));
        }
    }

    class ServerDataTransmitterIn implements Runnable {
        private Player player;
        private ObjectInputStream inputStream;

        public ServerDataTransmitterIn(Player player, ObjectInputStream inputStream) {
            this.player = player;
            this.inputStream = inputStream;
        }

        @Override
        public void run() {
            Object data;
            GameMessage message;

            while(true){
                try {
                    data = inputStream.readObject();
                    message = (GameMessage) data;
                    gameManager.getMessageQueueReceived().add(message);
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace(); return;
                }
            }

        }
    }

    class ServerDataTransmitterOut implements Runnable {
        private Player player;
        private ObjectOutputStream outputStream;

        public ServerDataTransmitterOut(Player player, ObjectOutputStream outputStream) {
            this.player = player;
            this.outputStream = outputStream;
        }

        @Override
        public void run() {
            Object data;
            GameMessage message;

            while(true){
                try {
                    if(!gameManager.getMessageQueueToSend().isEmpty() && gameManager.isSendPermit()){
                        message = gameManager.getMessageQueueToSend().poll();
                        outputStream.writeObject(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace(); return;
                }
            }
        }
    }

    class TimeManager implements Runnable {

        private Object lock;

        public TimeManager(Object lock){
            this.lock = lock;
        }

        @Override
        public void run() {
            long start;
            long elapsedTime;

            while (true) {
                try {
                    start = System.nanoTime();
                    gameManager.setTimeDelay(100);
                    Thread.sleep(SERVER_CYCLE_TIME);
                    synchronized (lock) {
                       lock.notifyAll();
                    }
                    elapsedTime = System.nanoTime() - start;
                    gameManager.setTimeDelay(elapsedTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static GameManager getGameManager(){
        return gameManager;
    }

    public static ExecutorService getExecutorService() {
        return executorService;
    }

    public static Indexer getIndexer() {
        return indexer;
    }

    private void runServerMode() throws BrokenBarrierException, InterruptedException {
        gameManager = new GameManager();

        for (int i = 0; i < SERVER_THREADS; ++i)
            executorService.execute(new ServerTask());

        //ustawianie ilosci graczy
        int playersNum;
        Scanner consoleIn = new Scanner(System.in);
        do{
            System.out.print("set number of players [1-4]: ");
            playersNum = consoleIn.nextInt();
            if(playersNum == 0) {
                System.out.println("[I] server shutdown");
                return;
            }else if(playersNum < 1 || playersNum > 4) {
                System.out.println("[I] incorrect number of players");
            }else{
                break;
            }
        }while(true);

        //tworzenie bariery transmitera danych dla odpowiedniej liczby graczy
        gameManager.setBarrier(GameManager.BarrierNum.TRANSMITTER_BARRIER, new CyclicBarrier(playersNum+1));

        //uruchamianie socet'a
        try {
            serverSocket = new ServerSocket(SERVER_SOCKET_NUM);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        //dołączanie graczy do gry
        Socket incoming;
        Player newPlayer;
        Tank newTank;
        for(int i = playersNum; i > 0; --i){
            try {
                incoming = serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }

            //jezeli odebrano polaczenie to stwórz nowego gracza i zainicjuj polaczenie
            newPlayer = new Player(indexer.getIndex());
            newTank = new Tank(new Position(50f, 50f), new Rotation(0), newPlayer, indexer.getIndex());
            gameManager.getPlayers().add(newPlayer);
            gameManager.getTanks().add(newTank);
            newPlayer.setTank(newTank);
            try {
                executorService.execute(new ServerConnector(newPlayer, incoming));
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        //bariera transmiterów (T1) - oczekiwanie na dolaczenie wszystkich graczy
        try {
            gameManager.getBarrier(GameManager.BarrierNum.TRANSMITTER_BARRIER).await();
            gameManager.resetBarrier(GameManager.BarrierNum.TRANSMITTER_BARRIER);
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }

        //bariera transmiterów (T2) - oczekiwanie na przygotowanie danych poczatkowych gry
        try {
            gameManager.getBarrier(GameManager.BarrierNum.TRANSMITTER_BARRIER).await();
            gameManager.resetBarrier(GameManager.BarrierNum.TRANSMITTER_BARRIER);
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }

        //bariera transmiterów (T3) - oczekiwanie na gotowość klientów
        try {
            gameManager.getBarrier(GameManager.BarrierNum.TRANSMITTER_BARRIER).await();
            gameManager.resetBarrier(GameManager.BarrierNum.TRANSMITTER_BARRIER);
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }

        //uruchomienie wątku obslugi czasu
        executorService.execute(new TimeManager(timeLock));

        while(true){
            gameManager.getBarrier(GameManager.BarrierNum.PEROID_BARRIER).await();
            gameManager.getBarrier(GameManager.BarrierNum.PEROID_BARRIER).reset();

            //odczyt przychodzacych widomosci i pobranie z nich danych
            gameManager.parseIncomingMessages();

            gameManager.prepareCycle();

            gameManager.getBarrier(GameManager.BarrierNum.TASK_BARRIER).await();
            gameManager.getBarrier(GameManager.BarrierNum.TASK_BARRIER).reset();

            gameManager.dataUpdate();

            gameManager.getBarrier(GameManager.BarrierNum.TASK_BARRIER).await();
            gameManager.getBarrier(GameManager.BarrierNum.TASK_BARRIER).reset();

            gameManager.collisionUpdate();

            gameManager.getBarrier(GameManager.BarrierNum.TASK_BARRIER).await();
            gameManager.getBarrier(GameManager.BarrierNum.TASK_BARRIER).reset();

            gameManager.afterUpdate();

            gameManager.getBarrier(GameManager.BarrierNum.TASK_BARRIER).await();
            gameManager.getBarrier(GameManager.BarrierNum.TASK_BARRIER).reset();

            gameManager.prepareOutputData();

            gameManager.closeCycle();

            //opoznienie petli tak zeby obliczanie danych odbywalo sie nie czesciej niz co 100ms
            synchronized (timeLock){
                timeLock.wait();
            }
        }

        //executorService.shutdown();
    }

    public static void main(String[] args){
        Game game = new Game();

        //wybór trybu aplikacji (client/server)
        try {
            game.runServerMode();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
    }
}
