import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/* 스프링 설정 파일 */
@Configuration
public class AppContext {
    @Bean
    public Hello hello(){
        Hello hello=new Hello();
        hello.setFormat("%s, 안녕하세용~!" );
        return hello;
    }
}
