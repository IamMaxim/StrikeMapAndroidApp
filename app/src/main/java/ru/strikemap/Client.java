package ru.strikemap;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

/**
 * Created by maxim on 15.10.2016.
 */
public class Client {
    public Socket socket;
    DataOutputStream dos;
    DataInputStream dis;
    ClientThread clientThread;

    public HashMap<Integer, Player> players = new HashMap<>();

    public Client(String ip, int port) throws IOException {
        socket = new Socket(ip, port);
        dos = new DataOutputStream(socket.getOutputStream());
        dis = new DataInputStream(socket.getInputStream());
        clientThread = new ClientThread(this);

        System.out.println("Client initialized");

        //Debug functions
        /*
        Net.sendTeamToServer(dos, 1);
        Net.sendCoordToServer(dos, 2, 3);
        Net.sendStateToServer(dos, Player.State.DEAD);
        */

        /*
        System.out.println("Closing socket");
        socket.close();
        System.out.println("Socket closed");
        */
    }
}
