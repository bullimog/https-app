package app.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import app.models.Greeting;
import org.springframework.web.servlet.ModelAndView;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;


@Controller
public class HomeController {


    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/greeting")
    public ModelAndView greeting(@RequestParam(value="name", defaultValue="World") String name) {

        Greeting g = new Greeting(counter.incrementAndGet(),
                String.format(template, name));

        HashMap<String, Object> params = new HashMap<>();
        params.put("greeting", g);
        return new ModelAndView("greeting", params);
    }
}
