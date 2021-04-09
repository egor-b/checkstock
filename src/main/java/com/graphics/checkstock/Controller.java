package com.graphics.checkstock;

import com.graphics.checkstock.service.CheckService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
public class Controller {

    private final CheckService checkService;

    @GetMapping(path = "/start")
    @ResponseStatus(HttpStatus.OK)
    public String start() throws IOException, InterruptedException {
        checkService.start();
        log.info("checking is scheduled");
        return "Start checking stocks";
    }

    @GetMapping(path = "/stat")
    @ResponseStatus(HttpStatus.OK)
    public String stat() {
        return checkService.getStat() + " times was checked GPU stock";
    }
}
