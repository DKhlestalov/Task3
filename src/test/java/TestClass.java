import ru.stepup.course.Cache;
import ru.stepup.course.Mutator;

public class TestClass implements TestInterface{

    private Integer x;
    private Integer y;

    private Integer count;

    public TestClass(Integer x, Integer y) {
        this.x = x;
        this.y = y;
        count = 0;
    }

    public Integer getCount() {
        return count;
    }
    public void resetCount(){count = 0;}
    @Mutator
    public void setX(Integer x) {
        this.x = x;
    }
    @Mutator
    public void setY(Integer y) {
        this.y = y;
    }
    @Cache
    public Integer getResult1(){
        count++;
        Double result = Math.pow(x,y);
        return result.intValue();
    }

    @Cache
    public Integer getResult2(){
        count++;
        return (x + y);
    }

    public Integer getResult3(){
        System.out.println("invoke getResult()");
        count++;
        return x + y;
    }

}
