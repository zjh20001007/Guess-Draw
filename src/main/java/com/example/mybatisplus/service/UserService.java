package com.example.mybatisplus.service;

import com.example.mybatisplus.model.domain.User;
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
public interface UserService extends IService<User> {

    List<User> getRank();

    Integer getMyRank(String openid);
}
