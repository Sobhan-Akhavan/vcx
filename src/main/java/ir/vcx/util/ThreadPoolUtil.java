package ir.vcx.util;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class ThreadPoolUtil {

    @Bean(value = "contentThreadPool")
    public ThreadPoolExecutor threadPoolFactory1() {
        return (ThreadPoolExecutor) Executors.newFixedThreadPool(20);
    }

    @Bean(value = "planThreadPool")
    public ThreadPoolExecutor threadPoolFactory2() {
        return (ThreadPoolExecutor) Executors.newFixedThreadPool(6);
    }

}
