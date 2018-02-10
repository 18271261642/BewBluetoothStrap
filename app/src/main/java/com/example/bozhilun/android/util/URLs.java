package com.example.bozhilun.android.util;

/**
 * 接口URL实体类
 */
public class URLs {
    // public final static String HTTPs = "http://apis.berace.com.cn/watch";
    public final static String HTTPs = "http://47.90.83.197:8080/watch";
    // public final static String  HTTPs       = "http://192.168.0.126:8080/watch";
//    public final static String HTTPs = "http://192.168.3.101:8080/smart-app";

    public final static String getvision = "/user/updateVersion";//版本号

    public final static String myHTTPs = "/user/register";//注册
    public final static String logon = "/user/login";//登录
    public final static String yonghuziliao = "/user/modify"; //完善用户资料
    public final static String xiugaimima = "/user/restPwd";//修改密码
    public final static String getUserInfo = "/user/getUserInfo";//查询用户信息
    public final static String ziliaotouxiang = "/user/updateImage";//头像
    public final static String disanfang = "/user/third";//第三方登录
    public final static String xiugaipassword = "/user/updatePwd";//修改密码
    public final static String yijian = "/user/feedback";// 意见反馈
    public final static String chaxunshuimianshuju = "/sleep/getSleepD";//查询睡眠数据
    public final static String getWeathers = "/user/getWeathers";//查询getWeathers
    public final static String getSportH = "/sport/getSportH";  // 获取运动日数据
    public final static String getSportW = "/sport/getSportW";    //获取运动周统计
    public final static String getSportM = "/sport/getSportM";//获取运动月数据

    public final static String getBloodPressureD = "/data/getBloodPressureD";//获取血压日统计
    public final static String getBloodPressureM = "/data/getBloodPressureM";// 获取血压月统计
    public final static String getBloodPressureW = "/data/getBloodPressureW"; //获取血压周统计

    public final static String getHeartD ="/data/getHeartD";//获取心率日统计
    public final static String getHeartRateW = "/data/getHeartRateW";//获取心率周统计
    public final static String getHeartRateM = "/data/getHeartRateM"; //获取心率月统计


    public final static String getBloodOxygenD = "/data/getBloodOxygenD";//获取血氧日统计
    public final static String getBloodOxygenM = "/data/getBloodOxygenM";//获取血氧月统计
    public final static String getBloodOxygenW = "/data/getBloodOxygenW";//获取血氧周统计

    public final static String getDataDay = "/data/getHeartRateDay";//获取心率日统计
    public final static String getDataUpData = "/data/upData";//上传血氧 血压 心率 数据 (选填)
    public final static String getData = "/data/getData"; // 数据统计四项主页面
    public final static String upHeart = "/data/upHeart";//上传全天心率血压步数活动量
    public final static String upSportData = "/sport/upSportData";//上传步数数据
    public final static String upSleep = "/sleep/upSleep";//上传睡眠2
    public final static String upSleepData = "/sleep/upSleepData";//上传睡眠
    public final static String getCountry = "/user/getCountry";
    public final static String sendEmail = "/user/sendEmail";
    public final static String MACBANGDING = "http://wx.berace.com.cn/wx/web/bind?mac=";//绑定微信运动


    public final static String getSportData = "/sport/getSportData";//步数日统计
    public final static String getSportWeek = "/sport/getSportW";//步数周统计
    public final static String getSportMonth = "/sport/getSportM";//步数yue统计

    public final static String getSleepD = "/sleep/getSleepD";//睡眠日统计
    public final static String getSleepW = "/sleep/getSleepW";//睡眠周统计
    public final static String getSleepM = "/sleep/getSleepM";//睡眠月统计
    public final static String myInfo = "/user/myInfo";//达标总数
    public final static String getVersion = "/user/getVersion";//升级
    public final static String startPage = "/user/startPage";//启动页


    public final static String HTTP = "http://119.29.78.138:8080";  //前缀，本地测试（暂时）
    public final static String Zhuce = "http://119.29.78.138:8080/v1/member.vhtml?c=register&type=0&";//上传用户名和密码（本地）
    public final static String Denglu = "http://119.29.78.138:8080/v1/member.vhtml?c=login&";
    public final static String XiuGAIXINXI = "http://119.29.78.138:8080/v1/member.vhtml?c=editUserInfo&token=";
    public final static String XiuGAIXI = "http://119.29.78.138:8080/Feelfriend/v1/member.vhtml?c=editUserInfo&token=";
    public final static String CHASHUJU = "http://119.29.78.138:8080/v1/member.vhtml?c=getUser&type=0&username=";
    //修改密码
    public final static String XiuGAIMima = HTTP + "/v1/passport.vhtml?c=editPWD&username=";


    public final static String LASTSTRING = "?c=save";
    public final static String LASTSTRING_GET = "?c=getSleepData";
    public final static String LASTSTRING_GET_SPORTS = "?c=getSportsData";
    public final static String LASTSTRING_GET_SPORTS_BYDAY = "?c=getSportsDataByDay";
    public final static String LASTSTRING_GET_SLEEP_BYDAY = "?c=getSleepDataByDay";

    public final static String Username = "username=";//用户名（邮箱）
    public final static String Password = "&password=";//密码
    public final static String HTTPS = "https://";
    //生产环境
    //public final static String  HOST         = "od.careeach.com";
    //测试环境
    public final static String HOST = "od.careeach.com";

    public final static String GET_SHARE = HTTP + HOST;

    public final static String HTTP_NEWDIR = "/action/json_201411";
    /*邮箱/==登录==注册*/
    public final static String EMAILREGISTER = GET_SHARE + HTTP_NEWDIR + "/login.jsp";
    /*运动管理*/
    public final static String SPORTMANAGE = GET_SHARE + HTTP_NEWDIR + "/sports.jsp";
    /*睡眠管理*/
    public final static String SLEPPMANAGE = GET_SHARE + HTTP_NEWDIR + "/sleep.jsp";

    /**
     * sis手表手表获取数据模块数据
     **/
    public final static String GET_WATCH_DATA_DATA = "/sport/getAllStepsByDay";


}
