# MVC3: 세션, 인터셉터, 쿠키 :cookie: 
## 1.로그인 처리를 위한 코드준비
##### 이번 챕터에서는 세션, 인터셉터, 쿠키에 관해 설명하는데 로그인 기능을 이용해 설명할것이므로 로그인과 관련된 코드를만들어보자
    //로그인 성공후 인증상태 정보를 세션에 보관할때 사용할 AuthInfo클래스
    public class AuthInfo {
        private Long id;
        private String email;
        private String name;
        public AuthInfo(Long id,String email, String name){
            this.id=id;
            this.email=email;
            this.name=name;
        }
        public Long getId() {
            return id;
        }
        public String getEmail() {
            return email;
        }
        public String getName() {
            return name;
        }
    }

    //암호 일치여부를 확인하기 위한 matchPassword()메소드를 member클래스에 추가하자.
    public class Member{
        private Long id;
        private String email;
        ....생략

        public boolean matchPassword(String password){
            return this.password.equals(password);
        }
    }

    //이메일과비밀번호가 일치하는지 확인해서 AuthInfo객체를 생성하는 AuthService클래스를 작성하자
        public class AuthService {
        private MemberDao memberDao;

        public void setMemberDao(MemberDao memberDao) {
            this.memberDao = memberDao;
        }
        public AuthInfo authenticate(String email,String password){
            Member member=memberDao.selectByEmail(email);
            if(member==null)
                throw new WrongIdPasswordException();
            if(!member.matchPassword(password))
                throw new WrongIdPasswordException();
            return new AuthInfo(member.getId(),member.getEmail(),member.getName());
        }
    }


    //이제 AuthService를 이용해서 로그인 요청을 처리하는 LoginController클래스를 작성하자.
    폼에 입력값을 전달받기위한 LoginCommand클래스.
         public class LoginCommand {
        private String email;
        private String password;
        private boolean rememberEmail;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public boolean isRememberEmail() {
            return rememberEmail;
        }

        public void setRememberEmail(boolean rememberEmail) {
            this.rememberEmail = rememberEmail;
        }
    }


    //폼에 입력된 값이 올바른지 검사하기 위한 LoginCommandValidator클래스
    public class LoginCommandValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz){
        return LoginCommand.class.isAssignableFrom(clazz);
    }
    @Override
    public void validate(Object target, Errors errors){
        ValidationUtils.rejectIfEmptyOrWhitespace(errors,"email","required");
        ValidationUtils.rejectIfEmpty(errors,"password","required");
        }
    }

    //로그인 요청을 처리하는 LoginController 클래스
    @Controller
    @RequestMapping("/login")
    public class LoginController {
        private AuthService authService;
        public void setAuthService(AuthService authService){
            this.authService=authService;
        }
        @GetMapping
        public String form(LoginCommand loginCommand){
            return "login/loginForm";
        }
        @PostMapping
        public String submit(LoginCommand loginCommand, Errors errors){
            new LoginCommanderValidator().validate(loginCommand,errors);
            if(errors.hasErrors())
                return "login/loginForm";
            try{
                AuthInfo authInfo=authService.authenticate(
                        loginCommand.getEmail(),
                        loginCommand.getPassword()
                );
                return "login/loginSuccess";
            }catch (WrongIdPasswordException e){
                errors.reject("idPasswordNotMatching");
                return "login/loginForm";
            }
        }
    }

#### ps. jsp코드는 직접 디렉토리를 열어확인해보자.
##### 이제 컨트롤러와 서비스를 스프링 빈으로 등록하자. 
    //MemberConfig 클래스
    @Configuration
    @EnableTransactionManagement
    public class MemberConfig{
    ..생략.
    @Bean
	public AuthService authService(){
		AuthService authService=new AuthService();
		authService.setMemberDao(memberDao());
		return authService;
	    }
    }

    //ControllerConfig 클래스에 추가.
    @Autowired
	private AuthService authService;
	
	@Bean
	public LoginController loginController(){
		LoginController controller=new LoginController();
		controller.setAuthService(authService);
		return controller;
	}
#### 이제 톰캣서버를 돌려 실행하면 된다. http://localhost:8080/12__MVC3____war/login (주소)

## 2. 컨트롤러에서 HttpSession 사용하기
#### 로그인 기능을 구현했는데 한가지빠진것이있다. 그것은 바로 로그인 상태를 유지하는것이다.
##### 로그인 상태를 유지하는 방법은 크게 HttpSession을 이용하는 방법과 쿠키를 이용하는 방법이 있다.
##### 외부 데이터베이스에 세션 데이터를 보관하는 방법도 사용하는데 큰틀에서 보면 HttpSession과 쿠키의 두가지 방법으로 나뉜다.
##### 이번챕터 에서는 HttpSession을 이용해서 로그인 상태를 유지하는 코드를추가하자.

#### `컨트롤러에서 HttpSession을 사용하려면 다음의 두가지 방법중 한가지를 사용하면된다`
- 요청 매핑 애노테이션 적용 메소드에 HttpSession 파라미터를 추가한다.
- 요청 매핑 애노테이션 적용 메소드에 HttpServletRequest파라미터를 추가하고 HttpServletRequest를 이용해 HttpSessio을구함.
#### 다음은 첫번째 방법을 사용한 코드 예이다.
    @PostMapping
    public String from(LoginCommand loginCommand,Errors erros, HttpSession session){
        ..// session을 사용하는 코드
    }

    요청 매핑 애노테이션 적용 메소드에 HttpSession 파라미터가 존재할 경우 스프링 MVC는
    컨트롤러 메소드를 호출할때 HttpSession 객체를 파라미터로 전달한다.
    HttpSession을 생성하기 전이면 새로운 HttpSession을 생성하고 그렇지 않으면 기존에
    존재하는 HttpSession을 전달한다.
#### 두번째 방법은 다음코드처럼 HttpServletRequest의 getSession()메소드를 이용하는것이다.
    @PostMapping
    public String submit(
        LoginCommand loginCommand,Errors errors,HttpServletRequest req){
            HttpSession session=req.getSession();
            ..//session을 사용하는 코드
        }
#### `첫번째 방법은 항상 HttpSession을 생성하지만 두번째 방법은 필요시점만 HttpSession을 생성할수있다.`

#### 그렇다면 LoginController 코드에서 인증후에 인증정보를 세션에담도록 submit()메소드 코드를 수정하자.
    //로그인에 성공하면 HttpSession의 "authInfo"속성에 인증정보객체를 저장하도록함.
    @PostMapping
    public String submit(LoginCommand loginCommand, Errors errors, HttpSession session){
        new LoginCommanderValidator().validate(loginCommand,errors);
        if(errors.hasErrors())
            return "login/loginForm";
        try{
            AuthInfo authInfo=authService.authenticate(
                    loginCommand.getEmail(),
                    loginCommand.getPassword()
            );
            session.setAttribute("authInfo",authInfo);
            return "login/loginSuccess";
        }catch (WrongIdPasswordException e){
            errors.reject("idPasswordNotMatching");
            return "login/loginForm";
        }
    }


    //로그아웃을 위한 컨트롤러 클래스는 HttpSession을제거하면된다.
    @Controller
    public class LogoutController {
        @RequestMapping("/logout")
        public String logout(HttpSession session){
            session.invalidate();
            return "redirect:/main";
        }
    }

#### 이제 다시 톰캣서버를 실행시킨후 로그인후에 main으로 이동하면 사용자정보를 출력해준다.
#### 로그인전이라면 "authInfo"속성이 존재하지않으므로 환영합니다[회원가입][로그인]을출력.
![image](https://user-images.githubusercontent.com/40031858/87225732-01d88a80-c3ca-11ea-94aa-b3d6b3899f48.png)

## 3. 비밀번호 변경 기능 구현
#### 비밀번호 변경할때 현재비밀번호와 새비밀번호를 입력받게 구현하므로 이를 구현해보자
    public class ChangePwdCommand {
        private String currentPassword;
        private String newPassword;

        public String getCurrentPassword() {
            return currentPassword;
        }

        public void setCurrentPassword(String currentPassword) {
            this.currentPassword = currentPassword;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }
    }

    //ChangePwdCommand 객체를 검증할 ChangePwdCommandValidator 클래스

    public class ChangePwdCommandValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz){
        return ChangePwdCommand.class.isAssignableFrom(clazz);
    }
    @Override
    public void validate(Object target, Errors errors){
        ValidationUtils.rejectIfEmptyOrWhitespace(
                errors, "currentPassword","required"
        );
        ValidationUtils.rejectIfEmpty(errors,"newPassword","required");
        }
    }

   //비밀번호 변경요청을 처리하는 컨트롤러 클래스
        @Controller
    @RequestMapping("/edit/changePassword")
    public class ChangePwdController {

        private ChangePasswordService changePasswordService;

        public void setChangePasswordService(
                ChangePasswordService changePasswordService) {
            this.changePasswordService = changePasswordService;
        }

        @GetMapping
        public String form(
                @ModelAttribute("command") ChangePwdCommand pwdCmd) {
            return "edit/changePwdForm";
        }

        @PostMapping
        public String submit(
                @ModelAttribute("command") ChangePwdCommand pwdCmd,
                Errors errors,
                HttpSession session) {
            new ChangePwdCommandValidator().validate(pwdCmd, errors);
            if (errors.hasErrors()) {
                return "edit/changePwdForm";
            }
            AuthInfo authInfo = (AuthInfo) session.getAttribute("authInfo");
            try {
                changePasswordService.changePassword(
                        authInfo.getEmail(),
                        pwdCmd.getCurrentPassword(),
                        pwdCmd.getNewPassword());
                return "edit/changedPwd";
            } catch (WrongIdPasswordException e) {
                errors.rejectValue("currentPassword", "notMatching");
                return "edit/changePwdForm";
            }
        }
    }
### jsp코드는 생략
    //이후에 ControllerConfig에 아래와같이 추가한다.
    @Autowired 
	private ChangePasswordService changePasswordService;
	@Bean
	public ChangePwdController changePwdController(){
		ChangePwdController controller=new ChangePwdController();
		controller.setChangePasswordService(changePasswordService);
		return controller;
	}
#### 이제 확인할차례다 비밀번호 변경 기능을 테스트하려면 다음순서대로 실행하면된다.
- http://localhost:8080/12__MVC3____war/login 으로 로그인 실행
- 메인화면 (http://localhost:8080/12__MVC3____war/main) 에서로그인확인
- 메인화면 에서 비밀번호 변경링크를 눌러서 변경폼으로 이동
- 비밀번호 변경
- 메인화면에서 로그아웃 후 다시로그인시도
- 비밀번호가 변경되었는지 확인
#### 주의할점!
    //서버를 재시작하면 세션이 삭제되기때문에 아래코드는 null을 리턴한다.
    AuthInfo authInfo=(AuthInfo) session.getAttribute("authInfo");
## 4.인터셉터 사용하기
##### 로그인하지않은상태에서 http://localhost:8080/12__MVC3____war/edit/changePassword 주소를 입력해보자.
##### 비밀번호 변경폼이 출력되는데 로그인하지않았는데 변경폼이 출력되는것은 이상하다. 그것보다는 로그인하지않은 상태에서 비밀번호 변경폼을
##### 요청하면 로그인 화면으로 이동시키는것이 더좋은방법이다.

##### 이를위해 다음과같이 HttpSession에 "authInfo"객체가 존재하는지 검사하고 존재하지 않으면 로그인 경로를 리다이렉트하도록 
##### ChangePwdController 클래스를 수정할수있다.
    @GetMapping
    public String form(
        @ModelAttribute("command") ChangePwdCommand pwdCmd,
        HttpSession session){
            AuthInfo authInfo=(AuthInfo) session.getAttribute("authInfo");
            if(authInfo==null)
            return "redirect:/login";
            return "edit/changePwdForm";
        }

    그런데 실제 웹어플리케이션에서는 비밀번호 변경 기능외에 더많은 기능에 로그인 여부를 확인해야한다.
    각 기능을 구현한 컨트롤러 코드마다 세션확인 코드를 삽입하면 너무많은 중복이일어난다.

    이렇게 다수의 컨트롤러에대해 동일한 기능을 적용해야할때 사용할수있는것이 HandlerInterceptor이다.

### 4.1 HandlerInterceptor 인터페이스 구현하기
##### org.springframework.web.HandlerInterceptor 인터페이스를 사용하면 다음의 세시점에 공통기능을 넣을수있다.
- 컨트롤러(핸들러) 실행 전
- 컨트롤러(핸들러) 실행 후, 아직 뷰를 실행하기전
- 뷰를 실핸한 이후
##### 세 시점을 처리하기 위해 HandlerInterceptor 인터페이스는 다음메소드를 정의하고있다.
    - boolen preHandle(
        HttpServletRequest request,
        HttpServletResponse response,
        Object handler) throws Exception;
    - void postHandle(
        HttpServletRequest request,
        HttpServletResponse response,
        Object handler,
        ModelAndView modelAndView) throws Exception;
    - void afterCompletion(
        HttpServletRequest request,
        HttpServletResponse response,
        Object handler,
        Exception ex) throws Exception;

##### preHandle()메소드는 컨트롤러(핸들러) 객체를 실행하기 전에 필요한 기능을 구현할때사용한다.
##### handler파라미터는 웹요청을 처리할 컨트롤러(핸들러) 객체이다. 이메소드를 사용하면 다음작업이 가능하다
- 로그인하지않은 경우 컨트롤러를 실행하지않음
- 컨트롤러를 실행하기 전에 컨트롤러에서 필요로 하는 정보를 생성
##### preHandle()메소드의리턴타입은 boolean이다. preHandle()메소드가 false를 리턴하면 컨트롤러(또는 다음 HandlerInterceptor)를 실행하지않는다.

    postHandle()메소드는 컨트롤러(핸들러)가 정상적으로 실행된 이후에 추가기능을 구현할때 사용한다.
    컨트롤러가 익셉션을 발생하면 postHandle()메소드는 실행하지않는다.

    afterCompletion() 메소드는 뷰가 클라이언트에 응답을 전송한 뒤에 실행된다.
     컨트롤러 실행 과정에서 익셉션이 발생하면 이 메소드의 네번째 파라미터로 전달된다. 익셉션이
     발생하지 안흐면 네번째 파라미터는 null이된다. 따라서 컨트롤러 실행이후에 예기치 않게
     발생한 익셉션을 로그로 남긴다거나 실행 시간을 기록하는등의 후처리를 하기에 적합한메소드이다.

    HandlerInterceptor 인터페이스의 각메소드는 아무기능도구현하지않은 자바8으 디폴트메소드이다.
    따라서 HandlerInterceptor 인터페이스의 메소드를 모두구현할 필요가없다.
    이 인터페이스를 상속받고 필요한 메소드만 재정의하면된다.

    비밀번호 변경 기능에 접근할때 HandlerInterceptor를 사용하면 로그인여부에 따라 로그인
    폼으로 보내거나 컨트롤러를 실행하도록 구현할수있다. 여기서 만들 HandlerInterceptor구현
    클래스는 preHandle()메소드를 사용한다. HttpSession에 "authInfo"속성이 존재하지않으면
    지정한 경로로 리다이렉트하도록 구현하면된다. 구현코드는 아래와같다.

    public class AuthCheckInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		HttpSession session = request.getSession(false);
		if (session != null) {
			Object authInfo = session.getAttribute("authInfo");
			if (authInfo != null) {
				return true;
			}
		}
		response.sendRedirect(request.getContextPath() + "/login");
		return false;
    	}
    } 

    preHandle()메소드는 HttpSession에 "authInfo"속성이 존재하면 true를 리턴한다.
    존재하지않으면 리다이렉트 응답을 생성한뒤 false를리턴. preHandle()메소드에서 true를
    리턴하면 컨트롤러를 실행하므로 로그인 상태면 컨트롤러를 실행한다. 반대로 false를리턴하면
    로그인상태가아니므로 지정한경로로 리다이렉트한다.

### 4.2 HandlerInterceptor설정하기.
##### HandlerInterceptor를 구현하면 HandlerInterceptor를 어디에 적용할지설정해야한다.
    //MvcConfig 에 다음을 추가하자
    @Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(authCheckInterceptor())
			.addPathPatterns("/edit/**")
			.excludePathPatterns("/edit/help/**");
	}    

    WebMvcConfigurer#addInterceptors()메소드는 인터셉터를 설정하는 메소드다.

    InterceptorRegistry#addInterceptor()메소드는 HandlerInterceptor 객체를 설정한다.

    InterceptorRegistry#addInterceptor()메소드는 InterceptorRegistration 객체를 리턴하는데
    이 객체의 addPathPatterns()메소드는 인터셉터를 적용할 경로 패턴을 지정한다.
    이경로는 Ant경로 패턴을 사용한다. 두개 이상 경로패턴을 지정하려면 각경로패턴을 콤마로 구분해서 지정.

## 5.컨트롤러에서 쿠키사용하기.
##### 사용자 편의를 위해 아이디를 기억해두었다가 다음에 로그인할때 아이디를 자도으로 넣어주는 사이트가많다.
##### 이기능을 사용할때 쿠키를사용한다.
    //loginCommand에 rememberEmail을 추가한다.
    public class LoginCommand {

	private String email;
	private String password;
	private boolean rememberEmail;

    - LoginController의 form()메소드: 쿠키가 존재할경우 폼에 전달할 커맨드 객체의 email프로퍼티를
    쿠키의 값으로 설정
    - LoginController의 submit()메소드: 이메일기억하기 옵션을 선택한경우 로그인성공후에
    이메일을 담고있는 쿠키를 생성

    //이제 LoginController를 수정하자.
    @Controller
    @RequestMapping("/login")
    public class LoginController {
    private AuthService authService;

    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping
    public String form(LoginCommand loginCommand,
    		@CookieValue(value = "REMEMBER", required = false) Cookie rCookie) {
		if (rCookie != null) {
			loginCommand.setEmail(rCookie.getValue());
			loginCommand.setRememberEmail(true);
		}
    	return "login/loginForm";
    }

    @PostMapping
    public String submit(
    		LoginCommand loginCommand, Errors errors, HttpSession session,
    		HttpServletResponse response) {
        new LoginCommandValidator().validate(loginCommand, errors);
        if (errors.hasErrors()) {
            return "login/loginForm";
        }
        try {
            AuthInfo authInfo = authService.authenticate(
                    loginCommand.getEmail(),
                    loginCommand.getPassword());
            
            session.setAttribute("authInfo", authInfo);

			Cookie rememberCookie = 
					new Cookie("REMEMBER", loginCommand.getEmail());
			rememberCookie.setPath("/");
			if (loginCommand.isRememberEmail()) {
				rememberCookie.setMaxAge(60 * 60 * 24 * 30);
			} else {
				rememberCookie.setMaxAge(0);
			}
			response.addCookie(rememberCookie);

            return "login/loginSuccess";
        } catch (WrongIdPasswordException e) {
            errors.reject("idPasswordNotMatching");
            return "login/loginForm";
        }
    }
    }    
---
    @CookieValue 애노테이션의 value속성은 쿠키의이름을 지정한다. 위는 REMEMBER인 쿠키를
    Cookie타입으로 전달받는다. 지정한 이름을 가진 쿠키가 존재하지않을수도있다면 required속성값을
    false로 지정한다.

    실제로 REMEMBER쿠키를 생성하는 부분은 로그인을 처리하는 submit()메소드이다.
    .쿠키를 생성하려면 HttpServletResponse객체가 필요하므로 submit()메소드의 파라미터로 
    HttpServletResponse 타입을 추가한다.    