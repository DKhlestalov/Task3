import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.stepup.course.PowInterface;
import ru.stepup.course.Utils;

public class CacheTests {

    @Test
    @DisplayName("Проверка кэширования методов после конструктора")
    public void testConstructorCache(){
        TestInterface tst = new TestClass(3,2);
        tst = Utils.cache(tst);
        Integer result = tst.getResult1();
        tst.getResult1();
        Assertions.assertEquals(1, tst.getCount(),  "Неверное количество вызовов кэшируемых методов: ожидалось 1, совершено " + tst.getCount());
        Assertions.assertEquals(result, tst.getResult1(), "Кэшированное значение отличается от оригинального");
        result = tst.getResult2();
        tst.getResult2();
        Assertions.assertEquals(tst.getCount(), 2, "Неверное количество вызовов кэшируемых методов: ожидалось 2, совершено " + tst.getCount());
        Assertions.assertEquals(result, tst.getResult2(), "Кэшированное значение отличается от оригинального");
    }

    @Test
    @DisplayName("Проверка кэширования методов после мутаторов")
    public void testMutatorCache(){
        TestInterface tst = new TestClass(3,2);
        tst = Utils.cache(tst);
        tst.setX(2);
        tst.setY(8);
        Integer result1 = tst.getResult1();
        Integer result2 = tst.getResult2();
        tst.setX(3);
        tst.setY(5);
        tst.getResult1();
        tst.getResult2();
        tst.setX(2);
        tst.setY(8);
        Assertions.assertEquals(4, tst.getCount(),  "Неверное количество вызовов кэшируемых методов: ожидалось 4, совершено " + tst.getCount());
        Assertions.assertEquals(4, tst.getCount(),  "Неверное количество вызовов кэшируемых методов: ожидалось 4, совершено " + tst.getCount());
        tst.resetCount();
        Assertions.assertEquals(result1, tst.getResult1(), "Кэшированное значение отличается от оригинального");
        Assertions.assertEquals(0, tst.getCount(),  "Неверное количество вызовов кэшируемых методов: ожидалось 0, совершено " + tst.getCount());
        Assertions.assertEquals(result2, tst.getResult2(), "Кэшированное значение отличается от оригинального");
        Assertions.assertEquals(0, tst.getCount(), "Неверное количество вызовов кэшируемых методов: ожидалось 0, совершено " + tst.getCount());
    }

    @Test
    @DisplayName("Отсутствие кэширования на методах без аннотаций")
    public void testNoCache(){
        TestInterface tst = new TestClass(3,2);
        tst = Utils.cache(tst);
        tst.getResult3();
        tst.getResult3();
        Assertions.assertEquals(tst.getCount(), 2, "Кэширование на методе без аннотации @Cache");
    }

}
