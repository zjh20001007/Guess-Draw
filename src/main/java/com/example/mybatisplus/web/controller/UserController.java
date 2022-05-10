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

import javax.websocket.server.PathParam;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 前端控制器
 *
 * @author
 * @version v1.0
 * @since 2022-05-10
 */
@Controller
@RequestMapping("/user")
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    /**
     * 描述:创建User
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


    /**
     * 描述：根据Id 查询
     */
    @RequestMapping("/rank/{userId}")
    @ResponseBody
    public JsonResponse rank(@PathVariable String userId) throws Exception {
        List<User> users = userService.getRank();
        Map<String,Map<User, Integer>> finalMap = new HashMap<>();
        Map<User, Integer> rankMap = new HashMap<>();

        //处理并列情况
        int rank = 1;
        if (users.size() != 0) {
            rankMap.put(users.get(0), rank);
            for (int i = 1; i < users.size(); i++) {
                System.out.println(users.get(i) + " " + i);
                if (users.get(i).getHig() == users.get(i - 1).getHig()) {
                    rankMap.put(users.get(i), rank);
                } else {
                    rank = i+1;
                    rankMap.put(users.get(i), rank);
                }
            }
        }
        finalMap.put("rank",rankMap);

        Map<User, Integer> myRankMap = new HashMap<>();
        User myself = userService.getById(userId);
        Integer myRank = userService.getMyRank(userId);
        myRankMap.put(myself, userService.getMyRank(userId));
        finalMap.put("myRank",myRankMap);
        System.out.println(finalMap);
        return JsonResponse.success(finalMap);
    }
}

