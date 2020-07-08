# 스프링 MVC 시작하기
#### 이번 실습에서는 pom.xml에는 <!packaging>으로 war값을준다.
#### 이 태그의 기본값은 jar로서 서블릿/JSP를 이용한 웹어플리케이션을 개발할경우 war값을준다

#### `저는 서버는 tomcat으로 하며 IDE는 intelliJ입니다` 

## 1.스프링 MVC를 위한 설정
- 스프링 MVC의 주요설정(HandlerMapping,ViewResolver 등)
- 스프링의 DispatcherServlet설정
### 1.1 스프링 MVC설정

    //다음은 java/config/MvcConfig.java의 코드입니다

    @Configuration
    @EnableWebMvc
    public class MvcConfig implements WebMvcConfigurer {
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
    //////////////////////////////
    @EnableWebMvc 애노테이션을 통해 스프링 MVC설정을 활성화한다.(스프링 MVC사용하는데 필요한 설정생산)
    DispatcherServlet의매핑경로를 '/'로 주었을때 JSP/HTML/CSS등을 올바르게 처리하기
    위한 설정을 추가함. 

    스프링MVC를 사용하려면 다양한 구성요소를 설정해야한다. 스프링2.5나 3버전에서는 상황에
    맞게 일일이 구성해야했으나 @EnableWebMvc애노테이션이 복잡한설정을 대신해준다.

    @EnableWebMvc애노테이션을 사용하면 내부적으로 다양한 빈설정을 추가해준다. 이설정을 직접하려면
    수십줄에 가까운 코드를 작성해야함..

    @EnableWebMvc애노테이션이 스프링 MVC를 사용하는데 필요한 기본적인 구성을 설정해준다면,
    WebMvcConfigurer 인터페이스는 스프링 mvc의 개별설정을 조정할때 사용한다.
    configureDefaultServletHandling()메소드와 configureViewResolvers()메소드는 WebMvcConfigurer인터
    페이스에 정의된 메소드로 각각 디폴트 서블릿과 ViewResolver와 관련된 설정을 조정한다.

### 1.2 web.xml파일에 DispatcherServlet설정
##### 스프링 MVC가 웹요청을하려면 DispatcherServlet을 통해서 웹요청을 받아야한다. 이를위해 web.xml파일에 DispatcherServlet등록
##### src/main/webapp/WEB-INF폴더에 web.xml파일을작성해보자 
    <?xml version="1.0" encoding="UTF-8"?>
    <web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/web-app_4_0.xsd"
            version="4.0">
    <servlet>
            <servlet-name>dispatcher</servlet-name>
            <servlet-class>
                org.springframework.web.servlet.DispatcherServlet
            </servlet-class>
            <init-param>
                <param-name>contextClass</param-name>
                <param-value>org.springframework.web.context.support.AnnotationConfigWebApplicationContext</param-value>
            </init-param>
            <init-param>
                <param-name>contextConfigLocation</param-name>
                <param-value>config.MvcConfig
                    config.ControllerConfig
                </param-value>
            </init-param>
            <load-on-startup>1</load-on-startup>
    </servlet>
        <servlet-mapping>
            <servlet-name>dispatcher</servlet-name>
            <url-pattern>/</url-pattern>
        </servlet-mapping>
        <filter>
            <filter-name>encodingFilter</filter-name>
            <filter-class>
                org.springframework.web.filter.CharacterEncodingFilter
            </filter-class>
            <init-param>
                <param-name>encoding</param-name>
                <param-value>UTF-8</param-value>
            </init-param>
        </filter>
        <filter-mapping>
            <filter-name>encodingFilter</filter-name>
            <url-pattern>/*</url-pattern>
        </filter-mapping>
    </web-app>
## 2 코드구현
##### 필요한 설정은 끝났으므로 실제코드를 구현하고 실행해보자 
- 클라이언트의 요청을 알맞게 처리할 컨트롤러
- 처리 결과를 보여줄 JSP
### 2.1 컨트롤러 구현    
#### 이파트의 실습코드에서 사용할 컨트롤러 코드는 다음과같습니다.    
    @Controller
    public class hello.HelloController {
        @GetMapping("/hello")
        public String hello(Model model,
                            @RequestParam(value="name",required = false)String name){
            model.addAttribute("greeting","안녕하세요,"+name);
            return "hello";
        }
    }

    ///////////
    @Controller 애노테이션을 적용한 클래스는 스프링 MVC에서 컨트롤러로사용합니당.
    
    @GetMapping 애노테이션은 메소드가 처리할 요청경로를 지정한다. 위코드의경우
    "/hello"경로로 들어온 요청을 hello()메소드를 이용해서 처리한다고 설정했다.
    이름에서 알 수 있듯이 HTTP요청 메소드중 GET메소드에 대한 매핑을 설정한다.

    Model파라미터는 컨트롤러의 처리결과를 뷰에 전달할때 사용한다.
    
    @RequestParam 애노테이션은 HTTP요청 파라미터의 값을 메소드의 파라미터로 전달할때 사용된다.
    위코드의 경우 name요청 파라미터의 값을 name파라미터에 전달한다.

    "greeting"이라는 모델속성에 값을 설정한다. 값으로는 "안녕하세요",와 name파라미터의 값을
    연결한 문자열을 사용한다. 뒤에서 작성할 JSP코드는 이속성을 이용해서 값을 출력한다.
    
    컨트롤러의 처리결과를 보여줄 뷰 이름으로 "HELLO"를 사용한다.

##### 스프링 MVC 프레임워크에서 컨트롤러(Controller)란 간단히 설명하면 웹요청을 처리하고 그결과를 뷰에 전달하는 스프링 빈객체이다.
##### 스프링 컨트롤러로 사용될 클래스는 @Controller 애노테이션을 붙여야하고 @Getmapping애노테이션이나 @PostMapping 애노테이션
##### 을이용해서 처리할 경로를 지정해주어야한다.

##### @GetMapping 애노테이션의 값은 서블릿 컨텍스트 경로(또는 웹 어플리케이션 경로)를 기준으로한다.
###### 예를들어 톰캣의 경우 webapps\sp5-chap8 폴더는 웹브라우저에서 http://host/sp5-chap8 경로에 해당하는데, 이때 sp5-chap09가 컨텍스트경로가된다.
##### 컨텍스트 경로가 /sp5-chap8인경우 http://host/sp5-chap08/main/list 경로를 처리하기 위한 컨트롤러는 @GetMapping("/main/list")를 사용한다.

##### RequestParam 애노테이션은 HTTP요청 파라미터를 메소드의 파라미터로 전달받을 수 있게해준다.
##### @RequestParam 애노테이션의 value속성은 HTTP요청 파라미터의 이름을 지정하고 required속성은 필수여부를 지정한다.

###### 위의 코드를 보면 파라미터로 전달받은 Model객체의 addAttribute()메소드를 실행하고 있는데 이는 뷰에 전달할 데이터를 지정하기위해사용된다.
    model.addAttribute("greeting","안녕하세요,"+name);

    Model#addAttribute()메소드의 첫번째 파라미터는 데이터를 식별하는데 사용되는
    속성이름이고 두번째 파라미터는 속성이름에 하당하는 값이다. 뷰 코드는 이속성 이름을 사용해서
    컨트롤러가 전달한 데이터에 접근하게 된다.

    마지막으로 @GetMapping이 붙은 메소드는 컨트롤러의 실행 결과를 보여쥴 뷰이름을리턴한다.
    예제코드에서는 "hello"를 뷰이름으로 리턴했다. 이 뷰이름은 논리적인 이름이며 
    실제로 뷰이름에 해당하는 뷰구현을 찾아주는 것은 ViewResolver가 처리한다.
    ViewResolver가 무엇인지는 다음챕터에서 작성하겠습니당.


##### 컨트롤러를 구현했다면 컨트롤러를 스프링 빈으로 등록할 차례이다. 컨트롤러 클래스를 비느로 등록할때 사용할 설정파일을 작성하장
    @Configuration
    public class ControllerConfig {
    @Bean
        public HelloController helloController(){
        return new HelloController();
        }
    }
### 2.2 JSP구현
##### 컨트롤러가 생성한 결과를 보여줄 뷰 코드를 만들어보자. 뷰코드는 jsp를이용해서 구현한다.    
##### 앞서 프로젝트 생성과정에서 src/main/webapp/WEB-INF 폴더에 view폴더를만들었는데 이view폴더에 hello.jsp를추가하장
    <%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <html>
    <head>
        <title>Hello</title>
    </head>
    <body>
        인사말 : ${greeting}
    </body>
    </html>
##### HelloController의 hello()메소드가 리턴한 뷰이름은 "hello"였는데 JSP파일의 이름을 보면 "hello.jsp"이다.
##### 뷰 이름과 JSP파일과의 연결은 MvcConfig클래스의 다음설정을 통해서 이루어진다.
    @Override
    public void configureViewResolvers(ViewResolverRegistry registry){
        registry.jsp("/WEB-INF/view/",".jsp")
    }

    registry.jsp()코드는 JSP를 뷰구현으로사용할 수 있도록 해주는 설정이다.
    .jsp()메소드의 첫번째 인자는 JSP파일 경로를 찾을 때 사용할 접두어 이며 두번째 인자는
    접미사이다. 뷰 이름의 앞과 뒤에 각각 접두어와 접미사를 붙여서 최종적으로 사용할 jsp파일의
    경로를 결정한다. 예를들어 뷰 이름이 "hello"인 경우 사용하는 JSP파일경로는 다음과같이 결정된다.
    /WEB-INF/view/hello.jsp
---
##### hello.jsp코드를보면 다음과같은 JSP EL(Expression Language)을 사용했다.
    인사말:${greeting}
    이 표현식은 "greeting"은 컨트롤러 구현에서 Model에 추가한 속성의 이름인 "greeting"과 동일하다
    이렇게 컨트롤러에서 설정한 속성을 뷰 JSP코드에서 접근할수있는 이유는 스프링 mvc프레임워크가 
    아래의 그림처럼 모델에 추가한 속성을 JSP코드에서 접근할수있게 HttpServletRequest에 옮겨주기때문
![image](https://user-images.githubusercontent.com/40031858/86917951-ecacf300-c160-11ea-970b-08082409c558.png)

#### 따라서 JSP로 뷰 코드를 구현할경우 컨트롤러에서 추가한 속성의 이름을 이용해서 속성값을 응답결과에 출력한다.

### 이제 코드를 실행해서 살펴보고 분석해봅시다. 아래와같은 주소로 입력하면됩니다. name=@@

http://localhost:8080/8_Spring_MVC_war/hello?name=junseong
