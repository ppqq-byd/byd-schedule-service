package com.ora.blockchain.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(path = "/hello")
@Api(description = "Welcome")
public class HelloWorldController {

    @RequestMapping(path = "/world", method = RequestMethod.GET)
    @ApiOperation(value = "Hello World")
    public String helloWorld() {
        return "Hello Blockchain World!";
    }

}