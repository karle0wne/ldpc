package ldpc;

import ldpc.util.service.StandService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.AbstractApplicationContext;

import static ldpc.util.template.LDPCEnums.*;

@Configuration
@ComponentScan(
        basePackages = {
                "ldpc.service",
                "ldpc.util.service"
        }
)
public class MainApplication {

    public static void main(String[] args) {
        AbstractApplicationContext context = new AnnotationConfigApplicationContext(MainApplication.class);
        StandService bean = context.getBean(StandService.class);
        bean.stand(TypeOfCoding.LDPC_GIRTH8_8_4, TypeOfChannel.AWGN, TypeOfDecoding.PRODUCT_SUM);
        context.registerShutdownHook();
    }
}
