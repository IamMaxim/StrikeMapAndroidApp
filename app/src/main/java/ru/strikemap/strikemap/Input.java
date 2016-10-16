package ru.strikemap.strikemap;

/**
 * Created by maxim on 15.10.2016.
 */

public class Input {
    public int left, right, up, down;
    public boolean cover, dead;

    /*
    7 null,
    6 null,
    5 dead
    4 cover
    3 left
    2 right
    1 down
    0 up
     */

    public Input(byte input) {
        left = (input >> 3 & 0b1);
        right = (input >> 2 & 0b1);
        up = (input & 0b1);
        down = (input >> 1 & 0b1);
        cover = (input >> 4 & 0b1) == 1;
        dead = (input >> 5 & 0b1) == 1;
    }

    @Override
    public String toString() {
        return "dead: " + dead + '\n' +
                "cover: " + cover + '\n' +
                "left: " + left + '\n' +
                "right: " + right + '\n' +
                "up: " + up + '\n' +
                "down: " + down;
    }
}
