## 스프링은 객체컨테이너이다. 

- 아래의 그림은 AnnotationConfigApplicationContext클래스의 계층도이다. 

![AnnotationConfigApplicationContext클래스의 계층도](https://user-images.githubusercontent.com/40031858/79677901-c1d19380-8230-11ea-8d42-9d755be29673.jpg)


    - BeanFactory 인터페이스는 객체생성과 검색에 대한 기능을 정의 (생성된 객체를
     검색하는데 필요한 getBen()메소드가 BeanFactory에 정의되어있음. 또한 싱글톤/
     프로토타입 빈인지 확인하는 기능도 제공.)

    - ApplicationContext 인터페이스는 메시지,프로필.환경변수 등을 처리할 수있는기능을 추가로 정의.

#####  최하단에 위치한 세개의 클래스는 BeanFactory와 ApplicationContext에 정의된 기능을 구현한다. 각 클래스의 차이는 다음과같다
- AnnotationConfigApplicationContext: 자바 애노테이션을 이용해 클래스로부터 객체설정정보가져옴
- GenericXmlApplicationContext: xml로부터 객체 설정 정보를 가져온다.
- GenericGroovyApplicationContext: 그루비 코드를 이용해 설정정보를 가져옴.

        ApplicationContext또는 Beanfactory는 빈객체의 생성, 초기화 보관 등을
        관리하고있어서 컨테이너(container)라고도 부른다.
---
### 이 예제에서 사용한 내용 정리
    @Configuration : 해당클래스를 스프링설정클래스로 지정
    @Bean : @Bean애노테이션을 메소드에 붙이면 해당 메소드가 생성한 객체를 스프링이 관리하는 
    빈 객체로 등록한다. @Bean annotation을 붙인 메소드는 객체를 생성하고 알맞게 초기화 해야함.
    이 예제에서는 Hello객체를 초기화하고있음

    main:
    AnnotationConfigApplicationContext 클래스는 자바설정에서 정보를 읽어 빈객체생성관리
    
    getBean() 메소드는 AnnotationConfigApplicationContext가 자바설정을 읽어와 생성한
    빈 객체를 검색할때 사용됨. getBean() 메소드의 첫번째 파라미터는 @Bean애노테이션의 메소드 
    이름인 빈 객체의 이름이먀 두번째 파라미터는 검색할 빈 객체의 타입이다.


## 싱글톤 객체.

    별도 설정을 하지 않을경우 스프링은 한개의 빈객체만을 생성하며 이때 빈객체는 '싱글톤
     범위를 갖는다'고 표현한다. 싱글톤은 단일 객체(single object)를 의미하는 단어로
      스프리은 기본적으로 한개의@Bean 애노테이션에 대해 한개의 빈객체를 생성.
    

- .getBean("hello", Hello.class)의 경우 "hello"은 빈객체의 이름이며 Hello.class는 빈객체의 타입이다.
