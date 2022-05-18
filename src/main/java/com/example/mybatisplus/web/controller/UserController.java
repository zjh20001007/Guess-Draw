package com.example.mybatisplus.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.ibatis.annotations.Param;
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
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Wrapper;
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


    @ResponseBody
    @RequestMapping("/login")
    public JSONObject login(@RequestParam("code") String code){
        //小程序端发送过来的code
        String result = "";
        //微信服务器接口
        String url="https://api.weixin.qq.com/sns/jscode2session?appid=wxb06b52c78c391e39&secret=860aa603e344c2aa7024308e6f48d5fa&js_code=";
        url+=code+"&grant_type=authorization_code";
        BufferedReader in = null;
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        JSONObject jsonObject = JSONObject.parseObject(result);
        String openid = (String)jsonObject.get("openid");
        QueryWrapper<User> wrapper = new QueryWrapper<User>();
        wrapper.eq("openid",openid);
        //不存在该openid用户则进行添加
        if(null == userService.getOne(wrapper)){
            User user = new User();
            user.setOpenid(openid);
            user.setHig(0);
            userService.save(user);
        }
        System.out.println(openid);
        return jsonObject;
    }

    /**
     * 描述:创建User
     */
    @RequestMapping("/create")
    @ResponseBody
    public JsonResponse create(@RequestBody JSONObject jsonObject) throws Exception {
        User user = new User();
        user.setName(jsonObject.getString("nickName"));
        user.setPicUrl(jsonObject.getString("avatarUrl"));
        userService.saveOrUpdate(user);
        return JsonResponse.success(null);
    }


    /**
     * 描述：根据Id 查询
     */
    @RequestMapping("/rank/{userId}")
    @ResponseBody
    public JsonResponse rank(@PathVariable Long userId) throws Exception {
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
        myRankMap.put(myself, userService.getMyRank(userId));
        finalMap.put("myRank",myRankMap);
        System.out.println(finalMap);
        return JsonResponse.success(finalMap);
    }
}

