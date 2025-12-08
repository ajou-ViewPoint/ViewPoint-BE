package com.www.viewpoint.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * 테스트 실행 전에 .env 파일에서 환경 변수를 로드하는 초기화 클래스.
 * 
 * @SpringBootTest는 main() 메서드를 호출하지 않으므로,
 * ViewpointApplication.main()에서 수행하는 dotenv 로드가 실행되지 않습니다.
 * 이 클래스를 사용하여 테스트 시에도 환경 변수를 로드합니다.
 * 
 * 사용법:
 * @SpringBootTest
 * @ContextConfiguration(initializers = DotenvInitializer.class)
 * class YourTest { ... }
 */
public class DotenvInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();

        dotenv.entries().forEach(entry ->
                System.setProperty(entry.getKey(), entry.getValue())
        );
    }
}

