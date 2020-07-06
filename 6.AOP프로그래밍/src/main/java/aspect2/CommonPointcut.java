package aspect2;

import org.aspectj.lang.annotation.Pointcut;

public class CommonPointcut {

	@Pointcut("execution(public * chap06..*(..))")
	public void commonTarget() {
	}

}
