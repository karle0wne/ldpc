package ldpc.util.template;

import java.util.concurrent.TimeUnit;

public class TimeLogger {

    private final long startFunctionTime;
    private long divDurationTime;
    private String name;

    public TimeLogger(String name, boolean soutStartInfo) {
        this.startFunctionTime = System.currentTimeMillis();
        this.divDurationTime = 0;
        this.name = name;
        if (soutStartInfo) {
            System.out.println("Начало работы метода: " + name);
        }
    }

    public void check() {
        long duration = TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - startFunctionTime);
        if (duration % 5 == 0 && divDurationTime != duration / 5) {
            System.out.println("Метод: " + name + ", с момента начала работы функции прошло уже " + duration + " минут!");
            divDurationTime = duration / 5;
        }
    }
}
