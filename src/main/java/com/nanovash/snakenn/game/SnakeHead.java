package com.nanovash.snakenn.game;

import com.nanovash.snakenn.game.util.LossException;
import com.nanovash.snakenn.game.util.State;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RequiredArgsConstructor
public class SnakeHead {

    private @NonNull @Getter @Setter
    Location location;
    private @Getter List<Location> tail = new ArrayList<>();

    public HashMap<Location, State> move(GameModel model, HashMap<Location, State> locations) throws LossException {
        Location futureHeadLocation = new Location(location.getX() + model.getDirection().getX(), this.location.getY() + model.getDirection().getY(), model.isWalls());
        Location growLocation = tail.isEmpty() ? location : tail.get(tail.size() - 1);
        boolean grow = false;
        if(model.getFood().equals(futureHeadLocation))
            grow = true;
        for (int i = 0; i < tail.size(); i++) {
            if(futureHeadLocation.equals(tail.get(i)))
                throw new LossException();
            if(i != (tail.size() - 1)) {
                Location l = tail.get(tail.size() - i - 2);
                tail.set(tail.size() - i - 1, l);
                locations.put(l, State.TAIL);
            }
            else {
                tail.set(0, location);
                locations.put(location, State.TAIL);
            }
        }
        this.location = futureHeadLocation;
        locations.put(futureHeadLocation, State.HEAD);
        if(grow) {
            tail.add(growLocation);
            locations.put(growLocation, State.TAIL);
            model.generateFood();
        }
        locations.put(model.getFood(), State.FOOD);
        return locations;
    }
}
