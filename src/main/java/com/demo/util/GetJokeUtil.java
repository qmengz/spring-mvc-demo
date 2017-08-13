package com.demo.util;

import com.demo.mobel.Articles;
import com.demo.mobel.Item;
import com.demo.mobel.ReplyTuwenMessage;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

/**
 * Created by qingmeng.zhao on 2017/7/29.
 */
public class GetJokeUtil {
    public static final String DEF_CHATSET = "UTF-8";
    public static final int DEF_CONN_TIMEOUT = 30000;
    public static final int DEF_READ_TIMEOUT = 30000;
    public static String userAgent = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.66 Safari/537.36";

    //配置您申请的KEY
    public static final String APPKEY = "78e8bd72863347eed7f7030c70efcb94";
    public static final String WXAPPKEY = "0e67dd39982aa251b6864fed9984ca6e";
    public static final String JDAPPKEY = "8fc3fb7922abb03ce30ec73a3f89b121";

    //2.最新笑话
    public static void getRequest2() {
        String result = null;
        String url = "http://japi.juhe.cn/joke/content/text.from";//请求接口地址
        Map params = new HashMap();//请求参数
        params.put("page", "");//当前页数,默认1
        params.put("pagesize", "");//每次返回条数,默认1,最大20
        params.put("key", APPKEY);//您申请的key


        result = net(url, params, "GET");
        JSONObject object = JSONObject.fromObject(result);
        if (object.getInt("error_code") == 0) {
            JSONObject resul = (JSONObject) object.get("result");
            JSONArray ja = resul.getJSONArray("data");
            JSONObject jb = (JSONObject) ja.get(0);
            System.out.println(jb.toString());
        } else {
            System.out.println(object.get("error_code") + ":" + object.get("reason"));
        }

    }


    //1.微信精选列表
    private JSONArray getRequest1() {
        String result = null;
        String url = "http://v.juhe.cn/weixin/query";//请求接口地址
        Map params = new HashMap();//请求参数
        params.put("pno", "");//当前页数，默认1
        params.put("ps", "5");//每页返回条数，最大100，默认20
        params.put("key", WXAPPKEY);//应用APPKEY(应用详细页查询)
        params.put("dtype", "json");//返回数据的格式,xml或json，默认json

        JSONArray ja = null;
        try {
            result = net(url, params, "GET");
            JSONObject object = JSONObject.fromObject(result);
            if (object.getInt("error_code") == 0) {
                JSONObject resul = (JSONObject) object.get("result");
                ja = resul.getJSONArray("list");
            } else {
                System.out.println(object.get("error_code") + ":" + object.get("reason"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ja;
    }

    public ReplyTuwenMessage wxjx(String fromName, String toName) {

        GetJokeUtil gju = new GetJokeUtil();

        ReplyTuwenMessage tw = new ReplyTuwenMessage();
        Articles articles = new Articles();

        tw.setMessageType("news");
        tw.setCreateTime(new Long(new Date().getTime()).toString());
        tw.setToUserName(fromName);
        tw.setFromUserName(toName);
        tw.setFuncFlag("0");
        tw.setArticleCount(1);

        JSONArray ja = gju.getRequest1();
        tw.setArticleCount(ja.size());
        List<Item> items = new ArrayList<Item>();

        for (int i = 0; i < ja.size(); i++) {
            JSONObject jb = (JSONObject) ja.get(i);
            Item item = new Item();
            item.setTitle(jb.getString("source") + "\r\n" + jb.getString("title"));
            item.setDescription(jb.getString("title"));
            item.setPicUrl(jb.getString("firstImg"));
            item.setUrl(jb.getString("url"));
            items.add(item);
        }
        articles.setItems(items);
        tw.setArticles(articles);

        return tw;
    }


    //4.图灵机器人
    public JSONObject getRequest3(String toName, String con) {
        JSONObject jobj = null;
        String result;
        //https://way.jd.com/turing/turing?info=在干吗&loc=&userid=8fc3fb7922abb03ce30ec73a3f89b121&appkey=8fc3fb7922abb03ce30ec73a3f89b121
        String url = "https://way.jd.com/turing/turing";//请求接口地址
        Map params = new HashMap();//请求参数
        params.put("info", con);//消息
        params.put("loc", "");//位置信息
        params.put("userid", toName);//户分配的唯一标志
        params.put("appkey", "8fc3fb7922abb03ce30ec73a3f89b121");//您申请的key
        result = net(url, params, "GET");
        JSONObject object = JSONObject.fromObject(result);
        if (object.getString("code").equals("10000") && object.getString("msg").equals("查询成功")) {
            jobj = (JSONObject) object.get("result");
        } else {
            System.out.println(object.get("code") + ":" + object.get("msg"));
        }
        return jobj;
    }

    //4.最新趣图

    /***
     * return {"code":100000,"text":"打字中，请稍等"}
     * */
    private JSONArray getRequest4() {
        String result;
        String url = "http://japi.juhe.cn/joke/img/text.from";//请求接口地址
        Map params = new HashMap();//请求参数
        params.put("page", "2");//当前页数,默认1
        params.put("pagesize", "5");//每次返回条数,默认1,最大20
        params.put("key", APPKEY);//您申请的key
        result = net(url, params, "GET");
        JSONArray ja = null;
        JSONObject object = JSONObject.fromObject(result);
        if (object.getInt("error_code") == 0) {
            JSONObject resul = (JSONObject) object.get("result");
            ja = resul.getJSONArray("data");
        } else {
        }
        return ja;
    }


    /**
     * 天气查询
     * */
    public String getRequest6(String city) {
        String result;
        String url = "https://way.jd.com/jisuapi/weather";//请求接口地址
        Map params = new HashMap();//请求参数
        params.put("city", city);//城市
        params.put("appkey", JDAPPKEY);//appkey
        result = net(url, params, "GET");
        JSONObject ja = null;
        JSONObject object = JSONObject.fromObject(result);
        StringBuffer weather = new StringBuffer();
        if (object.getInt("code") == 10000) {
            JSONObject resul = object.getJSONObject("result");
            if(resul.getInt("status") == 0) {
                ja = resul.getJSONObject("result");
                weather.append("查询城市: " + ja.getString("city") + "\r\n");
                weather.append("更新时间: " + ja.getString("updatetime") + "\r\n");
                weather.append("=====当前天气实况=====\r\n");
                weather.append("温度：    " + ja.getString("templow") + "℃ ~ " + ja.getString("temphigh") + "℃\r\n");
                weather.append("风向：	  " + ja.getString("winddirect") + "\r\n");
                weather.append("湿度：    " + ja.getString("humidity") + "\r\n");
                weather.append("=====相关天气指数=====\r\n");
                JSONArray index = ja.getJSONArray("index");
                for (int i = 0; i < index.size() - 2; i++) {
                    JSONObject jb = index.getJSONObject(i);
                    String ivalue1 = jb.getString("ivalue");
                    String iname1 = jb.getString("iname");
                    String detail = jb.getString("detail");
                    weather.append(iname1 + "：" + ivalue1 + "\r\n");
                    weather.append("\t" + detail + "\r\n ");
                }
            }else {
                weather.append("未查询到相关信息");
            }
        } else {
            weather.append("未查询到相关信息");
        }
        return weather.toString();
    }

    //4.最新趣图

    /***
     * return {"code":100000,"text":"打字中，请稍等"}
     * */
    public JSONObject getRequest5(String qword) {

        GetSongIdUtil gsu = new GetSongIdUtil();
        List songIds = gsu.etSongId(qword);

        String result;
        String url = "http://music.baidu.com/data/music/links";//请求接口地址
        Map params = new HashMap();//请求参数
        params.put("songIds", songIds.get(1));//歌曲ID
        result = net(url, params, "POST");
        JSONArray ja = null;
        JSONObject object = JSONObject.fromObject(result);
        if ("22000".equals(object.getString("errorCode"))) {
            JSONObject resul = object.getJSONObject("data");
            ja = resul.getJSONArray("songList");
        }
        return ja.getJSONObject(0);
    }

    public static void main(String[] args) throws Exception {
        GetJokeUtil gj = new GetJokeUtil();
        String recognition = "七宝天气";
        recognition = recognition.substring(0,recognition.length()-2);
        System.out.println(recognition);
        String weather = gj.getRequest6(recognition);
        System.out.println(weather);
    }

    /**
     * @param strUrl 请求地址
     * @param params 请求参数
     * @param method 请求方法
     * @return 网络请求字符串
     * @throws Exception
     */
    public static String net(String strUrl, Map params, String method) {
        HttpURLConnection conn = null;
        BufferedReader reader = null;
        String rs = null;
        try {
            StringBuffer sb = new StringBuffer();
            if (method == null || method.equals("GET")) {
                strUrl = strUrl + "?" + urlencode(params);
            }

            URL url = new URL(strUrl);
            conn = (HttpURLConnection) url.openConnection();
            if (method == null || method.equals("GET")) {
                conn.setRequestMethod("GET");
            } else {
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
            }
            conn.setRequestProperty("User-agent", userAgent);
            conn.setUseCaches(false);
            conn.setConnectTimeout(DEF_CONN_TIMEOUT);
            conn.setReadTimeout(DEF_READ_TIMEOUT);
            conn.setInstanceFollowRedirects(false);
            conn.connect();
            if (params != null && method.equals("POST")) {
                try {
                    DataOutputStream out = new DataOutputStream(conn.getOutputStream());
                    out.writeBytes(urlencode(params));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            InputStream is = conn.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, DEF_CHATSET));
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                sb.append(strRead);
            }
            rs = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
        return rs;
    }

    //将map型转为请求参数型
    public static String urlencode(Map<String, Object> data) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry i : data.entrySet()) {
            try {
                sb.append(i.getKey()).append("=").append(URLEncoder.encode(i.getValue() + "", "UTF-8")).append("&");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
