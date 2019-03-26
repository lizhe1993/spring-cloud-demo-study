package spring.cloud.demo.study;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import spring.cloud.demo.study.annotation.EnableLogFilter;

@SpringBootApplication
@RestController
@EnableLogFilter
public class FilterLogStarterDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(FilterLogStarterDemoApplication.class, args);
    }

    @GetMapping("/test")
    public String test() {
        return "this is a demo boot.";
    }

}
