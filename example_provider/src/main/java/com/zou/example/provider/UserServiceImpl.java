package com.zou.example.provider;
import com.zou.example.common.model.User;
import com.zou.example.common.service.UserService;
public class UserServiceImpl implements UserService{
    @Override
    public User getUser(User user){
        System.out.println("用户名:"+user.getName());
        return user;
    }
}
