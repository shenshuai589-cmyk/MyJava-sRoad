package com.campus.secondhand.mapper;

import com.campus.secondhand.dto.ProductQueryDTO;
import com.campus.secondhand.pojo.Product;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProductMapper {
    /**
     * 新增商品
     * @param product
     * @return 记录条数
     */
    int insert(Product product);

    List<Product> selectList(ProductQueryDTO queryDTO);

    Product findById(Long id);

    int updateViewCount(Long id);

    /**
     * 查看我的商品
     */
    List<Product> selectMyProducts(Long userId);

    /**
     * 修改商品状态
     */
    int updateStatus(Long id, Integer status);

    /**
     * 删除
     */
    int delete(Long id);

    /**
     * 修改商品
     */
    int update(Product product);

    List<Product> findAuditList();

    int updateAuditStatus(Long id, Integer auditStatus);

    Integer count();
}
