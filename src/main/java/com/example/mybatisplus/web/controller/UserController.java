package com.example.mybatisplus.web.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.mybatisplus.model.domain.AiDictionary;
import com.example.mybatisplus.model.domain.Picture;
import com.example.mybatisplus.service.AiDictionaryService;
import com.example.mybatisplus.service.PictureService;
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
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


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

    private static List<Integer> roomNum = new ArrayList<>();
    private static ConcurrentHashMap<Integer, ConcurrentHashMap<String, String>> user_pic = new ConcurrentHashMap<>();

    //AI词库
    private static ConcurrentHashMap<String, List<String>> user_wordList = new ConcurrentHashMap<>();

    @Autowired
    private UserService userService;

    @Autowired
    private PictureService pictureService;

    @Autowired
    private AiDictionaryService aiDictionaryService;

    public static void exit(Integer roomid) {
        roomNum.remove(roomid);
    }

    public static ConcurrentHashMap<Integer, ConcurrentHashMap<String, String>> getUser_pic() {
        return user_pic;
    }

    public static void removePic(String openId, Integer roomid) {
        System.out.println("删除前：" + user_pic);
        user_pic.get(roomid).remove(openId);
        if (user_pic.get(roomid).size() == 0) {
            user_pic.remove(roomid);
        }
        System.out.println("删除后：" + user_pic);
    }

    /**
     * 微信授权登录
     *
     * @param code
     * @param nickName
     * @param avatarUrl
     * @return
     */
    @ResponseBody
    @RequestMapping("/login")
    public JSONObject login(@RequestParam("code") String code, @RequestParam("nickName") String nickName, @RequestParam("avatarUrl") String avatarUrl) {
        //小程序端发送过来的code
        String result = "";
        //微信服务器接口
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid=wxb06b52c78c391e39&secret=860aa603e344c2aa7024308e6f48d5fa&js_code=";
        url += code + "&grant_type=authorization_code";
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


        String openid = (String) jsonObject.get("openid");
        QueryWrapper<User> wrapper = new QueryWrapper<User>();
        wrapper.eq("openid", openid);
        //不存在该openid用户则进行添加
        if (null == userService.getOne(wrapper)) {
            User user = new User();
            user.setOpenid(openid);
            user.setHig(0);
            user.setName(nickName);
            user.setPicUrl(avatarUrl);
            userService.save(user);
        }
        System.out.println(openid);
        return jsonObject;
    }

    /**
     * 描述:创建房间
     */
    @ResponseBody
    @RequestMapping("/createRoom")
    public JsonResponse createRoom() throws Exception {

        Random rd = new Random();
        int num = -1;
        while (roomNum.size() < 1000000) {
            num = rd.nextInt(1000001);
            if (!roomNum.contains(num)) {
                roomNum.add(num);
                break;
            }
        }
        System.out.println(roomNum);
        return JsonResponse.success(num);
    }


    /**
     * 描述:加入房间
     */
    @ResponseBody
    @RequestMapping("/joinRoom")
    public JsonResponse joinRoom(@RequestParam("roomId") Integer roomId) throws Exception {
        boolean flag = false;
        System.out.println(roomId);
        if (roomNum.contains(roomId)) {
            flag = true;
        }
        return JsonResponse.success(flag);
    }

    /**
     * 描述:选词
     */
    @ResponseBody
    @RequestMapping("/chooseWord")
    public JsonResponse chooseWord(@RequestParam("roomId") Integer roomId, @RequestParam("openId") String openId, @RequestParam("word") String word) throws Exception {
        ConcurrentHashMap<String, String> map = null;
        if (user_pic.get(roomId) != null) {
            map = user_pic.get(roomId);
        } else {
            map = new ConcurrentHashMap<>();
        }
        map.put(openId, word);
        user_pic.put(roomId, map);
        System.out.println("选词:" + user_pic);
        return JsonResponse.success(null);
    }

    /**
     * 描述：排行榜查询
     */
    @RequestMapping("/rank")
    @ResponseBody
    public JsonResponse rank(@RequestParam("openId") String openid) throws Exception {
        List<User> users = userService.getRank();
        Map<String, List<JSONObject>> finalMap = new HashMap<>();
        List<JSONObject> rankMap = new ArrayList<>();

        //处理并列情况
        int rank = 1;
        if (users.size() != 0) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name",users.get(0).getName());
            jsonObject.put("picUrl",users.get(0).getPicUrl());
            jsonObject.put("rank",rank);
            jsonObject.put("hig",users.get(0).getHig());
            rankMap.add(jsonObject);
            for (int i = 1; i < users.size(); i++) {
                System.out.println(users.get(i) + " " + i);
                if (users.get(i).getHig() == users.get(i - 1).getHig()) {
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("name",users.get(i).getName());
                    jsonObject1.put("picUrl",users.get(i).getPicUrl());
                    jsonObject1.put("rank",rank);
                    jsonObject1.put("hig",users.get(i).getHig());
                    rankMap.add(jsonObject1);
                } else {
                    rank = i+1;
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("name",users.get(i).getName());
                    jsonObject1.put("picUrl",users.get(i).getPicUrl());
                    jsonObject1.put("rank",rank);
                    jsonObject1.put("hig",users.get(i).getHig());
                    rankMap.add(jsonObject1);
                }
            }
        }
        System.out.println(rankMap);
        finalMap.put("rank", rankMap);


        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("openid", openid);
        User myself = userService.getOne(wrapper);
        List<JSONObject> myRankMap = new ArrayList<>();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name",myself.getName());
        jsonObject.put("picUrl",myself.getPicUrl());
        jsonObject.put("rank",userService.getMyRank(openid));
        jsonObject.put("hig",myself.getHig());
        myRankMap.add(jsonObject);
        finalMap.put("myRank", myRankMap);
        System.out.println(finalMap);
        return JsonResponse.success(finalMap);
    }

    /**
     * 描述:我的作品
     */
    @ResponseBody
    @RequestMapping("/myPic")
    public JsonResponse myPic(@RequestParam("openId") String openId) throws Exception {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("openid", openId);
        Long userId = userService.getOne(wrapper).getId();

        List<Picture> pictures = pictureService.getMyPic(userId);
        return JsonResponse.success(pictures);
    }

    /**
     * 描述:AI开始游戏
     */
    @ResponseBody
    @RequestMapping("/start")
    public JsonResponse start(@RequestParam("openId") String openId) throws Exception {
        List<String> wordList;
        if (user_wordList.get(openId) == null) {
            wordList = new ArrayList<>();
            user_wordList.put(openId, wordList);
        } else {
            wordList = user_wordList.get(openId);
        }
        AiDictionary aiDictionary = aiDictionaryService.selectWord(wordList);
        wordList.add(aiDictionary.getName());
        user_wordList.put(openId, wordList);
        System.out.println("词："+user_wordList.get(openId));
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("title",aiDictionary.getName());
        jsonObject.put("count",wordList.size());
        System.out.println(jsonObject);
        return JsonResponse.success(jsonObject);
    }


    /**
     * 描述:结束
     */
    @ResponseBody
    @RequestMapping("/over")
    public JsonResponse over(@RequestParam("openId") String openId) throws Exception {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("openid",openId);
        User user = userService.getOne(wrapper);
        List<String> wordList;
        if(user_wordList.get(openId) != null){
            wordList = user_wordList.get(openId);
            //刷新记录
            if(wordList.size()-1>user.getHig()){
                user.setHig(wordList.size());
                userService.saveOrUpdate(user);
            }
            System.out.println("结束前："+user_wordList);
            user_wordList.remove(openId);
            System.out.println("结束后："+user_wordList);

        }

        return JsonResponse.success(null);
    }



}

