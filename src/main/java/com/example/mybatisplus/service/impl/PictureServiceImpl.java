package com.example.mybatisplus.service.impl;

import com.example.mybatisplus.model.domain.Picture;
import com.example.mybatisplus.mapper.PictureMapper;
import com.example.mybatisplus.service.PictureService;
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
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture> implements PictureService {

    @Autowired
    PictureMapper pictureMapper;


    @Override
    public List<Picture> getMyPic(Long userId) {
        List<Picture> pictures = pictureMapper.getMyPic(userId);
        return pictures;
    }
}
