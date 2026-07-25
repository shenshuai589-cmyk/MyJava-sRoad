package com.campus.secondhand.mapper;

import com.campus.secondhand.dto.ReviewDTO;
import com.campus.secondhand.pojo.Review;
import com.campus.secondhand.vo.ReviewVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ReviewMapper {
    /**
     * 新增评价
     */
    int insert(Review review);

    /**
     * 查看用户评价
     */
    List<ReviewVO> findByUserId(Long userId);

    Review findByTradeAndUser(Long tradeId, Long userId);

}
