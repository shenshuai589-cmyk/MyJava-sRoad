package com.campus.secondhand.mapper;

import com.campus.secondhand.pojo.Trade;
import com.campus.secondhand.vo.TradeVO;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface TradeMapper {

    /**
     *创建交易
     */
    int insert(Trade trade);

    /**
     * 查询交易
     */
    Trade findById(Long id);

    /**
     * 修改状态
     * @param
     * @return
     */
    int updateStatus(Long id, Integer status);

    /**
     * 查询我购买的订单
     */
    List<TradeVO> selectBuyerTrades(Long userId);

    /**
     * 查询我卖出的订单
     */
    List<TradeVO> selectSellerTrades(Long userId);

    /**
     * 订单详情
     */
    TradeVO detail(Long id);

    Integer countFinish();

    BigDecimal sumAmount();
}
