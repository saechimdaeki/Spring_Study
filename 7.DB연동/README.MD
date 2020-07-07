# DB연동 
#### 저는 mysql5.1을 intelliJ에 연동하여 사용합니다.

## 1.DB테이블생성
    create database springjunseong character set=utf8;
    grant all privileges on springjunseong.* to 'root'@'localhost';
    create table springjunseong.MEMBER (
                            ID int auto_increment primary key,
                            EMAIL varchar(255),
                            PASSWORD varchar(100),
                            NAME varchar(100),
                            REGDATE datetime,
                            unique key (EMAIL)
    ) engine=InnoDB character set = utf8;
    
    root계정으로 springjunseong데이터베이스를 
    선택한다음 다음과 같이 데이터를 미리생성합니다.
    insert into member(EMAIL, PASSWORD, NAME, REGDATE) values ('anima94@naver.com', 
    '1234' , 'kjs', now());

## 2. DataSource 설정
#### JDBC API는 DriverManager외에 DataSource를 이용해서 DB연결을 구하는 방법을 정의한다.
#### DataSource를 사용하면 다음방식으로 Connection을 구할수있다.
    Connection conn=null;
    try{
        //dataSource는 생성자나 설정 메소드를 이용해서 주입받음
        conn=dataSource.getConnection();
        ...
---
##### 스프링이 제공하는 DB연동기능은 DataSource를 사용해서 DBConnection을 구한다.
##### DB연동에 사용할 DataSource를 스프링 빈으로 등록하고 DB연동기능을 구현한 빈객체는 DataSource를 주입받아 사용한다.
##### Tomcat JDBC 모듈은 javax.sql.DataSource를 구현한 DataSource클래스를 제공한다.이클래스를 스프링 빈으로 등록해서 DataSource로사용가능.
    @Configuration
    public class DbConfig {

    @Bean(destroyMethod = "close")
    public DataSource dataSource() {
        DataSource ds = new DataSource(); //datasource객체생성
        ds.setDriverClassName("com.mysql.jdbc.Driver"); //jdbc드라이버 클래스지정
        ds.setUrl("jdbc:mysql://localhost/springjunseong?characterEncoding=utf8");
        ds.setUsername("root");
        ds.setPassword("940215");  //자신의 사용자계정과암호.
        ds.setInitialSize(2);
        ds.setMaxActive(10);
        ds.setTestWhileIdle(true);
        ds.setMinEvictableIdleTimeMillis(60000 * 3);
        ds.setTimeBetweenEvictionRunsMillis(10 * 1000);
        return ds;
     }
    }        
## 2.1 Tomcat JDBC의 주요 프로퍼티     
| 설정 메소드 | 설명 | 
|---|---|
|`setInitialSize(int)`| 커넥션 풀을 초기화할 때 생성할 초기 커넥션개수를지정. 기본값은 10  |
|`setMaxActive(int)` | 커넥션 풀에서 가져올 수 있는 최대 커넥션 개수를 지정.기본값은 10 | 
|`setMaxIdle(int)` | 커넥션 풀에 유지할 수 있는 최대 커넥션개수를지정. 기본값은 maxActive와같다 | 
|`setMinIdle(int)` | 커넥션 풀에 유지할 쵯 ㅗ커넥션 개수를 지정한다. 기본값은 InitialSize에서가져온다| 
|`setMaxWait(int)` | 커넥션 풀에서 커넥션을 가져올 때 대기할 최대 시간을 밀리초 단위로 지정한다. 기본값은 3000밀리초(30초)이다.|
|`setMaxAge(long)` | 최초 커넥션 연결 후 커넥션의 최대 유효시간은 밀리초단위로지정. 기본값은 0이다.0은유효시간이없음을 의미.|
|`setValidationQuery(String)` |커넥션이 유효한지 검사할때 사용할 쿼리를 지정한다. 언제 검사할지는 별도 설정으로 지정한다. 기본값은 null이다. null이면검사하지않음. "select 1"이나 "select 1 from dual"과 같은 쿼리 주로 사용|
|`setValidationQueryTimeout(int)` | 검사쿼리의 최대 실행시간을 초 단위로 지정한다. 이 시간을 초과하면 검사에 실패한것으로 간주. 0이하로지정하면 비활성화한다. 기본값은 -1 |
|`setTestOnBorrow(boolean)`| 풀에서 커넥션을 가져올때 검사여부를 지정한다. 기본값은 false이다 |
|`setTestOnReturn(boolean)`| 풀에 커넥션을 유휴상태로 있는동안에 검사할지 여부를 지정.기본값은 false|
|`setTestWhileIdle(boolean)` |커넥션이 풀에 유휴상태로 있는 동안에 검사할지 여부지정 기본값은false |
|`setMinEvictableIdleTimeMillis(int) `| 커넥션 풀에 유휴상태로 유지할 최소시간을 밀리초단위로 지정.testwhileidle이 true이면 유휴시간이 이값을 초과한 커넥션을 풀에서 제거.기본값은 60초 |
|`setTimeBetweenEvictionRunsMills(int)` |커넥션 풀의 유휴 커넥션을 검사할 주기를 밀리초단위로 지정. 기본값은 5초이다. 이값을 1초이하로 설정하면 안댕 |

    위의 설정을 이해하려면 커넥션의 상태를 알아야한다. 커넥션 풀은 커넥션을 생성하고유지한다.
    커넥션 풀에 커넥션을 요청하면 해당커넥션은 활성상태가 되고 커넥션을 다시 커넥션 풀에 반환하면
    유휴상태가 된다. DataSource#getConnection()을 실행하면 커넥션 풀에서 커넥션을 가져와
    커넥션이 활성상태가 된다. 반대로 커넥션을 종료하면 커넥션은 풀로 돌아가 유휴상태가된다.
    
    아래의 코드를 보자 
        public class DbQuery {
        private DataSource dataSource;

        public DbQuery(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        public int count() {
            Connection conn = null;
            try {
                conn = dataSource.getConnection();
                try (Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery("select count(*) from member")) {
                    rs.next();
                    return rs.getInt(1);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                if (conn != null)
                    try {
                        conn.close();
                    } catch (SQLException e) {
                    }
            }
        }
    }

    위의 코드를 보면 커넥션 사용이 끝나고 커넥션을 종료하면 실제 커넥션을 끊지않고 풀에
    반환한다. 풀에 반환된 커넥션은 다시 유휴상태가 된다.

    커넥션 풀을 사용하는 이유는 성능때문이다. 매번 새로운 커넥션을 생성하면 그때마다 연결시간이
    소모된다. 커넥션 풀을 사용하면 미리 커넥션을 생성했다가 필요할때에 커넥션을 꺼내
    쓰므로 커넥션을 구하는 시간이 줄어 전체응답시간도 짧아진다. 그래서 커넥션풀을
    초기화할때 최소수준의 커넥션을 미리 생성하는것이좋다.

    커넥션풀에 생성된 커넥션은 지속적으로 재사용된다. 그런데 한 커넥션이 영원히 유지되는것은아니다.
    DBMS설정에 따라 일정시간 내에 쿼리를 실행하지않으면 연결을 끊기도한다.
    예를 들어 DBMS에 5분동안 쿼리를 실행하지않으면 DB연결을 끊도록 설정하였는데
    커넥션풀에 특정커넥션이 5분넘게 유휴상태로 존재했다고하자.이경우 DBMS는 해당 커넥션의
    연결을 끊지만 커넥션은 여전히 풀속에남아있다. 이상태에서 해당 커넥션을 풀에서 가져와
    사용하면 연결이 끊어진 커넥션이므로 익셉션이 발생하게 된다.

    이러한 문제를 방지하려면 커넥션풀의 커넥션이 유효한지 주기적으로 검사해야한다.
    이것과 관련된 속성이 minEvictableIdleTimeMillis,timeBetweenEvictionRunsMills,testWhileIdle이다.

## 3. JdbcTemplate을 이용한 쿼리 실행
### 3.1 JdbcTemplate 생성하기
    public class MemberDao{
    private JdbcTemplate jdbcTemplate;
	public MemberDao(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	    }
    }
    이제 이를 스프링 설정에서 MemberDao빈설정을 추가하면된다.
### 3.2 JdbcTemplate을 이용한 조회 쿼리 실행    
##### query()메소드는 sql파라미터로 전달받은 쿼리를 실행하고 RowMapper를 이용해서ResltSet의 결과를 자바객체로 변환한다.
##### sql파라미터가 아래와 같이 인덱스 기반 파라미터를 가진 쿼리이면 args파라미터를 이용해 각 인덱스 파라미터의값을 지정한다.
    select * from member where email = ?
##### 쿼리 실행 결과를 자바객체로 변환할때 사용하는 RowMapper인터페이스는 다음과같다.  
    public interface RowMapper<T>{
        T mapRow(ResultSet rs,int rowNum) throws SQLException;
    }

    RowMapper의 mapRow()메소드는 SQL실행 결과로 구한 ResultSet에서 한행의 데이터를 읽어와
    자바 객체로 변환하는 매퍼기능을 구현한다. RowMapper 인터페이스를 구현한 클래스를
    작성할 수도있지만 임의 클래스나 람다식으로 RowMapper의 객체를 생성해 query()메소드에 전달할
    때도 있다.     
    
	public Member selectByEmail(String email) {
		List<Member> results = jdbcTemplate.query(
				"select * from member where EMAIL = ?",
				new RowMapper<Member>() {
					@Override
					public Member mapRow(ResultSet rs, int rowNum) throws SQLException {
						Member member = new Member(
								rs.getString("EMAIL"),
								rs.getString("PASSWORD"),
								rs.getString("NAME"),
								rs.getTimestamp("REGDATE").toLocalDateTime());
						member.setId(rs.getLong("ID"));
						return member;
					}
				}, email);

		return results.isEmpty() ? null : results.get(0);
	}
### 3.3 결과가 1행인 경우 사용할 수 있는 queryForObject()메소드
#### 다음은 MEMBER테이블의 전체 행개수를 구하는 코드이다. 이코드는 query()메소드를 사용했다.
    public int count(){
        List<Integer> results=jdbcTemplate.query(
            "select count(*) from MEMBER",
            new RowMapper<Integer>(){
                @Override
                public Integer mapRow(ResultSet rs,int rowNum) throws SQLException{
                    return rs.getInt(1);
                }
            });
            return results.get(0);
    }

    count(*)쿼리는 결과가 한행뿐이니 쿼리결과를 List로받기보다는 Integer와 같은 정수타입으로 받으면
    편할것이다. 이를 위한 메소드가 바로 queryForObject()이다.
    queryForObject()를 이용하면 count(*)쿼리 실행코드를 아래처럼 할수있다.    

    	public int count() {
		Integer count = jdbcTemplate.queryForObject(
				"select count(*) from member", Integer.class);
		return count;
	}

    실행결과 칼럼이 두개이상이면 RowMapper를파라미터로 전달해서 결과를 생성할수있다.
    queryForObject()메소드 사용과 기존의 query()메소드를 사용한 코드의 차이점은
    리턴타입이 List가아니라 RowMapper로 변환해주는타입이라는점이다.

    *주의*
    queryForObject()메소드를 사용하려면 쿼리 실행결과는 반드시 한행이어야한다. 만약 쿼리실행
    결과 행이없거나 두개이상이면 익셉션이 발생한다.(행의 개수가 0이여도 익셉션발생)
    따라서 결과 행이 정확히 한개가아니면 queryForObject()메소드 대신 query()메소드를 사용

### 3.4 JdbcTemplate을 이용한 변경쿼리 실행
#### INSERT,UPDATE,DELETE 쿼리는 update()메소드를 사용한다.
#### update()메소드는 쿼리실행결과로 변경된 행의 개수를 리턴한다. update()메소드 사용예는 다음과같다.
    public void update(Member member) {
		jdbcTemplate.update(
				"update member set NAME = ?, PASSWORD = ? where EMAIL = ?",
				member.getName(), member.getPassword(), member.getEmail());
	}

### 3.5 PreparedStatementCreator를 이용한 쿼리실행
##### 지금껏 작성한 코드는 쿼리에서 사용할 값을 인자로 전달했다.
##### PreparedStatemet의 set메소드를 사용해서 직접 인덱스 파라미터의 값을 설정해야할때도있다.
##### 이경우 PreparedStatementCreator를 인자로 받는 메소드를 이용해서 직접 PreparedStatement를생성하고 설정해야한다.
    jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				// 파라미터로 전달받은 Connection을 이용해서 PreparedStatement 생성
				PreparedStatement pstmt = con.prepareStatement(
						"insert into member (EMAIL, PASSWORD, NAME, REGDATE) " +
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
		});

### 3.6 INSERT 쿼리 실행 시 KeyHolder를 이용해서 자동 생성키값구하기
    쿼리 실행후 auto_increment처럼 자동생성된 키값을 알고싶다면 어떻게해야할까
    JdbcTemplate은 자동으로 생성한 키값을 구할수있는 방법을 제공해준다.
    그것이 바로 keyholder를 사용하는것이다.

        KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				// 파라미터로 전달받은 Connection을 이용해서 PreparedStatement 생성
				PreparedStatement pstmt = con.prepareStatement(
						"insert into member (EMAIL, PASSWORD, NAME, REGDATE) " +
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
---
    KeyHolder keyHolder=new GeneratedKeyHolder();
    jdbcTemplate.udate(new PreparedStatementCreator({...생략},keyHolder));

    JdbcTemplate의 update()메소드는 PreparedStatement를 실행한 후 자동 생성된 키값을
    KeyHolder에 보관한다. KeyHolder에 보관된 키값은 getKey()메소드를 이용해서 구한다. 
    이매소드는 java.lang.Number를 리턴하므로 Number의 intValue(),longValue()등의 메소드를
    사용해서 원하는 타입의 값으로반환할수있다.

#### 이후는 코드를 실행해보자..

## 4. 트랜젝션 처리

##### 이메일이 유효한지 인증시점에 테이블의 데이터를 변경하는 기능은 다음코드처럼 회원정보에서 
##### 이메일을 수정하고 인증상태를 변경하는 두쿼리를 실행할것이다.
    jdbcTemplate.update("update member set email= ?" ,email);
    jdbcTemplate.update("insert into email_auth values (?,'T)",email);

    그런데 만약 첫번째 쿼리 실행후 두번째 쿼리 실행시점에서 문제가발생하면?????
    예를들어, 코드를 잘못 수정/배포해서 두번째 쿼리에서 사용할 테이블 이름이 잘못되었을수있고,
    중복된 값이 존재해서 INSERT 쿼리를 실행하는데 실패할수도있다.
    두번째 쿼리가 실패했음에도 첫번째 쿼리실행결과가 DB에반영되면 이후 사용자의 이메일 주소는
    인증되지않은체로 남아있을것이다..
    따라서 두번째 쿼리실행이 실패하면 첫번째 쿼리실행결과도 취소해야 올바른상태를 유지할것이다.
    
    이렇게 두개이상의 쿼리를 한작업으로 실행해야할때 사용하는것이 트랜젝션이다.
    트랜잭션은 여러 쿼리를 논리적으로 하나의 작업으로 묶어준다. 한 트랜잭션으로 묶인 
    쿼리 중 하나라도 실패하면 전체쿼리를 실패로 간주하고 실패이전에 실행한 쿼리를 취소한다.
    쿼리 실행결과를 취소하고 DB를 기존상태로 되돌리는것을 롤백이라고부른다.
    반면에 트랜젝션으로 묶인 모든쿼리가 성공해서 쿼리결과를 DB에실제로 반영하는것을
    커밋이라고한다.

    트랜젝션을 시작하면 트랜잭션을 커밋하거나 롤백할 때까지 실행한 쿼리들이 하나의 작업단위가된다.
    JDBC는 Connection의 setAutoCommit(false)를 이용해서 트랜젝션을 시작하고
    commit()과 rollback()을 이용해서 트랜젝션을 반영(커밋)하거나 취소(롤백)한다.



    Connection conn=null;
    try{
        conn=DriverManager.getConnection(jdbcUrl,user,pw);
        conn.setAutoCommit(false); //트랜젝션범위시작
        ..쿼리 실행
        conn.commit();
    }catch(SQLException ex){
        if(conn!=null)
        //트랜젝션 범위 종료:롤백
        try{conn.rollback(); }catch (SQLException e){}
    }finally{
        if(conn !=null)
        try{conn.close();} catch(SQLException e){}
    }

    위와 같은 방식은 코드로 직접 트랜젝션범위를 관리하기때문에 개발자가 트랜젝션을 커밋하는
    코드나 롤백하는 코드를 누락하기쉽다. 게다가 구조적인 중복이 반복되는 문제도있다.
    스프리이 제공하는 트랜잭션 기능을 사용하면 중복잉벗는 매우간단한 코드로 트랜잭션 범위를
    지정할수있다. 

### 4.1 @Transactional을 이용한 트랜잭션 처리
##### 스프리이 제공하는 @Transactional 애노테이션을 사용하면 트랜젝션 범위를 쉽게지정할수있다.
##### 다음과 같이 트랜잭션 범위에서 실행하고 싶은메소드에 @Transactional 애노테이션만 붙이면된다.
    
	@Transactional
	public void changePassword(String email, String oldPwd, String newPwd) {
		Member member = memberDao.selectByEmail(email);
		if (member == null)
			throw new MemberNotFoundException();

		member.changePassword(oldPwd, newPwd);

		memberDao.update(member);
	}

    스프리은 @Transactional 애노테이션이 붙은 changePassword()메소드를 동일한 트랜잭션 
    범위에서 실행한다. 따라서 memberDao.selectByEmail()에서 실행하는 쿼리와 member.changePassword()에서
    실행하는 쿼리는 한 트랜젝션에 묶인다.

### @Transactional애노테이션이 제대로 동작하려면 다음의 두내용을 스프링설정에 추가해야함.
- 플랫폼 트랜잭션 매니저(PlatformTransactionManager) 빈설정
- @Transactional 애노테이션 활성화 설정
---
    @Configuration
    @EnableTransactionManagement
    public class AppCtx {
        @Bean(destroyMethod = "close")
	public DataSource dataSource() {
		DataSource ds = new DataSource();
		ds.setDriverClassName("com.mysql.jdbc.Driver");
		ds.setUrl("jdbc:mysql://localhost/springjunseong?characterEncoding=utf8");
		..생략
		return ds;
	}

	@Bean
	public PlatformTransactionManager transactionManager() {
		DataSourceTransactionManager tm = new DataSourceTransactionManager();
		tm.setDataSource(dataSource());
		return tm;
	}
    @Bean
	public MemberDao memberDao() {
		return new MemberDao(dataSource());
	    }
    }

    PlatformTransactionManager는 스프링이 제공하는트랜잭션 매니저 인터페이스이다.
    스프링은 구현기술에 상관없이 동일한 방식으로 트랜잭션을 처리하기 위해 이 인터페이스를제공한다.
    JDBC는 DataSourceTransactionManager클래스를 PlatformTransactionManager로 사용한다.
    위 설정에서 보듯이 dataSource프로퍼티를 이용해서 트랜잭션 연동에 사용할 DataSource를지정한다.

    @EnableTransactionManagement 애노테이션은 @Transactional 애노테이션이 붙은 메소드를
    트랜잭션 범위에서 실행하는 기능을 활성화한다. 등록된 PlatformTransactionManager 
    빈을 사용해서 트랜잭션을 적용한다.

    트랜잭션 처리를 위한 설정을 완료하면 트랜잭션 범위에서 실행하고 싶은 스프링 빈객체의
    메소드에 @Transactional 애노테이션을 붙이면 된다. 예를들어 ChangePasswordService클래스의
    changePassword()메소드를 트랜잭션 범위에서 실행하고싶다면 아래와같이
    changePassword()메소드에 @Transactional 애노테이션을 붙이면된다.


    public class ChangePasswordService {
	private MemberDao memberDao;
	@Transactional
	public void changePassword(String email, String oldPwd, String newPwd) {
		Member member = memberDao.selectByEmail(email);
		if (member == null)
			throw new MemberNotFoundException();
		member.changePassword(oldPwd, newPwd);
		memberDao.update(member);
	}
	public void setMemberDao(MemberDao memberDao) {
		this.memberDao = memberDao;
	    }
    }

    ///////그다음 설정클래스에서 트랜잭션설정과 changepasswordservice클래스를 빈으로추가하자.
    @Configuration
    @EnableTransactionManagement
    public class AppCtx {
    @Bean
	public ChangePasswordService changePwdSvc() {
		ChangePasswordService pwdSvc = new ChangePasswordService();
		pwdSvc.setMemberDao(memberDao());
		return pwdSvc;
	    }
    }

### `changePwdSvc빈을 이용해서 암호변경기능을 실행하는 메인클래스를 보려면 MainForCPS클래스를보자`
    
### 4.2 @Transactional과 프록시    
##### 스프링은 @Transactional 애노테이션을 이용해서 트랜잭션을 처리하기 위해 내부적으로AOP를사용한다.
##### 스프링에서 AOP는 프록시를 통해서 구현된다는 것을 기억한다면 트랜잭션 처리도 프록시를 통해서 이루어진다고 유추할수있을것이다.

    실제로 @Transactional 애노테이션을 적용하기 위해 @EnableTransactionManagement 태그를 사용하면
    스프링은 @Transactional 애노테이션이 적용된 빈객체를 찾아서 알맞은 프록시 객체를 생성한다.
    예를들어 해당 샘플코드의 MainForCps의경우 다음과 같은 구조로 프록시를 사용하게된다.

![image](https://user-images.githubusercontent.com/40031858/86788153-ce7fbe00-c0a0-11ea-805e-8eaa1d0ef941.png)

    ChangePasswordService 클래스의 메소드에 @Transactional 애노테이션이 적용되어 있으므로
    스프링은 트랜잭션 기능을 적용한 프록시 객체를 생성한다. MainForCPS 클래스에서
    getBean("changePwdSvc",ChangePasswordService.class) 코드를 실행하면 
    ChangePasswordService 객체 대신에 트랜잭션 처리를 위해 생성한 프록시객체를 리턴한다.

    이 프록시 객체는 @Transactional 애노테이션이 붙은 메소드를 호출하면 위 그림의
    1.1과정처럼 PlatformTransactionManger를 사용해서 트랜잭션을 시작한다. 트랜잭션을 시작한 후
    실제 객체의 메소드를 호출하고 (1.2~1.3과정), 성공적으로 실행하면 트랜잭션을 커밋한다.(1.4과정)
### 4.3 @Transactional 적용 메소드의 롤백처리
##### 커밋을 수행하는 주체가 프록시 객체였던 것처럼 롤백을 처리하는 주체또한 프록시객체다. 예제코드를보자
    try{
        cps.changePassword("anima94@kakao.com","1234","1111");
        System.out.println("암호를변경했다능");
    }catch(MemberNotFoundException e){
        System.out.println("회원 데이터가 존재하지않습니다.");
    }catch(WrongIdPasswordException e){
        System.out.println("암호가 올바르지않습니다");
    }

    이 코드의 실행결과를 보면 WrongIdPasswordException이 발생했을때 트랜잭션이 롤백된것을
    알 수 있다. 실제로 @Transactional을 처리하기 위한 프록시 객체는 원본 객체의 메소드를
    실행하는 과정에서 RuntimeException이 발생하면 아래그림처럼 트랜잭션을 롤백한다.    
![image](https://user-images.githubusercontent.com/40031858/86789483-4f8b8500-c0a2-11ea-99a3-490fd34354ce.png)

    별도 설정을 추가하지 않으면 발생한 익셉션이 RuntimeException일 때 트랜잭션을 롤백한다.
    WrongIdPasswordException 클래스를 구현할 때 RuntimeException을 상속한 이유는 바로 트랜잭션
    롤백을 염두해두었기 때문이다.

    JdbcTemplate은 DB연동과정에 문제가 있으면 DataAcessException을 발생한다고 했는데
    DataAcessException 역시 RuntimeException을 상속받고있다. 따라서 
    JdbcTemplate의 기능을 실행하는 도중 익셉션이 발생해도 프록시는 트랜잭션을 롤백한다.

    SQLException은 RuntimeException을 상속하고 있지않으므로 SQLException이 발생하면 트랜잭션을
    롤백하지않는다. RuntimeException뿐만아니라 SQLException이 발생하는 경우에도 트랜잭션을 
    롤백하고싶다면 @Transactional의 rollbackFor속성을 사용해야한다.

    @Transactional(rollbackFor=SQLException.class)
    public void someMethod(){
        ...
    }

    위와 같이 @Transactional의 rollbackFor 속성을 설정하면 RuntimeException뿐만아니라 SQLException이
    발생하는 경우에도 트랜젝션을 롤백한다. 여러 익셉션 타입을 지정하고 싶다면 {}배열로 지정하면된다.

    rollbackFor와 반대설정을 제공하는것이 noRollbackFor속성이다. 이속성은 익셉션이 발생해도
    롤백시키지않고 커밋할 익셉션타입을 지정할때 사용한다. 

### 4.4 @Transactional의 주요 속성
| 속성 | 타입|설명| 
|---|---|--|
|`value` | String |트랜잭션을 관리할때 사용할 PlatformTransactionManager 빈의 이름을지정한다. 기본값은 " "다|
|`propagation` |Propagation |트랜잭션 전파타입을 지정한다. 기본값은 Propagation.REQUIRED이다 |
|`isolation` |Isolation | 트랜잭션 격리 레벨을 지정한다. 기본값은 Isolation.DEFAULT이다. |
|`timeout` |int |트랜잭션 제한시간을 지정한다. 기본값은 -1로 이경우 데이터베이스의 타임아웃시간을사용한다. 초단위로 지정한다. |

##### @Transactional 애노테이션의 value속성값이 없으면 등록된 빈중에서 타입이 PlatformTransactionManager인 빈을사용한다.

    ///AppCtx 설정클래스의 플랫폼 트랜잭션 매니저 빈설정
    @Bean
    public PlatformTransactionManager transactionManager(){
        DataSourceTransactionManager tm=new DataSourceTransactionManager();
        tm.setDataSource(dataSource());
        return tm;
    }
### Propagation 열거타입에 정의되어 있는값목록은 다음과같다
|값|설명|
|---|---|
|`REQUIRED`|메소드를 수행하는데트랜잭션이 필요하다는것을 의미한다. 현재 진행중인 트랜잭션이 존재하면 해당 트랜잭션을 사용한다. 존재하지 않으면 새로운 트랜잭션을 생성한다. |
|`MANDATORY` |메소드를 수행하는 데 트랜잭션이 필요하다는것을 의미한다. 하지만 REQUIRED와 달리 진행 중인 트랜잭션이 존재하지않을 경우 익셉션이 발생한다 |
|`REQUIRES_NEW` | 항상 새롱누 트랜잭션을 시작한다. 진행 중인 트랜잭션이 존재하면 기존 트랜잭션을 일시 중지하고 새로운 트랜잭션을 시작한다. 새로 시작된 트랜잭션이 종료된 뒤에 기존 트랜잭션이 계속된다|
|`SUPPORTS` |메소드가 트랜잭션을 필요로 하지는않지만 , 진행중인 트랜잭션이 존재하면 트랜잭션을 사용한다는것을 의미한다. 진행중인 트랜잭션이 존재하지않더라도 메소드는 정상적으로 동작한다. |
|`NOT_SUPPORTED` |메소드가 트랜잭션을 필요로 하지않음을 의미한다. SUPPORTS와 달리 진행 중인 트랜잭션이 존재할 경우 메소드가 실행되는 동안 트랜잭션은 일시 중지되고 메소드 실행이 종료된 후에 트랜잭션을 계속 진행한다. |
|`NEVER` |메소드가 트랜잭션을 필요로 하지않는다. 만약 진행중인 트랜잭션이 존재하면 익셉션이 발생한다 |
|`NESTED` |진행중인 트랜잭션이 존재하면 기존 트랜잭션에 중첩된 트랜잭션에서 메소드를 실행한다.진행중인 트랜잭션이 존재하지않으면 REQUIRED와 동일하게 도작한다. 이기능은 JDBC3.0드라이버를 사용할때에만 적용된다. |

### Isolation 열거타입에 정의된값은 다음과같다
|값|설명|
|---|---|
|`DEFAULT`|기본설정을사용한다|
|`READ_UNCOMMITTED`|다른 트랜잭션이 커밋하지않은 데이터를 읽을수있다|
|`READ_COMMITTED`|다른트랜잭션이 커밋한 데이터를 읽을수있다|
|`REPEATABLE_READ`|처음에 읽어 온 데이터와 두번째 읽어 온 데이터가 동일한값을갖는다|
|`SERIALIZABLE`|동일한 데이터에 대해서 동시에 두개이상의 트랜잭션을 수행할수없다|

### 4.5 @EnableTransactionManagement 애노테이션의 주요속성
|속성|설명|
|---|---|
|`proxyTargetClass`|클래스를 이용해서 프록시를 생성할지 여부를 지정한다. 기본값은 false로서 인터페이스를 이용해서 프록시를 생성|
|`order`|AOP적용 순서를 지정한다. 기본값은 가장 낮은 우선순위에 해당하는 int의 최댓값이다.|

### 4.6 트랜잭션 전파
#### Propagation열거타입목록에서 REQUIRED 값의 설명은 다음과같다.
##### `메소드를 수행하는데 트랜젝션이 필요하다는것을의미한다. 현재 진행중인 트랜잭션이 존재하면 해당트랜잭션을 사용한다`
##### `존재하지않으면 새로운 트랜잭션을 생성한다`

    public class someservice{
        private ANYservice anyService;
        @Transactional
        public void some(){
            anyService.any();
        }
        public void setAnyService(AnyService as){
            this.anyService=as;
        }
    }
    public class AnyService{
        @Transactional
        public void any(){..}
    }

    --------------------------------
    @Configuration
    @EnableTransactionManagement
    public class Config{
        @Bean
        public someservice some(){
            someservice some=new someservice();
            some.setAnyService(any());
            return some;
        }
        @Bean
        public AnyService any(){
            return new AnyService();
        }
    }

    someservice클래스와 anyservice클래스는 둘다@Transactional 애노테이션을 적용하고있다.
    위코드의 설정클래스를 보면 두클래스에 대해 프록시가 생성된다.
    someservice의 some()메소드를 호출하면 트랜잭션이 시작되고 anyservice의 any()메소드를
    호출해도 트랜잭션이 시작된다. 그런데 some()메소드는 내부에서 다시 any()메소드를 호출한다.
    이러한경우 트랜잭션 처리는 어떻게되는것일까???????

    @Transactional의 propagation속성은 기본값이 Propagation.REQUIRED이다.
    REQUIRED는 현재진행중인 트랜잭션이 존재하면 해당 트랜잭션사용을 중지하고 존재하지않으면
    새로운 트랜잭션을 생성한다고했다.
    처음 some()메소드를 호출하면 트랜잭션을 새로시작하지만 some()메소드 내부에서 any()메소드를호출하면
    이미 some()메소드에 의해 시작된 트랜잭션이 존재하므로 any()메소드를 호출하는 시점에는 트랜잭션을
    새로 생성하지않는다. 대신 존재하는 트랜잭션을 그대로 사용한다. 즉 some()메소드와 any()메소드를 한
    트랜잭션으로 묶어서 실행하는것이다.

    만약 any()메소드에 적용한 @Transactional의 propagation 속성값이 REQUIRES_NEW라면 
    기존 트랜잭션이 존재하는지 여부에 상관없이 항상 새로운 트랜잭션을 시작한다. 
    따라서 이경우는 some()메소드에 의해 트랜잭션이 생성되고 다시 any()메소드에 의해 트랜잭션이 생성된다.
-----
### 다음코드를 보자
    public class ChangePasswordService {
	private MemberDao memberDao;
	@Transactional
	public void changePassword(String email, String oldPwd, String newPwd) {
		Member member = memberDao.selectByEmail(email);
		if (member == null)
			throw new MemberNotFoundException();

		member.changePassword(oldPwd, newPwd);

		memberDao.update(member);
	    }
    }


    public class MemberDao {
	private JdbcTemplate jdbcTemplate;

    //Transactional 없음
        public void update(Member member) {
		jdbcTemplate.update(
				"update member set NAME = ?, PASSWORD = ? where EMAIL = ?",
				member.getName(), member.getPassword(), member.getEmail());
	    }
    }

    changePassword()메소드는 memberdao의 update()메소드를 호출하고잇다. 그런데 
    MemberDao.update()메소드는 @Transactional애노테이션이 적용되어잇지않다.
    이러한경우는 어떻게될까?

    비록 update()메소드에 @Transactional이 붙어있지않지만 JdbcTemplate클래스 덕에 트랜잭션
    범위에서 쿼리를 실행할수있게된다.JdbcTemplate은 진행중인 트랜잭션이 존재하면 해당트랜잭션
    범위에서 쿼리를 실행한다.

### 이제 해당 샘플코드의 Main을 실행시켜서 코드로직을 살펴보자 