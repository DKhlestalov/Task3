package ru.stepup.course;

public class Main {
    public static void check(PowInterface pow, Integer x, Integer y){
        if(x != null) pow.setX(x);
        if(y != null) pow.setY(y);
        System.out.println(pow);
        System.out.println(pow.getResult());
    }

    public static void main(String[] args) {
        PowInterface pow = new Pow(1, 1);
        pow = Utils.cacheWithClean(pow, 30);
        // кэширование значений
        for(int i=1;i<= 10;i++){
            for(int j=1;j<= 3;j++){
                check(pow, i, j);
            }
        }
        // в последующих выховах часть устаревших кэшей будет удалено и произведен новый вызов кэшируемого метода
        System.out.println("---------------------------------------------------------------------------------------");
        System.out.println("Проверим кэши");
        for(int i=1;i<= 10;i++){
            for(int j=1;j<= 3;j++){
                check(pow, i, j);
            }
        }
    }
}