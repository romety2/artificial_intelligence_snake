package com.nanovash.snakenn.neuralnetwork.util;

import com.nanovash.snakenn.neuralnetwork.Neuron;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class Connection
{
    private @Getter
    Neuron receiver;
    private @Getter double weight;
}
