package com.example.mybatisplus.service;

import com.example.mybatisplus.model.domain.AiDictionary;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 
 * @since 2022-05-10
 */
public interface AiDictionaryService extends IService<AiDictionary> {

    AiDictionary selectWord(String str);
}
