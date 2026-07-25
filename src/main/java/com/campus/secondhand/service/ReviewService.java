package com.campus.secondhand.service;

import com.campus.secondhand.dto.ReviewDTO;
import com.campus.secondhand.vo.ReviewVO;

import java.util.List;

public interface ReviewService {

    void add(ReviewDTO dto);

    List<ReviewVO> list(Long userId);
}
