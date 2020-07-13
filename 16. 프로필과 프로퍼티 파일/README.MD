# 프로필과 프로퍼티 파일:fish: 
## 1. 프로필 
#### 개발을 진행하는 동안에는 실제 서비스 목적으로 운영중인 DB를 이용할수는없다.
#### 개발하는동안에는 개발용 DB를 따로 사용하거나 개발PC에 직접 DB를 설치해서 사용하는데
##### 실 서비스 장비에 배포하기 전에 설정정보를 변경하고 배포하는 방법을 사용할 수도 있지만 이방법은 너무원시적이다
##### 이방법은 실수하기도 쉽고 반대로 실서비스 정보를 그대로 두고 개발을 진행할수도있다.

#### `이런 실수를 방지하는 방법은 처음부터 개발목적 설정과 실서비스 목적의 설정을 구분해 작성하는것이다`
### `이를 위한 스프링 기능이 프로필(profile)이다`

##### 프로필은 논리적인 이름으로서 설정집합에 프로필을 지정할수있다. 스프링 컨테이너는 설정 집합 중에서 지정한 이름을 사용하는 
##### 프로필을 선택하고 해당프로필에 속한 설정을 이용해서 컨테이너를 초기화할수있다. 예를들어 로컬 개발환경을 위한 DataSource설정을 "dev" 프로필로
##### 지정하고 실서비스 환경을 위한 DataSource설정을 "real"프로필로 지정한 뒤, "dev"프로필을 사용해서 스프링 컨테이너를 초기화할수있다.
### 1.1 @Configuration 설정에서 프로필 사용하기
#### @Configuration 애노테이션을 이용한 설정에서 프로필을 지정하려면 @Profile애노테이션을 사용한다.
    
    @Configuration
    @Profile("dev")
    public class DsDevConfig {
        @Bean(destroyMethod = "close")
        public DataSource dataSource() {
            DataSource ds = new DataSource();
            ds.setDriverClassName("com.mysql.jdbc.Driver");
            ds.setUrl("jdbc:mysql://localhost/springjunseong?characterEncoding=utf8");
            ds.setUsername("root");
            ds.setPassword("940215");
            ds.setInitialSize(2);
            ds.setMaxActive(10);
            ds.setTestWhileIdle(true);
            ds.setMinEvictableIdleTimeMillis(60000 * 3);
            ds.setTimeBetweenEvictionRunsMillis(10 * 1000);
            return ds;
        }
    }

    @Profile은 "dev"를 값으로 갖는다. 스프링 컨테이너를 초기화 할때 "dev"프로필을 활성화하면
    DsDevConfig 클래스를 설정으로 사용한다.
##### "dev"가 아닌 "real"프로필을 활성화 했을때 사용할 설정클래스는 다음과 같이 @Profile 애노테이션의 값으로 "real"을 지정한다.
    @Configuration
    @Profile("real")
    public class DsRealConfig {
        @Bean(destroyMethod = "close")
        public DataSource dataSource() {
            DataSource ds = new DataSource();
            ds.setDriverClassName("com.mysql.jdbc.Driver");
            ds.setUrl("jdbc:mysql://localhost/springjunseong?characterEncoding=utf8");
            ds.setUsername("root");
            ds.setPassword("940215");
            ds.setInitialSize(2);
            ds.setMaxActive(10);
            ds.setTestWhileIdle(true);
            ds.setMinEvictableIdleTimeMillis(60000 * 3);
            ds.setTimeBetweenEvictionRunsMillis(10 * 1000);
            return ds;
        }
    }

##### DsDevConfig 클래스와 DsProdConfig 클래스는 둘다 이름이 "dataSource"인 DataSource타입의 빈을 설정하고있다.

##### 두 "dataSource" 빈 중에서 어떤 빈을 사용할지는 활성화 한 프로필에 따라 달라진다.
##### "dev"프로필을 활성화하고싶으면 @Profile("dev") 애노테이션을 붙인 설정클래스의 dataSource빈을 사용하면된다. 
#### 특정 프로필을 선택하려면 컨테이너를 초기화하기 전에 setActiveProfiles()메소들르 사용해서 프로필을 선택해야한다.

    AnnotationConfigApplicationContext context=new AnnotationConfigApplicationContext();
    context.getEnvironment().setActiveProfiles("dev")
    context.register(MemberConfig.class,DsDevConfig.class,DsRealConfig.class);
    context.refresh();
#### getEnvironment()메소드는 스프링 실행환경을 설정하는데사용되는 Environment를리턴한다.
##### 이 Environment의 setActiveProfiles()메소드를 사용해서 사용할 프로필을 선택할수있다. 위 코드는 "dev"를 값으로 주었으므로 "dev"프로필에 
##### 속한 설정이 사용된다. 따라서 DsDevConfig 클래스와 DsRealConfig 클래스에 정의되어 있는 "dataSource"중에서 "dev"프로필에 속하는 
##### DsDevConfig에 정의된 "dataSource"빈을 사용한다.

#### 두 개 이상의 프로필을 활성화하고 싶다면 다음과 같이 프로필 이름을 메소드에 파라미터로 전달한다.
    context.getEnvironment().setActiveProfiles("dev","mysql");
##### 프로필을 선택하는 또다른방법은 spring.profiles.active 시스템 프로퍼티에 사용할 프로필 값을 지정하는 것이다. 
##### 두 개 이상인 경우 사용할 프로필을 콤마로 구분해서 설정하면된다. 시스템 프로퍼티는 명령행에서 -D옵션을 이용하거나 
##### System.setProperty()를 이용해서 지정할수있다. 아래 코드는 -D옵션을 이용한설정 예이다.
    java -Dspring.profiles.active=dev main.Main
##### 위와 같이 시스템 프로퍼티로 프로필을 설정하면 setActiveProfiles()메소드를 사용하지않아도 "dev"프로필이 활성화된다.
##### 자바의 시스템 프로퍼티 뿐만아니라 OS의 "spring.profiles.active"환경변수에 값을 설정해도된다. 프로필 우선순위는 다음과같다.
- setActiveProfiles()
- 자바 시스템 프로퍼티
- OS 환경변수
### 1.2 @Configuration을 이용한 프로필 설정
##### 중첩 클래스를 이용해서 프로필 설정을 한곳으로 모을수있다. 다음은 그예이다.        
    @Configuration
    public class MemberConfigWithProfile{
        @Autowired
        private DataSource dataSource;
        
        @Bean
        public MemberDao memberDao(){
            return new MemberDao(dataSource);
        }
        
        @Configuration
        @Profile("dev")
        public static class DsDevConfig{
            @Bean(destroyMethod="close")
            public DataSource dataSource(){
                DataSource ds=new DataSource();
                ds.setDriverClassName("com.mysql.jdbc.Driver");
                ...
                return ds;
            }
        }

        @Configuration
        @Profile("real")
        public static class DsRealConfig{
            @Bean(destroyMethod="close")
            public DataSource dataSource(){
                DataSource ds=new DataSource();
                ds.setDriverClassName("com.mysql.jdbc.Driver");
                ...
                return ds;
            }
        }
    }
#### 중첩된 @Configuration 설정을 사용할때 주의할점은 중첩클래스는 static이어야한다는점이다.

### 1.3 다수 프로필 설정
##### 스프링 설정은 두개 이상의 프로필 이름을 가질수있다. 아래코드는 real과 test프로필을 갖는설정예시이다.
##### real프로필을 사용할때와 test프로필을 사용할때 모두 해당설정을 사용한다.
    @Configuration
    @Profile("real,test")
    public class DataSourceJndiConfig{
        ...
##### 프로필 값을 지정할때 다음코드처럼 느낌표(!)를 사용할수도있다.
    @Configuration
    @Profile("!real")
    public class DsDevConfig{
         @Bean(destroyMethod="close")
            public DataSource dataSource(){
                DataSource ds=new DataSource();
                ds.setDriverClassName("com.mysql.jdbc.Driver");
                ...
                return ds;
            }
    }        

    "!real"값은 "real" 프로필이 활성화 되지 않을때 사용한다는것을 의미한다.
### 1.4 어플리케이션에서 프로필 설정하기
    <init-param>
        <param-name>spring.profiles.active</param-name>
        <param-value>dev</param-value>
    </init-param>
## 2. 프로퍼티 파일을 이용한 프로퍼티 설정
##### 스프링은 외부의 프로퍼티 파일을 이용해서 스프링 빈을 설정하는 방법을 제공하고있다. 예를들어 db.properties파일이 있다고하자
    db.driver=com.mysql.jdbc.Driver
    db.url=jdbc:mysql://localhost/springjunseong?characterEncoding=utf8
    db.user=root
    db.password=940215

    이파일의 프로퍼티값을 자바설정에서사요할수있으며 이를 통해 설정일부를 외부 프로퍼티 파일을
    사용해서 변경할수있다.
### 2.1 @Configuration 애노테이션 이용 자바 설정에서의 프로퍼티 사용
#### 자바설정에서 프로퍼티 파일을 사용하려면 다음 두가지를 설정한다
- PropertySourcesPlaceholderConfigurer 빈설정
- @Value 애노테이션으로 프로퍼티 값사용
##### 먼저 PropertySourcesPlaceholderConfigurer 클래스를 빈으로 등록한다.
    @Configuration
    public class PropertyConfig {
        @Bean
        public static PropertySourcesPlaceholderConfigurer properties(){
            PropertySourcesPlaceholderConfigurer configurer= new PropertySourcesPlaceholderConfigurer();
            configurer.setLocations(
                    new ClassPathResource("db.properties"),
                    new ClassPathResource("info.properties"));
            return configurer;
        }
    }  
##### PropertySourcesPlaceholderConfigurer#setLocations() 메소드는 프로퍼티 파일 목록을 인자로 전달받는다.
##### 이때 스프링의 Resource타입을 이용해서 파일경로를 전달한다. 
##### db.properties 파일이 클래스 패스에 위치하고 있다면 ClassPathResource클래스를 이용해서 프로퍼티 파일정보를 전달한다.

##### 위 코드에서 주의해서 볼 점은 PropertySourcesPlaceholderConfigurer 타입 빈을 설정하는 메소드가 정적(static) 메소드라는것이다.
##### 이는 PropertySourcesPlaceholderConfigurer 클래스가 특수한 목적의 빈이기 때문이며 정적 메소드로 지정하지 않으면 원하는 방식으로 동작X

##### PropertySourcesPlaceholderConfigurer 타입 빈은 setLocations()메소드로 전달받은 프로퍼티 파일 목록정보를 읽어와 필요할때 사용한다.
##### 이를 위한것이 @Value 애노테이션이다.
    @Configuration
    public class DsConfigWithProp {
        @Value("${db.driver}")
        private String driver;
        @Value("${db.url}")
        private String jdbcUrl;
        @Value("${db.user}")
        private String user;
        @Value("${db.password}")
        private String password;
        
        @Bean(destroyMethod = "close")
        public DataSource dataSource(){
            DataSource ds=new DataSource();
            ds.setDriverClassName(driver);
            ds.setUrl(jdbcUrl);
            ds.setUsername(user);
            ds.setPassword(password);
            ds.setInitialSize(2);
            ds.setMaxActive(10);
            ds.setTestWhileIdle(true);
            ds.setMinEvictableIdleTimeMillis(60000*3);
            ds.setTimeBetweenEvictionRunsMillis(10*1000);
            return ds;
        }
    }

### 2.2 빈 클래스에서 사용하기
##### 아래와 같이 빈으로 사용할 클래스에도@Value 애노테이션을 붙일수있다.
    public class Info{
        @Value("${info.version})
        private String version;
        public void printInfo(){
            System.out.println("version = " +version);
        }
        public void setVersion(String version){
            this.version=version;
        }
    }
#### @Value 애노테이션을 필드에 붙이면 플레이스홀더에 해당하는 프로퍼티를 필드에 할당한다.
#### 다음과 같이 @Value애노테이션을 set메소드에 적용할수도있다.
    public class Info{
        private String version;

        public void printInfo(){
            System.out.println("version= " +version);
        }
        @Value("${info.version}")
        public void setVersion(String version){
            this.version=version;
        }
    }    