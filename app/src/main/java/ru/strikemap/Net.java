package ru.strikemap;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by maxim on 14.10.2016.
 */
public class Net {
    private static int ACTION_TO_ADD = 0;

    //codes for actions
    public static final int
            ACTION_SET_STATE = ACTION_TO_ADD++,
            ACTION_SET_POS = ACTION_TO_ADD++,
            ACTION_SET_TEAM = ACTION_TO_ADD++,
            ACTION_ADD_CLIENT = ACTION_TO_ADD++,
            ACTION_REMOVE_CLIENT = ACTION_TO_ADD++,
            ACTION_SET_NAME = ACTION_TO_ADD++;

    public static void sendStateToServer(DataOutputStream dos, Player.State state) throws IOException {
        dos.writeByte(ACTION_SET_STATE);
        dos.writeInt(state.ordinal());
    }

    public static void sendCoordToServer(DataOutputStream dos, float x, float y) throws IOException {
        dos.writeByte(ACTION_SET_POS);
        dos.writeFloat(x);
        dos.writeFloat(y);
    }

    public static void sendTeamToServer(DataOutputStream dos, int team) throws IOException {
        dos.writeByte(ACTION_SET_TEAM);
        dos.writeInt(team);
    }

    public static void sendClientConnected(DataOutputStream dos, int id, String name) throws IOException {
        dos.writeByte(Net.ACTION_ADD_CLIENT);
        dos.writeInt(id);
        dos.writeUTF(name);
    }

    public static void sendRemoveClient(DataOutputStream dos, int id) throws IOException {
        dos.writeByte(Net.ACTION_REMOVE_CLIENT);
        dos.writeInt(id);
    }
}
