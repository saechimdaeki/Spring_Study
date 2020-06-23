import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({AppConf1.class,AppConf2.class})
public class AppConfImport {
    @Bean
    public MemberDao memberDao(){
        return new MemberDao();
    }
    @Bean
    public MemberPrinter memberPrinter(){
        return new MemberPrinter();
    }
}
