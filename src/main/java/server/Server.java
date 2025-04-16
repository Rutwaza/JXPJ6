package server;

import client.ClientHandler;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private ServerSocket serverSocket;
    private Socket socket;
    private static Server server;

    private List<ClientHandler> clients = new ArrayList<>();

    private Server() throws IOException {
        serverSocket = new ServerSocket(3001);
    }

    public static Server getInstance() throws IOException {
        return server!=null? server:(server=new Server());
    }

    public void makeSocket() {
        while (!serverSocket.isClosed()) {
            try {
                socket = serverSocket.accept();

                // Create input stream just to read the username first
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                String username = dis.readUTF();  // Client sends username first

                // Now pass the username to ClientHandler
                ClientHandler clientHandler = new ClientHandler(socket, clients, username);
                clients.add(clientHandler);

                System.out.println("Client connected: " + username + " (" + socket.toString() + ")");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
