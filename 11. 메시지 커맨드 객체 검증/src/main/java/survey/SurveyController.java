package survey;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/survey")
public class SurveyController {
    @GetMapping
    public ModelAndView form(){
        List<Question> questions=createQuestions();
        ModelAndView mav=new ModelAndView();
        mav.addObject("questions",questions);
        mav.setViewName("survey/surveyForm");
        return mav;
    }
    private List<Question> createQuestions(){
        Question q1=new Question("당신의 역할은 무엇입니깡?",
                Arrays.asList("서버","프론트","풀스택"));
        Question q2=new Question("많이사요하는 개발도구는 무엇입니깡?",
                Arrays.asList("이클립스","인텔리제이","서브라임"));
        Question q3=new Question("하고싶은말을 적어달라능");
        return Arrays.asList(q1,q2,q3);

    }


    @PostMapping
    public String submit(@ModelAttribute("ansData") AnswereData data){
        return "survey/submitted";
    }
}
