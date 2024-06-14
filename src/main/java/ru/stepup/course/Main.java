package ru.stepup.course;

public class Main {
    public static void check(PowInterface pow, Integer x, Integer y){
        if(x != null) pow.setX(x);
        if(y != null) pow.setY(y);
        //кэшируем
        System.out.println(pow);
        System.out.println(pow.getResult());
        //проверяем, что закэшировалось
        System.out.println(pow);
        System.out.println(pow.getResult());
    }

    public static void main(String[] args) {
        PowInterface pow = new Pow(2, 10);
        pow = Utils.cache(pow);
        // кэширование после конструктора 2 ^ 10
        check(pow, null, null);
        // кэширование 3 ^ 2
        check(pow, 3, 2);
        // кэширование 10 ^ 3
        check(pow, 10, 3);
        // проверки
        // проверяем кэш 2 ^ 10 (создается заново, т.к. кэш после конструктора не сохраняется после мутаторов)
        check(pow, 2, 10);
        // проверяем кэш 3 ^ 2
        check(pow, 3, 2);
        // проверяем кэш 10 ^ 3
        check(pow, 10, 3);

        // кэширование 3 ^ 3
        check(pow, 3, 3);
    }
}