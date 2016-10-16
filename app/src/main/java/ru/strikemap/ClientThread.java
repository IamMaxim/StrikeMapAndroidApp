package ru.strikemap;

import java.io.EOFException;
import java.io.IOException;
import java.net.SocketException;

/**
 * Created by maxim on 15.10.2016.
 */
public class ClientThread extends Thread {
    private Client client;

    public ClientThread(Client client) {
        this.client = client;
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                try {
                    Net.sendCoordToServer(client.dos, client.player.x, client.player.y);
                } catch (NullPointerException e) {}
                byte action = client.dis.readByte();
                System.out.println("Received action: " + action);
                if (action == Net.ACTION_ADD_CLIENT) {
                    Player player = new Player(0, 0, 0);
                    player.id = client.dis.readInt();
                    player.name = client.dis.readUTF();
                    //System.out.println("adding player " + player.name + " ");
                    client.players.put(player.id, player);
                } else if (action == Net.ACTION_REMOVE_CLIENT) {
                    int id = client.dis.readInt();
                    Player player = client.players.get(id);
                    //System.out.println("removing player " + player.name);
                    client.players.remove(id);
                } else if (action == Net.ACTION_SET_NAME) {
                    int id = client.dis.readInt();
                    String name = client.dis.readUTF();
                    client.players.get(id).name = name;
                } else if (action == Net.ACTION_SET_POS) {
                    int id = client.dis.readInt();
                    client.player.prevX = client.player.x;
                    client.player.prevY = client.player.y;
                    float x = client.dis.readFloat();
                    float y = client.dis.readFloat();
                    client.players.get(id).setPos(x, y);
                    client.lastPosUpdates.put(id, System.currentTimeMillis());
                } else if (action == Net.ACTION_SET_TEAM) {
                    int id = client.dis.readInt();
                    int team = client.dis.readInt();
                    client.players.get(id).team = team;
                } else if (action == Net.ACTION_SET_STATE) {
                    int id = client.dis.readInt();
                    Player.State state = Player.State.values()[client.dis.readInt()];
                    client.players.get(id).state = state;
                }
            } catch (IOException e) {
                //e.printStackTrace();
                try {
                    System.out.println("closing socket");
                    client.socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                break;
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }
}
