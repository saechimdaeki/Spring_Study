# 빈 라이프 사이클과 범위
## 1.컨테이너 초기화와 종료
    스프링 컨테이너는 초기화와 종료라는 라이프사이클을 갖는다. 아래 예시를보잠
    // 1.컨테이너 초기화
    AnnotationConfigApplicationCntext ctx=
        new AnnoationConfigApplicationContext(AppContext.class);

    // 2.컨테이너에서 빈객체를 구해서 사용
    Greeter g=ctx.getBean("greeter",Greeter.class);
    String msg=g.greet("스프링");
    System.out.println(msg);
    
    // 3.컨테이너 종료
    ctx.close();
---
    위의 코드를 보면 AnnotationConfigApplicationCntext의 생성자를 이용해 컨텍스트 객체를
    생성하는데 이 시점에 스프링 컨테이너를 초기화한다. 스프링 컨테이너는 설정클래스에서
    정보를 읽어와 빈 객체를 생성하고 각 빈을 연결(의존주입) 하는 작업을 수행한다.

    컨테이너 초기화가 완료되면 컨테이너를 사용할 수 있다.  컨테이너를 사용한다는 것은
    getBean()과 같은 메소드를 이용해서 컨테이너에 보관된 빈 객체를구한다는것을 뜻함.
    
    컨테이너 사용이 끝나면 컨테이너를 종료한다. 컨테이너를 종료할 때 사용하는 메소드가
    close()메소드이다. close()메소드는 AbstractApplicationContext 클래스에 정의되어 있다.
    자바 설정을 사용하는 AnnotationConfigApplicationContext클래스나 xml설정을 사용하는 GenericXmlApplicationContext 클래스 모두 AbstractApplicationContext 클래스를 상속받고있
    다. 따라서 앞선 코드처럼 close()메소드를 이용해 컨테이너를 종료할수있다.

    컨테이너를 초기화하고 종료할때에는 다음의 작업도 함께 수행한다.
    컨테이너 초기화 -> 빈 객체의 생성, 의존주입, 초기화
    컨테이너 종료 -> 빈객체의 소멸

## 2.스프링 빈 객체의 라이프사이클
#### 스프링 컨테이너는 빈객체의 라이프사이클을 관리한다. 컨테이너가 관리하는 빈객체의 라이프사이클은 
#### 다음과 같다.
#### 객체생성 -> 의존 설정 -> 초기화 -> 소멸  
    스프링 컨테이너를 초기화할 때 스프링 컨테이너는 가장 먼저 빈객체를 생성하고 의존을 설정한다.
    의존 자동 주입을 통한 의존설정이 이시점에 수행되고 모든 의존 설정이 완료되면 
    빈 객체의 초기화를 수행한다. 빈객체를 초기화하기 위해 스프링은 빈객체의 지정된
    메소드를 호출한다.

    스프링 컨테이너를 종료하면 스프링 컨테이너는 빈 객체의 소멸을 처리한다. 이때에도 지정한 메소드
    를 호출한다.
### 2.1 빈객체의 초기화와 소멸: 스프링 인터페이스 
##### 스프링 컨테이너는 빈객체를 초기화하고 소멸하기 위해 빈 객체의 지정한 메소드를 호출한다.
##### 스프링은 다음의 두 인터페이스에 이 메소드를 정의하고있다.
- org.springframework.beans.factory.InitializingBean
- org.springframework.beans.factory.DisposableBean
##### 
    두 인터페이스는 다음과 같다.
    public interface InitializingBean{
        void afterPropertiesSet() throws Exception;
    }

    public interface DisposableBean{
        void destory() throws Exception;
    }
---
    빈 객체가 InitializingBean 인터페이스를 구현하면 스프링 컨테이너는 초기화 과정에서
    빈 객체의 afterPropertiesSet()메소드를 실행한다. 빈 객체를 생성한 뒤에 초기화 과정이 필요하
    면 InitializingBean 인터페이스를 상속하고 afterPropertiesSet() 메소드를 알맞게 구현하면 
    된다. 

    스프링 컨테이너는 빈 객체가 DisposableBean 인터페이스를 구현한 경우 소멸 과정에서 빈 객체의
    destory() 메소드를 실행한다. 빈 객체의 소멸과정이 필요하면 DisposableBean 인터페이스를 상속하고
    destory()메소드를 알맞게 구현하면된다.

    초기화와 소멸 과정이 필요한 예가 데이터베이스 커넥션 풀이다. 커넥션 풀을 위한 빈 객체는
    초기화 과정에서 데이터베이스 연결을 생성한다. 컨테이너를 사용하는 동안 연결을 유지하고 
    빈 객체를 소멸할 때 사용중인 데이터베이스 연결을 끊어야한다

    또 다른예로 채팅 클라이언트가 있다. 채팅클라이언트는 시작할 때 서버와 연결을 생성하고 종료할 때
    끊는다. 이때 서버와 연결을 생성하고 끊는 작업을 초기화시점과 소멸시점에 수행하면댐.
    디렉토리의 Client와 Appctx, Main 클래스를 살펴보장. 
### 2.2 빈 객체의 초기화와 소멸: 커스텀 메소드 
    모든 클래스가 Initializing 인터페이스와 DisposabeBean 인터페이스를 상속받아 구현할 수 있는 것은 아니다. 
    직접 구현한 클래스가 아닌 외부에서 제공받은 클래스를 스프링 빈객체로 설정하고 싶을때두 있다.
    이 경우 소스코드를 받지 않았다면 두 인터페이스를 구현하도록 설정할 수 없다.
    이렇게 InitializingBean 인터페이스와 DisposableBean인터페이스를 구현할 수 없거나
    이 두인터페이스를 사용하고 싶지않은 경우에는 스프링 설정에서 직접 메소드를 지정할수있다.

    방법은 간단쓰 하다. @Bean태그에 initMethod 속성과 destroyMethod 속성을 사용해서 초기화 메소드와
    소멸 메소드의 이름을 지정하면된다. 
    Client2 클래스의 코드를보면댄다.
## 3.빈 객체의 생성과 관리 범위
    스프링 컨테이너는 빈객체를 한개만 생성한다고 했다. 예를들어 아래의 코드와 같이
    동일한 이름을 갖는 빈객체를 구하면 동일한 빈객체를 참조한다.
    Client client1=ctx.getBean("client",Client.class);
    Client client2=ctx.getBean("client",Client.class);
    // client1 == client2 ->true

    이렇게 한 식별자에 대해 한개의 객체만 존재하는 빈은 싱글톤범위를 갖는다.
    별도설정을 하지않으면 빈은 싱글톤범위를 갖는다.

    사용빈도가 낮긴하지만 프로토타입 범위의 빈을 설정할수도있다. 빈의 범위를 프로토타입으로 
    지정하면 빈객체를 구할때마다 매번새로운 객체를 생성한다. 
    예를들어 "client"이름을 갖는 빈을 프로토 타입범위의 빈으로 설정하면 client1과
    client2는 다른객체가된다.

    //client빈의 범위가 프로토타입일경우, 매번새로운객체생성
    Client client1=ctx.getBean("client",Client.class);
    Client client2=ctx.getBean("client",Client.class);
    // client1 == client2 ->false

---
    특정 빈을 프로토타입 범위로 지정하려면 다음과 같이 "prototype"을 갖는 @Scope애노테이션을 
    @Bean 애노테이션과 함께 사용하면된다.

    @Configuration
    public class AppCtxWithPrototype{
        @Bean
        @Scope("prototype")
        public Client client(){
            Client client=new Client();
            client.setHost("host");
            return client;
        }
    }

    싱글톤 범위를 명시적으로 지정하고싶다면 @Scope 애노테이션 값으로 "singleton"을주면된다
    @Bean(initMethod="connect", destroyMethod="close")
    @Scope("singleton")
    public Client2 client2(){
        Client2 client=new Client2();
        client.setHost("host");
        return client;
    }

    프로토타입 범위를 갖는 빈은 완전한 라이프사이클을 따르지 않는다는점ㅁ을 주의해야한다.
    스프링 컨테이너는 프로토타입의 빈 객체를 생성하고 프로퍼티를 설정하고 초기화 작업까지는 
    수행하지만, 컨테이너를 종료한다고 해서 생성한 프로토타입 빈 객체의 소멸 메소드를
    실행하지는 않는다. 따라서 프로토타입 범위의 빈을 사용할때에는 빈객체의 소멸처리를 코드에서직접.
