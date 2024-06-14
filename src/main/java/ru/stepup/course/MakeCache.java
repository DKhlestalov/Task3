package ru.stepup.course;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/*
По совету Александра кэширование организовано через сохранение значений параметров, с которыми вызываются мутаторы.
Данный метод имеет некоторые допущения:
- по причине нетривиальности решения для восстановления кэша, созданного после работы конструктора, кэш конструктора работает только до вызова мутаторов.
  Если мутаторы создадут состояние объекта, которое было после конструктора, оно будет кэшироваться заново.
- метод будет работать неэффективно, если в классе несколько мутаторов для одних и тех же полей класса; в этом случае в кэше может быть несколько значений для одного и того же состояния класса.
Но для учебных целей такого кэшированя достаточно.
 */
public class MakeCache implements InvocationHandler {
    private Object obj;
    private final State defaultState; // Состояние до вызова мутаторов
    private State activeState; // Состояние после вызова мутаторов
    private Map<State, Map <Method, Object>> cache = new HashMap<>(); //


    public MakeCache(Object obj) {
        this.obj = obj;
        activeState = null;
        // состояние после конструктора
        defaultState = State.getDefaultState(obj);
        cache.put(defaultState, new HashMap<>());
    }

    @Override
    public Object invoke(Object o, Method method, Object[] args) throws Throwable {
        Method objMethod = obj.getClass().getMethod(method.getName(), method.getParameterTypes());
        Object result;


        if (objMethod.isAnnotationPresent(Mutator.class)){
            // если мутатор, создаем текущее состояние, для последующего сохранения кэшируемых значений
            State newState = State.addState(activeState, objMethod, args);
//            if(activeState == null){
//                objMethod.getParameters()
//                // переносим текущий кэш созданный по значениям конструктора
//                State constructorState;
//            }

            if (!cache.containsKey(newState)) cache.put(newState, new HashMap<>()); // если состояния еще нет в кэше добавляем для него пустую мапу
            activeState = newState;
        }

        if (objMethod.isAnnotationPresent(Cache.class)){
            // если на методе есть аннотация @Cache, проверяем наличие значения в кэше
            State st = activeState;
            if (st == null) st = defaultState;

            if (cache.containsKey(st)) {
                if (cache.get(st).containsKey(objMethod))
                    return cache.get(st).get(objMethod);
            }
            else throw new RuntimeException("Не найдено текущее состояние в кэше"); // такого быть не должно
            result = objMethod.invoke(obj, args);
            cache.get(st).put(objMethod, result);
            return result;
        }
        return objMethod.invoke(obj, args);
    }
}
