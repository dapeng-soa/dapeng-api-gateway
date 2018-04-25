package com.github.dapeng.api.gateway.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * @author with struy.
 * Create by 2018/4/17 14:21
 * email :yq1724555319@gmail.com
 */

@Controller
@RequestMapping("/")
public class ApiIndexController {

    @GetMapping
    public ResponseEntity<?> index() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("gateway is working !");
    }
}
