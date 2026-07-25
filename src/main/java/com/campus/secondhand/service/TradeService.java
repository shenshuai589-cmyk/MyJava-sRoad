package com.campus.secondhand.service;

import com.campus.secondhand.dto.TradeDTO;
import com.campus.secondhand.pojo.Trade;
import com.campus.secondhand.vo.TradeVO;

import java.util.List;

public interface TradeService {
    /**
     * 创建交易
     */
    Trade create(TradeDTO dto);

    /**
     * 卖家确认
     */
    void confirm(Long id);

    /**
     *完成交易
     */
    void finish(Long id);

    /**
     * 我买的
     * @return
     */
    List<TradeVO> buyerList();

    /**
     * 我卖的
     */
    List<TradeVO> sellerList();

    /**
     * 取消订单
     */
    void cancel(Long id);

    /**
     * 订单详情
     */
    TradeVO detail(Long id);
}
