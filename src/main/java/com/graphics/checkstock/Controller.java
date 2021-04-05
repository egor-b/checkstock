package com.graphics.checkstock;

import com.graphics.checkstock.service.CheckService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Timer;

@Slf4j
@RestController
public class Controller {

    private Timer timer = new Timer();

    @GetMapping(path = "/start")
    @ResponseStatus(HttpStatus.OK)
    public String start() {
        timer.schedule(new CheckService(), 0, 10000);
        log.info("checking is scheduled");
        return "I'm checking every 10 seconds";
    }

    @GetMapping(path = "/stop")
    @ResponseStatus(HttpStatus.OK)
    public String stop() {
        log.info("checking is canceled");
        return "don't check anymore";
    }
}
