package ru.stepup.course;

// класс для возведения x в степень y
public class Pow implements PowInterface{
    private Integer x;
    private Integer y;

    public Pow(Integer x, Integer y) {
        this.x = x;
        this.y = y;
    }

    @Mutator
    public void setX(Integer x) {
        this.x = x;
    }
    @Mutator
    public void setY(Integer y) {
        if (y < 0 ) throw new IllegalArgumentException("Значение степени должно быть > 0");
        this.y = y;
    }
    @Cache(lifetime = 40)
    public Integer getResult(){
        System.out.println("invoke getResult()");
        Double result = Math.pow(x,y);
        return result.intValue();
    }
    @Cache(lifetime = 30)
    @Override
    public String toString() {
        System.out.println("invoke toString()");
        return "Pow{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
