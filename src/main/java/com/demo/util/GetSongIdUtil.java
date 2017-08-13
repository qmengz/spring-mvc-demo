package com.demo.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by qingmeng.zhao on 2017/7/30.
 */
public class GetSongIdUtil {

    public List etSongId(String qword){
        Document doc = null;
        List<String> songIds = new ArrayList<String>();
        try {
            doc = Jsoup.connect("http://musicmini.baidu.com/app/search/searchList.php?qword="+qword+"&ie=utf-8&page=0").get();
            Element th = doc.getElementById("sc-table");
            Elements links = th.getElementsByTag("input");
            for (Element link : links) {
                String songId = link.attr("id");
                if(!"selectAll".equals(songIds)){
                    songIds.add(songId);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return songIds;
    }

    public static void main(String[] args) {
        GetSongIdUtil s = new GetSongIdUtil();
        List<String> songIds  = s.etSongId("演员");
        System.out.println(songIds.toString());
    }

}
