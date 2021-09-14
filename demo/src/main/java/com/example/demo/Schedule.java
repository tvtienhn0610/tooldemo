package com.example.demo;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.gson.Gson;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@EnableScheduling
public class Schedule implements InitializingBean {

    SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
    String valueDefaute = "tientv";


    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("process runing !!!!");
    }

    @Scheduled(fixedRate = 60000)
    public void processGetData(){
        try {
            System.out.println("start process");
            WebClient webClient = new WebClient(BrowserVersion.CHROME);
            List<String> listV = new ArrayList<>();
//        webClient.getOptions().setUseInsecureSSL(true);
            webClient.getOptions().setCssEnabled(true);
            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            HtmlPage page = webClient.getPage("https://cointobuy.io/");

            Document doc = Jsoup.parse(page.asXml());
            Element element = doc.getElementById("ranks-list");

            Date date = new Date(System.currentTimeMillis());
            String datenow = formatter.format(date);

            for (Element row : element.select("tr")){
                String result = "";
                for (Element col : row.select("td")) {
                    // print results
//                System.out.println(col.ownText());
                    result += col.ownText()+"|";
                    for (Element span : col.select("span.trend-green")){
//                    System.out.println("green");
//                    System.out.println(span.ownText());
                        result += "green|"+span.ownText();
                    }

                    for (Element span : col.select("span.trend-red")){
//                    System.out.println("red");
//                    System.out.println(span.ownText());
                        result += "red|"+span.ownText();
                    }
                }
                result = result ;
                listV.add(result);
                System.out.println(result);

            }

            if (!valueDefaute.equals(listV.get(0))){
                System.out.println("in file !!!!");
                File file = new File("C:\\Users\\Bach_Vuong\\Documents\\tesst.txt");
                if(!file.exists()){
                    file.createNewFile();
                }
                FileWriter fw = new FileWriter(file,true);
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write("==================================================================================");
                bw.write("\n");
                for (String s : listV){
                    bw.write(s+datenow
                    );
                    bw.write("\n");
                }
                bw.close();
                valueDefaute = listV.get(0).toString();
            } else {
                System.out.println("web chua thay doi gi");
            }
            webClient.close();

        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
