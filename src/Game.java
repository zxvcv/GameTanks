import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
                } catch (InterruptedException | BrokenBarrierException e) {
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
                } catch (InterruptedException | BrokenBarrierException e) {
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
                } catch (InterruptedException | BrokenBarrierException e) {
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
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            //}
        }
    }

    class ClientTask implements Runnable {

        @Override
        public void run() {
            //while(true){
                //blokada rysowania
                //...

                //rysowanie obiektów na ekranie
                //...
            //}
        }
    }

    class ServerDataTransmitter implements Runnable {
        private Player player;
        private Socket socket;
        private ObjectOutputStream outputStream;
        private ObjectInputStream inputStream;

        public ServerDataTransmitter(Player player, Socket socket) throws IOException {
            this.player = player;
            this.socket = socket;
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());
        }

        @Override
        public void run() {
            Player updatedPlayer;
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
                outputStream.writeObject(new GameMessage("DATA_END"));
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
                outputStream.writeObject(new GameMessage("START"));
            } catch (IOException e) {
                e.printStackTrace(); return;
            }

            //while(true){

            //odbieranie danych klienta
            try {
                message = (GameMessage)inputStream.readObject();
                if(message.getMessage().matches("UNACTIVE")){
                    //ustawienie klienta w stan nieaktywny
                    updatedPlayer = (Player)inputStream.readObject();
                    updatedPlayer.setState(Player.PlayerState.UNACTIVE);
                } else if (message.getMessage().matches("EXIT")){
                    //ustawienie klienta w stan konczacy jego prace
                    updatedPlayer = new Player();
                    updatedPlayer.setState(Player.PlayerState.EXIT);
                }else
                    updatedPlayer = (Player)inputStream.readObject();

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace(); return;
            }

            try {
                //bariera transmiterów (T4) - oczekiwanie na dane klientów
                gameManager.getBarrier(GameManager.BarrierNum.TRANSMITTER_BARRIER).await();
                gameManager.resetBarrier(GameManager.BarrierNum.TRANSMITTER_BARRIER);

                //bariera transmiterów (T5) - oczekiwanie na zakończenie obliczeń
                gameManager.getBarrier(GameManager.BarrierNum.TRANSMITTER_BARRIER).await();
                gameManager.resetBarrier(GameManager.BarrierNum.TRANSMITTER_BARRIER);
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace(); return;
            }

            //wysyłanie danych zaktualizowanych obiektów do klientów w stanie aktywnym i nieaktywnym (wszyscy gracze, czolgi i pociski)
            try {
                for(Player p : gameManager.getPlayers())
                    outputStream.writeObject(p);
                for(Tank t : gameManager.getTanks())
                    outputStream.writeObject(t);
                for(Bullet b : gameManager.getBullets())
                    outputStream.writeObject(b);
                outputStream.writeObject(new GameMessage("DATA_END"));
            } catch (IOException e) {
                e.printStackTrace(); return;
            }

            //usuwanie graczy wychodzących z gry i zmniejszenie bariery transmitterów
            try {
                if(updatedPlayer.getState() == Player.PlayerState.EXIT){
                    inputStream.close();
                    outputStream.close();
                    gameManager.getTanks().remove(player.getTank());
                    gameManager.getPlayers().remove(player);
                }
                //zmniejszenie wielkiości TRANSMITTER_BARRIER
                //...
            } catch (IOException e) {
                e.printStackTrace();
            }

            //bariera transmiterów (T6) - oczekiwanie na usuniecie niaktywnych klientów
            try {
                gameManager.getBarrier(GameManager.BarrierNum.TRANSMITTER_BARRIER).await();
                gameManager.resetBarrier(GameManager.BarrierNum.TRANSMITTER_BARRIER);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e){}


            //}
        }
    }

    public static GameManager getGameManager(){
        return gameManager;
    }

    private void runServerMode() throws BrokenBarrierException, InterruptedException {
        gameManager = new GameManager(true);
        ExecutorService executorService = Executors.newFixedThreadPool(SERVER_THREADS + MAX_PLAYERS);

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
        for(int i = playersNum; i > 0; --i){
            try {
                incoming = serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }

            //jezeli odebrano polaczenie to stwórz nowego gracza i transmiter danych
            newPlayer = new Player();
            gameManager.getPlayers().add(newPlayer);
            try {
                executorService.execute(new ServerDataTransmitter(newPlayer, incoming));
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


        //tworzenie czołgów dla graczy (plus dla testu kilka pocisków)
        for(Player player : gameManager.getPlayers()){
            Tank tank = new Tank(new Position(1f, 1f), new Rotation(1), player);
            gameManager.getTanks().add(tank);
            player.setTank(tank);
            for(int i=0; i<5; ++i)
                gameManager.getBullets().add(new Bullet(new Position(1f, 1f), new Rotation(1), tank));
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

        //while(true){

        //bariera transmiterów (T4) - oczekiwanie na dane klientów
        try {
            gameManager.getBarrier(GameManager.BarrierNum.TRANSMITTER_BARRIER).await();
            gameManager.resetBarrier(GameManager.BarrierNum.TRANSMITTER_BARRIER);
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }

        gameManager.prepareCycle();

        gameManager.getBarrier(GameManager.BarrierNum.PEROID_BARRIER).await();
        gameManager.getBarrier(GameManager.BarrierNum.PEROID_BARRIER).reset();

        gameManager.dataUpdate();

        gameManager.getBarrier(GameManager.BarrierNum.TASK_BARRIER).await();
        gameManager.getBarrier(GameManager.BarrierNum.TASK_BARRIER).reset();

        gameManager.collisionUpdate();

        gameManager.getBarrier(GameManager.BarrierNum.TASK_BARRIER).await();
        gameManager.getBarrier(GameManager.BarrierNum.TASK_BARRIER).reset();

        gameManager.afterUpdate();

        gameManager.getBarrier(GameManager.BarrierNum.TASK_BARRIER).await();
        gameManager.getBarrier(GameManager.BarrierNum.TASK_BARRIER).reset();

        gameManager.closeCycle();

        //bariera transmiterów (T5) - oczekiwanie na zakończenie obliczeń
        try {
            gameManager.getBarrier(GameManager.BarrierNum.TRANSMITTER_BARRIER).await();
            gameManager.resetBarrier(GameManager.BarrierNum.TRANSMITTER_BARRIER);
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }

        //bariera transmiterów (T6) - oczekiwanie na usuniecie niaktywnych klientów
        try {
            gameManager.getBarrier(GameManager.BarrierNum.TRANSMITTER_BARRIER).await();
            gameManager.resetBarrier(GameManager.BarrierNum.TRANSMITTER_BARRIER);
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }

        //}

        executorService.shutdown();
    }

    private void runClientMode() throws IOException, ClassNotFoundException, BrokenBarrierException, InterruptedException {
        gameManager = new GameManager(false);
        int playerIndex;

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

        ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

        //uruchomienie wątku GUI klienta (Client Task)
        //...

        //obiektu gracza w celu uzgodnienia indeksu gracza
        Player player = (Player) inputStream.readObject();
        playerIndex = player.getIndex();

        //oczekiwanie na otrzymanie danych poczatkowych gry i odbiór tych danych
        Sendable data;
        GameMessage message = new GameMessage("");
        do{
            data = (Sendable)inputStream.readObject();
            if(data instanceof Player)
                gameManager.getPlayers().add((Player)data);
            else if(data instanceof Tank)
                gameManager.getTanks().add((Tank)data);
            else if(data instanceof Bullet)
                gameManager.getBullets().add((Bullet)data);
            else if(data instanceof Map)
                gameManager.setMap((Map)data);
            else if(data instanceof GameMessage)
                message = (GameMessage) data;
        }while(!message.getMessage().matches("DATA_END"));

        //potwierdzenie otrzymania danych poczatkowych i gotowosci do gry
        message.setMessage("READY");
        outputStream.writeObject(message);

        //oczekiwanie na sygnał startu gry od servera
        message = new GameMessage("");
        do{
            data = (Sendable)inputStream.readObject();
            if(data instanceof GameMessage)
                message = (GameMessage) data;
        }while(!message.getMessage().matches("START"));

        while(true){
            //wykrycie poczynan gracza
            //...

            //wysłanie danych do servera
            message = new GameMessage("");
            try {
                outputStream.writeObject(message);
                outputStream.writeObject(player);
            } catch (IOException e) {
                e.printStackTrace(); return;
            }

            //odebranie przeliczonych danych z servera i aktualizacja danych
            do{
                data = (Sendable)inputStream.readObject();
                if(data instanceof Player) {
                    for(Player p : gameManager.getPlayers())
                        if(p.compareTo((Player) data) == 0)
                            p = (Player) data;
                } else if(data instanceof Tank){
                    for(Tank t : gameManager.getTanks())
                        if(t.compareTo((Tank) data) == 0)
                            t = (Tank) data;
                } else if(data instanceof Bullet) {
                    for (Bullet b : gameManager.getBullets())
                        if (b.compareTo((Bullet) data) == 0)
                            b = (Bullet) data;
                } else if(data instanceof GameMessage)
                    message = (GameMessage) data;
            }while(!message.getMessage().matches("DATA_END"));

            //wstrzymanie czasu gry
            //...
        }
    }

    public static void main(String[] args){
        Game game = new Game();

        //wybór trybu aplikacji (client/server)
        try {
            if (args[0].matches("server"))
                game.runServerMode();
            else if (args[0].matches("client"))
                game.runClientMode();
            else {
                System.out.println("[I] unknown parameter");
                System.out.println("[I] program shutdown");
            }
        } catch (InterruptedException | IOException | BrokenBarrierException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
