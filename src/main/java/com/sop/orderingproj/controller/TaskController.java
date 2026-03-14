package com.sop.orderingproj.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sop.orderingproj.dto.TaskDto;


@RestController
@RequestMapping("/task")
public class TaskController {

    @PostMapping
    public ResponseEntity<TaskDto> postMethodName(@RequestBody TaskDto task) {
        //TODO: process POST request
        
        return new ResponseEntity<>(task, HttpStatus.OK);
    }
    
}
