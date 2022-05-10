package com.example.mybatisplus.web.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.mybatisplus.common.JsonResponse;
import com.example.mybatisplus.service.MultiplayerDictionaryService;
import com.example.mybatisplus.model.domain.MultiplayerDictionary;


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
@RequestMapping("/api/multiplayerDictionary")
public class MultiplayerDictionaryController {

    private final Logger logger = LoggerFactory.getLogger( MultiplayerDictionaryController.class );

    @Autowired
    private MultiplayerDictionaryService multiplayerDictionaryService;

    /**
    * 描述：根据Id 查询
    *
    */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public JsonResponse getById(@PathVariable("id") Long id)throws Exception {
        MultiplayerDictionary  multiplayerDictionary =  multiplayerDictionaryService.getById(id);
        return JsonResponse.success(multiplayerDictionary);
    }

    /**
    * 描述：根据Id删除
    *
    */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public JsonResponse deleteById(@PathVariable("id") Long id) throws Exception {
        multiplayerDictionaryService.removeById(id);
        return JsonResponse.success(null);
    }


    /**
    * 描述：根据Id 更新
    *
    */
    @RequestMapping(value = "", method = RequestMethod.PUT)
    @ResponseBody
    public JsonResponse updateMultiplayerDictionary(MultiplayerDictionary  multiplayerDictionary) throws Exception {
        multiplayerDictionaryService.updateById(multiplayerDictionary);
        return JsonResponse.success(null);
    }


    /**
    * 描述:创建MultiplayerDictionary
    *
    */
    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    public JsonResponse create(MultiplayerDictionary  multiplayerDictionary) throws Exception {
        multiplayerDictionaryService.save(multiplayerDictionary);
        return JsonResponse.success(null);
    }
}

