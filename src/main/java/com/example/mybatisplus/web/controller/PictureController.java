package com.example.mybatisplus.web.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.mybatisplus.common.JsonResponse;
import com.example.mybatisplus.service.PictureService;
import com.example.mybatisplus.model.domain.Picture;


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
@RequestMapping("/api/picture")
public class PictureController {

    private final Logger logger = LoggerFactory.getLogger( PictureController.class );

    @Autowired
    private PictureService pictureService;

    /**
    * 描述：根据Id 查询
    *
    */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public JsonResponse getById(@PathVariable("id") Long id)throws Exception {
        Picture  picture =  pictureService.getById(id);
        return JsonResponse.success(picture);
    }

    /**
    * 描述：根据Id删除
    *
    */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public JsonResponse deleteById(@PathVariable("id") Long id) throws Exception {
        pictureService.removeById(id);
        return JsonResponse.success(null);
    }


    /**
    * 描述：根据Id 更新
    *
    */
    @RequestMapping(value = "", method = RequestMethod.PUT)
    @ResponseBody
    public JsonResponse updatePicture(Picture  picture) throws Exception {
        pictureService.updateById(picture);
        return JsonResponse.success(null);
    }


    /**
    * 描述:创建Picture
    *
    */
    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    public JsonResponse create(Picture  picture) throws Exception {
        pictureService.save(picture);
        return JsonResponse.success(null);
    }
}

