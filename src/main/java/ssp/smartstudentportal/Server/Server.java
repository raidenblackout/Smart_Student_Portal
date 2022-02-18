package ssp.smartstudentportal.Server;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//create a server listening on port 8080
public class Server {
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(50);
    public static final ArrayList<ClientHandler> clients = new ArrayList<>();

    public Server() {
        Socket socket;
        ServerSocket serverSocket;
        try {
            //create a new server socket on port 8080
            serverSocket = new ServerSocket(8080);
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        System.out.println(clients.size());
                        try {
                            Thread.sleep(1000);
                        }catch (InterruptedException e){
                            e.printStackTrace();
                        }
                    }
                }
            });
            t.setDaemon(true);
            t.start();
            //listen for a connection
            while (true) {
                System.out.println("Waiting for connection...");
                socket = serverSocket.accept();
                System.out.println("Connection received");
                //create a new client handler
                ClientHandler clientHandler = new ClientHandler(socket);
                //add the client handler to the list of clients
                clients.add(clientHandler);
                threadPool.execute(clientHandler);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Server();
    }
}
