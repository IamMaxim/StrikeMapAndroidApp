package ru.strikemap;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.util.HashMap;

/**
 * Created by maxim on 15.10.2016.
 */
public class Client implements Serializable {
    public Socket socket;
    public DataOutputStream dos;
    public DataInputStream dis;
    public ClientThread clientThread;
    public Player player;

    public volatile HashMap<Integer, Player> players = new HashMap<>();
    public HashMap<Integer, Long> lastPosUpdates = new HashMap<>();
    public HashMap<Integer, Long> prevPosUpdates = new HashMap<>();

    public Client(final String ip, final int port) throws IOException {
        System.out.println("trying to connect to " + ip + ":" + port);
        socket = new Socket(ip, port);
        dos = new DataOutputStream(socket.getOutputStream());
        dis = new DataInputStream(socket.getInputStream());
        clientThread = new ClientThread(Client.this);
        clientThread.start();
        System.out.println("Client initialized");
    }
}
