package com.apps.miaowu.service.impl;

import com.apps.miaowu.bean.*;
import com.apps.miaowu.bean.extend.UserExtend;
import com.apps.miaowu.bean.result.APIResult;
import com.apps.miaowu.bean.result.ResultCode;
import com.apps.miaowu.bean.result.ResultEnum;
import com.apps.miaowu.common.exception.MiaowuException;
import com.apps.miaowu.dao.FollowMapper;
import com.apps.miaowu.dao.UserMapper;
import com.apps.miaowu.dao.extend.UserMapperExtend;
import com.apps.miaowu.service.UserService;
import com.apps.miaowu.common.utils.token.TokenHelper;
import com.apps.miaowu.common.utils.token.TokenModel;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserMapper userMapper;

    @Resource
    private UserMapperExtend userMapperExtend;

    @Resource
    private FollowMapper followMapper;

    @Resource
    private TokenHelper tokenHelper;

    @Resource
    private FollowServiceImpl followService;

    @Override
    public APIResult findAll() {
        UserExample example = new UserExample();
        List<User> results = userMapper.selectByExample(example);
        return APIResult.newResult(ResultEnum.SUCCESS, results);
    }

    @Override
    public APIResult updateUserInfo(User user) {
        try {
            varifyNullParam(user.getId());
        } catch (MiaowuException miaowuException) {
            return APIResult.newResult(ResultEnum.NO_CONTENT, null);
        }

        userMapper.updateByPrimaryKeySelective(user);
        return APIResult.newResult(ResultEnum.SUCCESS, null);
    }

    @Override
    public APIResult getInfo(String token) {
        // The token exists in Redis
        if (tokenHelper.check(token)) {
            // Get the user id
            TokenModel tokenModel = tokenHelper.get(token);
            Long userId = Long.valueOf(tokenModel.getUserId());

            // Get the user info
            User user = userMapper.selectByPrimaryKey(userId);

            // Get follows and fans
            APIResult follows = followService.findAllFollowsByUserId(userId);
            APIResult fans = followService.findAllFansByUserId(userId);

            Map<String, Object> res = new HashMap<>();
            res.put("user", user);// user info
            res.put("follows", follows.getData());// users this user follows
            res.put("fans", fans.getData());// this user's fans

            return APIResult.newResult(ResultEnum.SUCCESS, res);
        } else { // token not found
            return APIResult.newResult(ResultCode.BadRequest, "token out of date", null);
        }
    }

    @Override
    public APIResult findById(Long id) {
        UserExample example = new UserExample();
        example.createCriteria().andIdEqualTo(id);

        List<User> users = userMapper.selectByExample(example);
        users.forEach((item) -> {
            item.setPhone(null);
            item.setPassword(null);
            item.setIdCode(null);
        });

        return APIResult.newResult(ResultEnum.SUCCESS, users);
    }

    @Override
    public APIResult findAllUserWithFound() {
        List<UserExtend> results = userMapperExtend.selectUserWithFound();
        return APIResult.newResult(ResultEnum.SUCCESS, results);
    }

    @Override
    public APIResult findUserWithFoundById(Long id) {
        List<UserExtend> results = userMapperExtend.selectUserWithFoundById(id);
        return APIResult.newResult(ResultEnum.SUCCESS, results);
    }

    @Override
    public APIResult findAllUserWithSave() {
        List<UserExtend> results = userMapperExtend.selectUserWithSave();
        return APIResult.newResult(ResultEnum.SUCCESS, results);
    }

    @Override
    public APIResult findUserWithSaveById(Long id) {
        List<UserExtend> results = userMapperExtend.selectUserWithSaveById(id);
        return APIResult.newResult(ResultEnum.SUCCESS, results);
    }

    @Override
    public APIResult cascadeFindAllUser() {
        List<UserExtend> results = userMapperExtend.cascadeFindAllUser();
        return APIResult.newResult(ResultEnum.SUCCESS, results);
    }

    @Override
    public APIResult cascadeFindUserById(Long id) {
        List<UserExtend> results = userMapperExtend.cascadeFindUserById(id);
        return APIResult.newResult(ResultEnum.SUCCESS, results);
    }

    @Override
    public APIResult deleteUserById(Long id) {
        User user = userMapper.selectByPrimaryKey(id);
        if (user != null) {
            userMapper.deleteByPrimaryKey(id);
            return APIResult.newResult(ResultEnum.SUCCESS, null);
        } else {
            return APIResult.newResult(ResultEnum.NO_CONTENT, null);
        }
    }

    @Override
    public APIResult addUser(User user) {
        UserExample isUserExist = new UserExample();
        isUserExist.createCriteria().andIdCodeEqualTo(user.getIdCode())
                .andPhoneEqualTo(user.getPhone());
        List<User> users = userMapper.selectByExample(isUserExist);

        if (users.isEmpty()) {
            user.setCreateDate(new Date());
            userMapper.insert(user);
            return APIResult.newResult(ResultEnum.SUCCESS, null);
        } else {
            return APIResult.newResult(ResultCode.DATA_ALREADY_EXISTEDINT, "user exist", null);
        }
    }

    @Override
    public APIResult login(String phone, String password) {
        // Invalid input
        try{
            varifyNullParam(phone, password);
        } catch (MiaowuException miaowuException){
            return APIResult.newResult(ResultEnum.ILLEGAL_PARAM, null);

        }
        // Look up the user
        UserExample example = new UserExample();
        example.createCriteria().andPhoneEqualTo(phone);
        List<User> users = userMapper.selectByExample(example);

        // No account for this phone number
        if (users.size() == 0) {
            return APIResult.newResult(ResultEnum.NO_CONTENT, null);
        } else if (users.size() == 1) {
            User user = users.get(0);
            // Verified
            if (user.getPassword().equals(password)) {
                // Generate a token after the credentials are verified
                TokenModel model = tokenHelper.create(user.getId().toString());
                return APIResult.newResult(ResultEnum.SUCCESS, model);
            } else { // wrong password
                return APIResult.newResult(ResultCode.BadRequest, "Incorrect password", null);
            }
        } else {    // more than one record
            return APIResult.newResult(ResultCode.BadRequest, "too many users", null);
        }
    }


    @Override
    public APIResult logout(String token) {
        if (tokenHelper.delete(token)) {
            return APIResult.newResult(ResultEnum.SUCCESS, null);
        } else {
            return APIResult.newResult(ResultCode.ServerInnerError, "fail", null);
        }
    }

//    TODO: merge info and getById into one endpoint
}
