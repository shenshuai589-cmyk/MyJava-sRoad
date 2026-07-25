package com.campus.secondhand.mapper;

import com.campus.secondhand.pojo.ProductImage;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProductImageMapper {

    int insert(ProductImage productImage);

    List<ProductImage> findByProductId(Long productId);

    // 删除旧的照片
    int deleteByProductId(Long productId);
}
