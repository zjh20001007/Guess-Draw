package com.example.mybatisplus.web.controller;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.mybatisplus.common.JsonResponse;
import com.example.mybatisplus.model.domain.Picture;
import com.example.mybatisplus.model.domain.User;
import com.example.mybatisplus.service.FileService;
import com.example.mybatisplus.service.PictureService;
import com.example.mybatisplus.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/file")
@Slf4j
public class FileController {
    protected FileService fileService;

    protected ResourceLoader resourceLoader;

    //用户-作品url
    private static ConcurrentHashMap<String,String> user_picUrl = new ConcurrentHashMap<>();

    @Autowired
    private PictureService pictureService;

    @Autowired
    private UserService userService;

    public static String getPic(String openId){
        System.out.println("获取的openid："+openId);
        String ans = user_picUrl.get(openId);
        System.out.println("获取后："+user_picUrl);
        return ans;
    }

    public static void removePic(String openId){
        System.out.println("删除前："+user_picUrl);
        user_picUrl.remove(openId);
        System.out.println("删除后："+user_picUrl);
    }

    public FileController(FileService fileService, ResourceLoader resourceLoader) {
        this.fileService = fileService;
        this.resourceLoader = resourceLoader;
    }



    @ApiOperation(value = "文件上传", notes = "文件上传")
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ResponseEntity<Map<String, String>> upload(@RequestParam("file") MultipartFile file,@RequestParam("openId") String openId, HttpServletRequest request) throws IOException {
        System.out.println("file:"+openId);
        JSONObject jsonObject = JSONObject.parseObject(openId);
        Map<String, String> map = new HashMap();
        map = fileService.upload(file);


        user_picUrl.put(jsonObject.getString("openId"),map.get("url"));
        System.out.println(user_picUrl);
        return ResponseEntity.ok().body(map);
    }

    @ApiOperation(value = "文件上传", notes = "文件上传")
    @RequestMapping(value = "/AIupload", method = RequestMethod.POST)
    public JsonResponse AIupload(@RequestParam("file") MultipartFile file,@RequestParam("openId") String openId, HttpServletRequest request) throws IOException {
        System.out.println("file:"+openId);
        Map<String, String> map = new HashMap();
        map = fileService.upload(file);

//        System.out.println("user_pic:"+AIuser_picUrl.get(openId));




        String ans = "鸡蛋";
        System.out.println("答案是:"+ans);
        return JsonResponse.success(ans);
    }

    @ApiOperation(value = "文件上传", notes = "文件上传")
    @RequestMapping(value = "/AIuploadPic", method = RequestMethod.POST)
    public JsonResponse AIuploadPic(@RequestParam("file") MultipartFile file,@RequestParam("openId") String openId,@RequestParam("title") String title, HttpServletRequest request) throws IOException {
        System.out.println("file:"+openId);
        Map<String, String> map = new HashMap();
        map = fileService.upload(file);

        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("openid",openId);
        Long userId = userService.getOne(wrapper).getId();
        //先保存上一条作品记录
        Picture picture = new Picture();
        picture.setUserId(userId);
        picture.setUrl(map.get("url"));
        picture.setTitle(title);
        pictureService.save(picture);

        return JsonResponse.success(null);
    }


    private static String suffix(String fileName) {
        int i = fileName.lastIndexOf('.');
        return i == -1 ? "" : fileName.substring(i + 1);
    }

}
