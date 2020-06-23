import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/* Spring을사용한 Main클래스입니다 */
public class SpringMain {
    private static ApplicationContext ctx=null;
    public static void main(String[] args) throws IOException {
        BufferedReader reader=new BufferedReader(new InputStreamReader(System.in));
        ctx=new AnnotationConfigApplicationContext(AppConfImport.class);
        while(true){
            System.out.println("명령어를 입력해주세용");
            String command=reader.readLine();
            if(command.equalsIgnoreCase("exit")){
                System.out.println("프로그램을 종료합니당");
                break;
            }
            if(command.startsWith("new ")){
                processNewCommand(command.split(" "));
                continue;
            }else if(command.startsWith("change ")){
                processChangeCommand(command.split(" "));
                continue;
            }else if(command.equals("list")){
                processListCommand();
                continue;
            }else if(command.startsWith("info ")){
                processInfoCommand(command.split(" "));
                continue;
            }else if(command.equals("version")){
                processVersionCommand();
                continue;
            }
            printhelp();
        }
    }
    private static void processNewCommand(String[] arg){
        if(arg.length!=5){
            printhelp();
            return;
        }
        MemberRegisterService regSvc=ctx.getBean("memberRegSvc",MemberRegisterService.class);
        RegisterRequest req=new RegisterRequest();
        req.setEmail(arg[1]);
        req.setName(arg[2]);
        req.setPassword(arg[3]);
        req.setConfirmPassword(arg[4]);

        if(!req.isPasswordEqualToConfirmPassword()){
            System.out.println("암호와 확인이 일치하지않는다 \n");
            return;
        }try{
            regSvc.regist(req);
            System.out.println("등록했습니다 \n");
        }catch(DuplicateMemberException e){
            System.out.println("이미 존재하는 이메일입니덩\n");
        }
    }
    private static void processChangeCommand(String[] arg){
        if(arg.length!=4){
            printhelp();
            return;
        }
        ChangePasswordService changePwdSvc=ctx.getBean("changePwdSvc",ChangePasswordService.class);
        try{
            changePwdSvc.ChangePassword(arg[1],arg[2],arg[3]);
            System.out.println("암호를 변경했습니다 \n");
        }catch (MemberNotFoundException e){
            System.out.println("존재하지 않는 이메일입니다 \n");
        }catch(WrongIdPasswordException e){
            System.out.println("이메일과 암호가 일치하지않는다 \n");
        }
    }
    private static void processListCommand(){
        MemberListPrinter listPrinter=ctx.getBean("listPrinter",MemberListPrinter.class);
        listPrinter.printAll();
    }
    private static void processInfoCommand(String[] arg){
        if(arg.length!=2){
            printhelp();
            return;
        }
        MemberInfoPrinter infoPrinter=ctx.getBean("infoPrinter",MemberInfoPrinter.class);
        infoPrinter.printMemberInfo(arg[1]);
    }
    private static void printhelp(){
        System.out.println();
        System.out.println("잘못된 명령어입니다 아래명령어 사용법을 확인해보세요");
        System.out.println("명령어 사용법:");
        System.out.println("new 이메일 이름 암호 암호확인");
        System.out.println("change 이메일 현재비번 변경비번");
        System.out.println();
    }
    private static void processVersionCommand(){
        VersionPrinter versionPrinter=ctx.getBean("versionPrinter",VersionPrinter.class);
        versionPrinter.print();
    }
}
