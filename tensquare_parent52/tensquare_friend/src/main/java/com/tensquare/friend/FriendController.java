package com.tensquare.friend;

import com.tensquare.friend.client.UserClient;
import com.tensquare.friend.service.FriendService;
import entity.Result;
import entity.StatusCode;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/friend")
public class FriendController {

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private FriendService friendService;
    @Autowired
    private UserClient userClient;

    /*
     * @Author 雅雅5146
     * @Description  删除好友
     * @Date 2019/4/26 16:17
     * @Param [friendid, type]
     * @return entity.Result
     **/
    @RequestMapping(value = "/{friendid}",method = RequestMethod.DELETE)
    public Result deleteFriend(@PathVariable String friendid){
        //验证是否登录，并拿到当前登录的用户id
        Claims claims = (Claims) request.getAttribute("claim_user");
        if(claims == null){
            //说明当前用户没有用户角色
            return new Result(false, StatusCode.ACCESSERROR,"权限不足");
        }
        //获得当前登录用户的id
        String userid = claims.getId();
        friendService.deleteFriend(userid,friendid);
        userClient.updateFanscountAndFollowcount(userid,friendid,-1);
        return new Result(true,StatusCode.OK,"删除成功");
    }

    //添加好友，或者非好友
    @RequestMapping(value = "/like/{friendid}/{type}",method = RequestMethod.PUT)
    public Result addFriend(@PathVariable String friendid,@PathVariable String type){
        //验证是否登录，并拿到当前登录的用户id
        Claims claims = (Claims) request.getAttribute("claim_user");
        if(claims == null){
            //说明当前用户没有用户角色
            return new Result(false, StatusCode.ACCESSERROR,"权限不足");
        }
        //得到等前登录的用户id
        String userid = claims.getId();
        //判断是添加 好友 还是 非好友
        if(type != null){
            if(type.equals("1")){
                //添加好友
                int flag = friendService.addFriend(userid,friendid);
                if(flag == 0){
                    return new Result(false,StatusCode.ERROR,"不能重复添加好友");
                }
                if(flag == 1){
                    userClient.updateFanscountAndFollowcount(userid,friendid,1);
                    return new Result(true,StatusCode.OK,"添加成功");
                }
            } else if(type.equals("2")){
                //添加非好友
                int flag = friendService.addNoFriend(userid,friendid);
                if(flag == 0){
                    return new Result(false,StatusCode.ERROR,"不能重复添加非好友");
                }
                if(flag == 1){
                    return new Result(true,StatusCode.OK,"添加成功");
                }
            }
            return new Result(false,StatusCode.ACCESSERROR,"参数异常");
        }else {
            return new Result(false,StatusCode.ACCESSERROR,"参数异常");
        }
    }
}
