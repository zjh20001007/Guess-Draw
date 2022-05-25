package com.example.mybatisplus.service;

import com.example.mybatisplus.model.domain.AiDictionary;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 
 * @since 2022-05-10
 */
public interface AiDictionaryService extends IService<AiDictionary> {

    AiDictionary selectWord(List<String> list);
}
