package com.enfi.pvtool;

import com.enfi.pvtool.process.CalculateNum;
import com.enfi.pvtool.process.ConfigNum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class PvToolApplication {

    public static void main(String[] args) {
        SpringApplication.run(PvToolApplication.class, args);
    }

    @Component
    @Order(10)
    @ConditionalOnBean(ConfigNum.class)
    public class AfterServiceStarted implements ApplicationRunner {

        @Autowired
        CalculateNum calculateNum;
        /**
         * 会在服务启动完成后立即执行
         */
        @Override
        public void run(ApplicationArguments args) throws Exception {
            calculateNum.reloadFile();
        }
    }
}
