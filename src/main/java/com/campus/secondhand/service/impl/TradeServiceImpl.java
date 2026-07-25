package com.campus.secondhand.service.impl;

import com.campus.secondhand.dto.TradeDTO;
import com.campus.secondhand.exception.BusinessException;
import com.campus.secondhand.mapper.ProductMapper;
import com.campus.secondhand.mapper.TradeMapper;
import com.campus.secondhand.pojo.Product;
import com.campus.secondhand.pojo.Trade;
import com.campus.secondhand.service.TradeService;
import com.campus.secondhand.utils.UserContext;
import com.campus.secondhand.vo.TradeVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TradeServiceImpl implements TradeService {

    @Resource
    private TradeMapper tradeMapper;

    @Resource
    private ProductMapper productMapper;

    @Override
    public Trade create(TradeDTO dto) {
        //1。获取当前登录用户（买家）
        Long buyerId = UserContext.getUserId();

        // 2.查询商品
        Product product = productMapper.findById(dto.getProductId());

        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        // 不能买自己的商品
        if (product.getSellerId().equals(buyerId)) {
            throw new BusinessException("不能购买自己的商品");
        }

        /**
         * 3.判断商品状态
         *
         * 1 在售
         *
         * 其他（不能买）
         */
        if (product.getStatus() !=1){
            throw new BusinessException("商品当前不可购买");
        }

        // 4.创建交易对象
        Trade trade = new Trade();

        // 生成交易编号
        trade.setTradeNo(
                "T"+
                UUID.randomUUID()
                        .toString()
                        .replace("-","")
                        .substring(0,31)
        );

        trade.setProductId(product.getId());

        trade.setBuyerId(buyerId);

        trade.setSellerId(product.getSellerId());

        trade.setPrice(product.getPrice());

        trade.setTradeType(dto.getTradeType());
        trade.setMeetTime(dto.getMeetTime());

        trade.setMeetPlace(dto.getMeetPlace());

        trade.setStatus(0);

        tradeMapper.insert(trade);

        /**
         * 5.修改商品状态
         *
         * 在售 -> 交易中
         */
        productMapper.updateStatus(product.getId(), 2);

        return trade;

    }

    @Override
    public void confirm(Long id) {
        //1.查询订单
        Trade trade = tradeMapper.findById(id);

        if (trade == null) {
            throw new BusinessException("订单不存在");
        }

        // 2.当前用户

        Long userId = UserContext.getUserId();


        // 3.判断是否卖家
        if (!trade.getSellerId().equals(userId)) {
            throw new BusinessException("只有卖家可以确认订单");
        }

        // 4.判断订单状态
        if (trade.getStatus()!= 0){
            throw new BusinessException("订单状态错误");
        }

        tradeMapper.updateStatus(id, 1);

    }

    @Override
    public void finish(Long id) {
        // 1.查看订单是否存在
        Trade trade = tradeMapper.findById(id);
        if (trade == null) {
            throw new BusinessException("订单不存在");
        }

        //2. 判断当前账户是否是买卖的其中一方
        Long userId = UserContext.getUserId();

        // 判断是否是买方
        boolean buyer = trade.getBuyerId().equals(userId);

        // 判断是否是卖方
        Boolean seller = trade.getSellerId().equals(userId);

        // 买卖双方都可以完成
        if (!buyer && !seller) {
            throw new BusinessException("无权操作改订单");

        }

        // 查看当前订单状态，必须得是交易中
        if (trade.getStatus()!= 1){
            throw new BusinessException("订单位处于交易中");
        }
        tradeMapper.updateStatus(id, 2);
    }

    @Override
    public List<TradeVO> buyerList() {
        Long userId = UserContext.getUserId();

        return tradeMapper.selectBuyerTrades(userId);
    }

    @Override
    public List<TradeVO> sellerList() {
        Long userId = UserContext.getUserId();
        return tradeMapper.selectSellerTrades(userId);
    }

    @Override
    public void cancel(Long id) {
        Trade trade = tradeMapper.findById(id);
        if (trade == null) {
            throw new BusinessException("订单不存在");
        }
        Long userId = UserContext.getUserId();

        boolean buyer = trade.getBuyerId().equals(userId);
        boolean seller = trade.getSellerId().equals(userId);


        if (!buyer && !seller) {
            throw new BusinessException("无权该取消订单");
        }

        if(trade.getStatus()==2){
            throw new BusinessException("交易已完成，不能取消");
        }

        if (trade.getStatus()== 3){
            throw new BusinessException("订单已经取消");
        }

        // 订单取消
        tradeMapper.updateStatus(id, 3);

        // 商品重新上架
        productMapper.updateStatus(trade.getProductId(), 1);
    }

    @Override
    public TradeVO detail(Long id) {
        TradeVO tradeVO = tradeMapper.detail(id);

        if (tradeVO == null) {
            throw new RuntimeException("订单不存在");
        }
        return tradeVO;
    }
}
