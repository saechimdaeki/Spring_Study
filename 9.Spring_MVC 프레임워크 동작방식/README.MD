# 9.스프링 MVC 프레임워크 동작방식💣
#### 이번 챕터는 정리용 Readme만 제공합니다.😄

##### 앞 챕터에서 예제코드를 실행하기 위해 사용한 스프링MVC설정이다.
    @Configuration
    @EnableWebMvc
    public class MvcConfig implements WebMvcConfigurer{
        @Override
        public void configureDefaultServletHandling(
            DefaultServletHandlerConfigurer configurer){
                configurer.enable();
            }
        )
        @Override
        public void configureViewResolvers(ViewResolverRegistry registry){
            registry.jsp("/WEB-INF/view/",".jsp");
        }
    }

    위 설정을 하면 남은작업은 컨트롤러와 뷰생성을 위한 JSP코드를 작성하는 것이다.
    개발자는 스프링MVC가 어떻게 컨트롤러를 실행하고 뷰를 찾는지 자세히 알지 못해도 어느정도
    스프링 MVC를 이용해서 웹 어플리케이션을 개발해 나갈수있다.

    단순해 보이는 이 설정은 실제로 백여줄에 가까운 설정을 대신 만들어주는데 이것모두를 알필요는없다.
    하지만 스프링MVC를 구성하는 주요 요소가 무엇이고 각 구성요소들이 서로 어떻게 연결되는지 이해하면 
    다양한 환겨에서 스프링 MVC를 빠르게 적용하는데 많은도움이된다.

## 1. 스프링 MVC 핵심구성요소
##### `스프링 MVC의 핵심 구성요소와 각 요소간의 관계는 아래와같이 정리할수잇당.`
![image](https://user-images.githubusercontent.com/40031858/87031014-62769480-c21d-11ea-8006-16d60ad31680.png)

    위의 그림에서 <<spring bean>>이라고 표시한 것은 스프링 빈으로 등록해야 하는 것을 의미한다.
    회색 배경을 가진 구성요소는 개발자가 직접 구현해야 하는요소다. 예를 들어 컨트롤러 구성요소는
    개발자가 직접 구현해야 하고 스프링 빈으로 등록해야한다. 앞 챕터에서 구현한 HelloController가 컨트롤러
    에 해당한다.

    중앙에 위치한 DispatcherServlet은 모든 연결을 담당한다. 웹브라우저로부터 요청이 들어오면 
    DispatcherServlet은 모든 연결을 담당한다. 웹브라우저로부터 요청이 들어오면 DispatcherSevlet은
    그요청을 처리하기 위한 컨트롤러 객체를 검색한다. 이때 DispatcherServlet은 직접 컨트롤러를
    검색하지 않고 HandlerMapping이라는 빈객체에서 컨트롤러 검색을 요청한다.(2번 과정에 해당)
    
    HandlerMapping은 클라이언트의 요청 경로를 이용해서 이를 처리할 컨트롤러 빈 객체를
    DispatcherServlet에 전달한다.예를들어 웹 요청경로가 '/hello'라면 등록된 컨트롤러 빈중에서
    '/hello'요청 경로를 처리할 컨트롤러를 리턴한다.

    컨트롤러 객체를 DispatcherServlet이 전달받았다고 해서 바로 컨트롤러 객체의 메소드를 실행할 수 잇는것은
    아니다.DispatcherServlet은 @Controller 애노테이션을 이용해서 구현한 컨트롤러뿐만 아니라 
    스프링 2.5까지 주로 새용되었던 HttpRequestHandler 인터페이스를 구현한 클래스를 동일한 방식으로
    실행할 수 있도록 만들어졌다. @Controller,Controller인터페이스,HttpRequestHandler 인터페이스를
    동일한 방식으로 처리하기 위해 중간에 사용되는것이 바로 HandlerAdpater빈이다.

    DispatcherServlet은 HandlerMapping이 찾아준 컨트롤러 객체를 처리할수있는 HandlerAdpater
    빈에게 요청처리를 위임한다. (위의 3번과정)
    HandlerAdpater는 컨트롤러의 알맞은 메소드를 호출해서 요청을 처리하고(4~5번과정) 그결과를
    DispatcherServlet에 리턴한다(6번과정) 이때 HandlerAdpater는 컨트롤러의 처리 결과를 
    ModelAndView라는 객체로 변환해서 DispatcherServlet에 리턴한다

    HandlerAdpate로부터 컨트롤러의 요청 처리결고를 ModelAndView로 받으면 DispatcherServlet은
    결과를 보여줄 뷰를 찾기위해 ViewResolver 빈객체를 사용한다(7번과정)
    ModelAndView는 컨트롤러가 리턴한 뷰이름을 담고있는데 ViewResolver는 이 뷰이름에 해당하는
    View객체를 찾거나 생성해서 리턴한다. 응답을 생성하기 위해 JSP를 사용하는 ViewResolver는 매번
    새로운 View객체를 생성해서 Dispatcher에 리턴한다.
    
    DisptacherServlet은 ViewResolver가 리턴한 View객체에게 응답결과 생성을 요청한다(8번과정)
    JSP를사용하는 경우 View객체는 JSP를 실행함으로써 웹브라우저에 전송할 응답결과를 생성하고 이로써
    모든 과정이 끝난다.
    
    처리과정을 보면 DispatcherServlet를 중심으로 HandlerMapping,HandlerAdpater,컨트롤러,
    ViewResolver,View,JSP가  각자 역할을 수행해서 클라이언트의 요청을 처리하는 것을 알 수 있다.
    이중 하나라도 어긋나면 클라이언트의 요청을 처리할수없게 되므로 각 구성요소를 올바르게
    설정하는것이 중요하다.

### 1.1컨트롤러와 핸드러
##### 클라이언트의 요청을 실제로 처리하는 것은 컨트롤러이고 DispatcherServlet은 클라이언트의 요청을 전달받는 창구역할을한다.
##### 앞서 말했듯 DispatcherServlet은 클라이언트의 요청을 처리할 컨트롤러를 찾기위해 HandlerMapping을 사용한다.

#### `컨트롤러를 찾아주는 객체는 ControllerMapping타입이어야 할것같은데 실제는 HandlerMapping이다 왜???`
    스프링 MVC는 웹 요청을 처리할 수 있는 범용프레임워크이다. 
    설명에서는 @Controller 애노테이션을 붙인 클래스를 이용해서 클라이언트의 요청을 처리하지만 
    원한다면 자신이 직접 만든 클래스를 이용해서 클라이언트의 요청을 처리할수도있다.
    즉 DispatcherServlet 입장에서는 클라이언트 요청을 처리하는 객체의 타입이 반드시 @Controller를
    적용한 클래스일 필요는 없다,. 실제로 스프링이 클라이언트의 요청을 처리하기 위해 
    제공하는 타입중에는 HttpRequestHandler도 존재한다.

    이런 이유로 스프링 MVC는웹요청을 실제로 처리하는 객체를 Handler라고표현하며 
    @Controller 적용 객체나 Controller인터페이스를 구현한 객체는 모두 스프링 mvc입장에서는
    핸들러가 된다. 따라서 특정 요청 경로를 처리해주는 핸들러를 찾아주는 객체를 HandlerMapping이라한다.

    DispatcherServlet은 핸들러 객체의 실제 타입에 상관없이 실행결과를 ModelAndView라는
    타입으로만 받을수있으면된다. 그런데 핸들러의 실제 구현타입에 따라ModelAndView를 리턴하는 객체도
    (Controller 인터페이스를 구현한 클래스의 객체)있고, 그렇지 않은 객체도있다.
    따라서 핸들러의 처리 결과를 ModelAndView로 변환해주는 객체가 필요하며 HandlerAdpater가 이변환을
    처리해준다.

    핸들러 객체의 실제타입마다 그에 알맞은 HandlerMapping과 HandlerAdpater가 존재하기 때문에,
    사용할 핸들러의 종류에 따라 해당 HandlerMapping과 HandlerAdpater를 스프링빈으로
    등록해주어야한다. 물론 스프링이 제공하는 설정기능을 사용하면 두종류의 빈을 직접등록하지않아도됨.

## 2.DispatcherServlet과 스프링컨테이너.
##### 전 챕터의 web.xml 파일을 보면 다음과같이 DispatcherServlet의 contextConfiguration 초기화 파라미터를 이용해 설정클래스목록을 전달했다.
    <servlet>
        <servlet-name>dispatcher</servletname>
        <servlet-class>
            org.springframework.web.servlet.DispatcherServlet
        </servlet-class>
        <init-param>
            <param-name>contextClass</param-name>
            <param-value>
                org.springframework.web.context.support.AnnotationConfigWebApplicationContext
            </param-value>
        </init-param>
        <init-param>
            <param-name>contextConfigLocation</param-name>
            <param-value>
                config.MvcConfig
                config.ControllerConfig
            </param-value>
        </init-param>
        <load-on-startup>1</load-on-startup>
    </servlet>

    DispatcherServlet은 전달받은 설정 파일을 이용해서 스프링 컨테이너를 생성하는데 앞에서
    언급한 HandlerMapping,HandlerAdpater,컨트롤러,ViewResolver 등의 빈은 아래 그림처럼
    DispatcherServlet이 생성한 스프링 컨테이너에서 구한다. 따라서 DispatcherServlet이 사용하는
    설정파일에 이들 빈에 대한 정의가 포함되어 있어야한다.
![image](https://user-images.githubusercontent.com/40031858/87041314-0287ea00-c22d-11ea-8f32-a2bc1629d33c.png)

## 3.@Controller를 위한 HandlerMapping과 HandlerAdpater
##### `Controller 적용 객체는 DispatcherServlet입장에서 보면 한종류의 핸들러 객체이다.`
#### DispatcherServlet은 웹브라우저의 요청을처리할 핸들러객체를 찾기위해 HandlerMapping을 사용하고
#### 핸들러를 실행하기 위해 HandlerAdapter를 사용한다. DispatcherServlet은 스프링 컨테이너에서
#### HandlerMapping과 HandlerAdpater타입의 빈을 사용하므로 핸들러에 알맞은 HandlerMapping 빈과
#### HandlerAdpater빈이 스프링 설정에 등록되어 있어야 한다. 
#### 그런데 전챕터에서는 HandlerMapping이나 HandlerAdpater클래스를 빈으로 등록하는 코드는 보이지않고
#### `단지 @EnableWebMvc 애노테이션만 추가했다.`
    @Configration
    @EnableWebMvc
    public class MvcConfig{
        ...
    }

    위 설정은 매우 다양한 스프링 빈설정을 추가해준다. 
    이설정을 사용하지않고 설정코드를 직접 작성하려면 백 여줄에 가까운 코드를 입력해야한다.
    이 태그가 빈으로 추가해주는 클래스 중에는 @Controller 타입의 핸들러 객체를 처리하기 위한
    다음의 두 클래스도 포함되어있다.(o.s.w는 org.springframework.web을 줄여서쓴다...)

- o.s.w.servlet.mvc.method.annotation.RequestMappingHandlerMapping
- o.s.w.servlet.mvc.method.annotation.RequestMappingHandlerAdpater   
##### RequestMappingHandlerMapping은 @Controler 애노테이션이 적요된 객체의 요청 매핑 애노테이션(@GetMapping) 값을
##### 이용해서 웹브라우저의 요청을 처리할 컨트롤러 빈을찾는다.

##### RequestMappingHandlerAdpater는 컨트롤러의 메소드를 알맞게 실행하고 그결과를 ModelAndView객체로 변환해서 DispatcherServlet에 리턴.
##### 앞챕터의 HelloController 클래스를 다시보자.
    @Controller
    public class HelloController{
        @RequestMapping("/hello")
        public String hello(Model model,
        @RequestParam(value="name",required=false) String name){
            model.addAttribute("greeting","안녕하세요,"+name);
            return "hello";
        }
    }

    RequestMappingHandlerAdpater클래스는 "/hello"요청 경로에 대해 hello()메소드에를 호출한다.
    이때 Model객체를 생성해서 첫번째 파라미터로 전달한다. 비슷하게 이름인 "name"인 
    HTTP요청 파라미터의 값을 두번째 파라미터로 전달한다.

    ReuqetMappingHandlerAdpater는 컨트롤러 메소드 결과 값이 String 타입이면 해당 값을 뷰이름으로 갖는
    ModelAndView 객체를 생성해서 DispatcherServlet에 리턴한다. 이때 첫번째 파라미터로
    전달한 Model객체에 보관된 값도 ModelAndView에 함께 전달한다.
    예제코드는 "hello"를 리턴하므로 뷰이름으로 "hello"를 리턴한다.
## 4. WebMvcConfigurer 인터페이스와 설정
##### @EnableWebMvc 애노테이션을 사용하면 @Controller 애노테이션을 붙인 컨트롤러를 위한 설정을생산.
##### 또한 @EnableWebMvc 애노테이션을 사요하면 WebMvcConfigurer 타입의 빈을 이용해서 MVC 설정을 추가로 생성한다. 앞챕터의 설정을다시보장
    @Configuration
    @EnableWebMvc
    public class MvcConfig implements WebMvcConfigurer{
        @Override
        public void configureDefaultServletHandling(
            DefaultServletHandlerConfigurer configurer){
                configurer.enable();
        }
        @Override
        public void configureViewResolvers(ViewResolverRegistry registry){
            registry.jsp("/WEB-INF/view/",".jsp");
        }
    }   

    여기서 설정 클래스는 WebMvcConfigurer 인터페이스를 상속하고있다.
    @Configuration 애노테이션을 붙인 클래스 역시 컨테이너에 빈으로 등록되므로 MvcConfig클래스는
    WebMvcConfigurer 타입의 빈이된다.

    @EnableWebMvc 애노테이션을 사용하면 WebMvcConfigurer 타입인 빈 객체의 메소드를
    호출해서 MVC설정을 추가한다. 예를 들어 ViewResolver 설정을 추가하기 위해 WebMvcConfigurer
    타입인 빈객체의 configureViewResolver()메소드를 호출한다.
    따라서 WebMvcConfigurer 인터페이스를 구현한 설정클래스는 configureViewResolvers()메소드를
    재정의해서 알맞은 뷰관련설정을 추가하면된다.

#### 스프링 5버젼은 자바 8버젼부터 지원하는 디폴트메소드를 사요해 WebMvcConfigurer 인터페이스의
#### 메소드에 기본구현을 제공하고있다. 다음은 스프링5가 지원하는 WebMvcConfigurer인터페이스의 일부구현코드다.
    public interface WebMvcConfigurer{
        default void configurerPathMatch(PathMatchConfigurer configurer){
        }
        
        default void configureDefaultServletHandling(
            DefaultServletHandlerConfigurer configurer){
            }

        default void addFormatters(FormatterRegistry registry){
        }    
        ...생략
    }

    기본구현은 모두 빈 구현이다. 이인터페이스를 상속한 설정클래슨느 재정의가 필요한 메소드만
    구현하면된다. 앞선 챕터의 예제도 모든메소드가 아닌 두개의 메소드만 재정의했었다.

## 5.JSP를 위한 ViewResolver
##### 컨트롤러 처리 결과를 JSP를 이용해서 생성하기 위해 다음설정을 사용한다.
    @Configuration
    @EnableWebMvc
    public class MvcConfig implements WebMvcConfigurer{
        @Override
        public void configureViewResolvers(ViewResolverRegistry registry){
            registry.jsp("/WEB-INF/view/",".jsp");
        }
    }
##### WebMvcConfigurer 인터페이스에 정의된 configureViewResolvers()메소드는 ViewResolverRegistry 타입의 registry파라미터를갖는다.
##### ViewResolverRegistry#jsp()메소드를 사용하면 JSP를위한 ViewResolver를 설정할수있다.
##### 위 설정은 o.s.w.servlet.view.InternalResourceViewResolver 클래스를 이용해 다음설정과 같은 빈을 등록한다.
    @Bean
    public ViewResolver viewResolver(){
        InternalResourceViewResolver vr=new InternalResourceViewResolver();
        vr.setPrefix("/WEB-INF/view/");
        vr.setSuffix(".jsp");
        return vr;
    }
##### 컨트롤러의 실행 결과를 받은 DispatcherServlet은 ViewResolver에게 뷰이름에 해당하는 View객체를 요청하낟.
##### 이때 InternalResourceViewResolver는 "prefix+뷰이름+suffix"에 해당하는 경로를 뷰코드로 사용하는 InternalResourceView타입의 
##### View객체를 리턴한다. 예를들어 뷰이름이 "hello"라면 "/WEB-INF/view/hello.jsp"경로를 뷰코드로 사용하는 InternalResourceView 객체를 리턴한다.
##### DispatcherServlet이 InternalResourceView 객체에 응답생성을 요청하면 InternalResourceView 객체는 경로에 지정한 jsp코드를 실행해
##### 응답결과를 생성한다.        
---
#### DispatcherServlet은 컨트롤러의 실행 결과를 HandlerAdpater를 통해서 ModelAndView형태로 받는다.
#### Model에 담긴값은 Viewr객체에 Map형식으로 전달된다. 예를들어 HelloController 클래스는 다음과같이
#### Model에 "greeting"속성을 설정했다.
    @Configuration
    @EnableWebMvc
    public class MvcConfig implements WebMvcConfigurer{
        @Override
        public void configureViewResolvers(ViewResolverRegistry registry){
            registry.jsp("/WEB-INF/view/",".jsp");
        }
    }

    이 경우 DispatcherServlet은 View객체에 응답생성을 요청할때 greeting키를 갖는 Map객체를
    View객체에 전달한다. View객체는 전달받은 Map객체에 담긴 값을 이용해서 알맞은 응답결과를
    출력한다. InternalResourceView는 Map객체에 담겨있는 키값을 request.setAttribute()를 이용해서
    request의 속성에 저장한다. 그런뒤 해당 경로의 JSP를 실행한다.

    결과적으로 컨트롤러에서 지정한 Model속성은 request객체 속성으로JSP에 전달되기 때문에
    JSP는 다음과 같이 모델에 지정한 속성이름을 사용해서 값을 사용할수있게된다.

    <%--JSP 코드에서 모델의 속성이름을 사용해서 값접근 --%>
    인사말:${greeting}

## 6.디폴트 핸들러와 HandlerMapping의 우선순위
#### 앞 챕터의 web.xml 설정을 보면 DispatcherServlet에대한 매핑경로를 다음과 같이 '/'로주었다.
    <servlet>
        <servlet-name>dispatcher</servlet-name>
        <servlet-class>
            org.springframework.web.servlet.DispatcherServlet
        </servlet-class>
        ...생략
    </servlet> 

    <servlet-mapping>
        <servlet-name>dispatcher</servlet-name>
        <url-pattern>/</url-pattern>
    </servlet-mapping>

    매핑경로가 '/'인 경우.jsp로 끝나는 요청을 제외한 모든 요청을 DispatcherServlet이 처리한다.
    즉 /index.html이나 /css/bootstrap.css와 같이 확장자가 .jsp가 아닌 모든요청을
    DispatcherServlet이 처리하게된다.
    
    그런데 @EnableWebMvc애노테이션이 등록하는 HandlerMapping은 @Controller애노테이션을 적용한
    빈객체가 처리할 수 있는 요청경로만 대응할수있다. 예를들어 등록된 컨트롤러가 한개이고
    그 컨트롤러가 @GetMapping("/hello")설정을 사용한다면 /hello경로만 처리할수있게된다.
    따라서 "/index.html"이나 "/css/bootstrap.css"와 같은 요청을 처리할 수 있는 컨트롤러 객체를
    찾지 못해 DispatcherServlet은 404응답을 전송한다.

    "/index.html"이나 "/css/bootstrap.css"와 같은 경로를 처리하기 위한 컨트롤러 객체를
    직접 구현할 수도있지만, 그보다는 WebMvcConfigurer의 configureDefaultServletHandling()메소드
    를 사용하는것이 편하다. 앞장에서도다음설정을 사용했다.
---   
    @Configuration
    @EnableWebMvc
    public class MvcConfig implements WebMvcConfigurer{
        @Override
        public void configureDefaultServletHandling(
            DefualtServletHandlerConfigurer configurer){
            configurer.enable();
        }
    }
#### 위 설정에서 DefaultServletHandlerConfigurer#enable() 메소드는 다음의 두빈객체를 추가한다.
- DefaultServletHttpRequestHandler
- SimpleUrlHandlerMapping
---
    DefaultServletHttpRequestHandler는 클라이언트의 모든요청을 WAS(웹 어플리케이션 서버, 톰캣이나
    웹로직 등)가 제공하는 디폴트 서블릿에 전달한다. 예를들어 "/index.html"에 대한 처리를
    DefaultServletHttpRequestHandler에 요청하면 이요청을 다시 디폴트 서블릿에 전달해서 처리하도록 한다.
    그리고 SimpleUrlHandlerMapping을 이용해서 모든 경로("/**")를 DefaultServletHttp RequestHandler
    를 이용해서 처리하도록 설정한다.

    @EnableWebMvc 애노테이션이 등록하는 RequestMappingHandlerMapping의 적용 우선순위가
    DefaultServletHandlerConfigurer#eanble()메소드가 등록하는SimpleUrlHandlerMapping의 우선순위
    보다 높다. 때문에 웹브라우저의 요청이 들어오면 DispatcherServlet은 다음과 같은방식으로 요청을 처리한다.
    ⓵ RequestMappingHandlerMapping을 사용해서 요청을 처리할 핸들러를 검색한다.
    -존재하면 해당 컨트롤러를 이용해서 요청을 처리한다
    ⓶ 존재하지 않으면 SimpleUrlHandlerMapping을 사용해서 요청을 처리할 핸들러를 검색한다.
    - DefaultServletHandlerConfigurer#eanble()메소드가 등록한 SimpleUrlHandlerMapping은
    "/**" 경로 (즉 모든경로)에 대해 DefaultServletHttpRequestHandler를 리턴한다.
    - DispatcherServlet은 DefaultServletHttpRequestHadler에 처리를 요청한다.
    - DefaultServletHttpRequestHandler는 디폴트 서블릿에 처리를 위임한다.

    예를 들어 "index.html" 경로로 요청이 들어오면 1번과정에서 해당하는 컨트롤러를 찾지 못하므로
    2번 과정을 통해 디폴트 서블릿이 /index.html요청을 처리하게된다.

    DefaultServletHandlerConfigurer#enable()외에 몇몇 설정도 SimpleUrlHandlerMapping을 등록
    하는데 DefualtServletHandlerConfiguer#enable()이 등록하는 SimpleUrlHandlerMapping의 우선
    순위가 가장낮다. 따라서 DefaultServletHandlerConfigurer#eanble()을 설정하면 별도 설정이
    없는 모든 요청경로를 디폴트 서블릿이처리하게된다.

## 7. 정리
    DispatcherServlet은 웹 브라우저의 요청을 받기위한 창구역할을 하고, 다른 주요 구성요소들을
    이용해서 요청 흐름을 제어하는 역할을한다. HandlerMapping은 클라이언트의 요청을 처리할 핸들러
    객체를 찾아준다. 핸들러(커맨드) 객체는 클라이언트의 요청을 실제로 처리한 뒤 뷰 정보와 
    모델을 설정한다. HandlerAdpater는 DispatcherServlet과 핸들러 객체 사이의 변환을 알맞게
    처리해 준다. ViewResolver는 요청 처리 결과를 생성할 View를 찾아주고 View는 최종적으로 클라이
    언트에 응답을 생성해서 전달한다.

    
    
