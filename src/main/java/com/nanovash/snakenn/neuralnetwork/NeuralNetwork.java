package com.nanovash.snakenn.neuralnetwork;

import com.nanovash.snakenn.neuralnetwork.util.Connection;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NeuralNetwork
{

    private @Getter List<List<Neuron>> neuralNetwork = new ArrayList<>();
    private @Getter long id;

    /**
     * Tworzy losowa sieœ neuronowa
     */
    public NeuralNetwork()
    {
        id = System.currentTimeMillis();
        List<Neuron> output = new ArrayList<>();
        output.add(new Neuron(-2));

        List<Neuron> hidden = new ArrayList<>();
        for (int i = 0; i < 3; i++)
        {
            Neuron neuron = new Neuron(randomValue());
            neuron.addConnection(new Connection(output.get(0), randomValue()));
            hidden.add(neuron);
        }

        List<Neuron> input = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Neuron neuron = new Neuron(-2);
            for (int j = 0; j < 3; j++)
                neuron.addConnection(new Connection(hidden.get(j), randomValue()));
            input.add(neuron);
        }

        neuralNetwork.add(input);
        neuralNetwork.add(hidden);
        neuralNetwork.add(output);
    }

    /**
     * Tworzy sieæ neuronow¹ w oparciu o List<Double>
     * @param network siec w formie listy List<Double>
     * @throws IndexOutOfBoundsException
     */
    public NeuralNetwork(List<Double> network) throws IndexOutOfBoundsException
    {
        id = System.currentTimeMillis();
        List<Neuron> output = new ArrayList<>();
        output.add(new Neuron(-2));

        List<Neuron> hidden = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Neuron neuron = new Neuron(network.get(12 + i));
            neuron.addConnection(new Connection(output.get(0), network.get(15 + i)));
            hidden.add(neuron);
        }

        List<Neuron> input = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Neuron neuron = new Neuron(-2);
            for (int j = 0; j < 3; j++)
                neuron.addConnection(new Connection(hidden.get(j), network.get(i*3 + j)));
            input.add(neuron);
        }

        neuralNetwork.add(input);
        neuralNetwork.add(hidden);
        neuralNetwork.add(output);
    }

    /**
     * Oblicza wyjœcie sieci neuronowej w oparciu o wejœcie
     * @param input pola wejscia
     * @return wyjœcie sieci
     */
    public double calcOutput(double[] input)
    {
        for (int i = 0; i < neuralNetwork.size(); i++)
            for (int j = 0; j < neuralNetwork.get(i).size(); j++)
            {
                Neuron neuron = neuralNetwork.get(i).get(j);
                if(i == 0)
                    neuron.setValue(input[j]);
                neuron.passValues();
            }
        return neuralNetwork.get(neuralNetwork.size() - 1).get(0).getValue();
    }

    /**
     * Resetuje sieæ neuronow¹, inaczej neurony zachowalyby swoje wartoœci
     */
    public void reset()
    {
        for (int i = 0; i < neuralNetwork.size(); i++)
            for (int j = 0; j < neuralNetwork.get(i).size(); j++)
                neuralNetwork.get(i).get(j).setValue(0);
    }

    /**
     * Konwertuje sieæ neuronowa z formy instancji do List<Double> dla algorytmu genetycznego
     * @return sieæ w formie listy
     */
    public List<Double> toList()
    {
        List<Double> network = new ArrayList<>();
        for (List<Neuron> layer : neuralNetwork)
        {
            for (Neuron neuron : layer)
                if(neuron.getThreshold() != -2)
                    network.add(neuron.getThreshold());
            for (Neuron neuron : layer)
                for (Connection conn : neuron.getConnections())
                    network.add(conn.getWeight());
        }
        return network;
    }

    /**
     * Randomizer dla pol sieci takich jak wagi
     * @return losowy double pomiedzy -1 a 1
     */
    public static double randomValue()
    {
        return new Random().nextDouble() * 2 - 1;
    }
}
