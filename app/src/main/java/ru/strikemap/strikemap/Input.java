package ru.strikemap.strikemap;

/**
 * Created by maxim on 15.10.2016.
 */

public class Input {
    boolean left, right, up, down, cover, dead;

    /*
    7 null,
    6 null,
    5 dead
    4 cover
    3 up
    2 right
    1 down
    0 left
     */

    public Input(byte input) {
        left = (input & 0b1) == 1;
        right = (input >> 2 & 0b1) == 1;
        up = (input >> 3 & 0b1) == 1;
        down = (input >> 1 & 0b1) == 1;
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
