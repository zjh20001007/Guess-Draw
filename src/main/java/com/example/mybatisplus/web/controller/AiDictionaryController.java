package com.example.mybatisplus.web.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.mybatisplus.common.JsonResponse;
import com.example.mybatisplus.service.AiDictionaryService;
import com.example.mybatisplus.model.domain.AiDictionary;


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
@RequestMapping("/api/aiDictionary")
public class AiDictionaryController {

    private final Logger logger = LoggerFactory.getLogger( AiDictionaryController.class );

    @Autowired
    private AiDictionaryService aiDictionaryService;

    /**
    * 描述：根据Id 查询
    *
    */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public JsonResponse getById(@PathVariable("id") Long id)throws Exception {
        AiDictionary  aiDictionary =  aiDictionaryService.getById(id);
        return JsonResponse.success(aiDictionary);
    }

    /**
    * 描述：根据Id删除
    *
    */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public JsonResponse deleteById(@PathVariable("id") Long id) throws Exception {
        aiDictionaryService.removeById(id);
        return JsonResponse.success(null);
    }


    /**
    * 描述：根据Id 更新
    *
    */
    @RequestMapping(value = "", method = RequestMethod.PUT)
    @ResponseBody
    public JsonResponse updateAiDictionary(AiDictionary  aiDictionary) throws Exception {
        aiDictionaryService.updateById(aiDictionary);
        return JsonResponse.success(null);
    }


    /**
    * 描述:创建AiDictionary
    *
    */
    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    public JsonResponse create(AiDictionary  aiDictionary) throws Exception {
        aiDictionaryService.save(aiDictionary);
        return JsonResponse.success(null);
    }
}

