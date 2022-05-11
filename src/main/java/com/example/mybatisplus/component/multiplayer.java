package com.example.mybatisplus.component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.mybatisplus.model.domain.User;
import com.example.mybatisplus.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint("/websocket/multiplayer/{roomId}/{userId}")
public class multiplayer {

    private static SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");//创建时间格式对象
    //创建房间的集合，使用ConcurrentHashMap是为了保证线程安全，HashMap在多线程的情况下会出现问题
    private static ConcurrentHashMap<Long, ConcurrentHashMap<User, multiplayer>> roomList = new ConcurrentHashMap<>();
    // 与某个客户端的连接会话，需要通过他来给客户端发送消息
    private Session session;

    private static UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }


    @OnOpen
    public void onOpen(@PathParam("roomId") Long roomId, @PathParam("userId") Long userId, Session session) {
        System.out.println("opening...");
        this.session = session;
        this.joinRoom(roomId, userId);
    }


    //消息类型：1--聊天内容，2--图画内容
    @OnMessage
    public void onMessage(String message, @PathParam("roomId") Long roomId, @PathParam("userId") Long userId, Session session) throws IOException {

        JSONObject jsonObject = JSON.parseObject(message);
        if (1 == jsonObject.getInteger("state")) {

            //当前题目
            String title = jsonObject.getString("title");
            //用户输入的消息内容
            String mes = jsonObject.getString("message");
            //服务器返回的加密内容
            String ans;
            String userName;

            //查找该userId的用户
            User user = userService.getById(userId);

            //message加密
            char[] mesArray = mes.toCharArray();
            if (mes.equals(title)) {//用户猜图正确
                ans = "用户" + user.getName() + "答对了";
                userName = "系统";
            } else {//用户猜图不正确
                for (int i = 0; i < mesArray.length; i++) {
                    if (title.contains(mesArray[i] + "")) {
                        mesArray[i] = '*';
                    }
                }
                userName = user.getName();
                ans = String.valueOf(mesArray);
            }

            //广播给所有该房间的客户端
            ConcurrentHashMap<User, multiplayer> room = roomList.get(roomId);
            Map<String, Object> map = new HashMap<>();
            map.put("message", ans);
            map.put("name", userName);
            map.put("pic", user.getPicUrl());
            for (User item : room.keySet()) {
                room.get(item).sendMessage(map);
            }
        } else if (2 == jsonObject.getInteger("state")) {//画图信息
            jsonObject.remove("state");
            //广播给所有该房间的客户端
            ConcurrentHashMap<User, multiplayer> room = roomList.get(roomId);
            for (User item : room.keySet()) {
                room.get(item).sendMessage(jsonObject);
            }
        }

    }

    /**
     * 发送消息
     *
     * @param map
     */
    public void sendMessage(Map<String, Object> map) {
        JSONObject jsonObject = new JSONObject(map);
        try {
            this.session.getBasicRemote().sendText(jsonObject.toJSONString(map));
            System.out.println(jsonObject.toJSONString(map));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 加入房间
     *
     * @param roomId
     * @param userId
     */
    public void joinRoom(Long roomId, Long userId) {

        //查找该userId的用户
        User user = userService.getById(userId);

        if (!roomList.containsKey(roomId)) {
            // 创建房间不存在时，创建房间
            ConcurrentHashMap<User, multiplayer> room = new ConcurrentHashMap<>();
            // 添加用户
            room.put(user, this);
            roomList.put(roomId, room);
        } else {// 房间已存在，直接添加用户到相应的房间
            ConcurrentHashMap<User, multiplayer> room = roomList.get(roomId);
            room.put(user, this);
            //发送消息给房间内的其他人，通知他们user已经进入房间
            for (User item : room.keySet()) {
                System.out.println("用户" + user.getName() + "进入房间");
                Map<String, Object> map = new HashMap<>();
                map.put("name", user.getName());
                map.put("status", "进入房间");
                //传递个人信息
                room.get(item).sendMessage(map);
            }
        }
    }


    @OnClose
    public void onClose(@PathParam("roomId") Long roomId, @PathParam("userId") Long userId, Session session) {
        this.exitRoom(roomId, userId);
        System.out.println("onClose");
    }

    /**
     * 退出房间
     *
     * @param roomId
     * @param userId
     */
    public void exitRoom(Long roomId, Long userId) {
        //查找该userId的用户
        User user = userService.getById(userId);

        ConcurrentHashMap<User, multiplayer> room = roomList.get(roomId);
        if (room.keySet().size() == 1) {//只剩余一个人，解除房间
            room.remove(room.get(user));
            roomList.remove(room);
        } else {//还剩余多人，删除退出该房间的人
            room.remove(room.get(user));
            for (User item : room.keySet()) {
                System.out.println("用户" + user.getName() + "退出房间");
                Map<String, Object> map = new HashMap<>();
                map.put("name", user.getName());
                map.put("status", "退出房间");
                //广播退出信息
                room.get(item).sendMessage(map);
            }
        }
    }

}
