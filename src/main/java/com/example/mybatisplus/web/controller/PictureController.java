package com.example.mybatisplus.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.mybatisplus.model.domain.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.mybatisplus.common.JsonResponse;
import com.example.mybatisplus.service.PictureService;
import com.example.mybatisplus.model.domain.Picture;

import java.util.ArrayList;
import java.util.List;


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
@RequestMapping("/pic")
public class PictureController {

    private final Logger logger = LoggerFactory.getLogger( PictureController.class );

    @Autowired
    private PictureService pictureService;

    /**
     * 描述:查询作品
     */
    @RequestMapping("/{userId}")
    @ResponseBody
    public JsonResponse selectPic(@PathVariable Long userId) throws Exception {
        QueryWrapper<Picture> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id",userId);
        List<Picture> pictures = pictureService.list(wrapper);
        return JsonResponse.success(pictures);
    }


}

