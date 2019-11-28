package control;

import data.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.*;

public class Game {
    static final int SERVER_THREADS = 4;
    static final int MAX_PLAYERS = 4;
    static final int SERVER_SOCKET_NUM = 8100;
    static int threadnum = 0; //test

    private static GameManager gameManager;
    private ServerSocket serverSocket;

    class ClientTask implements Runnable{
        private int playerIndex;

        ClientTask(int index){
            playerIndex = index;
        }

        @Override
        public void run() {
            new UserKeyListener(playerIndex);
        }
    }

    class ClientGUITask implements Runnable {

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

    public static GameManager getGameManager(){
        return gameManager;
    }

    private void runClientMode() throws IOException, ClassNotFoundException, BrokenBarrierException, InterruptedException {
        gameManager = new GameManager();
        ExecutorService executorService = Executors.newFixedThreadPool(1);
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

        executorService.execute(new ClientTask(playerIndex));

        //oczekiwanie na otrzymanie danych poczatkowych gry i odbiór tych danych
        Object data;
        GameMessage message = new GameMessage("");
        do{
            data = inputStream.readObject();
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

    /*
        ///test przychodzacych daych
        System.out.println("###DANE_POCZATKOWE:");
        for(data.Player p : gameManager.getPlayers())
            System.out.println(p);
        for(data.Tank t : gameManager.getTanks())
            System.out.println(t);
        for(data.Bullet b : gameManager.getBullets())
            System.out.println(b);
        System.out.println("###END_DANE_POCZATKOWE\n");
        ///koniec testu
    */

        //potwierdzenie otrzymania danych poczatkowych i gotowosci do gry
        message.setMessage("READY");
        outputStream.writeObject(message);

        //oczekiwanie na sygnał startu gry od servera
        message = new GameMessage("");
        do{
            data = inputStream.readObject();
            if(data instanceof GameMessage)
                message = (GameMessage) data;
        }while(!message.getMessage().matches("START"));

        while(true){
        //for(int i=0; i<2; ++i){
            //wykrywanie poczynan gracz aw klase control.UserKeyListener
            //...

            //wysłanie wszystkich wiadomosci do servera
            try {
                synchronized (gameManager.getMessageQueue()){
                    for(GameMessage m : gameManager.getMessageQueue())
                        outputStream.writeObject(m);
                    gameManager.getMessageQueue().clear();
                }
                outputStream.writeObject(new GameMessage("DATA_END"));
            } catch (IOException e) {
                e.printStackTrace(); return;
            }

            //odebranie przeliczonych danych z servera i aktualizacja danych
            gameManager.getPlayers().clear();
            gameManager.getBullets().clear();
            gameManager.getTanks().clear();
            message = new GameMessage("");
            do{
                data = inputStream.readObject();
                if(data instanceof Player) {
                    gameManager.getPlayers().add((Player) data);
                } else if(data instanceof Tank){
                    gameManager.getTanks().add((Tank) data);
                } else if(data instanceof Bullet) {
                    gameManager.getBullets().add((Bullet) data);
                } else if(data instanceof GameMessage)
                    message = (GameMessage) data;
            }while(!message.getMessage().matches("DATA_END"));


            ///test przychodzacych daych
            System.out.println("###DANE_GRY:");
            for(Player p : gameManager.getPlayers())
                System.out.println(p);
            for(Tank t : gameManager.getTanks())
                System.out.println(t);
            for(Bullet b : gameManager.getBullets())
                System.out.println(b);
            System.out.println("###END_DANE_GRY\n");
            ///koniec testu


            //wstrzymanie czasu gry
            //...
        }

        //executorService.shutdownNow();
    }

    public static void main(String[] args){
        Game game = new Game();

        //wybór trybu aplikacji (client/server)
        try {
            game.runClientMode();
        } catch (InterruptedException | IOException | BrokenBarrierException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
