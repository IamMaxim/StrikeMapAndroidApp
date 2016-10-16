package ru.strikemap;

/**
 * Created by maxim on 14.10.2016.
 */
public class Player {
    private static int ID_TO_ADD = 0;

    public String name;
    public int id;
    public float x, y;
    public State state;
    public int team;

    public void setPos(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Player(int team, float x, float y) {
        id = ID_TO_ADD++;

        //avoid int overflow
        if (ID_TO_ADD == Integer.MAX_VALUE) ID_TO_ADD = 0;

        this.team = team;
        this.x = x;
        this.y = y;
        state = State.NORMAL;
        this.name = "Player" + (int)(Math.random() * 1000);
    }

    //this method is called on server in loop
    public void update() {
        //System.out.println("Updating player " + id + " with coords: [" + x + ", " + y + "] and team " + team);
    }

    public enum State {
        NORMAL,
        DEAD,
        COVER
    }
}