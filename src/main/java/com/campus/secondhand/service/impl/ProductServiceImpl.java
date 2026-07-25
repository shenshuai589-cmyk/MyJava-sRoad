package com.campus.secondhand.service.impl;

import com.campus.secondhand.dto.ProductDTO;
import com.campus.secondhand.dto.ProductQueryDTO;
import com.campus.secondhand.exception.BusinessException;
import com.campus.secondhand.mapper.*;
import com.campus.secondhand.pojo.*;
import com.campus.secondhand.service.ProductService;
import com.campus.secondhand.utils.UserContext;
import com.campus.secondhand.vo.ProductDetailVO;
import com.campus.secondhand.vo.UserVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Resource
    private ProductMapper productMapper;


    @Resource
    private ProductImageMapper productImageMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private FavoriteMapper favoriteMapper;

    @Resource
    private CategoryMapper categoryMapper;

    @Override
    public Product add(ProductDTO productDTO) {
        Product product = new Product();
        // 分类
        product.setCategoryId(productDTO.getCategoryId());

        //当前登录用户
        product.setSellerId(UserContext.getUserId());

        // 商品信息
        product.setTitle(productDTO.getTitle());

        product.setPrice(productDTO.getPrice());

        product.setConditionLevel(productDTO.getConditionLevel());

        product.setTradeType(productDTO.getTradeType());

        product.setDescription(productDTO.getDescription());

        // 默认状态
        // 待审核
        product.setAuditStatus(0);

        // 在售
        product.setStatus(1);

        // 未删除
        product.setIsDeleted(0);

        productMapper.insert(product);
        if (productDTO.getImages() != null
        && !productDTO.getImages().isEmpty()) {
            for(String imageUrl:productDTO.getImages()){
                ProductImage image = new ProductImage();

                image.setProductId(product.getId());
                image.setImageUrl(imageUrl);
                image.setSort(0);
                productImageMapper.insert(image);
            }

        }

        return product;

    }

    @Override
    public PageInfo<Product> page(ProductQueryDTO queryDTO) {
        PageHelper.startPage(
                queryDTO.getPage(),
                queryDTO.getSize()
        );

        List<Product> list = productMapper.selectList(queryDTO);

        return new PageInfo<>(list);
    }

    @Override
    public ProductDetailVO detail(Long id) {
        Product product = productMapper.findById(id);

        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        //2.增加浏览量
        productMapper.updateViewCount(id);

        Integer viewCount =  product.getViewCount() == null ? 0: product.getViewCount();

        product.setViewCount(viewCount + 1);

        //3.封装商品基本信息
        ProductDetailVO vo = new ProductDetailVO();

        BeanUtils.copyProperties(product, vo);



        //4.查询卖家信息
        Category category = categoryMapper.findById(product.getCategoryId());

        if (category != null) {
            vo.setCategoryName(category.getName());
        }

        User seller = userMapper.findById(product.getSellerId());

        if (seller != null) {
            UserVO sellerVO = new UserVO();
            BeanUtils.copyProperties(seller, sellerVO);
            vo.setSeller(sellerVO);
        }

        //6.图片

        List<ProductImage> imageList = productImageMapper.findByProductId(id);

        if (imageList != null) {
            List<String> images = imageList.stream()
                    .map(ProductImage::getImageUrl)
                    .toList();

            vo.setImages(images);
        }

        //7.收藏状态

        Long userId = UserContext.getUserId();

        if (userId != null) {
            Favorite favorite = favoriteMapper.findOne(userId, id);

            vo.setFavorite(favorite != null);
        }else{
            vo.setFavorite(false);
        }
        // 8.查询收藏数量
        Integer count = favoriteMapper.countByProductId(id);
        vo.setFavoriteCount(count);
        return vo;
    }

    @Override
    public PageInfo<Product> myProducts(ProductQueryDTO queryDTO) {
        Long userId = UserContext.getUserId();

        PageHelper.startPage(
                queryDTO.getPage(),
                queryDTO.getSize());

        List<Product> list = productMapper.selectMyProducts(userId);

        return new PageInfo<>(list);
    }

    @Override
    public void offline(Long id) {
        checkOwner(id);
        productMapper.updateStatus(id, 0);

    }

    @Override
    public void online(Long id) {
        checkOwner(id);

        productMapper.updateStatus(id, 1);
    }

    @Override
    public void delete(Long id) {
        checkOwner(id);
        productMapper.delete(id);
    }


    private Product checkOwner(Long id){

        Product product =
                productMapper.findById(id);


        if(product == null){

            throw new BusinessException("商品不存在");

        }


        if(!product.getSellerId()
                .equals(UserContext.getUserId())){

            throw new BusinessException("无权操作");

        }


        return product;
    }


    @Override
    public void update(Long id, ProductDTO dto) {
        // 1.检查是否是本人商品
        Product product = checkOwner(id);

        // 2.修改字段
        product.setCategoryId(dto.getCategoryId());

        product.setTitle(dto.getTitle());

        product.setPrice(dto.getPrice());

        product.setConditionLevel(dto.getConditionLevel());

        product.setTradeType(dto.getTradeType());

        product.setDescription(dto.getDescription());

        // 3.更新
        productMapper.update(product);

        // 4.修改商品图片
        productImageMapper.deleteByProductId(id);
        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            for(String url:dto.getImages()){
                ProductImage image = new ProductImage();
                image.setProductId(id);
                image.setImageUrl(url);
                image.setSort(0);
                productImageMapper.insert(image);
            }
        }
    }
}
