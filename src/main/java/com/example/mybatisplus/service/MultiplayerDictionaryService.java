package com.example.mybatisplus.service;

import com.example.mybatisplus.model.domain.MultiplayerDictionary;
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
public interface MultiplayerDictionaryService extends IService<MultiplayerDictionary> {

    List<MultiplayerDictionary> selectWord(List<String> list);
}
