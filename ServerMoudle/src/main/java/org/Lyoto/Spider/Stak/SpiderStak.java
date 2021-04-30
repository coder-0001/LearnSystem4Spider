package org.Lyoto.Spider.Stak;

import org.Lyoto.Spider.DefaultSpider;
import org.Lyoto.Spider.Strategy.impl.CatrgoryInfo;
import org.Lyoto.Spider.Strategy.impl.IteratorProcessor;
import org.Lyoto.Utils.MOOCUtils.Catgory;
import org.Lyoto.Utils.MOOCUtils.MOOCEntity;
import org.Lyoto.Utils.MOOCUtils.MOOCHeader;
import org.Lyoto.Utils.UrlUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.scheduler.RedisPriorityScheduler;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * @author Lyoto
 * @Date 2021-04-29 19:19
 **/
@Component
public class SpiderStak {

    @Autowired
    Catgory catgory;
    @Autowired
    UrlUtils urlUtils;
    @Autowired
    DefaultSpider defaultSpider;



    @Scheduled(initialDelay = 1000,fixedDelay = 100*1000)
    public void run(){
        Spider spider = Spider.create(defaultSpider);
        ArrayList<String> catgoryLsit = new ArrayList<>();
        catgoryLsit.addAll(urlUtils.catgoryId(catgory.getCatgoryList()));
        for(String catgory:catgoryLsit){
            try {
                HttpRequestBody custom = new HttpRequestBody().custom(("channelId=" + catgory).getBytes("UTF8"), MOOCHeader.ContentType, "UTF8");
                Request request = urlUtils.mocChannelUrl_post();
                request.setRequestBody(custom);
            spider = spider.addRequest(request);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        spider.setScheduler(new QueueScheduler().setDuplicateRemover(new RedisPriorityScheduler("localhost")))
                .thread(10)
                .runAsync();
    }



}