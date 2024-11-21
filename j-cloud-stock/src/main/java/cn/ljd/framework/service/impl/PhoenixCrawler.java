package cn.ljd.framework.service.impl;

import cn.ljd.framework.po.FhStockBasicPo;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/*
 凤凰网财经
 */
public class PhoenixCrawler {
    private static final String SRC_URL = "https://finance.pae.baidu.com/selfselect/getmarketrank?sort_type=1&sort_key=14&from_mid=1&pn=offset&rn=pageSize&group=ranklist&type=ab&finClientType=pc";
    private static final String ENCODING = "utf-8";

    // Used to save stock code names
    public static List<FhStockBasicPo> craw() {
        List<FhStockBasicPo> fhStockBasicPoList = new ArrayList<>();


        Integer offset = 0;
        Integer pageSize = 200;


        int idx = 0;
        int page = 0;
        while (true) {
            String url = SRC_URL;
            url = url.replace("pageSize", pageSize.toString());
            url = url.replace("offset", offset.toString());
            System.out.println(url);
            String html = getUrlHtml(url, ENCODING);

            try {
                JsonObject allObject = (JsonObject) (new JsonParser()).parse(html);
                JsonObject resultObject1 = allObject.getAsJsonObject("Result");
                JsonArray resultArray = resultObject1.getAsJsonArray("Result");
                JsonElement resultElement1 = resultArray.get(0);

                JsonObject resultObject2 = resultElement1.getAsJsonObject();

                JsonObject displayDataObject = resultObject2.getAsJsonObject("DisplayData");
                JsonObject resultDataObject = displayDataObject.getAsJsonObject("resultData");
                JsonObject tplDataObject = resultDataObject.getAsJsonObject("tplData");

                JsonObject resultObject = tplDataObject.getAsJsonObject("result");

                JsonArray rankList = resultObject.getAsJsonArray("rank");

                for (int i = 0; i < rankList.size(); i++) {
                    JsonElement jsonElement = rankList.get(i);
                    JsonObject stockObject = jsonElement.getAsJsonObject();
                    System.out.println(stockObject.get("name").getAsString());
                    String exchange = stockObject.get("exchange").getAsString().toLowerCase();
                    String stockCode = stockObject.get("code").getAsString();
                    String stockName = stockObject.get("name").getAsString();
                    FhStockBasicPo s = new FhStockBasicPo(idx++, stockCode, stockName);
                    s.setSqCode(exchange+stockCode);
                    fhStockBasicPoList.add(s);
                }
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
            page++;
            offset = pageSize * page;
            /*for(String key : jsonObject.keySet()){
                String firstKey = jsonObject.keySet().iterator().next();
            }*/

            /*Document doc = Jsoup.parse(html,ENCODING);

            // Find core node
            Element divtab01 = doc.getElementsByClass("tab01").last();

            // Find stocks
            Elements trs=divtab01.getElementsByTag("tr");
            for(Element tr:trs) {
                Elements tds=tr.getElementsByTag("td");
                if(tds.size()>2) {
                    Element codeElm=tds.get(0).getElementsByTag("a").last();
                    Element nameElm=tds.get(1).getElementsByTag("a").last();

                    FhStockBasicPo s=new FhStockBasicPo(idx++,codeElm.text(),nameElm.text());
                    fhStockBasicPoList.add(s);
                }
            }*/

           /* // Find next page url
            Element lastLink=divtab01.getElementsByTag("a").last();
            if(lastLink.text().equals("下一页")) {
                url="http://app.finance.ifeng.com/list/stock.php"+lastLink.attr("href");
            }else {
                break;
            }*/
           /* if (idx > 10) {
                break;
            }*/

        }
        //System.out.println("共找到"+idx+"个股票.");
        return fhStockBasicPoList;
    }

    public static String getUrlHtml(String url, String encoding) {
        StringBuffer sb = new StringBuffer();
        URL urlObj = null;
        URLConnection openConnection = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            urlObj = new URL(url);
            openConnection = urlObj.openConnection();
            isr = new InputStreamReader(openConnection.getInputStream(), encoding);
            br = new BufferedReader(isr);
            String temp = null;
            while ((temp = br.readLine()) != null) {
                sb.append(temp + "\n");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (isr != null) {
                    isr.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }


    public static void main(String[] args) {
        // 根据需要设置代理
        System.setProperty("http.proxyHost", "");
        System.setProperty("http.proxyPort", "");

        PhoenixCrawler.craw();
    }
}
