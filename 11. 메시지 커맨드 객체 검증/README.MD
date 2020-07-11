# 11 . MVC2: 메시지, 커맨드 객체 검증 :cat:

## 1. < spring:message> 태그로 메시지 출력하기 :dog:
##### 사용자 화면에 보일 문자열은 JSP에 직접코딩한다. 예를들어 로그인 폼을 보여줄 때 '아이디','비밀번호' 등의 문자열을 다음과같이 뷰코드에삽입
    <label>이메일</label>
    <input type="text" name="email">

    '이메일'과 같은 문자열은 로그인 폼,회원 가입 폼, 회원정보 수정폼에서 반복해서 사용된다.
    이렇게 문자열을 직접 하드코딩하면 동일 문자열을 변경할때 문제가있다. 
    예를들어 폼에서 사요할 '이메일'을 '이메일 주소'로 변경하기로 했다면 각 폼을 출력하는JSP를 찾아서
    모두 변경해야한다.

    문자열이 뷰 코드에 하드 코딩되어 있을때의 또다른 문제점은 다국어 지원에있다.
    전 세계를 대상으로 서비스를 제공해야하는경우 사용자의 언어설정에따라 '이메일','E-mail'과 같이 
    각언어에 맞게 문자열을 표시해야함. 그런데 뷰코드이 '이메일'이라고 문자열이 하드코딩되어
    있으면 언어별로 뷰코드를 따로 만드는 상황이 발생한다.

    두가지 문제를 해결하는 방법은 뷰코드에서 사용할 문자열을 언어별로 파일에 보관하고 뷰코드는
    언어에따라 알맞은 파일에서 문자열을 읽어와 출력하는것이다. 스프링은 자체적으로 이기능을
    제공하고있기때문에 약간의 수고만들이면 각언어별로 알맞은 문자열을 출력하도록 JSP코드를구현가능.

#### 문자열을 별도파일에 작서하고 JSP코드에서 이를 사용하려면 다음작업을 하면된다.
- 문자열을 담은 메시지파일을 작성한다.
- 메시지 파일에서 값을 읽어오는 MessageSource빈을 설저한다.
- JSP코드에서 < spring:message> 태그를 사용해서 메시지를 출력한다

### main/resources/message/label.properties에 다음과같이 작성하자
    member.register=회원가입
    term=약관
    term.agree=약관동의
    next.btn=다음단계

    member.info=회원저보
    email=이메일
    name=이름
    password=비밀번호
    password.confirm=비밀번호 확ㅇ니
    register.btn=가입완료

    register.done=<strong>{0}님</strong>,회원기입을 완료했습니다.
    go.main=메인으로이동


    //이후에 MvcConfig에서 다음메소드를 추가한다.
    @Bean
	public MessageSource messageSource(){
		ResourceBundleMessageSource ms=new ResourceBundleMessageSource();
		ms.setBasenames("message.label"); //message패키지에 속한 label프로퍼티파일로부터 메시지읽음.
		ms.setDefaultEncoding("UTF-8");
		return ms;
	}

    //주의할점은 빈의 아이디를 "messageSource"로 지정해야한다는것. 다른이름을사용할경우 동작하지않는다.
### 바뀐 JSp 코드들을 살펴보자.
    //step1.JSP
    <%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
    <%@ page contentType="text/html; charset=utf-8" %>

    <!DOCTYPE html>
    <html>
    <head>
        <title>><spring:message code="member.register"/></title>
    </head>
    <body>
        <h2><spring:message code="term"/></h2>
        <p>약관 내용</p>
        <form action="step2" method="post">
        <label>
            <input type="checkbox" name="agree" value="true">
            <spring:message code="term.agree"/>
        </label>
        <input type="submit" value="<spring:message code="next.btn"/>" />
        </form>
    </body>
    </html>

    //step2.jsp
    <%@ page contentType="text/html; charset=utf-8" %>
    <%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
    <%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
    <!DOCTYPE html>
    <html>
    <head>
        <title><spring:message code="member.register"/> </title>
    </head>
    <body>
        <h2><spring:message code="member.info"/></h2>
        <form:form action="step3" modelAttribute="registerRequest">
        <p>
            <label><spring:message code="email"/> <br>
            <form:input path="email" />
            </label>
        </p>
        <p>
            <label><spring:message code="name"/> <br>
            <form:input path="name" />
            </label>
        </p>
        <p>
            <label><spring:message code="password"/> <br>
            <form:password path="password" />
            </label>
        </p>
        <p>
            <label><spring:message code="password.confirm"/><br>
            <form:password path="confirmPassword" />
            </label>
        </p>
        <input type="submit" value="<spring:message code="register.btn"/> ">
        </form:form>


    //step3.jsp 
    <%@ page contentType="text/html; charset=utf-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
    <!DOCTYPE html>
    <html>
    <head>
        <title><spring:message code="member.register"/> </title>
    </head>
    <body>
        <p><spring:message code="register.done"
            arguments="${registerRequest.name}"/>
    </p>
        <p><a href="<c:url value='/main'/>">
            [<spring:message code="go.main"/> ]
        </a>
        </p>
    </body>
    </html>
- < spring:message> 커스텀 태그를 사용하기 위해 태그라이브러리 설정추가:
#### <%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
- < spring:message> 태그를 이용해서 메시지 출략

### 1.1 메시지 처리를 위한 MessageSource와 < spring:message>태그
##### 스프링은 지역에 상관없이 일관된 방법으로 메시지를 관리할수있는 MessageSource인터페이스를 정의한다.
##### 특정지역에 해당하는 메시지가 필요한 코드는 MessageSource의 getMessage()메소드를 이용해서 필요한 메시지를 가져와서 사용하는식이다.
    public interface MessageSource{
        String getMessage(String code,Object[] args,
        String defaultMessage,Locale locale);

        String getMessage(String code,Object[] args,Locale locale) throws NoSuchMessageException;
        ..//일부 메소드 생략
    }

    getMessage()메소드의 code파라미터는 메시지를 구분하기 위한 코드이고 locale파라미터는 지역을
    구분하기 위한 Locale이다. 같은 코드라 하더라도 지역에 따라 다른메시지를 제고할 수 있도록
    MessageSoruce를 설계했다. 이 기능을 사용하면 국내에서접근하면 한국어 해외에서는 영어로 처리를할수있다.
    
    MessageSource의 구현체로는 자바의 프로퍼티파일로부터 메시지를 읽어오는 ResourceBundleMessageSource
    클래스를 사용한다. 이클래스는 메시지코드와 일치하는 이름을가진 프로퍼티값을 메시지로제공.

    ResourceBundleMessageSource 클래스는 자바의 리소스번들(ResourceBundle)을 사용하기 때문에
    해당 프로퍼티 파일이 클래스 패스에 위치해야한다. 앞서 예제에서도 클래스 패스에 포함되는
    src/main/resources에 프로퍼티 파일을 위치시킴.

    <spring:message>태그는 스프링설정에 등록된 'messageSource'빈을 이용해 메시지를 구한다.
    즉 <spring:message> 태그를 실행하면 내부적으로 MessageSource의 getMessage()메소드를
    실행해서 필요한 메시지를 구한다. 

    <spring:message> 태그의 code속성에 지정한 메시지가 존재하지않으면 익셉션이 발생한다.
    익셉션이 발생하면 code값과 프로퍼티파일의 프로퍼티이름이 올바른지확인해야함.
### 1.2 < spring:message> 태그의 메시지 인자처리     :speak_no_evil: 
##### label.properties 파일을보면 다음과같은 프로퍼티를 포함하고있다
    register.done=<strong>{0}님</strong>,회원가입을 완료했습니다.
##### 이 프로퍼티는 값부분에 {0}을 포함한다. {0}은 인덱스 기반 변수중 0번인덱스의값으로 대치되는 부분을 표시한것.
##### MessageSource의 getMessage()메소드는 인덱스 기반변수를 전달하기위해 다음과같이 Object배열타입의 파라미터사용.
    String getMessage(String code,Object[] args,String defaultMessage,Locale locale);
    String getMessage(String code,Object[] args,Locale locale);
##### 위 메소드를 사용해 MessageSource빈을 직접실행한다면 다음과 같이Object배열을 생성해 인덱스기반 변수값전달가능        
    Object[] args=new Object[1];
    args[0]="자바";
    messageSource.getMessage("register.done",args,Locale.KOREA);
##### < spring:message>태그를 사용할때에는 arguments 속성을 사용해 인덱스 기반변수값을 전달한다.

## 2. 커맨드 객체의 값 검증과 에러메시지 처리 :apple:

#### 개발을 하다보면 폼값검증과 에러메시지 처리는 어플리케이션을 개발할때 놓쳐서는안된다.
#### `스프링은 이 두문제를 처리하기 위해 다음방법을 제공해준다`
- 커맨드 객체를 검증하고 결과를 에러코드로 저장
- JSP에서 에러코드로부터 메시지를 출력

### 2.1 커맨드 객체 검증과 에러코드 지정하기
##### 스프링 MVC에서 커맨드 객체의 값이 올바른지 검사하려면 다음의 두인터페이스를 사요한다.
- org.springframework.validation.Vlidator
- org.springframework.validation.Errors
#### 객체를 검증할때 사용하는 Vlidator인터페이스는 다음과같다.
    public interface Validator{
        boolean supports(Class<?> clazz);
        void validate(Object target,Errors errors);
    }
    
    이코드에서 supports()메소드는 Validator가 검증할수있는 타입인지 검사한다.
    validate()메소드는 첫번째 파라미터로 전달받은 객체를 검증하고 오류결과를 Errors에 담는기능을
    정의한다.
##### RegisterRequest객체를 검증하기위한 Validator구현클래스의 작성예를보자.
    public class RegisterRequestValidator implements Validator {
    private static final String emailRegExp =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" +
                    "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private Pattern pattern;
    public RegisterRequestValidator() {
        pattern = Pattern.compile(emailRegExp);
        System.out.println("RegisterRequestValidator#new(): " + this);
    }
    @Override
    public boolean supports(Class<?> clazz) {
        return RegisterRequest.class.isAssignableFrom(clazz);
    }
    @Override
    public void validate(Object target, Errors errors) {
        System.out.println("RegisterRequestValidator#validate(): " + this);
        RegisterRequest regReq = (RegisterRequest) target;
        if (regReq.getEmail() == null || regReq.getEmail().trim().isEmpty()) {
            errors.rejectValue("email", "required");
        } else {
            Matcher matcher = pattern.matcher(regReq.getEmail());
            if (!matcher.matches()) {
                errors.rejectValue("email", "bad");
            }
        }
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "required");
        ValidationUtils.rejectIfEmpty(errors, "password", "required");
        ValidationUtils.rejectIfEmpty(errors, "confirmPassword", "required");
        if (!regReq.getPassword().isEmpty()) {
            if (!regReq.isPasswordEqualToConfirmPassword()) {
                errors.rejectValue("confirmPassword", "nomatch");
             }
         }
        }
    }
##### 위 코드에서 궁금증이 있다. ValidationUtils.rejectIfEmptyOrWhitespace()메소드를 실행할 때 검사 대상 객체인 target을 파라미터로
##### 전달하지 않았는데 어떻게 target 객체의 "name"프로퍼티의 값을 검사할까???

    답은 Errors객체에 있다. 스프링MVC에서 Validator를 사용하는 코드는 위처럼 요청매핑 애노테이션
    적용 메소드에 Errors 타입 파라미터를 전달받고, 이 Errors객체를 Validator의 validate()
    메소드에 두번째 파라미터로 전달한다.
##### 이제 커맨드 객체를 검증하도록 수정해보자.
    //RegisterController의 handleStep3메소드
    @PostMapping("/register/step3")
    public String handleStep3(RegisterRequest regReq, Errors errors){
        new RegisterRequestValidator().validate(regReq,errors);
        if(errors.hasErrors())
            return "register/step2";
        try{
            memberRegisterService.regist(regReq);
            return "register/step3";
        }catch (DuplicateMemberException ex){
            errors.rejectValue("email","duplicate");
            return "register/step2";
        }
    }

    위처럼 요청 매핑 애노테이션 적용 메소드의 커맨드 객체 파라미터 뒤에 Errors 타입
    파라미터가 위히마녀, 스프링MVC는 handleStep3()메소드를 호출할때 커맨드 객체와 연결된
    Errors 객체를 생성해서 파라미터로 전달한다. 이 Errors객체는 커맨드 객체의 특정
    프로퍼티 값을 구할 수 잇는 getFieldValue()메소드를 제공한다.
    따라서 ValidationUtils.rejectIfEmptyOrWhitespace()메소드는 커맨드 객체를 전달받지않아
    도 Errors객체를 이용해서 값을 구할수있다.
### 2.2 Errors와 ValidationUtils 클래스의 주요 메소드
#### Errors 인터페이스가 제공하는 에러코드 추가메소드는 다음과같다.
- reject(String errorCode)
- reject(String errorCode,String defaultMessage)
- reject(String errorCode,Object[] errorArgs, String defaultMessage)
- rejectValue(String field,String errorCode)
- rejectValue(String field,String errorCode,String defaultMessage)
- rejectValue(String field,String errorCode,Object[] errorArgs, String default Message)    

---
    에러 코드에 해당하는 메시지가 {0}이나 {1}과 같이 인덱스 기반 변수를 포함하고 있는 경우
    Object 배열타입의 errorArgs 파라미터를 이용해서 변수에 삽입될 값을 전달한다.
    defaultMessage 파라미터를 가진 메소드를 사용하면, 에러코드에 해당하는 메시지가 존재하지
    않을 때 익셉션을 발생시키는 대신 defaultMessage를 출력한다.
##### ValidationUtils 클래스는 다음의 rejectIfEmpty()메소드와 rejectIfEmptyOrWhitespace()메소드를 제공한다.
- rejectIfEmpty(Errors errors, String field,String errorCode)
- rejectIfEmpty(Errors errors, String field, String errorCode,Object[] errorArgs)
- rejectIfEmptyOrWhitespace(Errors errors,String field,String errorCode)
- rejectIfEmptyOrWhitespace(Errors errors,String field,String errorCode,Object[] errorArgs)    

##### rejectIfEmpty()메소드는 field에 해당하는 프로퍼티 값이 null이거나 빈문자열("")인 경우 에러코드로 errorCode를 추가한다.
##### rejectIfEmptyOrWhitespace()메소드는 null이거나 빈 문자열인 경우 그리고 공백문자(스페이스,탭 등)로만 값이 구성된경우 에러코드를 추가한다.

### 2.3 커맨드 객체의 에러메시지 출력한다.
#### Errors에 에러코드를 추가하면 JSP는 스프링이 제공하는 < form:errors>태그를 사용해 에러에 해당하는 메시지출력가능.
    <form:errors> 태그의 path 속성은 에러 메시지를 출력할 프로퍼티 이름을 지정한다.
    예를들어 "email"프로퍼티에 에러코드가존재하면 <from:errors>태그는 에러코드에 해당하는 
    메시지를 출력한다. 에러코드가 두개이상 존재하면 각 에러코드는 해당하는 메시지가 출력된다.

    에러코드에 해당하는 메시지코드를 찾을때에는 다음규칙을 따르낟.
    1 에러코드+"."+커맨드객체이름+"."+필드명
    2 에러코드+"."+필드명
    3 에러코드+"."+필드타입
    4 에러코드

    프로퍼티 타입이 List나 목록인경우 다음순서를 사용해서 메시지코드를 생성한다.
    1 에러코드+"."+커맨드객체이름+"."+필드명[인덱스].중첩필드명
    2 에러코드+"."+커맨드객체이름+"."+필드명.중첩필드명
    3 에러코드+"."+필드명[인덱스].중첩필드명
    4 에러코드+"."+필드명.중첩필드명
    5 에러코드+"."+중첩필드명
    6 에러코드+"."+필드타입
    7 에러코드

    예를들어 errors.rejectValue("email","required")코드로 "email" 프로퍼티에 
    "require"에러코드를 추가했고 커맨드 객체의 이름이 "registerRequest"라면 다음순서대로 메시지
    코드를 검색한다.
    1 required.registerRequest.email
    2 required.email
    3 required.String
    4 required

    이중 가장먼저 검색되는 메시지코드를 사용한다. 즉메시지 중에 required.email 메시지 코드와
    required 메시지코드 두개가 존재하면 이중 우선순위가 높은 required.email 메시지 
    코드를 사용해서 메시지를 출력한다.
    특정 프로퍼티가 아닌 커맨드 객체에 추가한 글로벌 에러코드는 다음순서대로 메시지 코드를
    검색한다.
    1 에러코드+"."+커맨드객체이름
    2 에러코드
### 2.4 < form:errors> 태그의 주요속성
##### < form:errors>커스텀 태그는 프로퍼티에 추가한 에러코드 개수만큼 에러메시지를 출력한다.
##### 다음의 두속성을 사용해서 각 에러메시지를 구분해서 표시한다.
- element: 각에러메시지를 출력할때 사용할 HTML태그. 기본값은 span이다.
- delimeter: 각 에러메시지를 구분할때 사용할 html태그. 기본값은 < br/>이다.
#### 다음 코드는 두 속성의 사용예이다
    <form:errors path="userId" element="div" delimeter=""/>
    path 속성을 지정하지 않으면 글로벌 에러에 대한메시지를 출력한다    

## 3. 글로벌 범위 Validator와 컨트롤러 범위 Validator :banana:
##### 스프링 mvc는 모든 컨트롤러에 적용할수있는 글로벌 Validator와 단일컨트롤러에 적용할수있는 Validator를 설정하는 방법을제공한다.
##### 이를 사용하면 @Valid 애노테이션을 사용해서 커맨드 객체에 검증기능을 적용할수있다.
### 3.1 글로벌 범위 Validator 설정과 @Valid 애노테이션 
##### 글로벌 범위 Validator는 모든 컨트롤러에 적용할 수 있는 Validator이다. 글로벌 범위 Validator를 적용하려면 다음 두가지를 설정한다.
- 설정 클래스에서 WebMvcConfigurer의 getValidator() 메소드가 Validator구현객체를 리턴하도록 구현
- 글로벌 범위 Validator가 검증할 커맨드 객체에 @Valid 애노테이션 적용
---
    //글로벌 범위 Validator설정을 위한 WebMvcConfigurer인터페이스의 getValidator()메소드 구현
    @Configuration
    @EnableWebMvc
    public class MvcConfig implements WebMvcConfigurer{
        @Override
        public Validator getValidator(){
            return new RegisterRequestValidator();
        }
    }

    스프링mvc는 WebMvcConfigurer 인터페이스의 getValidator()메소드가 리턴한 객체를
    글로벌 범위 Validator로 사용한다. 글로벌 범위 Validator를 지정하면 @Valid 애노테이션을
    사용해서 Validator를 적용할수있다. 위코드에서 설정한 글로벌 범위 Validator인
    RegisterRequestValidator는 RegisterRequest타입에 대한 검증을 지원한다.  RegisterController 클래스의 
    handleStep3()메소드가 RegisterReqeust 타입 커맨드 객체를 사용하므로 아래와 같이 이 파라미터에
    @Valid 애노테이션을 붙여서 글로벌 범위 Validator를 적용할수있다.

    //@Valid 애노테이션을 이용한 예
    @Controller
    public class RegisterController{
        ...
        @PostMapping("/register/step3")
        public String handleStep3(@Valid RegisterRequest regReq, Errors errors){
            if(errors.hasErrors())
            return "register/step2";
            try{
                memberRegisterService.regist(regReq);
                return "register/step3";
            }catch(DuplicateMemberException ex){
                errors.rejectValue("email","duplicate");
                return "register/step2";
            }
        }
        ...
    }

    커맨드 객체에 해당하는 파라미터에 @Valid 애노테이션을 붙이면 글로벌 범위 Validator가
    해당 타입을 검증할 수 있는지 확인한다. 검증 가능하면 실제 검증을 수행하고 그결과를 Errors에
    저장한다. 이는 요청 처리 메소드 실행전에 적용된다.

    위 예의 경우 handleStep3()메소드를 실행하기 전에 @Valid 애노테이션이 붙은 regReq파라미터를
    글로벌 범위 Validator로 검증한다. 글로벌 범위 Validator가 RegisterRequest타입을 지원하므로
    regReq 파라미터로 전달되는 커맨드 객체에 대한 검증을 수행한다. 검증 수행결과는 Errors 타입
    파라미터로 받는다. 이과정은 handleStep3()메소드가 실행되기 전에 이뤄지므로 handleStep3()메소드는
    RegisterRequest객체를 검증하는 코드를 작성할 필요가없다. 파라미터로 전달받은 Errors를
    이용해서 검증에러가 존재하는지 확인하면된다.

    @Valid 애노테이션을 사욯라때 주의할점은 Errors타입 파라미터가 없으면 검증 실패시 400에러를
    응답한다는것이다.
### 3.2 @InitBinder 애노테이션을 이용한 컨트롤러 범위 Validator :cat2:
##### @InitBinder 애노테이션을 이용하면 컨트롤러 범위 Validator를 설정할수있다. 아래는 @InitBinder애노테이션을 사용해 컨트롤러 범위 
##### Validator를 설정한예이다.
    @Controller
    public class RegisterController{
        ..생략
        @PostMapping("/register/step3")
        public String handleStep3(@Valid RegisterRequest regReq, Errors errors){
            if(errors.hasErrors())
                return "register/step2";
            try{
                memberRegisterService.regist(regReq);
                return "register/step3";
            }catch(DuplicateMemberException ex){
                errors.rejectValue("email","duplicate");
                return "register/step2";
            }
        }
        @InitBinder
        protected void initBinder(WebDataBinder binder){
            binder.setValidator(new RegisterRequestValidatr();)
        }
    }
## 4. Bean Validation을 이용한 값 검증 처리 :bee:
##### @Valid 애노테이션은 Bean Validation 스펙에 정의 되어있다. 이 스펙은 @Valid 애노테이션뿐만아니라 @NotNull, @Digits,@Size 등의
##### 애노테이션을 정의하고있다. 애노테이션을 사용하면 Validator작성없이 애노테이션만으로 커맨드 객체의 값검증을 처리할수있다    

##### Bean Validation이 제공하는 애노테이션을 이용해서 커맨드 객체의 값을 검증하는 방법은 다음과같다.
- Bean Validation과 관련된 의존을 설정에 추가한다.
- 커맨드 객체에 @NotNull, @Digits 등으 애노테이션을 이용해서 검증규칙을 설정한다.
##### 가장 먼저 할작업은 Bean Validation 관련 의존을 추가하는 것이다. Bean Validation을 적용하려면 API를 정의한 모듈과 이 API를 구현한 프로바이더를
##### 의존으로 추가해야한다 아래는 pom.xml에 의존설정을 추가한예이다.
    <dependency>
        <groupId>javax.validation</groupId>
        <artifactId>validation-api</artifactId>
        <version>1.1.0.Final</version>
    </dependency>
     <dependency>
        <groupId>org.hibernate</groupId>
        <artifactId>hibernate-validator</artifactId>
        <version>5.4.2.Final</version>
    </dependency>
##### 커맨드 클래스는 다음과 같이 Bean Validation과 프로바이더가 제공하는 애노테이션을 이용해서 값 검증 규칙을 설정할수있다.
    public class RegisterRequest{
        @NotBlank
        @Email
        private String email;
        @Size(min=6)
        private String password;
        @NotEmpty
        private String confirmPassword;
        @NotEmpty
        private String name;
    }    
## 4.1 Bean Validation의 주요 애노테이션
##### Bean Validation1.1에서 제고하는 주요 애노테이션은 아래와같다. 모든 애노테이션은javax.validation.constraints 패키지에정의됨.
|애노테이션|주요속성|설명|지원타입|
|---|---|---|---|---|    
|@AssertTrue , @AssertFalse | |값이 true인지 또는 false인지 검사 null은 유효하다고판단|boolean, Boolean |
|@DecimalMax, @DecimalMin |String value(최대 또는 최소값), boolean inclusive(지정값포함여부, 기본값은 true) |지정한 값보다 작거나 같은지또는 크거나 같은지 검사. inclusive가 false면 value로 지정한 값은 포함하지않는다. null은유효하다고판단 |BigDecimal, BigInteger,CharSequence,정수타입 |
| @Max, @Min |long value | 지정한 값보다 작거나 같은지 또는 크거나 같은지 검사한다. null은 유효하다고 판단한다 | BigDecimal , BigInteger, 정수타입 |
|@Digits | int integer (최대 정수 자리수), int fraction (최대 소수점 자리수) | 자릿수가 지정한 크기를 넘지 않는지 검사한다. null은 유효하다고 판단한다. |BigDecimal, BigInteger, CharSequence, 정수타입|
|@Size |int min (최소크기, 기본값:0), int max(최대크기, 기본값: 정수최대값) | 길이나크기가 지정한값범위에 있는지 검사. null은유효하다고 판단 | CharSequence, Collection, Map, 배열|
|@Null, @NotNull |  |값이 null인지 또는 null이아닌지 검사| |
|@Pattern  | String regexp (정규 표현식) | 값이 정규표현식에 일치하는지 검사. null은 유효하다고 판단. |CharSequence |

    위의 표를보면 @NotNull을 제외한 나머지 애노테이션은 검사 대상값이 null인 경우 유효한 것으로
    판단하는 것을 알 수있다. 따라서 필수 입력값을 검사할때에는 다음과 같이 @NotNull과 @Size를
    함께 사용해야한다.

    @NotNull
    @Size(min=1)
    private String title;

##### 스프링 5버전은 Bean Validation 2.0을 지원하므로 아래표를보면된다.
|애노테이션|설명|지원타입|
|---|---|---|
|@NotEmpty |문자열이나 배열의 경우 null이아니고 길이가 0인지 검사한다. 콜렉션의 경우 null이 아니고 크기가 0이 아닌지 검사한다. | CharSequence, Collection , Map, 배열|
|@NotBlank |null이 아니고 최소한 한개 이상의 공백아닌 문자를 포함하는지 검사한다. |CharSequence |
|@Positive , @PositveOrZero | 양수인지 검사한다. OrZero가 붙은것은 0 또는 음수인지 검사한다. null은 유효하다고 판단한다. |BigDecimal, BigInteger, 정수타입 |
|@Negative , @NegativeOrZero |음수인지 검사한다. OrZero가 붙은 것은 0또는 음수인지 검사한다. null은 유효하다고 판단한다. | BigDecimal, BigInteger , 정수타입 |
|@Email | 이메일 주소가 유효한지 검사한다. null은 유효하다고 판단한다 |CharSequence |
|@Future , @FutureOrPresent |해당 시간이 미래 시간인지 검사한다. OrPresent가 붙은것은 현재 또는 미래시간인지 검사한다. null은 유효하다고 판단한다| 시간관련타입|
| @Past, @PastOrPresent| 해당 시간이 과거시간인지 검사한다. OrPresent가 붙은것은 현재 또는 과거시간인지 검사한다. null은 유효하다고 판단한다. | 시간관련타입 |