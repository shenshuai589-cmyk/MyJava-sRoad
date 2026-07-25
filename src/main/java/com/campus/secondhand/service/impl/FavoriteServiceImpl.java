package com.campus.secondhand.service.impl;

import com.campus.secondhand.exception.BusinessException;
import com.campus.secondhand.mapper.FavoriteMapper;
import com.campus.secondhand.pojo.Favorite;
import com.campus.secondhand.service.FavoriteService;
import com.campus.secondhand.utils.UserContext;
import com.campus.secondhand.vo.FavoriteVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    @Resource
    private FavoriteMapper favoriteMapper;


    @Override
    public void add(Long productId) {
        Long userId = UserContext.getUserId();

        Favorite exist = favoriteMapper.findOne(userId, productId);

        if (exist != null) {
            throw new BusinessException("已经收藏过了");
        }

        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setProductId(productId);
        favoriteMapper.insert(favorite);

    }

    @Override
    public void delete(Long productId) {
        Long userId = UserContext.getUserId();

        favoriteMapper.delete(userId,productId);
    }

    @Override
    public List<FavoriteVO> myFavorites() {
        Long userId = UserContext.getUserId();

        return favoriteMapper.findByUserId(userId);
    }
}
