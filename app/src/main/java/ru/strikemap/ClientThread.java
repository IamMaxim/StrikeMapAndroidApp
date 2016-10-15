package ru.strikemap;

import java.io.EOFException;
import java.io.IOException;

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
                byte action = client.dis.readByte();
                if (action == Net.ACTION_ADD_CLIENT) {
                    Player player = new Player(0, 0, 0);
                    player.id = client.dis.readInt();
                    player.name = client.dis.readUTF();
                    client.players.put(player.id, player);
                } else if (action == Net.ACTION_REMOVE_CLIENT) {
                    client.players.remove(client.dis.readInt());
                } else if (action == Net.ACTION_SET_NAME) {
                    int id = client.dis.readInt();
                    String name = client.dis.readUTF();
                    client.players.get(id).name = name;
                } else if (action == Net.ACTION_SET_POS) {
                    int id = client.dis.readInt();
                    float x = client.dis.readFloat();
                    float y = client.dis.readFloat();
                    client.players.get(id).setPos(x, y);
                } else if (action == Net.ACTION_SET_TEAM) {
                    int id = client.dis.readInt();
                    int team = client.dis.readInt();
                    client.players.get(id).team = team;
                }
            } catch (EOFException e) {
                System.out.println("Client disconnected");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
