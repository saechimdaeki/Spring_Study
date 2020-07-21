## @ResponseBody를 사용할경우
- Http의 Body에 문자내용을 직접 반환
- `viewResolver`대신에 `HttpMessageConverter`가 동작
- 기본문자처리:`StringHttpMessageConverter`
- 기본객체처리:`MappingJackson2HttpMessageConverter`
- byte처리 등등 기타 여러 HttpMessageConverter가 기본으로 등록되어있음


#### 참고: 클라이언트의 HTTP Accept헤더와 서버의 컨트롤러 반환 타입 정보 둘을 조합해서 `HttpMessageConverter`가 선택된다.
![image](https://user-images.githubusercontent.com/40031858/88055427-f3dcf380-cb99-11ea-814f-599027106306.png)

```
테스트 코드는 의존관계없이 설계가 되어야한다
그러므로 @AfterEach 애노테이션을통해 각테스트가 끝날때마다 초기화 하는메소드를 만들자.
```