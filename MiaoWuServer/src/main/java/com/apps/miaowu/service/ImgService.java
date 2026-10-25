package com.apps.miaowu.service;

import com.apps.miaowu.bean.result.APIResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author lzy
 * @create 2020/7/16 17:05
 */
public interface ImgService {
    /**
     *  add img
     *
     * @param request HTTP request
     * @param file image file
     * @return APIResult
     */
    APIResult addImg(HttpServletRequest request, MultipartFile file);

    /**
     * del img
     *
     * @param url image URL
     * @return APIResult
     */
    APIResult delImg(String url);
}
