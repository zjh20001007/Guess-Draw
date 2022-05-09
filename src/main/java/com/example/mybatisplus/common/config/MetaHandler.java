package com.example.mybatisplus.common.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MetaHandler implements MetaObjectHandler {

    //private static final Logger logger = LoggerFactory.getLogger(MetaHandler.class);

    /**
     * 新增数据执行
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        //Admin userEntity = new Admin();
        this.strictInsertFill(metaObject, "lastLoginTime", LocalDateTime.class, LocalDateTime.now());        //创建人
        this.strictInsertFill(metaObject, "orderTime", LocalDateTime.class, LocalDateTime.now());        //创建人
        this.strictInsertFill(metaObject, "applyadtime", LocalDateTime.class, LocalDateTime.now());        //创建人
        this.strictInsertFill(metaObject, "applyamtime", LocalDateTime.class, LocalDateTime.now());        //创建人
        this.strictInsertFill(metaObject, "time", LocalDateTime.class, LocalDateTime.now());        //创建人

        //this.setFieldValByName("createBy", userEntity.getLoginName(), metaObject);
        //this.setFieldValByName("updateTime", new Date(), metaObject);
        //更新人
        // this.setFieldValByName("updateBy", userEntity.getLoginName(), metaObject);
    }
    /**
     * 更新数据执行
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "payTime", LocalDateTime.class, LocalDateTime.now()); // 起始版本 3.3.0(推荐使用)
        //this.setFieldValByName("updateBy", userEntity.getLoginName(), metaObject);
    }

}