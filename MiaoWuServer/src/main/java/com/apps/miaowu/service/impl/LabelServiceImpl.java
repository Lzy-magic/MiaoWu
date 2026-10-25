package com.apps.miaowu.service.impl;

import com.apps.miaowu.bean.ArticleLabel;
import com.apps.miaowu.bean.Label;
import com.apps.miaowu.bean.LabelExample;
import com.apps.miaowu.bean.result.APIResult;
import com.apps.miaowu.bean.result.ResultCode;
import com.apps.miaowu.bean.result.ResultEnum;
import com.apps.miaowu.dao.ArticleLabelMapper;
import com.apps.miaowu.dao.LabelMapper;
import com.apps.miaowu.service.LabelService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class LabelServiceImpl implements LabelService {

    @Resource
    LabelMapper labelMapper;

    @Resource
    ArticleLabelMapper articleLabelMapper;

    @Override
    public APIResult addLabels(Long articleId, String[] labelNames) {
        if(articleId == null || labelNames == null){
            return APIResult.newResult(ResultCode.BadRequest, "invalid params", null);
        }
        for (String labelName:
                labelNames ) {
            LabelExample labelExample =  new LabelExample();
            labelExample.createCriteria().andNameEqualTo(labelName);
            List<Label> labels = labelMapper.selectByExample(labelExample);
            // Label not found, add it
            if(labels.isEmpty()){
                Label label = new Label();
                label.setName(labelName);
                // Create the label
                labelMapper.insert(label);
                // Link it to the article
                ArticleLabel articleLabel = new ArticleLabel();
                articleLabel.setArticleId(articleId);
                articleLabel.setLabelId(label.getId());
                articleLabelMapper.insert(articleLabel);
                return APIResult.newResult(ResultEnum.SUCCESS,null);
            } else {
                ArticleLabel articleLabel = new ArticleLabel();
                articleLabel.setArticleId(articleId);
                articleLabel.setLabelId(labels.get(0).getId());
                articleLabelMapper.insert(articleLabel);
                return APIResult.newResult(ResultEnum.SUCCESS,null);
            }
        }
        return APIResult.newResult(ResultCode.BadRequest,"labelNames is empty",null);

    }

}
