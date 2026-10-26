package com.apps.miaowu.web.controller;


import com.apps.miaowu.annotation.NoneAuth;
import com.apps.miaowu.bean.User;
import com.apps.miaowu.bean.result.APIResult;
import com.apps.miaowu.service.UserService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("/users")
public class UserController {

    @Resource
    private UserService userService;

    @GetMapping(value = "")
    public APIResult findAll() {
        return userService.findAll();
    }

    @NoneAuth
    @PostMapping(value = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "", required = false),
            @ApiImplicitParam(name = "name", value = "", required = false),
            // The ID card number is optional for now, to keep registration easy
            @ApiImplicitParam(name = "idCode", value = "", required = true),
//          TODO: could the region be derived from the phone number?
            // The frontend should send the number with its international dialing code.
//            @ApiImplicitParam(name = "contry_id", value = "", required = true),
            @ApiImplicitParam(name = "phone", value = "", required = true),
            @ApiImplicitParam(name = "create_date", value = "", required = false),
            @ApiImplicitParam(name = "birthday", value = "", required = true),
            @ApiImplicitParam(name = "password", value = "", required = true)
    })
    public APIResult addUser(User user) {
        if (user.getIdCode() == null || user.getIdCode().trim().isEmpty()) {
            throw new IllegalArgumentException("missing required parameter: idCode");
        }
        if (user.getPhone() == null || user.getPhone().trim().isEmpty()) {
            throw new IllegalArgumentException("missing required parameter: phone");
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new IllegalArgumentException("missing required parameter: password");
        }
        return userService.addUser(user);
    }

    @PutMapping(value = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "", required = false),
            @ApiImplicitParam(name = "name", value = "", required = false),
            // The ID card number is optional for now, to keep registration easy
            @ApiImplicitParam(name = "idCode", value = "", required = false),
//          TODO: could the region be derived from the phone number?
            // The frontend should send the number with its international dialing code.
            @ApiImplicitParam(name = "country_id", value = "", required = true),
            @ApiImplicitParam(name = "phone", value = "", required = true),
            @ApiImplicitParam(name = "create_date", value = "", required = false),
            @ApiImplicitParam(name = "birthday", value = "", required = false),
            @ApiImplicitParam(name = "password", value = "", required = false)
    })
    public APIResult updateUserInfo(User user) {
        return userService.updateUserInfo(user);
    }

    @NoneAuth
    @ApiOperation(value = "Get all user info",notes = "The token must belong to the given user id")
    @GetMapping(value = "info")
    public APIResult info(String token) {
        return userService.getInfo(token);
    }

    @NoneAuth
    @PostMapping(value = "login")
    public APIResult login(String phone, String password) {
        return userService.login(phone, password);
    }

    @NoneAuth
    @ApiOperation(value = "Get public user info")
    @GetMapping(value = "{id}")
    public APIResult findById(@PathVariable Long id){
        return userService.findById(id);
    }

    @DeleteMapping(value = "{id}")
    public APIResult deleteById(@PathVariable long id){return userService.deleteUserById(id);}

    @NoneAuth
    @PostMapping(value = "logout")
    public APIResult logout(String token){ return userService.logout(token);}

    @InitBinder
    protected void init(HttpServletRequest request, ServletRequestDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }
//
}
