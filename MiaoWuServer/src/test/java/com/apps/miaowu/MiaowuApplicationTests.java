package com.apps.miaowu;

import com.alibaba.fastjson.JSON;
import com.apps.miaowu.bean.Animal;
import com.apps.miaowu.bean.Article;
import com.apps.miaowu.bean.User;
import com.apps.miaowu.bean.result.APIResult;
import com.apps.miaowu.bean.result.ResultCode;
import com.apps.miaowu.bean.result.ResultEnum;
import com.apps.miaowu.common.exception.MiaowuException;
import com.apps.miaowu.common.utils.LogUtils;
import com.apps.miaowu.common.utils.rabbitMQ.Producer;
import com.apps.miaowu.common.utils.token.TokenModel;
import com.apps.miaowu.service.AnimalService;
import com.apps.miaowu.service.ArticleService;
import com.apps.miaowu.service.UserService;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.util.HtmlUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Self-contained tests: fixtures are inserted by sql/test-seed.sql before each test
 * and removed by sql/test-cleanup.sql afterwards. The Elasticsearch test is skipped
 * when no Elasticsearch node is reachable.
 */
@SpringBootTest(classes = {MiaowuApplication.class})
@Sql(scripts = "classpath:sql/test-seed.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:sql/test-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class MiaowuApplicationTests {

    static final long TEST_ID = 900001L;
    static final String TEST_PHONE = "19900000000";
    static final String TEST_PASSWORD = "test-pass-900001";

    @Autowired
    ArticleService articleService;

    @Autowired
    UserService userService;

    @Autowired
    Producer producer;

    @Autowired
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient client;

    @Autowired
    AnimalService animalService;

    @Test
    void contextLoads() {
        assertNotNull(articleService);
        assertNotNull(userService);
        assertDoesNotThrow(() -> producer.produce());
    }

    @Test
    void testHtmlEscapeRoundTrip() {
        String html = "<p><b>1. What is Vue?</b></p><p>Rich text & more</p>";
        String escaped = HtmlUtils.htmlEscapeHex(html);
        assertFalse(escaped.contains("<"));
        assertEquals(html, HtmlUtils.htmlUnescape(escaped));
    }

    @Test
    void testFindArticleById() {
        APIResult apiResult = articleService.cascadeFindById(TEST_ID);
        assertEquals(ResultCode.SuccessCode, apiResult.getCode());
        Map<?, ?> data = (Map<?, ?>) apiResult.getData();
        Article article = (Article) data.get("article");
        assertEquals("test-article-900001", article.getTitle());
    }

    @Test
    void testLogin() {
        APIResult apiResult = userService.login(TEST_PHONE, TEST_PASSWORD);
        assertEquals(ResultCode.SuccessCode, apiResult.getCode());
        TokenModel tokenModel = (TokenModel) apiResult.getData();
        assertNotNull(tokenModel.getToken());

        APIResult info = userService.getInfo(tokenModel.getToken());
        assertEquals(ResultCode.SuccessCode, info.getCode());
        User user = (User) ((Map<?, ?>) info.getData()).get("user");
        assertEquals(TEST_ID, user.getId().longValue());
        userService.logout(tokenModel.getToken());
    }

    @Test
    void testLoginWrongPassword() {
        APIResult apiResult = userService.login(TEST_PHONE, "wrong-password");
        assertEquals(ResultCode.BadRequest, apiResult.getCode());
        assertNull(apiResult.getData());
    }

    @Test
    void testLoginUnknownPhone() {
        APIResult apiResult = userService.login("19900000099", TEST_PASSWORD);
        assertEquals(ResultCode.NoSuchContent, apiResult.getCode());
    }

    @Test
    void testJson() {
        User user = new User();
        user.setName("test");
        User user2 = new User();
        user2.setName("test2");
        Map<String, Object> map = new HashMap<>();
        map.put("user", user);
        map.put("follow", user2);
        String json = JSON.toJSONString(map);
        assertTrue(json.contains("\"test\""));
        assertTrue(json.contains("\"test2\""));
    }

    @Test
    void testLogOut() {
        APIResult login = userService.login(TEST_PHONE, TEST_PASSWORD);
        TokenModel tokenModel = (TokenModel) login.getData();
        APIResult logout = userService.logout(tokenModel.getToken());
        assertEquals(ResultCode.SuccessCode, logout.getCode());
        // token is no longer valid after logout
        assertEquals(ResultCode.BadRequest, userService.getInfo(tokenModel.getToken()).getCode());
    }

    @Test
    void testPage() {
        APIResult result = articleService.findArticleWithLabelByPage(5, 1);
        assertEquals(ResultCode.SuccessCode, result.getCode());
        List<?> list = (List<?>) result.getData();
        assertNotNull(list);
        assertTrue(list.size() <= 5);
    }

    @Test
    void testLog() {
        assertNotNull(LogUtils.getBusinessLogger());
        LogUtils.getBusinessLogger().info("test log, for business");
    }

    @Test
    void testES() {
        boolean esAvailable;
        try {
            esAvailable = client.ping(RequestOptions.DEFAULT);
        } catch (Exception e) {
            esAvailable = false;
        }
        Assumptions.assumeTrue(esAvailable, "Elasticsearch is not reachable, skipping");
        APIResult result = articleService.findArticleByKeyAndValueFuzzily("title", "test");
        assertNotNull(result);
        assertEquals(ResultCode.SuccessCode, result.getCode());
    }

    @Test
    void testGetAnimalById() {
        APIResult result = animalService.findById(TEST_ID);
        assertEquals(ResultCode.SuccessCode, result.getCode());
        Object data = result.getData();
        Animal animal = data instanceof List ? (Animal) ((List<?>) data).get(0) : (Animal) data;
        assertEquals("test-animal-900001", animal.getName());
    }

    @Test
    void testException() {
        assertThrows(MiaowuException.class, () -> animalService.varifyNullParam((Object) null));
    }

    @Test
    void testResultEnum() {
        assertEquals("success", ResultEnum.SUCCESS.getMsg());
        assertEquals(ResultCode.SuccessCode, ResultEnum.SUCCESS.getCode());
    }
}
