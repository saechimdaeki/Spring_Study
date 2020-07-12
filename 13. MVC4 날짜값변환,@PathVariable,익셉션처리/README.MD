# MVC4: 날짜 값 변환, @PathVariable, 익셉션처리 :balloon: 
## 1. 날짜를 이용한 회원 검색기능
##### MemberDao클래스에 selectByRegdate()메소드를 추가하자.
    public List<Member> selectByRegdate(
			LocalDateTime from,LocalDateTime to) {
		List<Member> results=jdbcTemplate.query(
				"select * from MEMBER where REGDATE between ? and ? " +
						"order by REGDATE desc", new RowMapper<Member>() {
					@Override
					public Member mapRow(ResultSet rs, int rownum) throws SQLException {
						Member member=new Member(
								rs.getString("EMAIL"),
								rs.getString("PASSWORD"),
								rs.getString("NAME"),
								rs.getTimestamp("REGDATE").toLocalDateTime());
						member.setId(rs.getLong("ID"));
						return member;
					}
				},
				from, to);
		return results;
	}

    selectByRegdate()메소드는 REGDATE값이 두 파라미터로 전달받은 from과 to사이에 있는 Member목록을
    구한다. 이메소드를 이용해서 특정기간 동안 가입한 회원목록을 보여줄거임!

## 2.커맨드 객체 Date타입 프로퍼티 변환 처리: @DateTimeFormat
#### 검색 기준시간을 표현하기위해 아래의 커맨드클래스를 구현해 사용한다.
    public class ListCommand {

    @DateTimeFormat(pattern = "yyyyMMddHH")
    private LocalDateTime from;
    @DateTimeFormat(pattern = "yyyyMMddHH")
    private LocalDateTime to;

    public LocalDateTime getFrom() {
        return from;
    }

    public void setFrom(LocalDateTime from) {
        this.from = from;
    }

    public LocalDateTime getTo() {
        return to;
    }

    public void setTo(LocalDateTime to) {
        this.to = to;
     }
    }

    위처럼 커맨드 객체에 @DateTimeFormat 애노테이션이 적요오디어 있으면 @DateTimeFormat
    에서 지정한 형식을 이용해서 문자열을 LocalDateTime 타입으로 변환한다.
#### 컨트롤러 클래스는 ㅏㅇ래와같이 별도설정없이 ListCommand클래스를 커맨드객체로사용하자
    @Controller
    public class MemberListController {
        private MemberDao memberDao;

        public void setMemberDao(MemberDao memberDao) {
            this.memberDao = memberDao;
        }
        @RequestMapping("/members")
        public String list(
                @ModelAttribute("cmd") ListCommand listCommand, Model model){
            if(listCommand.getFrom()!=null && listCommand.getTo()!=null){
                List<Member> members=memberDao.selectByRegdate(
                        listCommand.getFrom(),listCommand.getTo());
                model.addAttribute("members",members);
            }
            return "member/memberList";
        }
    }  

    //새로운 컨트롤러 코드를 작성했으니 ControllerConfig설정클래스에 빈을추가하자.

    @Autowired
	private MemberDao memberDao;
	
	@Bean
	public MemberListController memberListController(){
		MemberListController controller=new MemberListController();
		controller.setMemberDao(memberDao);
		return controller;
	}
##### jsp는 코드생략 디렉토리폴더를 직접보자.
##### http://localhost:8080/13__MVC4___PathVariable__war/members/list 를입력하면 from파라미터와 to파라미터가 존재하지않는다.
##### 때문에 커맨드 객체의 from프로퍼티와 to프로퍼티값은 null이된다. 
##### 따라서 http://localhost:8080/13__MVC4___PathVariable__war/members 여기에들어가 from과 to에 시간을입력하고 조회를 누르면된다.
 ![image](https://user-images.githubusercontent.com/40031858/87237779-20c03680-c435-11ea-8398-8cd036fbbcf1.png)

### 2.1 변환 에러 처리
##### 위 뷰에서 from이나 to에 20190712를 입력하면 지정한형식은 "yyyyMMddHH"이기 때문에 "yyyMMdd"부분만 조회하면 지정한 형식과 일치하지않는다.
##### (400에러발생..)
##### 400 에러대신 알맞은 에러메시지를 보여주고싶다면 MemberListController에 다음과같이추가하자.
    //기존코드에 Erros추가한것.
    @Controller
    public class MemberListController {
        private MemberDao memberDao;

        public void setMemberDao(MemberDao memberDao) {
            this.memberDao = memberDao;
        }
        @RequestMapping("/members")
        public String list(
                @ModelAttribute("cmd") ListCommand listCommand, Errors errors, Model model){
            if (errors.hasErrors())
                return "member/memberList";
            if(listCommand.getFrom()!=null && listCommand.getTo()!=null){
                List<Member> members=memberDao.selectByRegdate(
                        listCommand.getFrom(),listCommand.getTo());
                model.addAttribute("members",members);
            }
            return "member/memberList";
        }
    }

## 3. 변환 처리에 대한 이해
#### `궁금증이 있다. 누가 문자열을 LocalDateTime타입으로 변환하는것일까??`
##### 답은 WebDataBinder에 있다. 
##### 스프링 MVC는 요청 매핑 애노테이션 적용 메소드와 DispatcherSevlet사이를 연결하기 위해 RequestMappingHandlerAdapte객체를 사용한다.
##### 이 핸들러 어댑터 객체는 요청 파라미터와 커맨드 객체사이의 변환처리를 위해 WebDataBinder를 이용한다.

##### WebDataBinder는 커맨드객체를 생성한다. 그리고 커맨드 객체의 프로퍼티와 같은 이름을 갖는 요청파라미터를 이용해 프로퍼티값을생성한다.
![image](https://user-images.githubusercontent.com/40031858/87238025-2bc89600-c438-11ea-9b1e-0effb9f576c5.png)

##### WebDataBinder는 직접 타입을 변환하지 않고 위 그림처럼 ConversionService에 그역할을 위임한다.
##### 스프링 MVC를 위한 설정인 @EnableWebMvc애노테이션을 사용하면 DefaultFormattingConversionService를 ConversionSevice로 사용한다.

##### DefaultFormattinConversionService는 int,long과 같은 기본 데이터 타입뿐만 아니라 @DateTimeFormat 애노테이션을 사용한
##### 시간관련 타입 변환기능을 제공한다. 이런이유로 커맨드로 사용할 클래스에 @DateTimeFormat 애노테이션만 붙이면 지정한 형식의 
##### 문자열을 시간타입 값으로받을수있는것이다.
---
##### WebDataBinder는 < form:input>에도 사용된다. < form:input>태그를 사용하면 아래와 같이 path속성에 지정한 프로퍼티 값을 
##### String으로 변환해서 < input>태그의 value속성값으로 생성한다. 이때 프로퍼티값을 String으로 변환할때 WebDataBinder의 ConversionService를사용
![image](https://user-images.githubusercontent.com/40031858/87238114-52d39780-c439-11ea-8d2e-b839cd1aa1a0.png)

## 4. MemberDao 클래스 중복코드 정리 및 메소드 추가
    //RowMapper 생성코드의 중복을 제거했다.
    public class MemberDao {

        private JdbcTemplate jdbcTemplate;
        private RowMapper<Member> memRowMapper =
                new RowMapper<Member>() {
                    @Override
                    public Member mapRow(ResultSet rs, int rowNum)
                            throws SQLException {
                        Member member = new Member(rs.getString("EMAIL"),
                                rs.getString("PASSWORD"),
                                rs.getString("NAME"),
                                rs.getTimestamp("REGDATE").toLocalDateTime());
                        member.setId(rs.getLong("ID"));
                        return member;
                    }
                };

        public MemberDao(DataSource dataSource) {
            this.jdbcTemplate = new JdbcTemplate(dataSource);
        }

        public Member selectByEmail(String email) {
            List<Member> results = jdbcTemplate.query(
                    "select * from MEMBER where EMAIL = ?",
                    memRowMapper, email);

            return results.isEmpty() ? null : results.get(0);
        }

        public void insert(Member member) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection con)
                        throws SQLException {
                    // 파라미터로 전달받은 Connection을 이용해서 PreparedStatement 생성
                    PreparedStatement pstmt = con.prepareStatement(
                            "insert into MEMBER (EMAIL, PASSWORD, NAME, REGDATE) " +
                                    "values (?, ?, ?, ?)",
                            new String[] { "ID" });
                    // 인덱스 파라미터 값 설정
                    pstmt.setString(1, member.getEmail());
                    pstmt.setString(2, member.getPassword());
                    pstmt.setString(3, member.getName());
                    pstmt.setTimestamp(4,
                            Timestamp.valueOf(member.getRegisterDateTime()));
                    // 생성한 PreparedStatement 객체 리턴
                    return pstmt;
                }
            }, keyHolder);
            Number keyValue = keyHolder.getKey();
            member.setId(keyValue.longValue());
        }

        public void update(Member member) {
            jdbcTemplate.update(
                    "update MEMBER set NAME = ?, PASSWORD = ? where EMAIL = ?",
                    member.getName(), member.getPassword(), member.getEmail());
        }

        public List<Member> selectAll() {
            List<Member> results = jdbcTemplate.query("select * from MEMBER",
                    memRowMapper);
            return results;
        }

        public int count() {
            Integer count = jdbcTemplate.queryForObject(
                    "select count(*) from MEMBER", Integer.class);
            return count;
        }

        public List<Member> selectByRegdate(LocalDateTime from, LocalDateTime to) {
            List<Member> results = jdbcTemplate.query(
                    "select * from MEMBER where REGDATE between ? and ? " +
                            "order by REGDATE desc",
                    memRowMapper,
                    from, to);
            return results;
        }

        public Member selectById(Long memId) {
            List<Member> results = jdbcTemplate.query(
                    "select * from MEMBER where ID = ?",
                    memRowMapper, memId);

            return results.isEmpty() ? null : results.get(0);
        }
    }
## 5. @PathVariable을 이용한 경로 변수처리
##### 다음은 id가 1인 회원의 저보를 조회하기 위한 URL이다.
    http://localhost:8080/13__MVC4___PathVariable__war/members/1

    이 형식의 url을 사용하면 각 회원마다 경로의 마지막 부분이 달라진다. 이렇게 경로의 일부가
    고정되지않고 달라질때 사용할수 있는것이 @PathVariable애노테이션이다.
    @PathVariable애노테이션을 사용하면 아래와같이 가변경로를 처리할수있다.    


    @Controller
    public class MemberDetailController {
        private MemberDao memberDao;

        public void setMemberDao(MemberDao memberDao) {
            this.memberDao = memberDao;
        }
        @GetMapping("/members/{id}")
        public String detail(@PathVariable("id") Long memId, Model model){
            Member member=memberDao.selectById(memId);
            if(member==null)
                throw new MemberNotFoundException();
            model.addAttribute("member",member);
            return "member/memberDetail";
        }
    }
#### 매핑경로에 '{경로변수}'와 같이 중괄호로 둘러 쌓인 부분을 경로변수라고 부른다. "{경로변수}"에 해당하는 값은
##### 같은 경로변수 이름을 지정한 @PathVariable 파라미터에 전달된다.
---

    //ControllerConfig 에 설정추가
    @Bean
	public MemberDetailController memberDetailController(){
		MemberDetailController controller=new MemberDetailController();
		controller.setMemberDao(memberDao);
		return controller;
	}

##### 이제 http://localhost:8080/13__MVC4___PathVariable__war/members/1 와 같이 입력하면
##### 아이디가 1인 회원이 존재하면 @PathVariable을 통해 전달받은 경로 변수값을 사요해 회원정보를 뷰에 전달함.
![image](https://user-images.githubusercontent.com/40031858/87238338-561c5280-c43c-11ea-8898-e25b692a82ba.png)

## 6. 컨트롤러 익셉션 처리하기
##### 없는 ID를 경로변수로 사용한다면 MemberNotFoundException이 발생한다.
##### 또한 MemberDetailController가 사용하는 경로변수는 Long타입인데 실제 요청경로에 숫자가 아닌 문자를 입력할겨우 Long타입으로 변환할수없기에
#### 400에러가 발생한다.

##### `타입변환 실패에 따른 익셉션은 어떻게해야 에러화면을 보여줄수있을까???`
#### 이럴때 사용하는것이 바로 @ExceptionHandler 애노테이션이다 :thumbsup:

##### 같은 컨트롤러에 @ExceptionHandler 애노테이션을 적용한 메소드가 존재하면 그 메소드가 익셉션을 처리한다. 따라서 컨트롤러에서 발생한 익셉션을
##### 직접 처리하고싶다면 @ExceptionHandler 애노테이션을 적용한 메소드를 구현하면된다.
    //MemberDetailController에 익셉션 처리코드추가
    @ExceptionHandler(TypeMismatchException.class)
    public String handleTypeMismatchException(){
        return "member/invalidId";
    }
    @ExceptionHandler(MemberNotFoundException.class)
    public String handleNotFoundException(){
        return "member/noMember";
    }

    위 코드르로면 @ExceptionHandler의 값으로 TypeMismatchException.class를주었다.
    이 익셉션은 경로 변수값의 타입이 올바르지않을때 발생한다. 이 익셉션이 발생하면
    에러응답을 보내는 대신 handleTypeMismatchException()메소드를 실행한다.
##### 익셉션 객체에 대한 정보를 알고싶다면 메소드의 파라미터로 익셉션 객체를 전달받아 사용하면된다.
    @ExceptionHandler(TypeMismatchException.class)
    public String handleTypeMismatchException(TypeMismatchException ex){
        //ex사용해서 로그 남기는등 작업
        return "member/invalidId";
    }    
### 6.1 @ControllerAdvice를 이용한 공통익셉션처리
##### 컨트롤러 클래스에 @ExceptionHandler 애노테이션을 적용하면 해당 컨트롤러에서 발생한 익셉션만을 처리한다.
##### 다수의 컨트롤러에서 동일 타입의 익셉션이 발생할수도있다. 이때 익셉션 처리코드가 동일하다면 어떻게 해야할까? 각 컨트롤러 클래스마다 익셉션
##### 처리 메소드를 구현하는 것은 불필요한 코드중복을 발생시킨다.

#### 여러 컨트롤러에서 동일하게 처리할 익셉션이 발생하면 @ControllerAdvice 애노테이션을 이용해서 중복을 없앨수있다.
    @ControllerAdvice("spring")
    public class CommonExceptionHandler{
        @ExceptionHandler(RuntimeException.class)
        public String handleRuntimeException(){
            return "error/commonException";
        }
    }

    @ControllerAdvice애노테이션이 적용된 클래스는 지정한 범위의 컨트롤러에 공통으로 사용될
    설정을 지정할수있다. 위코드는 "spring"패키지와 그 하위패키지에 속한 컨트롤러 클래스를 위한
    공통기능을 정의했다. spring패키지와 그 하위패키지에 속한 컨트롤러에서 RuntimeException이
    발생하면 handleRuntimeException()메소드를 통해서 익셉션을 처리한다.

    /* 물론 @ControllerAdvice적용 클래스가 동작하려면 해당클래스를 스프링에 빈으로 등록해야함.*/

### 6.2 @ExceptionHandler 적용 메소드의 우선순위
##### @ControllerAdvice 클래스에 있는 @ExceptionHandler 메소드와 컨트롤러 클래스에있는 @ExceptionHandler 메소드중 컨트롤러 클래스에
##### 적용된 @ExceptionHandler메소드가 우선한다. 즉 컨트롤러의 메소드를 실행하는 과정에서 익셉션이 발생하면 다음의 순서로 익셉션을 
##### 처리할 @ExceptionHandler메소드를 찾는다.
    - 같은 컨트롤러에 위치한 @Exception 메소드중 해당익셉션을 처리할수있는 메소드검색
    - 같은 클래스에 위치한 메소드가 익셉션을 처리할수없는경우 @ControllerAdvice클래스에 
    위치한 @ExceptionHandler메소드 검색
#### @ControllerAdvice애노테이션은 공통설정을 적용할 컨트롤러 대상을 지정하기위해 아래와같은 속성을 제공한다.
|속성|타입 |설명 |
|---|---|---|
|value basePackages| String[]|공통 설정을 적용할 컨트롤러가 속하는 기준패키지|
|annotations |Class<? extends Annotation>[] |특정 애노테이션이 적용된 컨트롤러 대상 |
|assignableTypes |Class<?>[] | 특정타입또는 그 하위 타입인 컨트롤러 대상 |

### 7.3 @ExceptionHandler 애노테이션 적용 메소드의 파라미터와 리턴타입
##### @ExceptionHandler애노테이션을 붙인 메소드는 다음파라미터를 가질수있다
- HttpServletRequest, HttpServletResponse, HttpSession
- Model
- 익셉션
#### 리턴 가능한 타입은 다음과같다
- ModelAndView
- String (뷰 이름)
- (@ResponseBody 애노테이션을 붙인경우) 임의객체
- ResponseEntity
