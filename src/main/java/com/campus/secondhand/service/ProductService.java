package com.campus.secondhand.service;

import com.campus.secondhand.dto.ProductDTO;
import com.campus.secondhand.dto.ProductQueryDTO;
import com.campus.secondhand.pojo.Product;
import com.campus.secondhand.vo.ProductDetailVO;
import com.campus.secondhand.vo.ProductVO;
import com.github.pagehelper.PageInfo;

public interface ProductService {

    Product add(ProductDTO productDTO);

    PageInfo<Product> page(ProductQueryDTO queryDTO);

    ProductDetailVO detail(Long id);

    /**
     * 我的商品
     */
    PageInfo<Product> myProducts(ProductQueryDTO queryDTO);

    /**
     * 下架商品
     */
    void offline(Long id);

    /**
     * 上架商品
     */
    void online(Long id);

    /**
     * 删除商品
     */
    void delete(Long id);

    /**
     * 修改商品
     */
    void update(Long id, ProductDTO dto);

}
