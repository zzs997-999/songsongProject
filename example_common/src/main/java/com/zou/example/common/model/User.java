package com.zou.example.common.model;

import com.zou.example.common.service.UserService;

import java.io.Serializable;

public class User implements Serializable {
    private String name;
    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name=name;
        return;
    }
}
