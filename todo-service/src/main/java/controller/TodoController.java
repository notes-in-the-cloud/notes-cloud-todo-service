package controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TodoController {

    @GetMapping("/Hello World")
    public String helloWorld() {
        return "Hello World!";
    }
}
