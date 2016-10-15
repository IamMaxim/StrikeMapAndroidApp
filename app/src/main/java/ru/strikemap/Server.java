package ru.strikemap;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

/**
 * Created by maxim on 14.10.2016.
 */
public class Server {
    private ServerSocket serverSocket;
    private ArrayList<Client> clients = new ArrayList<>();
    private ServerThread serverThread;
    private Thread connectionAcceptor;

    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        serverThread = new ServerThread();
        serverThread.start();

        System.out.println("Server initialized");

        connectionAcceptor = new Thread(() -> {
            while (!serverSocket.isClosed()) {
                try {
                    Socket s = serverSocket.accept();
                    Client client;
                    clients.add(client = new Client(s));
                    System.out.println("Client " + client.player.name + " connected");
                    serverThread.add(client.player);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        connectionAcceptor.start();
    }

    public class Client {
        //streams for transferring data
        public DataInputStream dis;
        public DataOutputStream dos;
        public Socket socket;
        //on-server player object
        public Player player = new Player(0, 0, 0);
        //update data in background
        ConnectionUpdateThread connectionUpdateThread;

        public Client(Socket socket) throws IOException {
            this.socket = socket;
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
            connectionUpdateThread = new ConnectionUpdateThread(this);
            connectionUpdateThread.start();
        }

        private class ConnectionUpdateThread extends Thread {
            private Client client;
            private Player player;

            ConnectionUpdateThread(Client client) {
                this.client = client;
                this.player = client.player;
            }

            @Override
            public void run() {
                //send init data to all clients
                

                while (!isInterrupted()) {
                    try {
                        //read updates from client
                        byte action = dis.readByte();
                        System.out.println("Received action " + action);
                        if (action == Net.ACTION_SET_STATE) {
                            Player.State state = Player.State.values()[dis.readInt()];
                            System.out.println("Setting state " + state.toString());
                            player.state = state;
                        } else if (action == Net.ACTION_SET_POS) {
                            player.x = dis.readFloat();
                            player.y = dis.readFloat();
                            System.out.println("Setting position to " + player.x + " " + player.y);
                        } else if (action == Net.ACTION_SET_TEAM) {
                            player.team = dis.readInt();
                            System.out.println("Setting team to " + player.team);
                        }
                    } catch (EOFException | SocketException e) {
                        //EOF means socket is closed
                        System.out.println("Removing client from server");

                        //cleanup player lists
                        serverThread.remove(client.player.id);
                        clients.remove(client);
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
