package com.example.mybatisplus.component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.mybatisplus.model.domain.AiDictionary;
import com.example.mybatisplus.model.domain.Picture;
import com.example.mybatisplus.model.domain.User;
import com.example.mybatisplus.service.AiDictionaryService;
import com.example.mybatisplus.service.PictureService;
import com.example.mybatisplus.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint("/websocket/AI/{userId}")
public class AI {

    private static List<String> wordList = new ArrayList<>();
    // 与某个客户端的连接会话，需要通过他来给客户端发送消息
    private Session session;

    private static AiDictionaryService aiDictionaryService;

    @Autowired
    public void setAiDictionaryService(AiDictionaryService aiDictionaryService) {
        this.aiDictionaryService = aiDictionaryService;
    }

    private static UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    private static PictureService pictureService;

    @Autowired
    public void setPictureService(PictureService pictureService) {
        this.pictureService = pictureService;
    }

    @OnOpen
    public void onOpen(@PathParam("userId") Long userId, Session session) {
        System.out.println("opening...");
        this.session = session;
    }


    //消息类型：0--开始游戏，1--继续游戏，2--结束游戏
    @OnMessage
    public void onMessage(String message, @PathParam("userId") Long userId, Session session) throws IOException {
        String[] msg = message.split(",");
        String str = "";
        if(msg[0].equals("0")){
            AiDictionary aiDictionary = aiDictionaryService.selectWord(str);
            wordList.add(aiDictionary.getName());
            this.sendMessage(aiDictionary.getName());
        }else if(msg[0].equals("1")){

            //先保存上一条作品记录
            Picture picture = new Picture();
            picture.setUserId(userId);
            picture.setUrl(msg[1]);
            picture.setTitle(msg[wordList.size()-1]);
            pictureService.save(picture);

            //出词
            str = "("+wordList.get(0);
            for(int i=1;i<wordList.size();i++){
                str = str + "," + wordList.get(i);
            }
            str += ")";

            AiDictionary aiDictionary = aiDictionaryService.selectWord(str);
            if(aiDictionary == null){
                this.sendMessage("您已通关！");
            }else{
                wordList.add(aiDictionary.getName());
                this.sendMessage(aiDictionary.getName());
            }
        }else {
            User user = userService.getById(userId);

            //刷新记录
            if(wordList.size()>user.getHig()){
                user.setHig(wordList.size());
                userService.saveOrUpdate(user);
            }
            wordList.clear();
        }
    }

    /**
     * 发送消息
     *
     * @param word
     */
    public void sendMessage(String word) {

        try {
            this.session.getBasicRemote().sendText(word);
            System.out.println(word);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @OnClose
    public void onClose(@PathParam("userId") Long userId, Session session) {
        System.out.println("onClose");
    }


}
