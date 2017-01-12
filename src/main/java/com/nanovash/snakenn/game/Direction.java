package com.nanovash.snakenn.game;

import lombok.Getter;

public enum Direction
{
    UP(0, -1),
    DOWN(0, 1),
    LEFT(-1, 0),
    RIGHT(1, 0);

    private @Getter int x;
    private @Getter int y;

    Direction(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public boolean isOpposite(Direction d)
    {
        return (x != 0 && (x + d.getX() == 0)) || (y != 0 && (y + d.getY() == 0));
    }

    public static Direction getRelativeLeft(Direction d)
    {
        switch (d)
        {
            case UP:
                return LEFT;
            case LEFT:
                return DOWN;
            case DOWN:
                return RIGHT;
            case RIGHT:
                return UP;
        }
        return null;
    }

    public static Direction getRelativeRight(Direction d)
    {
        switch (d)
        {
            case UP:
                return RIGHT;
            case RIGHT:
                return DOWN;
            case DOWN:
                return LEFT;
            case LEFT:
                return UP;
        }
        return null;
    }
}
