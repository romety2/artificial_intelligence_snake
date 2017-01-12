package com.nanovash.snakenn.game;

import com.nanovash.snakenn.game.util.LossException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode(of={"x", "y"})
public class Location
{
    private @Getter @Setter int x;
    private @Getter @Setter int y;

    public Location(int x, int y) throws LossException
    {
        this.x = x;
        this.y = y;
        if (x < 0 || x >= GameModel.SIDE || y < 0 || y >= GameModel.SIDE)
            throw new LossException();
    }

}