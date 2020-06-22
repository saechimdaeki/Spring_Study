import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SingleTonMain {
    public static void main(String[] args)
    {
        AnnotationConfigApplicationContext ctx=new AnnotationConfigApplicationContext(AppContext.class);
        Hello h1=ctx.getBean("hello",Hello.class);
        Hello h2=ctx.getBean("hello",Hello.class);
        System.out.println("(h1 == h2) =" +(h1==h2));
        ctx.close();
    }
    //별도 설정을 하지않을경우 스프리은 한개의 빈 객체만을 생성. 이 객체는 싱글톤범위를가짐.
}
