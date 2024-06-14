package ru.stepup.course;

import lombok.EqualsAndHashCode;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode
public class State {

    private Map <Method, List<Object>> mutatorStates = new HashMap<>();

    private State(Map<Method, List<Object>> mutators) {
        this.mutatorStates = mutators;
    }

    public static State addState(State curState, Method meth, Object [] args){
        Map <Method, List<Object>> newMutators = new HashMap<>();
        if(curState != null) newMutators.putAll(curState.mutatorStates);

        newMutators.put(meth, Arrays.asList(args));
        return new State(newMutators);
    }

    // Состояние по умолчанию, для объектов у которых не вызывались мутаторы
    public static State getDefaultState(Object object){
        Map <Method, List<Object>> defaultState = new HashMap<>();
        defaultState.put(null, null);

        return new State(defaultState);
    }

    @Override
    public String toString() {
        return "State{" +
                "mutatorStates=" + mutatorStates +
                '}';
    }
}
