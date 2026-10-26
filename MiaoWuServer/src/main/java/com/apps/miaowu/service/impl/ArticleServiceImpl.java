package com.apps.miaowu.service.impl;

import com.alibaba.fastjson.JSON;
import com.apps.miaowu.bean.*;
import com.apps.miaowu.bean.extend.ArticleExtend;
import com.apps.miaowu.bean.extend.CommentExtend;
import com.apps.miaowu.bean.result.APIResult;
import com.apps.miaowu.bean.result.ResultCode;
import com.apps.miaowu.bean.result.ResultEnum;
import com.apps.miaowu.common.exception.MiaowuException;
import com.apps.miaowu.constant.NormalConstant;
import com.apps.miaowu.dao.*;
import com.apps.miaowu.dao.extend.ArticleMapperExtend;
import com.apps.miaowu.dao.extend.CommentMapperExtend;
import com.apps.miaowu.dao.extend.LabelMapperExtend;
import com.apps.miaowu.service.ArticleService;
import com.apps.miaowu.common.utils.token.TokenHelper;
import com.apps.miaowu.common.utils.token.TokenModel;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
public class ArticleServiceImpl implements ArticleService {
    @Resource
    ArticleMapper articleMapper;

    @Resource
    ArticleMapperExtend articleMapperExtend;

    @Resource
    ThumbUpMapper thumbUpMapper;

    @Resource
    LabelMapperExtend labelMapperExtend;

    @Resource
    CommentMapperExtend commentMapperExtend;


    @Resource
    UserMapper userMapper;

    @Resource
    private TokenHelper tokenHelper;

    @Resource
    private RestHighLevelClient client;

    @Override
    public APIResult findAll() {
        ArticleExample example = new ArticleExample();
        List<Article> results = articleMapper.selectByExample(example);
        return APIResult.newResult(ResultEnum.SUCCESS, results);
    }

    @Override
    public APIResult findClipArticleWithAuthorNameByUserIdOrderByUpdateDesc(Long userId) {
        List<ArticleExtend> articles = articleMapperExtend
                .selectClipArticleWithAuthorNameByUserIdOrderByUpdateDesc(userId);
        if (articles.size() == 0) {
            return APIResult.newResult(ResultEnum.BAD_REQUEST, null);
        } else {
            return APIResult.newResult(ResultEnum.SUCCESS, articles);
        }
    }

    @Override
    public APIResult deleteById(Long id) {
        // Also delete the related likes
        if ((articleMapper.selectByPrimaryKey(id) != null)) {
            articleMapper.deleteByPrimaryKey(id);
            ThumbUpExample thumbUpExample = new ThumbUpExample();
            thumbUpExample.createCriteria().andArticleIdEqualTo(id);
            thumbUpMapper.deleteByExample(thumbUpExample);
            return APIResult.newResult(ResultEnum.SUCCESS, null);
        } else {
            return APIResult.newResult(ResultCode.BadRequest, "article not exited", null);
        }
    }

    @Override
    public APIResult findAllArticleWithAnimal() {
        List<ArticleExtend> results = articleMapperExtend.selectArticleWithAnimal();
        return APIResult.newResult(ResultEnum.SUCCESS, results);
    }


    @Override
    public APIResult<List<ArticleExtend>> findArticleWithAnimalById(Long id) {
        // Null checks on parameters
        try {
            varifyNullParam(id);
        } catch (MiaowuException miaowuException) {
            return APIResult.newResult(ResultEnum.ILLEGAL_PARAM, null);
        }

        List<ArticleExtend> results = articleMapperExtend.selectArticleWithAnimalById(id);
        return APIResult.newResult(ResultEnum.SUCCESS, results);
    }

    @Override
    public APIResult findAllArticleWithLabel() {
        List<ArticleExtend> results = articleMapperExtend.selectArticleWithLabel();
        return APIResult.newResult(ResultEnum.SUCCESS, results);
    }

    @Override
    public APIResult findArticleWithLabelById(Long id) {
        // Null checks on parameters
        try {
            varifyNullParam(id);
        } catch (MiaowuException miaowuException) {
            return APIResult.newResult(ResultEnum.ILLEGAL_PARAM, null);
        }

        List<ArticleExtend> results = articleMapperExtend.selectArticleWithLabelById(id);
        return APIResult.newResult(ResultEnum.SUCCESS, results);
    }

    @Override
    public APIResult findArticleWithCommentById(Long articleId) {
        // TODO: unfinished
        return APIResult.newResult(ResultEnum.NO_CONTENT, null);
    }

    @Override
    public APIResult cascadeFindAll() {
        // This should be written differently
        // List<ArticleExtend> articleExtends = articleMapperExtend.cascadeFindAll();
        // for(ArticleExtend articleExtend : articleExtends){
        // List<ArticleLabel> articleLabels = articleExtend.getArticleLabels();
        // for(ArticleLabel articleLabel : articleLabels){
        // labelMapper.selectByPrimaryKey(articleLabel.getId());
        // }
        //
        // }
        return APIResult.newResult(ResultEnum.NO_CONTENT, null);

    }

    @Override
    public APIResult cascadeFindById(Long id) {
        // Null checks on parameters
        try {
            varifyNullParam(id);
        } catch (MiaowuException miaowuException) {
            return APIResult.newResult(ResultEnum.ILLEGAL_PARAM, null);
        }

        HashMap res = new HashMap<String, Object>();
        // Article info
        Article article = articleMapper.selectByPrimaryKey(id);
        res.put("article", article);

        // Null check on the article
        try {
            varifyNullParam(article);
        } catch (MiaowuException miaowuException) {
            return APIResult.newResult(ResultEnum.NO_CONTENT, null);
        }

        // Comments
        List<CommentExtend> commentExtends = commentMapperExtend.findByArticleIdWithUser(id);
        res.put("comments", commentExtends);

        // Labels
        List<Label> labels = labelMapperExtend.findLabelByArticleId(id);
        res.put("labels", labels);

        // User info
        User user = userMapper.selectByPrimaryKey(article.getAuthorId());
        user.setPassword(null);
        user.setIdCode(null);
        user.setPhone(null);
        res.put("author", user);

        return APIResult.newResult(ResultEnum.SUCCESS, res);

    }

    @Override
    public APIResult findArticleByKeyAndValueFuzzily(String key, String value) {
        SearchRequest searchRequest = new SearchRequest("article");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery(key, value));
        searchRequest.source(searchSourceBuilder);
        try {
            SearchResponse response = client.search(searchRequest);
            return APIResult.newResult(ResultEnum.SUCCESS, JSON.toJSONString(response.getHits()));
//            String id = JsonUtils.getArrayColumns(response.toString(), "hits.hits._source", "id");
//            System.out.println(id);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    @Override
    public APIResult thumbUpOrDown(Long articleId, Long userId) {
        // Null checks on parameters
        try {
            varifyNullParam(articleId, userId);
        } catch (MiaowuException miaowuException) {
            return APIResult.newResult(ResultEnum.ILLEGAL_PARAM, null);
        }

        // Get all likes of the user
        ThumbUpExample example = new ThumbUpExample();
        example.createCriteria().andUserIdEqualTo(userId);
        List<ThumbUp> thumbUps = thumbUpMapper.selectByExample(example);

        // Get the article
        Article article = articleMapper.selectByPrimaryKey(articleId);
        ThumbUp find = new ThumbUp();

        // Check whether the user already liked the article
        for (ThumbUp thumbUp : thumbUps) {
            if (thumbUp.getArticleId().equals(articleId)) {
                find = thumbUp;
                break;
            }
        }

        // The user has not liked the article
        if (find.getId() == null) {
            // Insert the like
            ThumbUp thumbUp = new ThumbUp();
            thumbUp.setArticleId(articleId);
            thumbUp.setUserId(userId);
            thumbUpMapper.insert(thumbUp);

            // Null checks on parameters
            try {
                varifyNullParam(article);
            } catch (MiaowuException miaowuException) {
                return APIResult.newResult(ResultEnum.NO_CONTENT, null);
            }

            // Initialize the like count
            if (article.getThumbUp() == null) {
                article.setThumbUp(1L);
            } else {
                article.setThumbUp(article.getThumbUp() + 1L);
            }
            // Update the article
            articleMapper.updateByPrimaryKey(article);
            return APIResult.newResult(ResultEnum.SUCCESS, article.getThumbUp() + 1L);
        } else { // already liked: remove the like
            thumbUpMapper.deleteByPrimaryKey(find.getId());

            // The null check guards against tampering; normally the like count is >= 1 here
            if (article.getThumbUp() != null) {
                // Decrement the like count
                article.setThumbUp(article.getThumbUp() - 1L);
                articleMapper.updateByPrimaryKey(article);
                return APIResult.newResult(ResultEnum.SUCCESS,article.getThumbUp() - 1L);
            } else {
                return APIResult.newResult(ResultCode.ServerInnerError, "Server Inner Error, thumb up number is zero",
                        null);
            }
        }
    }

    @Override
    public APIResult addArticle(ArticleWithBLOBs article) {
        if (article == null) {
            return APIResult.newResult(ResultCode.BadRequest, "invalid params", null);
        }
        // ArticleWithBLOBs articleWithBLOBs =
        // articleMapper.selectByPrimaryKey(article.getId());
        // if(articleWithBLOBs != null){
        article.setWriteDate(new Date());
        article.setLastUpdate(new Date());
        article.setThumbUp(0L);
        articleMapper.insert(article);
        return APIResult.newResult(ResultEnum.SUCCESS, article.getId());
        // }
        // else{
        // return APIResult.newResult(ResultCode.BadRequest,"article exists",null);
        // }
    }

    @Override
    public APIResult updateArticle(HttpServletRequest request, ArticleWithBLOBs article) {
        // Token check
        String authStr = request.getHeader(NormalConstant.AUTHORIZATION);
        TokenModel model = tokenHelper.get(authStr);
        String userId = model.getUserId();
        if (!Long.valueOf(userId).equals(article.getAuthorId())) {
            return APIResult.newResult(ResultEnum.UNAUTH, null);
        }

        // Null checks on parameters
        try {
            varifyNullParam(article);
        } catch (MiaowuException miaowuException) {
            return APIResult.newResult(ResultEnum.ILLEGAL_PARAM, null);
        }

        article.setLastUpdate(new Date());
        // article.setLastUpdate(LocalDateTime.now());
        articleMapper.updateByPrimaryKeySelective(article);
        return APIResult.newResult(ResultEnum.SUCCESS, null);
    }

    @Override
    public APIResult findAllWithClipByUserIdOrderByUpdateDesc(Long userId) {
        // Null checks on parameters
        try {
            varifyNullParam(userId);
        } catch (MiaowuException miaowuException) {
            return APIResult.newResult(ResultEnum.ILLEGAL_PARAM, null);
        }

        ArticleExample example = new ArticleExample();
        List<ArticleExtend> results = articleMapperExtend.selectAllByUserIdOrderByUpdateDesc(userId);
        return APIResult.newResult(ResultEnum.SUCCESS, results);
    }

    @Override
    public APIResult findArticleByAuthorIdOrderByUpdateDesc(Long authorId) {
        // Null checks on parameters
        try {
            varifyNullParam(authorId);
        } catch (MiaowuException miaowuException) {
            return APIResult.newResult(ResultEnum.ILLEGAL_PARAM, null);
        }

        ArticleExample example = new ArticleExample();
        example.createCriteria().andAuthorIdEqualTo(authorId);
        List<Article> lists = articleMapper.selectByExample(example);
        if (!lists.isEmpty()) {
            return APIResult.newResult(ResultEnum.SUCCESS, lists);
        } else {
            return APIResult.newResult(ResultEnum.NO_CONTENT, null);
        }
    }

    // TODO: untested
    @Override
    public APIResult findFollowsArticleByUserIdOrderByUpdateDesc(Long userId) {
        // Null checks on parameters
        try {
            varifyNullParam(userId);
        } catch (MiaowuException miaowuException) {
            return APIResult.newResult(ResultEnum.ILLEGAL_PARAM, null);
        }

        User user = userMapper.selectByPrimaryKey(userId);
        List<ArticleExtend> articleExtends = articleMapperExtend
                .selectFollowsArticleWithAuthorNameByUserIdOrderByUpdateDesc(userId);

        // Null checks on parameters
        try {
            varifyNullParam(user, articleExtends);
        } catch (MiaowuException miaowuException) {
            return APIResult.newResult(ResultEnum.NO_CONTENT, null);
        }

        return APIResult.newResult(ResultEnum.SUCCESS, articleExtends);
    }

    @Override
    public APIResult findById(Long articleId) {
        Article article = articleMapper.selectByPrimaryKey(articleId);
        if (article != null) {
            return APIResult.newResult(ResultEnum.SUCCESS, article);
        } else {
            return APIResult.newResult(ResultEnum.NO_CONTENT, null);
        }
    }

    @Override
    public APIResult findArticleWithLabelByPage(Integer count, Integer page) {
        // Null checks on parameters
        try {
            varifyNullParam(count,page);
        } catch (MiaowuException miaowuException) {
            return APIResult.newResult(ResultEnum.ILLEGAL_PARAM, null);
        }
        Integer start = count * (page - 1);
        Integer end = count * (page);
        List<ArticleExtend> articleExtends = articleMapperExtend.selectArticleWithLabelByPage(start, end);
        return APIResult.newResult(ResultEnum.SUCCESS, articleExtends);
    }
}
