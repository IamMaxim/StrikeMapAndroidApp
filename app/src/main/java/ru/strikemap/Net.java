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
        System.out.println("sending ADD_CLIENT");
        dos.writeByte(Net.ACTION_ADD_CLIENT);
        dos.writeInt(id);
        dos.writeUTF(name);
    }

    public static void sendNameToServer(DataOutputStream dos, String name) throws IOException {
        dos.writeByte(Net.ACTION_SET_NAME);
        dos.writeUTF(name);
    }

    public static void sendNameToClient(DataOutputStream dos, int id, String name) throws IOException {
        dos.writeByte(Net.ACTION_SET_NAME);
        dos.writeInt(id);
        dos.writeUTF(name);
    }

    public static void sendRemoveClient(DataOutputStream dos, int id) throws IOException {
        dos.writeByte(Net.ACTION_REMOVE_CLIENT);
        dos.writeInt(id);
    }

    public static void sendStateToClient(DataOutputStream dos, int id, Player.State state) throws IOException {
        dos.writeByte(Net.ACTION_SET_STATE);
        dos.writeInt(id);
        dos.writeInt(state.ordinal());
    }

    public static void sendCoordsToClient(DataOutputStream dos, int id, float x, float y) throws IOException {
        dos.writeByte(Net.ACTION_SET_POS);
        dos.writeInt(id);
        dos.writeFloat(x);
        dos.writeFloat(y);
    }

    public static void sendTeamToClient(DataOutputStream dos, int id, int team) throws IOException {
        dos.writeByte(Net.ACTION_SET_TEAM);
        dos.writeInt(id);
        dos.writeInt(team);
    }
}
