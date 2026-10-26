package com.apps.miaowu.service.impl;

import com.apps.miaowu.bean.Follow;
import com.apps.miaowu.bean.FollowExample;
import com.apps.miaowu.bean.FollowExample.Criteria;
import com.apps.miaowu.bean.User;
import com.apps.miaowu.bean.result.APIResult;
import com.apps.miaowu.bean.result.ResultCode;
import com.apps.miaowu.bean.result.ResultEnum;
import com.apps.miaowu.dao.FollowMapper;
import com.apps.miaowu.dao.UserMapper;
import com.apps.miaowu.service.FollowService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class FollowServiceImpl implements FollowService {

    @Resource
    UserMapper userMapper;

    @Resource
    FollowMapper followMapper;

    // Follow a user
    @Override
    public APIResult addOrDelFollow(Long userId, Long fansId) {
        Follow follow = new Follow();
        follow.setUserId(userId);
        follow.setFansId(fansId);
        FollowExample followExample = new FollowExample();
        followExample.createCriteria().andFansIdEqualTo(fansId).andUserIdEqualTo(userId);
        // user or article not found
        if(userId == null || fansId == null){
            return APIResult.newResult(ResultCode.BadRequest,"invalid params",null);
        }
        // user does not exist
        else if(userMapper.selectByPrimaryKey(userId) == null || userMapper.selectByPrimaryKey(fansId) == null) {
            return APIResult.newResult(ResultCode.BadRequest,"Parameter Error",null);
        }
        // already following
        else if(!followMapper.selectByExample(followExample).isEmpty()){
            // unfollow, TODO: untested
            try {
                followMapper.deleteByExample(followExample);
                return APIResult.newResult(ResultEnum.SUCCESS, null);
            } catch (Exception e) {
                return APIResult.newResult(ResultCode.ServerInnerError, e.toString(), null);
            }
        } else{
            try{
                followMapper.insert(follow);
                return APIResult.newResult(ResultEnum.SUCCESS, follow);
            } catch (Exception e){
                return  APIResult.newResult(ResultCode.ServerInnerError,e.toString(),null);
            }
        }
    }

    @Override
    public APIResult findAllFollowsByUserId(Long userId) {
        FollowExample example = new FollowExample();
        // Find all users this user follows
        example.createCriteria().andFansIdEqualTo(userId);
        List<Follow> follows = followMapper.selectByExample(example);
        ArrayList<User> users = new ArrayList<>();
        for (Follow follow : follows) {
            users.add(userMapper.selectByPrimaryKey(follow.getUserId()));
        }
        if (users.isEmpty()) {
            return APIResult.newResult(ResultEnum.NO_CONTENT, null);
        }
        return APIResult.newResult(ResultEnum.SUCCESS, users);
    }

    @Override
    public APIResult findAllFansByUserId(Long userId) {
        FollowExample example = new FollowExample();
        // Find all fans of this user
        example.createCriteria().andUserIdEqualTo(userId);
        List<Follow> follows = followMapper.selectByExample(example);
        ArrayList<User> users = new ArrayList<>();
        for (Follow follow : follows) {
            users.add(userMapper.selectByPrimaryKey(follow.getFansId()));
        }
        if (users.isEmpty()) {
            return APIResult.newResult(ResultEnum.NO_CONTENT, null);
        }
        return APIResult.newResult(ResultEnum.SUCCESS, users);
    }
}
