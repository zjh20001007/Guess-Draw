package com.example.mybatisplus.service.impl;

import com.example.mybatisplus.model.domain.AiDictionary;
import com.example.mybatisplus.mapper.AiDictionaryMapper;
import com.example.mybatisplus.service.AiDictionaryService;
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
public class AiDictionaryServiceImpl extends ServiceImpl<AiDictionaryMapper, AiDictionary> implements AiDictionaryService {

    @Autowired
    AiDictionaryMapper aiDictionaryMapper;
    @Override
    public AiDictionary selectWord(List<String> list) {
        if(list.size() == 0){
            return aiDictionaryMapper.selectWord(null);
        }
        return aiDictionaryMapper.selectWord(list);
    }
}
