package com.example.mylivetvtest.keyUtil;



import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Configs {

    /**
     * Need change data start
     **/
    public static String Cache_pwd_key = "Cache_pwd_key";
    public static String Cache_fmac_key = "Cache_fmac_key";
    public static final int SEEK_TIME = 1000;
    public static final boolean debug = false;
    public static final String APPID = "200";
    //	public static final String APPID ="302";
    public static boolean isMobile = false;
    public static final String NewTag = "Latest";
    public static final String HotTag = "HOT";
    public static final String AllTag = "ALL";
    public static final String PKG_NAME = "com.vod.pv";
    public static boolean isLastNeedPassword = false;  //最后一项输入密码


    /**
     * Need change data end
     **/

    public static String link;
    public static String chip;
    public static final int NETWORK_NOT_CONNECT = 0x200301;
    public static final int NETWORK_CONNECT = 0x200302;
    public static final int AUTH_STOP = 0x200303;
    public static final int AUTH_FAIL = 0x200304;
    public static final int SUCCESS = 0x200305;
    public static final int SUCCESS_MTV = 0x200315;
    public static final int FAIL = 0x200306;
    public static final int VIDEO_PLAY = 0x200307;
    public static final int NETWORK_EXCEPTION = 0x200308;
    public static final int CONTENT_IS_NULL = 0x200309;
    public static final int AUTH_SUCCESS_READ_LIST_CACHE = 0x200310;
    public static final int CACHE_LIST_NEW = 0x300311;

    public static final class Handler {
        public static final int PROGRAM_DETAIL_NULL = 0x300001;
        public static final int PROGRAM_DETAIL_OK = 0x300002;
        public static final int PROGRAM_DETAIL_PARSE_FAILURE = 0x300003;
    }

    //PLAYER VERSION  file in /ASSETS/MoonPlayer_xx.apk
    //public static final String PLAYER_VERSION = "1.0";
    public static final String PLAYER_PKG = "com.sino.video";
    public static final String APK_NAME = "update.apk";
    public static final String INTENT_PARAM = "intent_param1";
    public static final String INTENT_PARAM_2 = "intent_param2";
    public static final String INTENT_SEARCH = "INTENT_SEARCH";
    //	public static final String CONTENT_CACHE_DIR = Environment.getExternalStorageDirectory().toString()+"/test/";
//	public static final String CONTENT_CACHE_FILE = CONTENT_CACHE_DIR+"jus t"+APPID;
    //public static final String CONTENT_CACHE_FILE = MyApplication.getApplication().getCacheDir().getAbsolutePath() + "/just" + APPID;
    //public static final String ALL_LIST_CACHE = MyApplication.getApplication().getCacheDir().getAbsolutePath() + "/AllList" + APPID;
    //    public static final String CHANEL_URL = BuildConfig.APP_HOST + "/Pvod/Vod/InitList.jsp?appid=" + BuildConfig.APP_TV_ID + "&l=" + Locale.getDefault().getLanguage() + "&gj=" + Locale.getDefault().getCountry();
    public static final String FORCETV_URL = "http://127.0.0.1:9906/cmd.xml?cmd=switch_chan&id=%s&server=%s";
    public static final String PLAY_URL = "http://127.0.0.1:9906/%s.ts";

    public static class BroadCast {
        public static final String UPDATE_MSG = PKG_NAME + ".update";
        public static final String APP_GET_MSG = PKG_NAME + ".msg";
    }

    public static class URL {
        private static final String APP_ID = APPID;

        public static String MAC = MACUtils.getMac();

        //vodchina1.ibcde.net(xiangYunZhiBo)
        //vod1.ibcde.net(yueGangShiJie)
       // public static String HOST1 = BuildConfig.APP_HOST;//正式库1
        //public static String HOST2 = BuildConfig.APP_HOST;//正式库2
        //public static String HOST3 = BuildConfig.APP_HOST;//正式库2
//        public static String HOST1 = "http://vodlist.maoq.pw";//正式库1
//        public static String HOST2 = "http://pvbox.live.telecom.ipuvideo.com";//正式库2
//        public static String HOST3 = "http://pvbox.cdn.pvbox.live";//正式库2
        public static String HOST = "http://vodlist.maoq.pw";//正式库
//public static String HOST ="http://54.39.49.80:10033"  ;
        //        public static String HOSTTEST = "http://192.168.28.155:9011";//测试库
        public static String HOSTTEST = "http://192.168.31.220:9011";//测试库

        public static List<String> GetChangeList() {
            List<String> list = new ArrayList<String>();
            //list.add(HOST1);
            //list.add(HOST2);
            //list.add(HOST3);
            return list;
        }

        /*public static List<MD_Host> GetHostList() {
            List<MD_Host> list = new ArrayList<>();
            String main = BuildConfig.APP_HOST.substring(7, BuildConfig.APP_HOST.length());
            int subindex = main.indexOf(":");
            if (subindex > 0) {
                main = main.substring(0, subindex);
            }
            list.add(new MD_Host(BuildConfig.APP_HOST, main, "H0"));
            list.add(new MD_Host("http://pvbox.live.ipuvideo.com", "pvbox.live.ipuvideo.com", "H4"));
            list.add(new MD_Host("http://pvbox.live.mobile.ipuvideo.com", "pvbox.live.com.east.android.mobile.ipuvideo.com", "H5"));
            list.add(new MD_Host("http://pvbox.live.telecom.ipuvideo.com", "pvbox.live.telecom.ipuvideo.com", "H6"));
            list.add(new MD_Host("http://pvbox.live.unicom.ipuvideo.com", "pvbox.live.unicom.ipuvideo.com", "H7"));
            list.add(new MD_Host(HOST1, "vodlist.maoq.pw", "H1"));
            list.add(new MD_Host(HOST2, "pvbox.cdn.pvbox.co", "H2"));
            list.add(new MD_Host(HOST3, "pvbox.cdn.pvbox.live", "H3"));

            return list;
        }*/

        //
        //		public static final String HOST1="http://192.168.1.210:9017/Api/";//测试库1
//		public static final String HOST2="http://192.168.1.210:9017/Api/";//测试库2
//		public static final String HOST3="http://192.168.1.210:9017/Api/";//测试库3
//		public static String HOST=HOST1;//测试库
        public static String getTVlistUrl() {

            return HOST + "/Pvod/Vod/InitList.jsp?l=" + Locale.getDefault().getLanguage() + "&gj=" + Locale.getDefault().getCountry();
        }

        public static String getAskURL() {
            return HOST + "/Pvod/Init/ask.jsp?appid=" + APP_ID + "&mac=" + MAC + "&l=" + Locale.getDefault().getLanguage() + "&gj=" + Locale.getDefault().getCountry();
        }
        public static String getAskURL2() {
            return HOST + "/Pvod/Init/initError.jsp?appid=" + APP_ID + "&mac=" + MAC + "&l=" + Locale.getDefault().getLanguage() + "&gj=" + Locale.getDefault().getCountry();
        }

        public static String getHotURL() {
            return HOST + "/Pvod/Vod/hot.jsp?appid=" + APP_ID + "&mac=" + MAC + "&l=" + Locale.getDefault().getLanguage() + "&gj=" + Locale.getDefault().getCountry();
        }

        public static String getReplayMenuURL() {
            return HOST + "/Pvod/Replay/rclass.jsp?appid=" + APP_ID + "&mac=" + MAC + "&l=" + Locale.getDefault().getLanguage() + "&gj=" + Locale.getDefault().getCountry();
        }

        public static String getReplayDataURL() {
            return HOST + "/Pvod/Replay/redate.jsp?appid=" + APP_ID + "&mac=" + MAC + "&l=" + Locale.getDefault().getLanguage() + "&gj=" + Locale.getDefault().getCountry();
        }

        public static String getReplayListURL() {
            return HOST + "/Pvod/Replay/relist.jsp?appid=" + APP_ID + "&mac=" + MAC + "&l=" + Locale.getDefault().getLanguage() + "&gj=" + Locale.getDefault().getCountry();
        }

        public static String getRecommendationURL() {
            return HOST + "/Pvod/Vod/YLike.jsp?appid=" + APP_ID + "&mac=" + MAC + "&l=" + Locale.getDefault().getLanguage() + "&gj=" + Locale.getDefault().getCountry();
        }

        public static String getSearchList() {
            return HOST + "/Pvod/Vod/KeySearchList.jsp?appid=" + APP_ID + "&mac=" + MAC + "&l=" + Locale.getDefault().getLanguage() + "&gj=" + Locale.getDefault().getCountry();
        }

        public static String getSearchCon() {
            return HOST + "/Pvod/Vod/KeySearch.jsp?appid=" + APP_ID + "&mac=" + MAC + "&l=" + Locale.getDefault().getLanguage() + "&gj=" + Locale.getDefault().getCountry();
        }

        public static String GetVodSub() {
            return HOST + "/Pvod/Vod/vodsub.jsp?appid=" + APP_ID + "&mac=" + MAC + "&l=" + Locale.getDefault().getLanguage() + "&gj=" + Locale.getDefault().getCountry();
        }

        public static String getDownApkUrl() {
            return "http://download.ipuvideo.com/";
        }

        /**
         * 获取列表缓存地址
         */
        public static String getPass() {
            return HOST + "/Pvod/Init/sn.jsp";
        }

        /**
         * 获取列表缓存地址
         */
        public static String getInfoURL() {
            return HOST + "/Pvod/Init/info.jsp?appid=" + APP_ID + "&mac=" + MAC + "&l=" + Locale.getDefault().getLanguage() + "&gj=" + Locale.getDefault().getCountry();
        }

        /**
         * 获取列表缓存地址
         */
        public static String getListCache() {
            String Url = HOST + "/Pvod/Vod/VodMenu.jsp?appid=" + APP_ID + "&mac=" + MAC + "&l=" + Locale.getDefault().getLanguage() + "&gj=" + Locale.getDefault().getCountry();
//            Log.d("test",Url);
            return Url;
        }

        /**
         * 获取一级菜单
         */
        public static String getLeftMenuApi() {
            return HOST + "/Pvod/Vod/GetMenu.jsp";
        }

        /**
         * 获取年份
         */
        public static String GetYearApi() {
            return HOST + "/Pvod/Vod/GetYear.jsp";
        }

        /**
         * 获取筛选条件参数
         */
        public static String GetParamApi() {
            return HOST + "/Pvod/Vod/GetParam.jsp";
        }

        /**
         * 获取节目列表基地址
         */
        public static String GetProgramApi() {
            return HOST + "/Pvod/Vod/GetVideo.jsp";
        }

        /**
         * 获取授权地址
         */
        public static String getAuthApi() {
            return HOST + "/Pvod/Init/init.jsp?appid=" + APP_ID + "&mac=" + MAC;
        }

        /**
         * 获取二级菜单基地址，后面还要加CID
         */
        public static String getSeconMenuApi() {
            return HOST + "Video/submenu?appid=" + APP_ID + "&mac=" + MAC + "&cid=";
        }

        public static String getKgUpSubUrl() {
            return HOST + "/Kg/List/upSing";
        }

        /**
         * 获取剧集基地址
         */
        /*public static String getDramaApi() {
            if (BuildConfig.APP_ID.equals("3")) {
                return HOST + "/MacCms/Vod/cli.jsp?appid=" + APP_ID + "&mac=" + MAC + "&sid=";
            } else {
                return HOST + "/Pvod/Vod/cli.jsp?appid=" + APP_ID + "&mac=" + MAC + "&sid=";
            }

        }*/

        public static String getKgUrl() {
            return HOST + "/Kg/List/cli.jsp";
        }

        /**
         * get vod program detail info
         */
        public static void getProgramDetailApi() {
            /*if (BuildConfig.APP_ID.equals("3")) {
                return HOST + "/MacCms/Vod/detail.jsp?appid=" + APP_ID + "&mac=" + MAC + "&l=" + Locale.getDefault().getLanguage() + "&gj=" + Locale.getDefault().getCountry()+"&sid=";
            } else {
                return HOST + "/Pvod/Vod/detail.jsp?appid=" + APP_ID + "&mac=" + MAC+ "&l=" + Locale.getDefault().getLanguage() + "&gj=" + Locale.getDefault().getCountry() + "&sid=";
            }*/

        }

        public static String getAppMsgApi() {
            return HOST + "/Pvod/Init/appmsg.jsp?appid=" + APPID + "&mac=" + MAC + "&l=" + Locale.getDefault().getLanguage() + "&gj=" + Locale.getDefault().getCountry();
        }

        public static String getAppUpdateApi() {
            return HOST + "/Pvod/Init/Updata.jsp?appid=" + APPID + "&mac=" + MAC + "&l=" + Locale.getDefault().getLanguage() + "&gj=" + Locale.getDefault().getCountry();
        }

        public static String getQrAuth() {
            return HOST + "/Ais/Box/QrAuth.jsp";
        }

        public static String getBoxAuth() {
            return HOST + "/Ais/Box/BoxAuth.jsp";
        }

        public static String getAdApi() {
//			return HOST +"VideoAd?mac="+MAC+"&appid="+APP_ID+"&cid=";
            return "";
        }

    }

    public static class Success {
        public static final int GET_LEFT_MENU = 0x000001;
        public static final int GET_LEFT_MENU_AUTH = 0x000002;
        public static final int GET_SECON_MENU = 0x000003;
        public static final int GET_PROGRAM_LIST = 0x000004;
        public static final int GET_DRAMA_LIST = 0x000005;
        public static final int AUTH_OK = 0x000006;

        public static final int GET_CACHE_LEFT_MENU = 0x000007;

        public static final int GET_AD = 0x000008;

        public static final int CLOSE_AD = 0x000009;
        public static final int UPDATE_AD_TIME = 0x000010;
        public static final int STOP_AD_TIME = 0x001001;
    }

    public static class Failure {
        public static final int GET_LEFT_MENU = 0x000011;
        public static final int GET_LEFT_MENU_AUTH = 0x000012;
        public static final int GET_SECON_MENU = 0x000013;
        public static final int GET_PROGRAM_LIST = 0x000014;
        public static final int GET_DRAMA_LIST = 0x000015;

        public static final int AUTH_WRONG = 0x000016;
        public static final int GET_CACHE_LEFT_MENU = 0x000017;
        public static final int NETWORK_EXCEPTION = 0x000018;
        public static final int GSON_WRONG = 0x000019;//Gson解析出错
        public static final int GET_LEFT_MENU_NET_WRONG = 0x000020;

        public static final int GET_AD = 0x000021;
    }

    /**
     * 缓存目录
     */
    /*public static class CachePath {
        private static final String ABSOLUTE_PATH = MyApplication.getApplication().getCacheDir().getAbsolutePath();
        public static final String AUTH = ABSOLUTE_PATH + "/auth" + APPID;
        public static final String LEFT_MENU = ABSOLUTE_PATH + "/leftmenu" + APPID;
    }

    public static class Code {
        public static final String AUTH_OK = "0";
    }

    public static class File {
        public static final int START_DOWNLOAD = 0x000101;
        //		public static final String UPDATE_PATH="/data"
//				+ Environment.getDataDirectory().getAbsolutePath() + "/"
//				+ Configs.PKG_NAME+ "";
        public static final String UPDATE_PATH = Utils.getStoragePath();

        public static String getUpdatePath() {
//			String parentPath =Configs.File.UPDATE_PATH;
            StringBuffer strBuffer = new StringBuffer();
            String parentPath = MyApplication.ctx().getCacheDir().getAbsolutePath();
            strBuffer.append(parentPath).append(java.io.File.separator).append(Configs.APK_NAME);
            String apkPath = strBuffer.toString();
            return strBuffer.toString();
        }

        public static String getJSONPath() {
//			String parentPath =Configs.File.UPDATE_PATH;
            StringBuffer strBuffer = new StringBuffer();
            String parentPath = MyApplication.ctx().getCacheDir().getAbsolutePath();
            strBuffer.append(parentPath).append(java.io.File.separator).append(BuildConfig.APP_ID + ".json");
            String apkPath = strBuffer.toString();
            return strBuffer.toString();
        }

    }*/

    public static class Other {
        /**
         * 改变多少次主机就提示用户手动重试
         */
        public static final int HOST_CHANGE_TIME = 4;
    }

    public static final class HeartBeat {
        public static final String URL = "";
        public static final String MAC = Configs.URL.MAC;
    }
}
