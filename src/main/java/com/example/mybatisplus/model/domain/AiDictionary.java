package com.example.mybatisplus.model.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author 
 * @since 2022-05-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="AiDictionary对象", description="")
public class AiDictionary extends Model<AiDictionary> {

    private static final long serialVersionUID = 1L;

    @TableId("id")
    private Long id;

    private String name;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
