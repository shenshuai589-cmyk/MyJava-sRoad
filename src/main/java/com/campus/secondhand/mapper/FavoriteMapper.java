package com.campus.secondhand.mapper;

import com.campus.secondhand.pojo.Favorite;
import com.campus.secondhand.vo.FavoriteVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FavoriteMapper {

    /**
     * 添加收藏
     */
    int insert(Favorite favorite);

    /**
     * 取消收藏
     */
    int delete(Long userId,Long productId);

    /**
     * 查看我的收藏
     */
    List<FavoriteVO> findByUserId(Long userId);

    /**
     * 判断是否收藏
     */
    Favorite findOne(Long userId, Long productId);

    Integer countByProductId(Long productId);

}
