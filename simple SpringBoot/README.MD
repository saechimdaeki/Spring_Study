# SpringBoot :boom: 

## 1 프로젝트 생성
#### 부트 프로젝트는 https://start.spring.io 사이트에서 생성한다.

### 스프링 부트의 starter 모듈은 다음 두가지를 제공한다.
- 메이븐 의존 설정 추가
- 기본 설정 추가
##### 예를 들어 spring-boot-starter-web 모듈은 spring-webmvc,JSON,Vlidator, 내장 톰캣 등 웹 개발에 필요한 의존을 설정한다.
##### 또한 스프링 MVC를 위한 다양한 구성요소(DispatcherServlet, 디폴트 서블릿,Jackson 등)에 대한 설정을 자도생성하는 기능을 제공.

#### 이제 다운받은 코드를보자
    @SpringBootApplication
    public class DemoApplication {
        public static void main(String[] args) {
            SpringApplication.run(DemoApplication.class, args);
        }
    }
##### @SpringBootApplication 애노테이션이 중요하다. SpringApplication.run()을 이용해서 이 애노테이션을 붙인 클래스를 실행하면 여러 설정을 자동으로 
##### 처리한다. 웹 starter를 사용하면 웹 관련 자동 설정 기능을 활성화하고 JDBC starter를 사용하면 DB관련 자동 설정 기능을 활성화한다. 
##### @SpringBootApplication 애노테이션을 사용하면 컴포넌트 스캔 기능도 활성화한다. 
##### @Component,@Service,@Controller,@RestController 등 컴포넌트 스캔 대상 애노테이션을 붙인 클래스를 빈으로 등록한다.
##### 마지막으로 application.properties 파일은 설정정보를 담는다 . JDBC URL이나 웹 캐시 시간과 같은 설정을 변경하고 싶을때 프로퍼티파일을 사용한다.

## 2 간단한 JSON 응답 컨트롤러 생성과 실행
    public class Now {
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    private OffsetDateTime time;
    public Now(){
        time=OffsetDateTime.now();
    }
    public OffsetDateTime getTime(){
        return time;
    }
    }

    /////
    @RestController
    public class NowController {
        @GetMapping("/now")
        public Now now(){
            return new Now();
        }
    }
##### 스프링 부트는 컴포넌트 스캔기능을 기본으로 활성화한다. @RestController 애노테이션도 스캔대상이므로 NowController 클래스를 설정 클래스에
#### 추가하지 않아도 자동으로 빈으로 등록한다.    
#### 이제 서버를 이용해 확인해보자 스프링부트는 내장톰캣을 이용해서 웹어플리케이션을 구동한다
    ./mvnw spring-boot:run
#### http://localhost:8080/now로 들어가보자.
![image](https://user-images.githubusercontent.com/40031858/87292863-6ae00f80-c53c-11ea-856d-a59f5b048146.png)


### 이제 DB연동을 한후에 코드를 작성하자

    @RestController
    public class MemberApi {
        private JdbcTemplate jdbcTemplate;
        @GetMapping("/members")
        public List<String> members(){
            return jdbcTemplate.queryForList(
                    "select email from member order by email"
        ,String.class
            );
        }
        @Autowired
        public void setJdbcTemplate(JdbcTemplate jdbcTemplate){
            this.jdbcTemplate=jdbcTemplate;
        }
    }
#### http://localhost:8080/members    
![12](https://user-images.githubusercontent.com/40031858/87292959-906d1900-c53c-11ea-866a-d9213b1b23ad.JPG)

## 4. 실행가능한 패키지 생성
##### 스프링 부트를 이용하면 실행 가능한 패키지도 쉽게 만들 수 있다. 다음명령어를 실행하자.
    ./mvnw package
##### 위 명령어를 실행하면 target폴더에 jar파일이 생성된다. 이파일은 스프링 부트 플러그인이 만든 실행가능한 jar파일이다. 
#### 다음 명령어를 통해 이 jar파일을 실행할수있다.
    java -jar target/파일이름.jar    