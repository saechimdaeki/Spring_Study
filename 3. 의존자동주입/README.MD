# 스프링DI
- 한 클래스가 다른클래스의 메소드를 실행할때 이를 '의존'한다고 표현한다.
(DI는 의존하는 객체를 직접 생성하는 대신 의존객체를 전달받는 방식을 사용한다.)

        의존은 변경에 의해 영향을 받는 관계를 의미한다. 예를들어 MemberDao의 insert()메소드의 이름을 insertMember()로 변경하면 이 메소드를 사요하는 MemberRegisterService클래스의 소스코드도 함께 변경된다. 이렇게 변경에 따른 영향이 전파되는 관계를 '의존'한다고 표현한다.

- 의존하는 대상이 있으면 그대상을 구하는 방법도 필요하다. 
이중 제일쉬운방법은 객체를 직접생성하는것. 
하지만 이문제는 유지보수관점에서 문제점을 유발할 수 있다.

      DI를 통한 의존처리를 할 수 있다. (의존하는 객체를 직접생서하는 대신 의존 객체를
       전달받는 방식)
      ex) public MemberRegisterService(MemberDao memberDao){
          this.memberDao=memberDao;
      }
------------------
    - DI를 적용한 결과 MemberRegisterService클래스를 사용하는 코드는 
    다음과 같이 MemberRegisterService객체를 생성할때 생성자에 MemberDao객체를 전달해야한다. 코드가 더길어지는데도 DI를 하는 이유는 변경의 유연함 때문이다. 

        MemberDao dao=new MemberDao();
        //의존객체를 생성자를 통해주입.
        MemberRegisterService svc=new MemberRegisterService(dao)

## 스프링을 이용한 객체 조립과 사용 

    - @Configuration 애노테이션은 스프링 설정 클래스를 의미한다. 
    이 애노테이션을 붙여야 스프링 설정 클래스로 사용가능하다. 
    - @Bean 애노테이션은 해당 메소드가 생성한 객체를 스프링 빈이라고 설정한다. 
    ex)memberDao()메소드를 이용해서 생성한 빈 객체는 "memberDao"라는 이름으로
     스프링에 등록된다.
    - @Authowired 애노테이션은 스프링 빈에 의존하는 다른빈을 자동으로 주입하고 싶을때 사용.
------------
#### DI방식
-  1. 생성자 식 
- 2. SETTER 메소드식.


### 궁금증 

     아래의 코드에서 memberDao()가 새로운 MemberDao 객체를 생성해서 리턴하므로 
     memberRegSvc()에서 생성한 MemberRegisterService객체와  changePwdSvc()에서 생성한 ChangePasswordService객체는 서로다른 MemberDao 객체를 사용하는 것아닌가???

        @Bean
        public MemberDao memberDao(){
            return new Memberao();
        }
        @Bean
        public MemberRegisterService memberRegSvc(){
            return new MemberRegisterService(memberDao());
        }
        @Bean
        public ChangePasswordService changePwdSvc(){
            ChangePasswordService pwdSvc=new ChangePasswordService();
            pwdSvc.setMemberDao(memberDao());
            return pwdSvc;
        }

## 위의 궁금증에 대한 답변:

    스프링컨테이너는 @Bean이 붙은 메소드에 대해 한개의 객체만 생성한다. 
    이는 다른 설정 메소드에서 memberDao()를 몇번을 호출하더라도 항상 같은 객체를 리턴한다는 것을 의미한다.
    이게 어떻게 가능하나면 스프링은 설정클래스를 그대로 사용하지않는다.
     대신 설정 클래스를 상속한 새로운 설정 클래스를 만들어 사용한다. 
     스프링이 런타임에 생성한 설정클래스의 memberDao()메소드는 매번 새로운 객체를 생성하지 않고 한번 생성한 객체를 보관했다가 이후에는 동일한 객체를 리턴한다. 
    따라서 memberRegSvc()메소드와 changePwdSvc()메소드에서 memberDao()메소드를
     각각 실행해도 동일한 MemberDao 객체를 사용한다.

## @Autowired 애노테이션.

#### @Autowired 애노테이션은 스프링빈에 의존하는 다른빈을 자동으로 주입하고 싶을때 사용한다.

    @Autowired
    private MemberDao memberDao;
    
    여기서 @Autowired 애노테이션은 스프링의 자동주입 기능을 위한것이다. 
    스프링 설정클래스의 필드에 @Autowired 앤테이션을 붙이면 해당타입의 
    빈을 찾아서 필드에 할당한다. 위 설정의 경우 스프링 컨테이너는 MemberDao타입의 빈을 memberDao 필드에 할당한다.
    이렇듯 @Autowired 애노테이션을 이용해서 다른 설정파일에 정의한 빈을 필드에 
    할당했다면 설정 메소드에서 이필드를 사용해서 필요한 빈을 주입하면 된다. 
    또한 , @Autowired 애노테이션을 의존 주입대상에 붙이면 세터메소드를 사용해서 의존 주입을 
    하지않아도 스프링 컨테이너가 @Autowired를 붙인 필드에 자동으로 해당 타입의 빈 객체를 주입한다.

## @Import 애노테이션

    @Autowired 같은 두개이상의 설정파일을 사용하는 또다른 방법은 @Import 애노테이션을 사용하는 것이다.  @Import 애노테이션은 함께 사용할 설정 클래스를 지정한다. 

-------------
##### 예를들어,

        @Configuration
        @Import(AppConf2.class)
        public class AppConfImport{
            @Bean
            public MemberDao memberDao(){
                return new MemberDao();
            }
        }
        이 소스에서 AppConfImoprt 설정 클래스를 사용하면, @Import 애노테이션으로 지정한 
        AppConf2 설정 크래스도 함께 사용하기 때문에 스프링 컨테이너를 생성할때 AppConf2 
        설정 클래스를 지정할 필요가 없다. AppConfImport 클래스만 사용하면 AppCof2클래스의 
        설정도 함께 사용해서 컨테이너를 초기화한다.
        //Tip: @Import({AppConf1.class,AppConf2.class}) //이렇게 배열을 이용해서 두개이상의 설정클래스도 지정할수있다.

### 주입 대사 객체를 모두 빈객체로 설정해야 하나?

        @Configuration
        public class AppCtx{
            private MemberPrinter printer=new MemberPrinter(); //빈이아님.
            @Bean
            Public MemberPrinter listPrinter(){
                return new MemberListPrinter(memberDao(),printer);
            }
        }
        위는 정상적으로 동작한다.

        객체를 스프링 빈으로 등록할때와 등록하지않을 때의 차이는 스프링 컨테이너가 객체를 관리하는지 여부이다. 다만 위코드와 같이 설정하면 MemberPrinter를 빈으로 등록하지않으므로 스프링 컨테이너에서 MemberPrinter를 구할수없다
        MemberPrinter printer=ctx.getBean(MemberPrinter.class); //MemberPrinter를 빈으로 등록하지않았으므로 익셉션발생.

#### 스프링에서 의존 자동주입을 설정하려면 @Autowired 애노테이션이나 @Resource애노테이션을 사용한다.

    자동 주입 기능을 사용하면 스프링이 알아서 의존 객체를 찾아서 주입한다. 예를 들어 자동 주입을 사용하면 의존 객체를 명시하지 않아도 스프링이 필요한 의존 빈객체를 찾아서 주입해준다

-----------------------------------------

        @Bean
        public MemberDao memberDao(){
            return new MemberDao();
        }
        @Bean
        public ChangePasswordService changePwdSvc(){
            ChangePasswordService pwdSvc=new ChangePasswordService();
            pwdSvc.setMemberDao(memberDao());
            return pwdSvc;
        }
        위의 코드를 자동주입 기능을 사용한다면 아래코드와 같이 의존객체를 직접 명시하지않아도 스프링이 의존객체를 주입해준다.
        @Bean
        public MemberDao memberDao(){
            return new MemberDao();
        }
        @Bean
        public ChangePasswordService changePwdSvc(){
            ChangePasswordService pwdSvc=new ChangePasswordService();
            return pwdSvc;
        }

- Autowired 애노테이션을 필드나 세터 메소드에 붙으면 스프링은 타입이 일치하는 빈객체를 찾아서 주입한다.

### @Qualifier 애노테이션을 이용한 의존 객체 선택
    - 자동 주입 가능한 빈이 두 개 이상이면 자동 주입할 빈을 지정할 수 있는 방법이 필요한데 이때 @Qualifier애노테이션을 사용한다. @Qualifier애노테이션을 사용하면 자동 주입 대상 빈을 한정할수있다.

    @Autowired 애노테이션도 @Qualifier 애노테이션이 없으면 필드나 파라미터 이름을 한정자로 사용한다. 
    아래의 코드는 printer필드에 일치하는 빈이 두 개 이상 존재하면 한정자로 필드 이름인 "printer"를 사용한다.

    Public class MemverInfoPrinter2{
        @Autowired
        private MemberPrinter printer;
        ...생략.
    }
    
    

### 자동 주입할 대상이 필수가 아닌경우에는 @Autowired 애노테이션의 required 속성을 다음과 같이 false로 지정하면 된다

    public class MemberPrinter {
	private DateTimeFormatter dateTimeFormatter;
	public void print(Member member){
        ...생략
	}
	@Autowired(required = false)
	public void setDateTimeFormatter(DateTimeFormatter dateTimeFormatter){
		this.dateTimeFormatter=dateTimeFormatter;
	    }

    }
    이렇게 하면 매칭되는 빈이 없어도 익셉션이 발생하지 않으며 자동주입을 수행하지않는다. 위예에서 DateTimeFormatter 타입의 빈이 존재하지않으면 익셉션을 발생하지 않고 setDateFormatter()메소드를 실행하지 않는다. 
    
    Spring5이후는 @Autowired 애노테이션의 required속성을 false로 하는 대신에 의존 주입 대상에 자바8의 Optional을 사용해도 된다.
    @Autowired
	public void setDateTimeFormatter(Optional<DateTimeFormatter> dateTimeFormatter){
		if(formatterOpt.isPresent()){
            this.dateTimeFormatter=formatterOpt.get();
        }else{
            this.dateTimeFormatter=null;
            }
	    }
    }
    // 자동 주입 대상 타입이 Optional인 겨우 일치하는 빈이 존재하지 않으면 값이 없는 Optional을 인자로 전달하고 (익셉션발생X) 일치하는 빈이 존재하면 해당 빈을 값으로 갖는 Optional을 인자로 전달한다. 위의코드는 isPresent()메소드가 true이면 값이 존재하므로 해당값을 dateTimeFormatter필드에 할당한다.

- 필수 여부를 지정하는 세번째 방법은 @Nullable 애노테이션을 사용하는 것이다.

        public class MemberPrinter {
    	private DateTimeFormatter dateTimeFormatter;
	    public void print(Member member){
            ...생략
    	}
	    @Autowired
	    public void setDateTimeFormatter(@Nullable DateTimeFormatter dateTimeFormatter){
		    this.dateTimeFormatter=dateTimeFormatter;
	        }
        }
        @Autowired 애노테이션을 붙인 새터 메소드에서 @Nullable 애노테이션을의존 주입 대상 
        파라미터로 붙이면, 스프링 컨테이너는 세터 메소드를 호출할때 자동 주입할 빈이 존재하면
         해당 빈을 인자로 전달하고, 존재하지 않으면 인자로 null을 전달. 
      
------------
### 차이점

        일치하는 빈이 없으면 값 할당 자체를 하지않는 @Autowired(required=false)와 달리
        @Nullable 애노테이션을 사용하면 일치하는 빈이 없을때 null값을 할당한다. 유사하게
        Optional타입은 매칭되는 빈이 없으면 값이 없는 Optional을 할당한다. 기본생성자에서
        자동 주입 대상이 되는 필드를 초기화 할때는 이점에 유의해야한다.