package ru.stepup.course;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.*;

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
    // Кол-во элементов для хэшмапы кэша, при достижении которого запустится очистка
    private int numElements;
    private final State defaultState; // Состояние до вызова мутаторов
    private State activeState; // Состояние после вызова мутаторов
    private Map<State, Map <Method, CacheValue>> cache = new ConcurrentHashMap<>(); //
    ExecutorService executor;


    public MakeCache(Object obj) {
        this.obj = obj;
        activeState = null;
        // состояние после конструктора
        defaultState = State.getDefaultState(obj);
        cache.put(defaultState, new ConcurrentHashMap<>());
    }

    public MakeCache(Object obj, int numElements) {
        this(obj);
        this.numElements = numElements;
        // один поток под ExecutorService, который будет демоном
        this.executor = Executors.newFixedThreadPool(1, new ThreadFactory() {
            public Thread newThread(Runnable r) {
                Thread t = Executors.defaultThreadFactory().newThread(r);
                t.setDaemon(true);
                return t;
            }
        });
    }

    @Override
    public Object invoke(Object o, Method method, Object[] args) throws Throwable {
        Method objMethod = obj.getClass().getMethod(method.getName(), method.getParameterTypes());
        Object result;


        if (objMethod.isAnnotationPresent(Mutator.class)){
            // если мутатор, создаем текущее состояние, для последующего сохранения кэшируемых значений
            State newState = State.addState(activeState, objMethod, args);

            if (!cache.containsKey(newState)) cache.put(newState, new ConcurrentHashMap<>()); // если состояния еще нет в кэше добавляем для него пустую мапу
            activeState = newState;
        }

        if (objMethod.isAnnotationPresent(Cache.class)){
            // время жизни кэша
            long lifetime = objMethod.getAnnotation(Cache.class).lifetime();
            // если на методе есть аннотация @Cache, проверяем наличие значения в кэше
            State st = activeState;
            if (st == null) st = defaultState;

            if (cache.containsKey(st)) {
                if (cache.get(st).containsKey(objMethod)) { // нашли значение в кэше
                    cache.get(st).get(objMethod).setExpireTime(System.currentTimeMillis() + lifetime); // продлеваем время жизни
                    return cache.get(st).get(objMethod).getValue();
                }
            }
            else throw new RuntimeException("Не найдено текущее состояние в кэше"); // такого быть не должно

            Future<ConcurrentHashMap> taskFuture = null;
            if(numElements != 0 & cache.size() > numElements){
                /*
                    Очистку проводим передачей задачи экзекьютору, параллельно с получением нового значения для кэша, т.к.:
                    1. получение нового значения "затратная операция" и за время ее выполнения мы считаем, что успеем почистить существующий кэш
                    2. мы не потеряем во время чистки новое полученное значение и сохраним его затем в кэше
                    3. это многопоточно-безопасно
                 */

                taskFuture = executor.submit(new Cleaner());
            }
            result = objMethod.invoke(obj, args);
            if (taskFuture!=null) {
//                cache = taskFuture.get();
                try {
                    cache = taskFuture.get();
                } catch (ExecutionException ee) {
                    System.err.println("Ошибка при очистке кэша");
                    System.err.println(ee);
                } catch (InterruptedException ie) {
                    System.err.println("Очистка прервана");
                }
                if (!cache.containsKey(st)) cache.put(st, new ConcurrentHashMap<>());
            }
            CacheValue cacheValue = new CacheValue(result, System.currentTimeMillis() + lifetime);
            if (lifetime == 0) cacheValue.setExpireTime(0L);
            cache.get(st).put(objMethod, cacheValue);
            return result;
        }
        return objMethod.invoke(obj, args);
    }

    // очистка кэша от устаревших значений
    private class Cleaner implements Callable <ConcurrentHashMap>{
        @Override
        public ConcurrentHashMap call() {
            // удалим состояние после конструктора
            if (activeState != null) cache.remove(defaultState);
            // удалим просроченные значения
            for(State state : cache.keySet()){
                Map<Method, CacheValue> map = cache.get(state);
                for(Method method : map.keySet()){
                    CacheValue cacheValue = map.get(method);
                    // если 0, то живет всегда
                    if (cacheValue.getExpireTime() == 0) continue;
                    if (cacheValue.getExpireTime() < System.currentTimeMillis()) map.remove(method);
                }
                if (cache.get(state).isEmpty()) cache.remove(state);
            }
            return new ConcurrentHashMap<>(cache);
        }
    }
}
