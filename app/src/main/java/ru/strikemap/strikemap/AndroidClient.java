package ru.strikemap.strikemap;

import java.io.IOException;

import ru.strikemap.Client;
import ru.strikemap.Net;
import ru.strikemap.Player;

/**
 * Created by maxim on 15.10.2016.
 */

public class AndroidClient {
    private Client client;
    private Player.State lastState;

    public AndroidClient() {

    }

    public void parseInput(Input input) {
        if (input.cover) {
            try {
                if (client.player.state != lastState) {
                    client.player.state = Player.State.COVER;
                    Net.sendStateToServer(client.dos, client.player.state);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }
}