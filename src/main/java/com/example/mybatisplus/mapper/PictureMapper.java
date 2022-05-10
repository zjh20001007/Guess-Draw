package com.example.mybatisplus.mapper;

import com.example.mybatisplus.model.domain.Picture;
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
public interface PictureMapper extends BaseMapper<Picture> {

    List<Picture> getRank();
}
