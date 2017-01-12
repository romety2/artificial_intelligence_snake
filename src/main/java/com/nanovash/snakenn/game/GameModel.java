package com.nanovash.snakenn.game;

import com.nanovash.snakenn.game.util.LossException;
import com.nanovash.snakenn.game.util.State;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class GameModel
{

    private @Getter SnakeHead snake;
    private @Getter Direction direction = Direction.UP;
    private Direction pendingDirection = Direction.UP;
    private @Getter @Setter Location food;
    private @Getter @Setter boolean walls = false;
    private @Getter boolean lost;

    public final static int SIDE = 10;

    Random random = new Random();

    public HashMap<Location, State> start()
    {
        lost = false;
        HashMap<Location, State> locations = new HashMap<>();
        try
        {
            snake = new SnakeHead(new Location(2, 2, walls));
        }
        catch (LossException le)
        {
        }
        locations.put(snake.getLocation(), State.HEAD);
        generateFood();
        locations.put(food, State.FOOD);
        return locations;
    }

    public HashMap<Location, State> update()
    {
        HashMap<Location, State> locations = new HashMap<>();
        direction = pendingDirection;
        try
        {
            locations = snake.move(this, locations);
        }
        catch (LossException e)
        {
            lost = true;
            return null;
        }
        return locations;
    }

    public void setPendingDirection(Direction d)
    {
        if(!direction.isOpposite(d))
            pendingDirection = d;
    }

    public void generateFood()
    {
        List<Location> locations = new ArrayList<>();
        for (int i = 0; i < SIDE; i++)
        {
            for (int j = 0; j < SIDE; j++)
            {
                boolean isntTailPiece = true;
                Location l = null;
                try
                {
                    l = new Location(i, j, walls);
                }
                catch (LossException ignored)
                {
                }
                for (Location tailPiece : snake.getTail())
                    if(l.equals(tailPiece))
                    {
                        isntTailPiece = false;
                        break;
                    }
                if(isntTailPiece && (!l.equals(snake.getLocation())) && (!l.equals(food)))
                    locations.add(l);
            }
        }
        food = locations.get(random.nextInt(locations.size()));
    }
}
