package com.bmi.mobileart.service.Handler;

import com.bmi.mobileart.service.DAO.CertifyType;
import com.bmi.mobileart.service.DAO.User;
import com.bmi.mobileart.service.Server.*;
import com.bmi.mobileart.service.utils.TextUtils;
import org.nutz.dao.Cnd;
import org.nutz.dao.FieldFilter;
import org.nutz.dao.util.Daos;

import java.util.*;

/**
 * 用户模块
 *
 * @author hoganliu
 */
public class UserHandler extends BaseHandler {

    /**
     * 用户名或手机号是否已存在
     */
    public void api_isUserExist() {
        Map res = new HashMap();

        //name
        String name = (String) this.getArgument("name");
        if (!TextUtils.isEmpty(name)) {
            if(TextUtils.isPhoneNum(name)){   //不允许手动设置手机号格式的用户名
                res.put("name", true);
            }else {
                User u = dao.fetch(User.class, Cnd.where("name", "=", name));
                res.put("name", u != null);
            }
        }

        //phone
        String phone = (String) this.getArgument("phone");
        if (!TextUtils.isEmpty(phone)) {
            User u = dao.fetch(User.class, Cnd.where("phone", "=", phone));
            res.put("phone", u != null);
        }

        if(res.size() <= 0){
            this.writeError("参数不足");
        }else {
            this.writeResult(res);
        }
    }

    /**
     * 注册用户，这个允许用户名是手机号（默认），如果用户名为空则自动生成
     */
    public void api_regUser() {
        String name = (String) this.getArgument("name");
        String pwd = (String) this.getArgument("pwd");
        String phone = (String) this.getArgument("phone");

        if (TextUtils.isEmpty(pwd) || TextUtils.isEmpty(phone)) {
            this.writeError("参数不足");
            return;
        }

        //验证手机号
        if(!TextUtils.isPhoneNum(phone)){
            this.writeError("手机号非法");
            return;
        }

        //验证密码
        if(pwd.length() != 32){
            this.writeError("密码非法");
            return;
        }

        User existUser = null;
        if(TextUtils.isEmpty(name)){
            existUser = dao.fetch(User.class, Cnd.where("phone", "=", phone));
            if (existUser != null) {
                this.writeError("手机号已注册");
                return;
            }

            //生成随机用户名
            name = TextUtils.getRandomStr(4) + TextUtils.getRandomCode(8);
            while (dao.fetch(User.class, Cnd.where("name", "=", name)) != null){
                name = TextUtils.getRandomStr(4) + TextUtils.getRandomCode(8);
            }
        }else{
            //验证用户名
            if(name.length() < 6){
                this.writeError("用户名不足6位");
                return;
            }

            existUser = dao.fetch(User.class, Cnd.where("name", "=", name).or("phone", "=", phone));
            if (existUser != null) {
                this.writeError("用户名已存在或手机号已注册");
                return;
            }
        }

        User u = new User();
        u.setName(name);
        u.setPwd(pwd);
        u.setPhone(phone);
        u.setCreateTime(new Date());

        if (dao.insert(u) == null) {
            this.writeError("数据库错误");
            return;
        }


        Map res = new HashMap();
        res.put("user", u.toKVPair());
        this.writeResult(res);
    }

    /**
     * 登录
     */
    public void api_doLogin() {
        String idStr = (String) this.getArgument("idstr");  //可能是用户名，可能是密码
        String pwd = (String) this.getArgument("pwd");

        if (TextUtils.isEmpty(idStr) || TextUtils.isEmpty(pwd)) {
            this.writeError("参数不足");
            return;
        }

        User u = dao.fetch(User.class,
                Cnd.where(Cnd.exps("name", "=", idStr).or("phone", "=", idStr)).and("pwd", "=", pwd));

        if (u == null) {
            this.writeError("用户名(手机号)或密码错误");
            return;
        }

        //生成authKey
        String seed = u.getName() + u.getPhone() + new Date().toString() + new Random().nextInt(1000);
        String md5 = TextUtils.MD5(seed);
        if (TextUtils.isEmpty(md5)) {
            this.writeError("服务器内部错误");
            return;
        }

        u.setAuthKey(md5);
        if (updateUser(u, "^authKey$") <= 0) {
            this.writeError("数据库错误");
            return;
        }

        Map res = new HashMap();
        res.put("authKey", u.getAuthKey());
        res.put("user", u.toKVPair());
        this.writeResult(res);
    }

    /**
     * 退出登录
     */
    public void api_doLogout() {
        User user = checkUserAuth(this);
        if (user == null) {
            return;
        }

        user.setAuthKey(null);

        if (updateUser(user, "^authKey$") <= 0) {
            this.writeError("数据库错误");
            return;
        }

        this.writeResult(null);
    }

    /**
     * 修改密码(已经登录情况下)，需要新旧密码
     */
    public void api_changePwd() {
        User user = checkUserAuth(this);
        if (user == null) {
            return;
        }

        String oldPwd = (String) this.getArgument("oldpwd");
        String pwd = (String) this.getArgument("pwd");

        if (TextUtils.isEmpty(oldPwd) || TextUtils.isEmpty(pwd)) {
            this.writeError("参数不足");
            return;
        }

        //验证新密码
        if(pwd.length() != 32){
            this.writeError("新密码非法");
            return;
        }

        if (!user.getPwd().equals(oldPwd)) {
            this.writeError("旧密码不正确");
            return;
        }

        user.setPwd(pwd);
        user.setAuthKey(null);  //需要重新登录

        if (updateUser(user, "^pwd|authKey$") <= 0) {
            this.writeError("数据库错误");
            return;
        }

        this.writeResult(null);
    }

    /**
     * 重置密码(未登录情况下，验证手机号之后)
     */
    public void api_resetPwd(){
        String phone = (String) this.getArgument("phone");
        String pwd = (String) this.getArgument("pwd");

        if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(pwd)) {
            this.writeError("参数不足");
            return;
        }

        //验证新密码
        if(pwd.length() != 32){
            this.writeError("密码非法");
            return;
        }

        User user = dao.fetch(User.class, Cnd.where(Cnd.exps("phone", "=", phone)));
        if (user == null) {
            this.writeError("手机号未注册");
            return;
        }

        user.setPwd(pwd);
        user.setAuthKey(null);  //需要重新登录

        if (updateUser(user, "^pwd|authKey$") <= 0) {
            this.writeError("数据库错误");
            return;
        }

        this.writeResult(null);
    }

    /**
     * 修改手机号
     */
    public void api_changePhone(){
        User user = checkUserAuth(this);
        if (user == null) {
            return;
        }

        String phone = (String) this.getArgument("phone");

        if (TextUtils.isEmpty(phone)) {
            this.writeError("参数不足");
            return;
        }

        //验证手机号
        if(!TextUtils.isPhoneNum(phone)){
            this.writeError("手机号非法");
            return;
        }

        User existUser = dao.fetch(User.class, Cnd.where("phone", "=", phone));
        if (existUser != null) {
            this.writeError("手机号已注册");
            return;
        }

        user.setPhone(phone);

        if (updateUser(user, "^phone$") <= 0) {
            this.writeError("数据库错误");
            return;
        }

        Map res = new HashMap();
        res.put("user", user.toKVPair());
        this.writeResult(res);
    }

    /**
     * 修改用户名
     */
    public void api_changeName() {
        User user = checkUserAuth(this);
        if (user == null) {
            return;
        }

        String name = (String) this.getArgument("name");

        if (TextUtils.isEmpty(name)) {
            this.writeError("参数不足");
            return;
        }

        //验证用户名
        if(name.length() < 6){
            this.writeError("用户名不足6位");
            return;
        }

        User existUser = dao.fetch(User.class, Cnd.where("name", "=", name));
        if (existUser != null) {
            this.writeError("用户名已存在");
            return;
        }

        user.setName(name);

        if (updateUser(user, "^name$") <= 0) {
            this.writeError("数据库错误");
            return;
        }

        Map res = new HashMap();
        res.put("user", user.toKVPair());
        this.writeResult(res);
    }

    /**
     * 更新用户信息,不指定id则需要authKey(修改自己的信息)
     */
    public void api_updateInfo() {
        User user = null;

        String id = (String) this.getArgument("id");
        if(!TextUtils.isEmpty(id)){
            user = dao.fetch(User.class, Cnd.where("id", "=", id));
            if (user == null) {
                this.writeError("用户不存在");
                return;
            }
        }else {
            user = checkUserAuth(this);
            if (user == null) {
                return;
            }
        }

        boolean needUpdate = false;

        //nickname
        String nickname = (String) this.getArgument("nickname");
        if (!TextUtils.isEmpty(nickname)) {
            user.setNickName(nickname);
            needUpdate = true;
        }

        //avatarurl
        String avatarurl = (String) this.getArgument("avatarurl");
        if (!TextUtils.isEmpty(avatarurl)) {
            user.setAvatarUrl(avatarurl);
            needUpdate = true;
        }

        //profile
        String profile = (String) this.getArgument("profile");
        if (!TextUtils.isEmpty(profile)) {
            user.setProfile(profile);
            needUpdate = true;
        }

        if(needUpdate) {
            if (updateUser(user, "^nickName|avatarUrl|profile$") <= 0) {
                this.writeError("数据库错误");
                return;
            }
        }

        Map res = new HashMap();
        res.put("user", user.toKVPair());
        this.writeResult(res);
    }

    /**
     * 获取用户信息(不指定用户名/id则为获取自身信息),优先级：id > name > authKey
     */
    public void api_getInfo(){
        User user = null;

        String id = (String) this.getArgument("id");
        if(!TextUtils.isEmpty(id)){
            user = dao.fetch(User.class, Cnd.where("id", "=", id));
            if (user == null) {
                this.writeError("用户不存在");
                return;
            }
        }else {
            String name = (String) this.getArgument("name");
            if (!TextUtils.isEmpty(name)) {
                user = dao.fetch(User.class, Cnd.where("name", "=", name));
                if (user == null) {
                    this.writeError("用户不存在");
                    return;
                }
            }else{
                user = checkUserAuth(this);
                if (user == null) {
                    return;
                }
            }
        }

        Map res = new HashMap();
        res.put("user", user.toKVPair());
        this.writeResult(res);
    }

    /**
     * 获取认证类型列表
     */
    public void api_getCertifyTypes(){
        List<CertifyType> certifyTypes = dao.query(CertifyType.class, null);
        Map res = new HashMap();
        res.put("certifyTypes", certifyTypes);
        this.writeResult(res);
    }

    /**
     * 认证
     */
    public void api_certify(){
        String id = (String) this.getArgument("id");
        String certifyTypes = (String) this.getArgument("certifytypes");
        if (TextUtils.isEmpty(id) || TextUtils.isEmpty(certifyTypes)) {
            this.writeError("参数不足");
            return;
        }

        User user = dao.fetch(User.class, Cnd.where("id", "=", id));
        if (user == null) {
            this.writeError("用户不存在");
            return;
        }

        user.setCertifyTypes(certifyTypes);
        user.setCertifyTime(new Date());

        if (updateUser(user, "^certifyTypes|certifyTime$") <= 0) {
            this.writeError("数据库错误");
            return;
        }

        Map res = new HashMap();
        res.put("user", user.toKVPair());
        this.writeResult(res);
    }

    public void api_getAllUsers(){
        String page = (String) this.getArgument("page");
        if (TextUtils.isEmpty(page)) {
            page = "1";
        }

        String pageSize = (String) this.getArgument("pagesize");
        if (TextUtils.isEmpty(pageSize)) {
            pageSize = "10";
        }

        List<User> users = dao.query(User.class, Cnd.NEW().desc("createtime"),
                dao.createPager(Integer.parseInt(page), Integer.parseInt(pageSize)));

        //总用户数
        int allCnt = dao.count(User.class, null);

        Map res = new HashMap();
        res.put("allCnt", allCnt);
        res.put("cnt", users.size());
        res.put("users", users);
        res.put("page", page);

        this.writeResult(res);
    }

    /**
     * 通用方法，检查授权情况
     */
    public static User checkUserAuth(BaseHandler h) {
        String authKey = (String) h.getArgument("authkey");
        if (TextUtils.isEmpty(authKey)) {
            h.writeError("参数不足");
            return null;
        }

        User u = h.dao.fetch(User.class, Cnd.where("authkey", "=", authKey));
        if (u == null) {
            h.writeError("用户验证失败");
            return null;
        }

        return u;
    }

    /**
     * 只更新需要的字段
     * @param user
     * @param actived
     * @return
     */
    private int updateUser(User user, String actived){
        return Daos.ext(dao, FieldFilter.create(User.class, actived)).update(user);
    }
}
