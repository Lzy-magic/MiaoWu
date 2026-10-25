package com.apps.miaowu.service.impl;

import com.apps.miaowu.bean.Comment;
import com.apps.miaowu.bean.User;
import com.apps.miaowu.bean.result.APIResult;
import com.apps.miaowu.bean.result.ResultCode;
import com.apps.miaowu.bean.result.ResultEnum;
import com.apps.miaowu.dao.CommentMapper;
import com.apps.miaowu.service.CommentService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class CommentServiceImpl implements CommentService {

    @Resource
    CommentMapper commentMapper;

    @Override
    public APIResult addComment(Long userId, Long articleId, String content) {
        // A profile without an ID card number counts as incomplete
        //
        // and cannot comment
        if (userId == null) {
            return APIResult.newResult(ResultCode.BadRequest, "Parameters are missing", null);
//            return "Please complete your profile before commenting~";
        } else {
            Comment comment = new Comment();
            comment.setCommentContent(content);
            comment.setArticleId(articleId);
            comment.setUserId(userId);

//            comment.setDate(LocalDateTime.now());
            comment.setDate(new Date());

            commentMapper.insert(comment);
            return APIResult.newResult(ResultEnum.SUCCESS, null);
        }
    }

    @Override
    public APIResult deleteComment(User user, Long commentId) {
        if (commentMapper.selectByPrimaryKey(commentId) != null) {
            commentMapper.deleteByPrimaryKey(commentId);
            return APIResult.newResult(ResultEnum.SUCCESS,  null);
        } else {
            return APIResult.newResult(ResultCode.BadRequest, "can't not find the comment", null);
        }
    }

    @Override
    public APIResult updateComment(User user, Comment comment) {
        if (commentMapper.selectByPrimaryKey(comment.getId()) != null) {
            commentMapper.updateByPrimaryKey(comment);
            return APIResult.newResult(ResultEnum.SUCCESS,  null);
        } else {
            return APIResult.newResult(ResultCode.BadRequest, "can't not find the comment", null);
        }
    }
}
