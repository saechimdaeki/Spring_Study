import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args){
        AnnotationConfigApplicationContext ctx=new AnnotationConfigApplicationContext(AppContext.class);
        Hello h=ctx.getBean("hello",Hello.class);
        String messgae=h.hello("saechimdaeki");
        System.out.print(messgae);
        ctx.close();
    }
}
