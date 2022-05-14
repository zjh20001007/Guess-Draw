package com.example.mybatisplus.service.impl;

import com.example.mybatisplus.model.domain.MultiplayerDictionary;
import com.example.mybatisplus.mapper.MultiplayerDictionaryMapper;
import com.example.mybatisplus.service.MultiplayerDictionaryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 
 * @since 2022-05-10
 */
@Service
public class MultiplayerDictionaryServiceImpl extends ServiceImpl<MultiplayerDictionaryMapper, MultiplayerDictionary> implements MultiplayerDictionaryService {

    @Autowired
    MultiplayerDictionaryMapper multiplayerDictionaryMapper;
    @Override
    public List<MultiplayerDictionary> selectWord(String str) {

        return multiplayerDictionaryMapper.selectWord(str);
    }
}
