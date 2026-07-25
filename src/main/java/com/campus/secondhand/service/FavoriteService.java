package com.campus.secondhand.service;

import com.campus.secondhand.pojo.Favorite;
import com.campus.secondhand.vo.FavoriteVO;

import java.util.List;

public interface FavoriteService {


    void add(Long productId);

    void delete(Long productId);


    List<FavoriteVO> myFavorites();
}
