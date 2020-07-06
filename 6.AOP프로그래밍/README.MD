# AOP(Aspect Oriented Programming) 프로그래밍


### 이 예제는 pom.xml파일에 aspectjweaver의존을 추가합니다. 
#### 이모듈은 AOP구현할때 사용하는 모듈


#### 1. 예제 세팅
    public interface Calculator{
        public long factorial(long num);
    }

    //////////////////////////////////

    public class ImpeCalculator implements Calculator {
    public long factorial(long num) {
        long result=1;
        for(long i=1; i<=num; i++)
            result*=i;
        return result;
        }
    }


    /////////////////////////////
    public class RecCalculator implements Calculator{
    public long factorial(long num) {
        if(num==0)
            return 1;
        else
            return num*factorial(num-1);
       }
    }

## 2. 프록시와 AOP
##### `앞서 구현한 두 클래스의 실행시간을 비교해보자. `
    쉬운방법은 메소드의 시작과 끝에서 시간을 구하고 시간차를 출력하면된다.

    ImpeCalculator impe=new ImpeCalculator();
    long start1=System.currentTimeMills();
    long fact1=impe.factorial(4);
    long end1=System.currentTimeMills();
    System.out.printf("impecalculator 실행시간= %d",(end1-start1));

    RecCalculator rec=new RecCalculator();
    long start2=System.currentTimeMills();
    long fact2=rec.factorial(4);
    long end2=System.currentTimeMillse();
    System.out.printf("rec 실행시간 =%d",(end2-start2));

##### 음... 기존코드 수정하지않고 코드중복 피할수있는 방법이 있지않을까??
#### `이때 사용하는것이 프록시 객체이다`

    public class ExeTimeCalculator implements Calculator {
    private Calculator delegate;
    public ExeTimeCalculator(Calculator delegate)
    {
        this.delegate=delegate;
    }    
    public long factorial(long num) {
        long start=System.nanoTime();
        long result=delegate.factorial(num);
        long end=System.nanoTime();
        System.out.printf("%s.factorial(%d) 실행 시간 =%d\n",
                delegate.getClass().getSimpleName(),num,(end-start));
        return result;
        }
    }

    위의 ExeTimeCalculator클래스를 사용하면 다음과 같은 방법으로 ImpeCalculator 
    시간을 측정할 수 있다.

    ImpeCalculator impe=new ImpeCalculator();
    ExeTimeCalculator calc=new ExeTimeCalculator(impe);
    long result=calc.factorial(4);

### 위의 코드에서 calc.factorial()을 실행하면 다음과 같은 순서로 코드가 실행된다.

![image](https://user-images.githubusercontent.com/40031858/86575888-ebd65e80-bfb2-11ea-9f9b-76662a2570b5.png)

    public class MainProxy {
    public static void main(String[] args)
    {
        ExeTimeCalculator cal1=new ExeTimeCalculator(new ImpeCalculator());
        System.out.println(cal1.factorial(20));

        ExeTimeCalculator cal2=new ExeTimeCalculator(new RecCalculator());
        System.out.println(cal2.factorial(20));
        }
    }
    결과가 깔삼하게 나옴.
#### `위의 결과에서 다음을 알 수 있습니다.`
    1. 기존코드를 변경하지 않고 실행시간을 출력할 수 잇다. ImpeCalculator 클래스나
    RecCalculator 클래스의 코드 변경없이 이 두클래스의 factorial()메소드 실행시간을 출력할수 
    있게되었당.
    2. 실행시간을 구하는 코드의 중복을 제거하였다. 

    이것들이 가능한 이유는 ExeTimeCalculator클래스를 다음과 같이 구현했기 때문이다.
    - factorial()기능자체를 직접구현하기보다는 다른객체에 factorial()의 실행을 위임한다.
    - 계산기능외에 다른 부가적인 기능을 실행한다.(시간측정)

### `핵심 기능의 실행은 다른객체에 위임하고 부가적인 기능을 제공하는 객체를 프록시라고 부른다`
### `실제 핵심기능을 실행하는 객체는 대상객체라고 부른다`
#### 위의 예제에서 ExeTimeCalculator가 프록시이고 ImpeCalculator객체가 프록시의 대상객체가 된다. 

<code>

    프록시의 핵심 기능은 구현하지 않는다는 점이다. 
    ImpeCalculator나 RecCalculator는 팩토리얼 연산이라는 핵심기능을 구현하지만
    ExeTimeCalculator는 팩토리얼 연산자체를 구현하지않는다.
    즉, 프록시는 핵심기능을 구현하지 않는 대신 여러 객체에 공통으로 적용할 수 있는 기능을 구현한다.

    공통기능과 핵심기능 구현을 분리하는것이 AOP의 핵심이라 볼 수 있다.

</code>

## 2.1 AOP
#### `AOP는 Aspect Oriented Programming의 약자로 여러 객체에 공통으로 적용할수있는 기능을`
#### `분리해서 재사용성을 높여주는 프로그래밍 기법이다 AOP는 핵심기능과 공통 기능의 `
#### `구현을 분리함으로써 구현한 코드의 수정없이 공통기능을 적용할 수 있게 만들어준다`

#### AOP기본 개념은 핵심기능에 공통기능을 삽입하는 것이다. 핵심기능의 코드를 수정하지 않으면서
#### 공통기능의 구현을 추가하는 것이 aop이다. 핵심기능에 공통기능을 추가하는 방법은 다음과같다.
- 컴파일 시점에 코드에 공통 기능을 삽입하는 방법
- 클래스 로딩시점에 바이트 코드에 공통기능을 삽입하는 방법
- 런타임에 프록시 객체를 생성해서 공통 기능을 삽입하는 방법

#### 이 중 스프링이 제공하는 AOP방식은 프록시를 이용한 세번째방법이다.

##### 프록시 방식은 위의 예제처럼 중간에 프록시 객체를 생성하고 실제 객체의 기능실행 전,후에 공통기능을 호출한다.
##### 스프링 AOP는 프록시 객체를 자동으로 만들어준다. 상위타입의 인터페이스를 상속받은 프록시 클래스를 직접 구현할필요가없다. 
#### `단지 공통기능을 구현한 클래스만 알맞게 구현하면 된다`

#### AOP에서 공통기능을 Aspect라고 하는데 Aspect외에 알아둬야할 용어를 표로정리해본다.
| 용어 | 의미 | 
|---|:---:|
|`Advice`| 언제 공통 관심 기능을 핵심 로직에 적용할지 정의하고있다. 예를 들어 메소드 호출전에 트랜젝션 시작 기능을 적용한다는것 정의  |
|`Joinpoint` | Advice를 적용 가능한 지점을 의미한다. 메소드호출, 필드 값 변경 등이 JoinPoint에 해당한다. 스프링은 프록시를 이용해 AOP를 구현하기 때문에 메소드 호출에 대한 JoinPoint만 지원 | 
|`Pointcut` | Joinpoint의 부분집합으로서 실제 Advice가 적용되는 Joinpoint를 나타낸다. 스프링에서는 정규 표현식이나 AspectJ의 문법을 이용해 Pointcut을 정의할수있다. | 
|`Weaving` | Advice를 핵심 로직코드에 적용하는것을 의미한다 | 
|`Aspect` | 여러 객체에 공통으로 적용되는 기능을 Aspect라고한다. 트랜젝션이나 보안등이 Aspect의 예.|

## 2.2 Advice의 종류

| 종류 | 설명 | 
|---|---|
|`Before Advice`| 대상 객체의 메소드 호출 전에 공통기능을 실행한다.  |
|`After Returning Advice` | 대상 객체의 메소드가 익셉션 없이 실행된 이후에 공통기능을 실행 | 
|`After Throwing Advice` | 대상 객체의 메소드를 실행하는 도중 익셉션이 발생한경우 공통기능실행 | 
|`After Advice` | 익셉션 발생 여부에 상관없이 대상 객체의 메소드 실행 후 공통 기능을실행 try-catch의 finally와비슷 | 
|`Around Advice` | 대상 객체의 메소드 실행 전, 후 또는 익셉션 발생 시점에 공통기능을 실행하는데 사용|

    이 중 널리 사용된는 것은 Around Advice이다. 이유는 대상 객체의 메소드를 실행 하기 전/후
    익셉션 발생 시점 등 다양한 시점에 원하는 기능을 삽입할 수 있기 때문이다.
    캐시 기능, 성능 모니터링 기능과 같은 Aspect를 구현할때에는 Around Advice를 주로이용.

## 3.스프링 AOP구현
##### 스프링 aop를 이용해서 공통기능을 구현하고 적용하는 방법은 단순하다. 다음과같은 절차만 따르자.
- Aspect로 사용할 클래스에 @Aspect 애노테이션을 붙인다.
- @Pointcut애노테이션으로 공통기능을 적용할 Pointcut을 정의한다.
- 공통기능을 구현한 메소드에 @Around애노테이션을 적용한다.
### 3.1 @Aspect,@Pointcut,@Around를 이용한 AOP구현

    @Aspect
    public class ExeTimeAspect {
	@Pointcut("execution(public * chap06..*(..))")
        private void publicTarget(){
        }
        @Around("publicTarget()")
        public Object measure(ProceedingJoinPoint joinPoint)throws Throwable{
            long start=System.nanoTime();
            try{
                Object result=joinPoint.proceed();
                return result;
            }finally {
                long finish=System.nanoTime();
                Signature sig=joinPoint.getSignature();
                System.out.printf("%s.%s(%s) 실행시간: %d ns\n",
                        joinPoint.getTarget().getClass().getSimpleName(),
                        sig.getName(), Arrays.toString(joinPoint.getArgs()),
                        (finish-start));
            }
        }
    }

    위 코드를 보자.
    @Pointcut은 공통기능을 적용할 대상을 설정한다. 현재 패키지에 있는 모든 타입의 
    public 메소드를 Pointcut으로 설정하였다

    @Around 애노테이션은 Around Advice를 설정한다. @Around 애노테이션의 값이
    "publicTarget()"인데 이는 publicTarget()메소드에 정의한 Pointcut에 공통기능을 적용한다는 
    것을 의미한다. publicTarget()와 마찬가지로 패키지에 속한 빈객체의 public 메소드에
    @Around 가 붙은 measure()메소드를 적용한다.

    measure()메소드의 ProceedingJoinPoint타입 파라미터는 프록시 대상 객체의 메소드를
    호출할 때 사용한다. 

#### 해당 디렉토리에 첨부된 예제의 샘플코드를 살펴보자 .(AppCtx와 MainAspect클래스 )
##### @Aspect 애노테이션을 붙인 클래슬르 공통 기능으로 적용하려면 @EnableAspectJAutoProxy 애노테이션을 설정클래스에 붙여야한다.
##### 이 애노테이션을 추가하면 스프링은 @Aspect애노테이션이 붙은 빈객체를 찾아 빈객체의 @Pointcut설정고 @Around설정을 사용한다.   


### 3.2 ProceedingJoinPoint의 메소드

    Around Advice에서 사요할 공통기능 메소드는대부분 파라미터로 전달받은 ProceedingJoinPoint의
    proceed()메소드만 호출하면 된다.  예를들어 아래에서도 proceed()메소드를 호출했당.

    public class ExeTimeAspect{
        public Object measure(ProceedingJoinPoint joinPoint) throws Throwable{
            long start=System.nanoTime();
            try{
                Object result=joinPoint.proceed();
                return result;
            }finally{
                ..생략스!
            }
        }
    }

    물론 호출되는 대상 객체에 대한 정보, 실행되는 메소드에 대한 정보, 메소드를 호출 할 때
    전달된 인자에 대한 정보가 필요할 때가 있다. 이들정보에 접근할수잇도록 ProceedingJoinPoint
    인터페이스는 다음메소드를 제공해준다
    - Signature getSignature():호출되는 메소드에 대한 정보를 구한다
    - Object getTarget():대상객체를구한다.
    - Object[] getArgs():파라미터 목록을 구한다.

    org.aspectj.lang.Signature 인터페이스는 다음메소드를 제공한다.

    - String getName(): 호출되는 메소드의 이름을 구한다
    - String toLongString(): 호출되는 메소드를 완전하게 표현한 문장을 구함.(메소드의 리턴타입,
    파라미터 타입 모두표시댐)
    - String toShortString(): 호출되는 메소드를 축약해서 표현한 문장을 구함.(기본구현은 메소드의
    이름만 구함)
## 4.프록시 생성방식    

#### 예제코드에서 다음과 같이 변경해보자(MainAspect클래스 )
    //수정 전
    Calculator cal=ctx.getBean("calculator,Calculator.class);
    //수정 후
    RecCalculator cal=ctx.getBean("calculator",RecCalculator.class);

    코드 수정 후에 MainAspect클래스를 실행해보자. 정상실행될거란 예상과 달리 익셉션이 발생한다.


    스프링은 AOP를 위한 프록시 객체를 생성할 때 실제 생성할 빈 객체가 인터페이스를 상속하면
    인터페이스를 이용해서 프록시를 생성한다. 앞서 예에서도 RecCalculator클래스가 Calculator
    인터페이스를 상속하므로 Calculator 인터페이스를 상속받은 프록시 객체를 생성했다.

    따라서 아래 코드처럼 빈의 실제타입이 RecCalculator라고 하더라도 "calculator"이름에 해당하는
    빈 객체의 타입은 Calculator 인터페이스를 상속받은 프록시타입이된다.

    //Appctx.class
    //AOP적용시 RecCalculator가 상속받은 Calculator인터페이스를 이용해서 프록시 생성
    @Bean
    public Calculator calculator(){
        return new RecCalculator();
    }

    //자바코드:
    //"calculator"빈의 실제 타입은 Calculator를 상속한 프록시 타입이므로
    //RecCalculator로 타입변환을 할 수없기때문에 익셉센발생
    RecCalculator cal=ctx.getBean("calculator",RecCalculator.class);

##### 빈 객체가 인터페이스를 상속할때 인터페이스가 아닌 클래스를 이용해서 프록시를 생성하고싶다면 다음과 같이 설정하면된다.
    @Configuration
    @EnableAspectJAutoProxy(proxyTargetClass=true)
    public class AppCtx{

    @EnableAspectJAutoProxy애노테이션의 proxyTargetClass 속성을 true로 지정하면 인터페이스가 아닌
    자바 클래스를 상속받아 프록시를 생성한다. 스프링이 프록시를 이용해 생성한 빈객체를
    구할 때 다음과 같이 getBean()메소드에 실제 클래스를 이용해서 빈객체를 구할수있게된다.

    @Configuration
    @EnableAspectJAutoProxy(proxyTargetClass=true)
    public class AppCtx{
        ...생략스
    }
    //자바 코드, "calculator" 프록시의 실제 타입은 RecCalculator를 상속받았으므로
    //RecCalculator로 타입변환가능
    RecCalculator cal=ctx.getBean("calculator",RecCauculator.class);

## 4.1 execution 명시자 표현식
##### 앞서 Aspect를 적용할 위치를 지정할때 사용한 Pointcut 설정을 보면 execution 명시자를 사용했다.
     @Pointcut("execution(public * java ..*(..))")
    private void publicTarget(){
    }
#### execution 명시자는 Advice를 적용할 메소드를 지정할때 사용한다. 기본형식은 다음과 같다.
    execution(수식어패턴? 리턴타입패턴 클래스이름패턴? 메소드이름패턴(파라미터패턴))

    '수식어패턴'은 생략가능하며 public,protected등이온다. 스프링 AOP는 Public메소드에만
    적용할수 있기때문에 사실상 Public만의미있음..   
    '리턴타입패턴'은 리턴타입을 명시한다. '클래스이름패턴'과 '메소드이름패턴'은 클래스 이름및
    메소드 이름을 패턴으로 명시한다. '파라미터패턴'은 매칭될 파라미터에 대해서 명시
    각패턴은 '*'을 이용하여 모든 값을 표현할수잇다. 또한 '..'을 이용하여 0이상이라는 의미를 표현할수있다.

`몇가지 예시를 보자`

| 예 | 설명 | 
|---|:---:|
|`execution(public void set*(..))`| 리턴타입이 void이고, 메소드 이름이 set으로 시작하고, 파라미터가 0개 이상인 메소드호출. 파라미터 부분에 '..'을 사용하여 파라미터가 0개이상인 것을 표현했당.  |
|`execution(* chap07 .*.*())` | chap07 패키지의 타입에 속한 파라미터가없는 모든메소드호출 | 
|`execution(* chap07 ..*.*(..))` | chap07 패키지 및 하위 패키지에 있는, 파라미터가 0개 이상인 메소드 호출. 패키지 부분에 '..'을 사용하여 해당 패키지 또는 하위패키지를 표현 | 
|`execution(Long chap07.Calculator.factorial(..))` | 리턴 타입이 Long인 Calculator타입의 factorial() 호출 | 
|`execution (* get*(*))` | 이름이 get으로 시작하고 파라미터가 한개인 메소드 호출|
|`execution (* get*(*,*))`   | 이름이 get으로 시작하고 파라미터가 두개인메소드호출   |
|`exection(* read*(Integer, ..))`   | 메소드 이름이 read로 시작하고, 첫 번째 파라미터 타입이 Integer이며, 한 개이상의 파라미터를 갖는 메소드 호출   |

## 4.2 Advice 적용순서
#### 한 Pointcut에 여러 Advice를 적용할수도있다.(CacheAspect,AppCtxWithCache,MainAspectWithCache 클래스 참고)

    @Aspect
    public class CacheAspect {
        private Map<Long, Object> cache = new HashMap<>();
        @Pointcut("execution(public * chap06..*(long))")
        public void cacheTarget() {
        }
        @Around("cacheTarget()")
        public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
            Long num = (Long) joinPoint.getArgs()[0];
            if (cache.containsKey(num)) {
                System.out.printf("CacheAspect: Cache에서 구함[%d]\n", num);
                return cache.get(num);
            }
            Object result = joinPoint.proceed();
            cache.put(num, result);
            System.out.printf("CacheAspect: Cache에 추가[%d]\n", num);
            return result;
        }
    }

    CacheAspect클래스는 간단하게 캐시를 구현한 공통기능이다. 동작순서는 아래와같다.
    - 9행 :첫번째 인자를 long타입으로 구한다.
    - 10행~13행 :9행에서 구한키값이 cache에존재하면 키에해당하는값리턴
    - 14행: 10행에서구한값이 cache에 존재하지않으면 프록시대상객체를실행
    - 15행: 프록시 대상객체를 실행한 결과를 cache에추가
    - 17행: 프록시 대상객체의 결과리턴
    @Around 값으로 cacheTarget()메소드를 지정했다. @Pointcut설정은 첫번째 인자가 long인
    메소드를 대상으로한다. 따라서 execute()메소드는 앞서 작성한 Calculator의 factorial(long)
    메소드에 적용된다. 


##### 어떤 Aspect가 먼저 적용될지는 스프링 프레임워크나 자바버전에 따라 달라질 수 있다.
##### 이렇기에 적용순서가 중요하다면 직접 순서를 적용해야한다.이럴때 사용하는것이 @Order애노테이션이다.

    @Aspect애노테이션과 함께 @Order애노테이션을 클래스에 붙이면 @Order애노테이션에 지정한 값에 따라 적용
    순서를 결정한다.

    @Order애노테이션의 값이 작으면 먼저적용하고 크면 나중에적용한다.

## 4.2 @Around의 Pointcut설정과 @Pointcut재사용     
##### @Pointcut애노테이션이 아닌 @Around 애노테이션에 execution 명시자를 직접 지정할수있다.
    @Aspect
    public class CacheAspect {
	@Pointcut("execution(public * chap06..*(long))")
	public void cacheTarget() {
	    }
    }


    만약 같은 Pointcut을 여러 Advice가 함께 사용한다면 공통 Pointcut을 재사용할 수도있다.
    다시 ExeTimeAspect를보자.

    @Aspect
    public class ExeTimeAspect {
	@Pointcut("execution(public * chap06..*(..))")
	private void publicTarget() {
	}

	@Around("publicTarget()")
	public Object measure(ProceedingJoinPoint joinPoint) throws Throwable {
        ...
        }
    }
    
    이 코드에서 @Around는 publicTarget()메소드에 설정한 Pointcut을 사용한다.
    publicTarget()메소드는 private인데 이경우 같은 클래스에 있는 @Around 애노테이션에서만
    해당 설정을 사용할 수있다. 다른 클래스에 위치한 @Around 애노테이션에서 publicTarget()
    메소드의 Pointcut을 사용하고 싶다면 publicTarget()메소드를 public으로 바꾸면된다.

    @Aspect
    public class ExeTimeAspect {
	@Pointcut("execution(public * chap06..*(..))")
	public void publicTarget() {
	    }
    }


    그리고 해당 Pointcut의 완전한 클래스 이름을 포함한 메소드 이름을 @Around 애노테이션에서 사용하면 된다.
    예를들어 다음 설정은 CacheAspect 클래스의 @Around 메소드에서 ExeTimeAspect 클래스의 publicTarget()
    에 정의된 Pointcut을 사용한다.

    @Aspect
    public class CacheAspect{
        @Around("aspect.ExeTimeAspect.publicTarget()")
        public Object execute(ProceedingJoinPoint joinPoint) throws Throwable{

        }
    }

    CacheAspect와 ExeTimeAspect클래스는 같은 패키지에 위치하므로 패키지 이름이 없는
    간단한 클래스 이름으로 설정가능하다. 


    @Aspect
    publicclass CacheAspect{
        @Around("ExeTimeAspect.publicTarget()")
        public Object execute(ProceedingJoinPoint joinPoint) throws Throwable{

        }
    }
    여러 Aspect에서 공통으로 사용하는 Pointcut이 있다면 별도 클래스에 Pointcut을 정의하고
    각 Aspect클래스에서 해당 Pointcut을 사용하도록 구성하면 Pointcut관리가 편해짐.
