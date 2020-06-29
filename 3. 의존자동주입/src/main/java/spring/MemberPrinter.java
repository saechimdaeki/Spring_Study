package spring;

import org.springframework.beans.factory.annotation.Autowired;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class MemberPrinter {
	private DateTimeFormatter dateTimeFormatter;
	public void print(Member member) {
		System.out.printf(
				"회원 정보: 아이디=%d, 이메일=%s, 이름=%s, 등록일=%tF\n", 
				member.getId(), member.getEmail(),
				member.getName(), member.getRegisterDateTime());
	}
	@Autowired(required = false)
	public void setDateTimeFormatter(Optional<DateTimeFormatter> formatterOpt){
		if(formatterOpt.isPresent())
			this.dateTimeFormatter=formatterOpt.get();
		else
			this.dateTimeFormatter=null;
	}

}
