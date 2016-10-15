package ru.strikemap;

import java.util.HashMap;

/**
 * Created by maxim on 14.10.2016.
 */
public class ServerThread extends Thread {
    private final HashMap<Integer, Player> players = new HashMap<>();

    //synchronized access to hashMap
    public void add(Player player) {
        synchronized (players) {
            players.put(player.id, player);
        }
    }

    //synchronized access to hashMap
    public void remove(int id) {
        synchronized (players) {
            players.remove(id);
        }
    }

    //update all players
    @Override
    public void run() {
        while (!isInterrupted()) {
            //synchronized access to hashMap
            synchronized (players) {
                players.values().forEach(Player::update);
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
