package com.example.mybatisplus.mapper;

import com.example.mybatisplus.model.domain.AiDictionary;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 
 * @since 2022-05-10
 */
public interface AiDictionaryMapper extends BaseMapper<AiDictionary> {

    AiDictionary selectWord(String str);
}
