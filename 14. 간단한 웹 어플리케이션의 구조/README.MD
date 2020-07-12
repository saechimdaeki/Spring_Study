# 간단한 웹 어플리케이션의 구조 :wave: 
#### 간단한 웹어플리케이션을 개발할때 사용하는 전형적인 구조는 다음요소를 포함한다
- 프론트 서블릿
- 컨트롤러+ 뷰
- 서비스
- DAO
##### 프론트 서블릿은 웹브라우저의 모든요청을 받는 창구역할을 한다. 프론트 서블릿은 요청을 분석해서
##### 알맞은 컨트롤러에 전달한다. 스프링MVC에서는 DispatcherServlet이 프론트 서블릿의 역할을 수행한다.
![image](https://user-images.githubusercontent.com/40031858/87238668-8e259480-c440-11ea-80eb-1a43323fe0c1.png)

##### 컨트롤러는 실제 웹 브라우저의 요청을 처리한다. 지금까지 구현했던 스프링컨트롤러가 이에해당.
##### 컨트롤러는 클라이언트(브라우저)의 요청을 처리하기 위해 알맞은 기능을 실행하고 그 결과를 뷰에전달한다. 
#### `컨트롤러의 주요역할은 다음과같다`
- 클라이언트가 요구한 기능을 실행
- 응답결과를 생서하는데 필요한 모델 생성
- 응답결과를 생성할 뷰 선택

##### 컨트롤러는 어플리케이션이 제공하는 기능과 사용자 요청을 연결하는 매개체로서 기능 제공을 위한 로직을 직접 수행하지는 않는다.
##### 대신 해당로직을 제공하는 서비스에 그처리를 위임한다. 예를들어 앞서 작성했던 ChangePasswordController의 경우 다음코드 처럼
##### ChangePasswordService에 비밀번호 변경처리를 위임했다.

    @PostMapping
    public String submit(
        @ModelAttribute("command") ChangePwdCommand pwdCmd,
        Errors errors, HttpSession session){
            new ChangePwdCommandValidator().validate(pwdCmd,errors);
            if(errors.hasErrors())
            return "edit/changePwdForm";

            AuthInfo authInfo=(AuthInfo) session.getAttribute("authInfo");
            try{
                //컨트롤러는 로직 실행을 서비스에 위임한다.
                changePasswordService.changePassword(
                    authInfo.getEmail(),
                    pwdCmd.getCurrentPassword(),
                    pwdCmd.getNewPassword());
                    return "edit/changePwd";
            }catch(IdPasswordNotMatchingException e){
                errors.rejectValue("currentPassword","notMatching");
                return "edit/changePwdForm";
            }
        }
#### 서비스는 기능의 로직을 구현한다. 사용자에게 비밀번호 변경 기능을 제공하려면 수정폼을 제공하고, 로그인 여부를 확인하고,
#### 실제로 비밀번호를 변경해야한다. 이중에서 핵심로직은 비밀번호를 변경하는것이다.

##### 서비스는 DB여농이 필요하면 DAO를 사용한다. DAO는 Data Access Object의 약자로서 DB와 웹 어플리케이션 간에 데이터를 이동시켜 주는
##### 역할을 맡는다. 어플리케이션은 DAO를 통해서 DB에 데이터를 추가하거나 DB에서 데이터를 읽어온다.
##### 목록이나 상세화면과 같이 데이터를 조회하는 기능만 있고 부가적인 로직이 없는경우에는 컨트롤러에서 직접 DAO사용하기도함.

## 2.서비스의 구현
##### 서비스는 핵심이되는 기능의 로직을 제공한다. 예를들어 비밀번호 변경기능은 다음로직을 서비스에서 수행한다.
- DB에서 비밀번호를 변경할 회원의 데이터를 구한다.
- 존재하지않으면 익셉션을 발생시킨다.
- 회원데이터의 비밀번호를 변경한다.
- 변경내역을 db에반영한다.
##### 웹 어플리케이션을 사용하든 명령행에서 실행하든 비밀번호 변경기능을 제공하는 서비스는 동일한 로직을 수행한다.
#### 위 예처럼 몇단계의 과정을 거치는데 중간과정에서 실패가 나면 이전까지 했던 것을 취소해야하고 모든 과정을 성공적으로
##### 진행했을때 완료해야한다. 이런이유로 서비스 메소드를 트랜잭션 범위에서 실행한다.
#### 비밀번호 변경 기능도 다음과 같이 스프링의 @Transactional을 이용해서 트랜잭션 범위에서 비밀번호 변경기능을 수행했다.
    @Transactional
    public void changePassword(String email,String oldPwd,String newPwd){
        Member member=memberDao.selectByEmail(email);
        if(member==null)
        throw new MemberNotFoundException();
        member.changePassword(oldPwd,newPwd);
        memberDao.update(member);
    }

##### 같은 데이터를 사용하는 기능들을 한 개의 서비스 클래스에 모아서 구현할수도있다. 예를 들어 회원 가입 기능과 비밀번호 변경기능은
##### 모두 회원에 대한 기능이므로 다음과 같이 MemberService라는 클래스를 만들어 회원과 관련된 모든 기능을 제공하도록 구현할수있을것이다.
    public class MemberService{
        ...
        @Transactional
        public void regist(RegisterRequest req){...}

        @Transactional
        public void changePassword(String email,String oldPwd,String newPwd){...}
    }
##### 서비스 클래스의 메소드는 기능을 실행하는데 필요한 값을 파라미터로 전달받는다. 예를 들어 비밀번호 변경 기능을 제공한 메소드는 다음과 같이
##### 세개의 파라미터를 이용해 기능 실행에 필요한 값을 전달받았다.
    public void changePassword(String email,String oldPwd,String newPwd)
##### 회원가입 기능은 다음과 같이 필요한 데이터를 담고 있는 별도의 클래스를 파라미터로 사용했다
    public void regist(RegisterRequest req)
##### 필요한 데이터를 전달받기 위해 별도 타입을 만들면 스프링 MVC의 커맨드 객체로 해당타입을 사용할 수 있어 편하다.
##### 회원 가입 요청을 처리하는 컨트롤러 클래스의 코드는 다음과 같이 서비스 메소드의 입력 파라미터로 사용되는 타입을 커맨드 객체로 사용했다
    @PostMapping("/register/step3")
    public String handleStep3(RegisterRequest regReq, Errors errors){
        ...
        memberRegisterService.regist(regReq);
        ...
    }      
##### 비밀번호 변경의 changePassword()메소드 처럼 웹 요청 파라미터를 커맨드 객체로 받고 커맨드 객체의 프로퍼티를 서비스 메소드에 인자로 전달할수도있다.
    @RequestMapping(method=RequestMethod.POST)
    public String submit(
        @ModelAttribute("command") ChangePwdCommand pwdCmd,
        Errors errors, HttpSession session){
            ...
            changePasswordService.changePassword(
                authInfo.getEmail(),
                pwdCmd.getCurrentPassword(),
                pwdCmd.getNewPassword());
            ...
        }
##### 커맨드 클래스를 작성한 이유는 스프링 MVC가 제공하는 폼 값 바인딩과 검증, 스프링 폼 태그와의 연동기능을 사용하기 위함이다.
##### 서비스 메소드는 기능을 실행한 후에 결과를 알려주어야한다. 결과는 크게두가지방식으로 알려준다.
- 리턴 값을 이용한 정상결과
- 익셉션을 이용한 비정상 결과
##### 위 두가지를 잘보여주는 예를보자.
    public class AuthService{
        ...생략
        public AuthInfo authenticate(String email, String password){
            Member member= memberDao.selectByEmail(email);
            if(member==null)
            throw new WrongIdPasswordException();
            if(!member.matchPassword(password)){
                throw new WrongPasswordException();
            }
            return new AuthInfo(member.getId(),member.getEmail(), member.getName());
        }
    }        

##### AuthService 클래스의 authenticate()메소드를 보면 리턴타입으로 AuthInfo를 사용하고있다. authenticate()메소드는 인증에 성공할 경우 인증 
##### 정보를 담고있는 AuthInfo객체를 리턴해서 정상적으로 실행되었음을 알려준다. 물론 비밀번호 변경처럼 리턴 타입이 void인 경우는 익셉션이 발생하지
##### 않은 것이 정상적으로 실행된 것을 의미한다
---
##### authenticate()메소드는 인증 대상 회원이 존재하지 않거나 비밀번호가 일치하지 않는 경우 WrongIdPasswordException을 발생시킨다.
##### 따라서 authenticate()를 실행하는 코드는 이 메서드가 익셉션을 발생하면 인증에 실패했다는 것을 알 수 있다. 
##### 실제 LoginController 클래스는 다음과 같이익셉션이 발생한 경우 이를 인증실패로보고 알맞게 처리한것을 확인할수있당.
    @RequestMapping(method=RequestMethod.POST)
    public String submit(
        LoginCommand loginCommand, Errors errors, HttpSession session,
        HttpServletResponse response){
            ...
            try{
                AuthInfo authInfo = authService.authenticate(
                    loginCommand.getEmail(),
                    loginCommand.getPassword());

                session.setAttribute("authInfo",authInfo);
                ...
                return "login/loginSuccess";    
            }catch(WrongIdPasswordException e){
                //서비스는 기능실행에 실패할경우 익셉션을 발생시킴
                errors.reject("idPasswordNotMatching");
                return "login/loginForm";
            }
        }
## 3. 컨트롤러에서의 DAO접근
##### 서비스 메소드에서 어떤 로직도 수행하지 않고 단순히 DAO의 메소드만 호출하고 끝나는 코드도있다.
##### 예를 들어 회원 데이터 조회를 위한 서비스 메소드를 다음과 같이 구현하곤 한다.
    public class MemberService{
        ...
        public Member getMember(Long id){
            return memberDao.selectById(id);
        }
    }
##### 이 코드에서 MemberService 클래스의 getMember()메소드는 MemberDao의 selectByEmail()메소드만 실행할 뿐 추가로직은 없다.
##### 컨트롤러 클래스는 이서비스 메소드를 이용해서 회원 정보를 구하게된다.
    @RequestMapping("/member/detail/{id}")
    public String detail(@PathVariable("id") Long id,Model model){
        //사실상 DAO를 직접호출하는것과 동일
        Member member=memberService.getMember(id);
        if(member==null)
        return "member/notFound";
        model.addAttribute("member",member);
        return "member/memberDetail";
    } 
##### 위 코드에서 memberSevice.getMember(id)코드는 사실상 memberDao.selectById()메소드를 실행하는 것과 동일하다.
##### 이 경우 컨트롤러는 서비스를 사용해야 한다는 압박에서 벗어나 다음과 같이 DAO에 직접 접근해도 큰틀에서 웹 어플리케이션의 계층구조는 유지된다.
    @RequestMapping("/member/detail/{id}")
    public String detail(@PathVariable("id") Long id, Model model){
        Member member=memberDao.selectByEmail(id);
        if(member==null)
        return "member/notFound";
        model.addAttribute("member",member);
        return "member/memberDetail";
    }

