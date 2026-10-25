package com.apps.miaowu.web.controller;

import com.apps.miaowu.annotation.NoneAuth;
import com.apps.miaowu.bean.ArticleWithBLOBs;
import com.apps.miaowu.bean.result.APIResult;
import com.apps.miaowu.service.ArticleService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/articles")
public class ArticleController {

    @Resource
    ArticleService articleService;

    @Resource
    protected HttpServletRequest request;

    @PutMapping(value = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "", required = false),
            @ApiImplicitParam(name = "title", value = "", required = true),
            @ApiImplicitParam(name = "authorId", value = "", required = true),
            @ApiImplicitParam(name = "content", value = "", required = false),
            @ApiImplicitParam(name = "writeDate", value = "", required = false),
            @ApiImplicitParam(name = "lastUpdate", value = "", required = false),
            @ApiImplicitParam(name = "thumbUp", value = "0", required = false),
            @ApiImplicitParam(name = "animalId", value = "", required = false),
            //1是空间，2是救助文
            @ApiImplicitParam(name = "type", value = "", required = true),
    })
    public APIResult addArticle(ArticleWithBLOBs article) {
        return articleService.addArticle(article);
    }

    @PostMapping(value = "")
    public APIResult updateArticle(ArticleWithBLOBs article) {
        return articleService.updateArticle(request, article);
    }

    @NoneAuth
    @GetMapping(value = "")
    public APIResult findAll() {
        return articleService.findAll();
    }

    @NoneAuth
    @GetMapping(value = "page={page}&count={count}")
    public APIResult findArticleWithLabelByPage(@PathVariable String page, @PathVariable String count){
        return articleService.findArticleWithLabelByPage(Integer.valueOf(page), Integer.valueOf(count));
    }

    @NoneAuth
    @GetMapping(value = "with=clips&userId={userId}")
    public APIResult findAllWithClipByUserIdOrderByUpdateDesc(@PathVariable Long userId) {
        return articleService.findAllWithClipByUserIdOrderByUpdateDesc(userId);
    }

    @DeleteMapping(value = "{id:\\d+}")
    public APIResult deleteById(@PathVariable Long id) {
        return articleService.deleteById(id);
    }

    @NoneAuth
    @GetMapping(value = "key={key}&value={value}")
    public APIResult findArticleWithTitleFuzzily(@PathVariable String key,@PathVariable String value) {
        return articleService.findArticleByKeyAndValueFuzzily(key, value);
    }

    @NoneAuth
    @GetMapping(value = "{id:\\d+}")
    APIResult cascadeFindById(@PathVariable Long id) {
        return articleService.cascadeFindById(id);
    }

    @NoneAuth
    @GetMapping(value = "authorId={authorId}")
    APIResult findByAuthorIdOrderByUpdateDesc(@PathVariable long authorId) {
        return articleService.findArticleByAuthorIdOrderByUpdateDesc(authorId);
    }

    @NoneAuth
    @GetMapping(value = "fansId={fansId}")
    APIResult findFollowsArticleByUserIdOrderByUpdateDesc(@PathVariable Long fansId) {
        return articleService.findFollowsArticleByUserIdOrderByUpdateDesc(fansId);
    }


    /*
     * Query-string variants of the routes above. The legacy path-style URLs
     * (e.g. /articles/page=1&count=10) are kept for backward compatibility;
     * the "params" conditions make /articles?page=1&count=10 etc. reachable
     * instead of falling through to findAll() or /{id}.
     */
    @NoneAuth
    @GetMapping(value = "", params = {"page", "count"})
    public APIResult findArticleWithLabelByPageQuery(@RequestParam Integer page, @RequestParam Integer count) {
        return articleService.findArticleWithLabelByPage(page, count);
    }

    @NoneAuth
    @GetMapping(value = "", params = {"key", "value"})
    public APIResult findArticleWithTitleFuzzilyQuery(@RequestParam String key, @RequestParam String value) {
        return articleService.findArticleByKeyAndValueFuzzily(key, value);
    }

    @NoneAuth
    @GetMapping(value = "", params = {"with=clips", "userId"})
    public APIResult findAllWithClipByUserIdQuery(@RequestParam Long userId) {
        return articleService.findAllWithClipByUserIdOrderByUpdateDesc(userId);
    }

    @NoneAuth
    @GetMapping(value = "", params = "authorId")
    public APIResult findByAuthorIdQuery(@RequestParam Long authorId) {
        return articleService.findArticleByAuthorIdOrderByUpdateDesc(authorId);
    }

    @NoneAuth
    @GetMapping(value = "", params = "fansId")
    public APIResult findFollowsArticleByFansIdQuery(@RequestParam Long fansId) {
        return articleService.findFollowsArticleByUserIdOrderByUpdateDesc(fansId);
    }

}
