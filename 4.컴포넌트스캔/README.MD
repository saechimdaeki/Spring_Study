# 컴포넌트 스캔

### 자동 주입과 함께 사용하는 추가기능이 컴포넌트 스캔이다.
#### 컴포넌트 스캔은 스프링이 직접 클래스를 검색해서 빈으로 등록해주는 기능
#### 설정 클래스에 빈으로 등록하지 않아도 원하는 클래스를 빈으로 등록할 수 있으므로 컴포넌트 스캔 기능을 사용하면 설정코드가 크게 줄어듦

### 1. @Component 애노테이션으로 스캔대상 지정 
    스프링이 검색해서 빈으로 등록할 수 있으려면 클래스에 @Component애노테이션을 붙여야함.
    @Component애노테이션은 해당 클래스를 스캔대상으로 표시한다. 
    이 Readme와 같은 디렉토리에 있는 소스에서
    ChangePasswordService
    MemberDao
    MemberInfoPrinter
    MemberListPreinter
    MemberRegisterService
    에 @Component 애노테이션을 붙인다.

##### @Component애노테이션에 값을 주었는지에 따라 빈으로 등록할 때 사용할 이름이 결정된다.
##### @Component애노테이션에 값을 주지않을경우 클래스 이름의 첫글자를 소문자로 바꾼이름을 빈으로 사용한다.
##### ex) 클래스 이름이 MemberDao라면 빈이름은 memberDao가됨.


### 2. @ComponentScan 애노테이션으로 스캔 설정
    @Component 애노테이션을 붙인 클래슬르 스캔해서 스프링 빈으로 등록하려면 설정 클래스에
    @ComponentScan 애노테이션을 적용해야한다.
    
    코드의 Appctx 클래스를 보면 @ComponentScan애노테이션의 basePackages 속성값은 {"spring"}
    이다. 이 속성은 스캔 대상 패키지 목록을 지정한다. 이는 spring패키지와 그 하위패키지에
    속한 클래스를 스캔대상으로 설정한다. 스캔 대상에 해당하는 클래스 중에서 
    @Component애노테이션이 붙은 클래스의 객체를 생성해서 빈으로 등록한다.

### 3. 스캔 대상에서 제외하거나 포함하기
    excludeFilters 속성을 사용하면 스캔할 때 특정 대사을 자동 등록 대상에서 제외할 수 있다. 다음 코드는 excludeFilters 속성의 사용 예를 보여준다.

    @Configuration
    @ComponentScan(basePackages = {"spring"},
    excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX,pattern = "spring\\..*Dao"))
    public class AppCtxWithExclude {
        @Bean
        public MemberDao memberDao(){
            return new MemberDao();
        }
        @Bean
        @Qualifier("printer")
        public MemberPrinter memberPrinter1(){
            return new MemberPrinter();
        }
    }


    위의 코드는 @Filter 애노테이션의 type속성값으로 Filter.REGEX를 주었다.
    이는 정규표현식을 사용해서 제외대상을 지정한다는 것을 의미한다.
    pattern 속성은 FilterType에 적용할 값을 설정한다.
    위 코드에서는 "spring."으로 시작하고 Dao로 끝나는 정규표현식을
    지정했다.
### 4. 기본 스캔대상
    @Component애노테이션을 붙인 클래스만 컴포넌트 스캔 대상에 포홤되는 것은 아니다.
    다음 애노테이션을 붙인 클래스가 컴포넌트 스캔대상에 포함된다.       
    
    @Component(org.springframework.stereotype패키지)
    @Controller(org.springframework.streotype패키지)
    @Service(org.springframework.streotype패키지)
    @Repository(org.springframework.stereotype패키지)
    @Aspect(org.aspectj.lang.annotation패키지)
    @Configuration(org.springframework.context.annotation패키지)

    위에서 @Aspect애노테이션을 제외한 나머지 애노테이션은 실제로 @Component애노테이션에
    대한 특수 애노테이션이다. 예를 들어 @Controller 애노테이션은 다음과 같다.

    ////
    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Component
    public @interface Controller{
        @AliasFor(annotation=Component.class)
        String value() default "";
    }

    @Component애노테이션이 붙어 있는데, 스프링은 @Controller애노테이션을 
    @Component애노테이션과 동일하게 컴포넌트 스캔대상에 포함한다. @Controller 애노테이션이나
    @Repository애노테이션 등은 컴포넌트 스캔 대상이 될뿐만 아니라 스프링 프레임워크에서
    특별한 기능과 연관되어 있다. 예를 들어 @Controller 애노테이션은 웹 MVC와 관련있고
    @Repository애노테이션은 DB연동과 관련있다. 

 ### 5.컴포넌트 스캔에 따른 충돌처리 
 ### 5.1 빈 이름충돌
    예를들어, spring패키지와 spring2패키지에 MemberRegisterService 클래스가 존재하고 두
    클래스 모두 @Component애노테이션을 붙였다고하자 이상태에서 아래의 @ComponentScan 애노테이션을 사용하면 어떻게 될까?
    @Configuration
    @ComponentScan(basePackages={"spring","srping2"})
    public class AppCtx{
        ...
    }

    이를 실행하면 에러가일어난다. 컴포넌트 스캔과정에서 서로 다른타입인데 같은 빈이름을
    사용하는 경우가 있다면 둘 중 하나에 명시적으로 빈이름을 지정해서
    이름 충돌을피해야한다 

### 5.2 수동 등록한 빈과 충돌

    @Component
    public class MemberDao{
        ...
    }
    자동 등록된 빈의 이름은 memberDao이다.
    설정 클래스에서 직접 MemberDao클래스를 "memberDao"라는 이름의 빈으로 등록하면??

    @Configuration
    @ComponentScan(basePackages={"spring"})
    public class AppCtx{
        @Bean
        public MemberDao memberDao(){
            MemberDao memberDao= new MemberDao();
            return memberDao;
        }
    }
    스캔할때 사용하는 빈 이름과 수동 등록한 빈이름이 같은 경우 수동 등록한 빈이 우선이다.
    즉, MemberDao타입 빈은 AppCtx에서 정의한 한개만 존재한다.

    그렇다면 다른이름을 사용한다면?????

    @Configuration
    @ComponentScan(basePackages={"spring"})
    public class AppCtx{
        @Bean
        public MemberDao memberDao2(){
            MemberDao memberDao= new MemberDao();
            return memberDao;
        }
    }

    이 경우 스캔을 통해 등록한 "memberDao" 빈과 수동 등록한 "memberDao2"빈이
    모두 존재한다. MemberDao타입의 빈이 두개가 생성되므로 자동 주입하는 코드는 
    @Qualifier 애노테이션을 사용해서 알맞게 빈을 선택해야함 주의~
