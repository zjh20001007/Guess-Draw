package com.example.mybatisplus.web.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.mybatisplus.common.JsonResponse;
import com.example.mybatisplus.service.UserService;
import com.example.mybatisplus.model.domain.User;


/**
 *
 *  前端控制器
 *
 *
 * @author
 * @since 2022-05-10
 * @version v1.0
 */
@Controller
@RequestMapping("/user")
public class UserController {

    private final Logger logger = LoggerFactory.getLogger( UserController.class );

    @Autowired
    private UserService userService;

    /**
    * 描述:创建User
    *
    */
    @RequestMapping("/create")
    @ResponseBody
    public JsonResponse create(@RequestBody JSONObject jsonObject) throws Exception {
        User user = new User();
        user.setName(jsonObject.getString("nickName"));
        user.setPicUrl(jsonObject.getString("avatarUrl"));
        user.setOpenid(jsonObject.getString("openid"));
        user.setHig(0);
        userService.save(user);
        return JsonResponse.success(null);
    }
}

