package com.nanovash.snakenn.neuralnetwork;

import com.nanovash.snakenn.neuralnetwork.util.Connection;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class Neuron
{

    private @Getter List<Connection> connections = new ArrayList<>();
    private @Getter @Setter double value = 0;
    private @NonNull @Getter @Setter double threshold;

    /**
     * Przekacuje wartosc sieci do podlaczonych neuronow jesli wartosc jest wieksza niz prog
     */
    public void passValues() {
        if(value > threshold)
            for (Connection conn : connections)
                conn.getReceiver().setValue(conn.getReceiver().getValue() + value * conn.getWeight());
    }

    public void addConnection(Connection c)
    {
        connections.add(c);
    }
}
