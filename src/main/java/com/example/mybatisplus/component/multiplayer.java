package com.example.mybatisplus.component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.mybatisplus.model.domain.MultiplayerDictionary;
import com.example.mybatisplus.model.domain.User;
import com.example.mybatisplus.service.MultiplayerDictionaryService;
import com.example.mybatisplus.service.UserService;
import com.example.mybatisplus.web.controller.FileController;
import com.example.mybatisplus.web.controller.UserController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint("/websocket/multiplayer/{roomId}/{openid}")
public class multiplayer {

    private int[] scoreMap = {0,5,3,3,2,2,1};
    private static ConcurrentHashMap<Integer, Integer> room_score = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<Integer, List<String>> wordList = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<Integer, ConcurrentHashMap<User, Integer>> user_score = new ConcurrentHashMap<>();

    private static SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");//创建时间格式对象
    //创建房间的集合，使用ConcurrentHashMap是为了保证线程安全，HashMap在多线程的情况下会出现问题
    private static LinkedHashMap<Integer, LinkedHashMap<User, multiplayer>> roomList = new LinkedHashMap<>();
    // 与某个客户端的连接会话，需要通过他来给客户端发送消息
    private Session session;

    private static UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    private static MultiplayerDictionaryService multiplayerDictionaryService;

    @Autowired
    public void setMultiplayerDictionaryService(MultiplayerDictionaryService multiplayerDictionaryService) {
        this.multiplayerDictionaryService = multiplayerDictionaryService;
    }


    @OnOpen
    public void onOpen(@PathParam("roomId") Integer roomId, @PathParam("openid") String openid, Session session) {
        System.out.println("opening...");
        this.session = session;
        this.joinRoom(roomId, openid);
    }


    @OnMessage
    public void onMessage(String message, @PathParam("roomId") Integer roomId, @PathParam("openid") String openid, Session session) throws IOException {
        System.out.println(message);
        JSONObject jsonObject = JSON.parseObject(message);
        //查找该userId的用户
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("openid", openid);
        User user = userService.getOne(wrapper);


        if ("聊天".equals(jsonObject.getString("state"))) {



            ConcurrentHashMap<User,Integer> userScore = user_score.get(roomId);


            //当前题目
            String title = jsonObject.getString("title");
            //用户输入的消息内容
            String mes = jsonObject.getString("message");
            //服务器返回的加密内容
            String ans;
            String userName;

            //message加密
            char[] mesArray = mes.toCharArray();
            if (mes.equals(title)) {//用户猜图正确
                ans = "答对了";
                userName = user.getName();
                if(room_score.get(roomId) == null){
                    room_score.put(roomId,1);
                }else{
                    int num = room_score.get(roomId);
                    room_score.put(roomId,num+1);
                }
                int before = userScore.get(user);
                userScore.put(user,before+scoreMap[room_score.get(roomId)]);
                user_score.put(roomId,userScore);
                System.out.println("before:"+before+"now:"+user.getName()+":"+userScore.get(user));
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
            LinkedHashMap<User, multiplayer> room = roomList.get(roomId);
            System.out.println(room.size());
            Map<String, Object> map = new HashMap<>();
            map.put("status", "聊天");
            map.put("message", ans);
            map.put("nickName", userName);
            map.put("avatarUrl", user.getPicUrl());
            for (User item : room.keySet()) {
                room.get(item).sendMessage(map);
            }
        }
        else if ("开始游戏".equals(jsonObject.getString("state"))) {//画图信息
            //广播给所有该房间的客户端
            LinkedHashMap<User, multiplayer> room = roomList.get(roomId);
            ConcurrentHashMap<User,Integer> userScore = user_score.get(roomId);

            Map<String, Object> map = new HashMap<>();
            map.put("status", "开始游戏");
            for (User item : room.keySet()) {
                userScore.put(user,0);
                user_score.put(roomId,userScore);
                room.get(item).sendMessage(map);
            }
        } else if ("提供词库".equals(jsonObject.getString("state"))) {
            System.out.println("提供词库");
            List<String> list = null;
            List<String> words;
            if(wordList.get(roomId) == null){
                words = new ArrayList<>();
            }else{
                words = wordList.get(roomId);
            }
            for (User item : roomList.get(roomId).keySet()) {
                if (words.size() > 0) {
                    //出词
                    list = new ArrayList<>();
                    for (int i = 0; i < words.size(); i++) {
                        list.add(words.get(i));
                    }
                }
                List<MultiplayerDictionary> multiplayerDictionaries = multiplayerDictionaryService.selectWord(list);
                Map<String, Object> map = new HashMap<>();
                map.put("status", "词名");
                for (MultiplayerDictionary multiplayerDictionary : multiplayerDictionaries) {
                    words.add(multiplayerDictionary.getName());
                    map.put(multiplayerDictionary.getName(), multiplayerDictionary.getPrompt());
                }
                wordList.put(roomId, words);
                System.out.println(map);
                roomList.get(roomId).get(item).sendMessage(map);
            }
        }
        else if ("猜词".equals(jsonObject.getString("state"))) {
            room_score.remove(roomId);
            Integer count = jsonObject.getInteger("count");
            System.out.println("第"+count+"张");
            ConcurrentHashMap<String, String> room_pic = UserController.getUser_pic().get(roomId);
            User user1 = null;
            String user1_openid = null;
            Integer i = 0;

            for (Map.Entry<String, String> entry : room_pic.entrySet()) {
                i++;
                if (i == count && entry.getKey() != null) {
                    user1_openid = entry.getKey();
                    break;
                }
            }
            if (user1_openid != null) {
                QueryWrapper<User> wrapper1 = new QueryWrapper<>();
                wrapper1.eq("openid",user1_openid);
                user1 = userService.getOne(wrapper1);
                System.out.println("user1:"+user1);
                System.out.println("roompic:"+room_pic.keySet());

                String picName = room_pic.get(user1_openid);
                System.out.println("词："+picName);

                String picUrl = FileController.getPic(user1.getOpenid());
                QueryWrapper<MultiplayerDictionary> wrapper2 = new QueryWrapper<>();
                wrapper2.eq("name", picName);
                String prompt = multiplayerDictionaryService.getOne(wrapper2).getPrompt();

                Map<String, Object> map = new HashMap<>();
                map.put("status", "猜图");
                map.put("name", user1.getName());
                picUrl = picUrl.substring(1);
                map.put("url", picUrl);
                map.put("prompt", prompt);
                map.put("remain", room_pic.size()-count);
                map.put("title", picName);
                for(User item:roomList.get(roomId).keySet()){
                    roomList.get(roomId).get(item).sendMessage(map);
                }


            }else {
                Map<String, Object> map = new HashMap<>();
                map.put("status", "error");
                this.sendMessage(map);
            }
        }
        else if ("结束".equals(jsonObject.getString("state"))){
            ConcurrentHashMap<User,Integer> userScore = user_score.get(roomId);
            Map<String,Object> map = new HashMap<>();

            map.put("status","得分");
            int i = 0;
            for(User item:roomList.get(roomId).keySet()){
                Map<String,Object> map1 = new HashMap<>();
                map1.put("score",userScore.get(item));
                map1.put("nickName", item.getName());
                map1.put("avatarUrl", item.getPicUrl());
                map.put(String.valueOf(i),map1);

                i++;
                userScore.put(item,0);

                deleteFile(FileController.getPic(item.getOpenid()));
                FileController.removePic(item.getOpenid());
            }

            for(User item:roomList.get(roomId).keySet()){
                roomList.get(roomId).get(item).sendMessage(map);
                UserController.removePic(item.getOpenid(),roomId);
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
     * @param openid
     */
    public void joinRoom(Integer roomId, String openid) {

        //查找该userId的用户
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("openid", openid);
        User user = userService.getOne(wrapper);

        if (!roomList.containsKey(roomId)) {
            // 创建房间不存在时，创建房间
            LinkedHashMap<User, multiplayer> room = new LinkedHashMap<>();
            ConcurrentHashMap<User,Integer> userScore = new ConcurrentHashMap<>();
            userScore.put(user,0);
            user_score.put(roomId,userScore);
            // 添加用户
            room.put(user, this);

            System.out.println("房间号:" + roomId);
            roomList.put(roomId, room);
            Map<String, Object> map = new HashMap<>();
            map.put("status", "用户信息");
            map.put("nickName", user.getName());
            map.put("avatarUrl", user.getPicUrl());

            List<String> words = new ArrayList<>();
            System.out.println("词库："+words);
            wordList.put(roomId, words);
            this.sendMessage(map);
        } else {// 房间已存在，直接添加用户到相应的房间

            ConcurrentHashMap<User,Integer> userScore = user_score.get(roomId);
            userScore.put(user,0);
            user_score.put(roomId,userScore);

            Map<String, Object> joinMap = new HashMap<>();
            joinMap.put("status","加入");
            joinMap.put("message","用户" + user.getName() + "进入房间");
            for (User item : roomList.get(roomId).keySet()) {
                roomList.get(roomId).get(item).sendMessage(joinMap);
            }
            LinkedHashMap<User, multiplayer> room = roomList.get(roomId);
            room.put(user, this);

            System.out.println("用户" + user.getName() + "进入房间");
            System.out.println("room:" + room.keySet());
            //发送消息给房间内的其他人加入房间的user的信息
            Map<String, Object> mapToMe = new HashMap<>();
            mapToMe.put("status", "用户信息");
            int i = 0;
            for (User item : room.keySet()) {
                Map<String, Object> subMap = new HashMap<>();
                subMap.put("nickName", item.getName());
                subMap.put("avatarUrl", item.getPicUrl());
                mapToMe.put(String.valueOf(i), subMap);
                i++;
            }
            for (User item : room.keySet()) {
                room.get(item).sendMessage(mapToMe);
            }
        }
    }


    @OnClose
    public void onClose(@PathParam("roomId") Integer roomId, @PathParam("openid") String openid, Session session) {
        this.exitRoom(roomId, openid);
        wordList.clear();
        System.out.println("onClose");
    }

    /**
     * 退出房间
     *
     * @param roomId
     * @param openid
     */
    public void exitRoom(Integer roomId, String openid) {
        ConcurrentHashMap<User,Integer> userScore = user_score.get(roomId);
        //查找该userId的用户
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("openid", openid);
        User user = userService.getOne(wrapper);

        LinkedHashMap<User, multiplayer> room = roomList.get(roomId);
        if (room.keySet().size() == 1) {//只剩余一个人，解除房间
            room.remove(room.get(user));
            roomList.remove(room);
            UserController.exit(roomId);
            wordList.clear();
            user_score.remove(roomId);
        } else {//还剩余多人，删除退出该房间的人
            userScore.remove(user);
            user_score.put(roomId,userScore);
            room.remove(user);

            Map<String, Object> exitMap = new HashMap<>();
            exitMap.put("status","退出");
            exitMap.put("message","用户" + user.getName() + "退出房间");
            for (User item : room.keySet()) {
                room.get(item).sendMessage(exitMap);
            }

            System.out.println("用户" + user.getName() + "退出房间");
            //发送消息给房间内的其他人加入房间的user的信息
            Map<String, Object> mapToMe = new HashMap<>();
            System.out.println(room.keySet());
            mapToMe.put("status", "用户信息");
            if(room.size() == 1){
                Map<String, Object> map = new HashMap<>();
                for (User item : room.keySet()) {
                    map.put("status", "用户信息");
                    map.put("nickName", item.getName());
                    map.put("avatarUrl", item.getPicUrl());
                }
                for (User item : room.keySet()) {
                    room.get(item).sendMessage(map);
                }
            }else{
                int i = 0;
                for (User item : room.keySet()) {
                    Map<String, Object> subMap = new HashMap<>();
                    subMap.put("nickName", item.getName());
                    subMap.put("avatarUrl", item.getPicUrl());
                    mapToMe.put(String.valueOf(i), subMap);
                    i++;
                }
                for (User item : room.keySet()) {
                    room.get(item).sendMessage(mapToMe);
                }
            }

        }
    }
    public void deleteFile(String path) throws IOException {
        path = "./file"+path;
        System.out.println("文件路径："+path);
        File file = new File(path);
        if (file!=null){
            System.out.println(file.delete());
        }else{
            System.out.println("没找到文件");
        }
    }
}
