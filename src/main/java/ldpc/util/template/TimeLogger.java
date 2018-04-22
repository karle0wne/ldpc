package ldpc.util.template;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class TimeLogger {

    private final long startFunctionTime;
    private final Logger logger;
    private long divDurationTime;
    private String name;

    public TimeLogger(String name) {
        this.startFunctionTime = System.currentTimeMillis();
        this.logger = Logger.getLogger(this.getClass().getName());
        this.divDurationTime = 0;
        this.name = name;
        this.logger.info("Начало работы метода: " + name);
    }

    public void check() {
        long duration = TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - startFunctionTime);
        if (duration % 5 == 0 && divDurationTime != duration / 5) {
            logger.warning("Метод: " + name + ", с момента начала работы функции прошло уже " + duration + " минут!");
            divDurationTime = duration / 5;
        }
    }
}
