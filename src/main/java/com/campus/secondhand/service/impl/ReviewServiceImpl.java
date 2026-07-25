package com.campus.secondhand.service.impl;

import com.campus.secondhand.dto.ReviewDTO;
import com.campus.secondhand.exception.BusinessException;
import com.campus.secondhand.mapper.ReviewMapper;
import com.campus.secondhand.mapper.TradeMapper;
import com.campus.secondhand.pojo.Review;
import com.campus.secondhand.pojo.Trade;
import com.campus.secondhand.service.ReviewService;
import com.campus.secondhand.utils.UserContext;
import com.campus.secondhand.vo.ReviewVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Resource
    private ReviewMapper reviewMapper;

    @Resource
    private TradeMapper tradeMapper;

    @Override
    public void add(ReviewDTO dto) {
        // 当前登录用户
        Long userId = UserContext.getUserId();
        // 1.查看交易
        Trade trade = tradeMapper.findById(dto.getTradeId());
        if (trade == null){
            throw new BusinessException("交易不存在");
        }
        // 2.判断交易是否完成
        if (trade.getStatus() !=2){
            throw new BusinessException("交易未完成，不能评价");
        }

        // 3.判断评价人是否参与交易

        Long toUserId;

        if (trade.getBuyerId().equals(userId)){

            toUserId = trade.getSellerId();

        }else if(trade.getSellerId().equals(userId)){

            toUserId = trade.getBuyerId();

        }else{
            throw new BusinessException("无权评价");
        }

        // 防止重复评价
        Review exist = reviewMapper.findByTradeAndUser(dto.getTradeId(), userId);
        if (exist != null){
            throw new BusinessException("该交易已经评价过");
        }


        //4.组装评价对象
        Review review = new Review();

        review.setTradeId(dto.getTradeId());

        review.setFromUserId(userId);

        review.setToUserId(toUserId);

        review.setScore(dto.getScore());

        review.setContent(dto.getContent());

        review.setTags(dto.getTags());

        reviewMapper.insert(review);

    }

    @Override
    public List<ReviewVO> list(Long userId) {
        return reviewMapper.findByUserId(userId);
    }
}
