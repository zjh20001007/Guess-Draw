package com.example.mybatisplus.mapper;

import com.example.mybatisplus.model.domain.MultiplayerDictionary;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 
 * @since 2022-05-10
 */
public interface MultiplayerDictionaryMapper extends BaseMapper<MultiplayerDictionary> {

    List<MultiplayerDictionary> selectWord(List<String> list);
}
