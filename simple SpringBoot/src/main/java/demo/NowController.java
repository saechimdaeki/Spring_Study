package demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NowController {
    @GetMapping("/now")
    public Now now(){
        return new Now();
    }
}
