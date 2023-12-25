package ir.vcx.util;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class ThreadPoolUtil {

    @Bean
    public ThreadPoolExecutor threadPoolFactory() {
        return (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
    }

}
