package com.demo.util;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
/**
 * Created by qingmeng.zhao on 2017/7/30.
 */
public class HttpUtil {

    public static void toPOST(){
        try {
            String url = "http://ting.baidu.com/data/music/links?songIds=242078437";
            System.out.println("url =" + url);
            HttpPost post = new HttpPost(url);
            HttpResponse rs = new DefaultHttpClient().execute(post);
            String result = EntityUtils.toString(rs.getEntity());
            System.out.println("repost rs = '" + result + "'");
            if (rs.getStatusLine().getStatusCode() == 200) {
            }
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            System.out.println(pw.toString());
        }
    }

    public static void main(String[] args) {
        HttpUtil.toPOST();
    }

}
