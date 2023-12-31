package com.chatroy.royai.listener;


import com.chatroy.royai.MsgOrImg.messageOrImg;
import com.chatroy.royai.utils.OK3HttpClient;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import love.forte.simboot.annotation.ContentTrim;
import love.forte.simboot.annotation.Filter;
import love.forte.simboot.annotation.FilterValue;
import love.forte.simboot.annotation.Listener;
import love.forte.simbot.ID;
import love.forte.simbot.definition.Member;
import love.forte.simbot.event.FriendMessageEvent;
import love.forte.simbot.event.GroupMessageEvent;
import love.forte.simbot.message.*;
import love.forte.simbot.resources.PathResource;
import love.forte.simbot.resources.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.Random;

@Component
public class HelloListener implements messageOrImg {
    Random random = new Random();
    SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
    String  tx = "腾讯是什么?tx是什么?";

    @Listener
    @Filter(value = "/help")
    public void help(GroupMessageEvent event) throws MalformedURLException {
        MessagesBuilder messagesBuilder = new MessagesBuilder()
                .text("如果无聊了可以@Royill 你好(会有意想不到的惊喜)\ntest功能\n#求签功能" +
                        "\n#来点AI图片(输入数字指定图片)" +
                        "\n#来点二次元\n#禁言抽奖与自助禁言(bot必须是管理员哦)"+
                        "\n#随机歌曲"+
                        "\n#对话 (不是ChatGPT 只是一个简单的对话bot)"+
                        "\n#合成 (两个emoji！别用其他的 api也有问题 有些不识别（)")
                .image(Resource.of(new URL("https://api.paugram.com/wallpaper/")));
        event.getGroup().sendBlocking(messagesBuilder.build());
    }



    @Listener
    @Filter(value = "#来点AI图片")
    public void img(GroupMessageEvent event){
        int i = random.nextInt(IMG_AI.length);
        MessagesBuilder image = new MessagesBuilder().image(IMG_AI[i]);
        event.getSource().sendBlocking(image.build());
    }
    @Listener
    @Filter(value = "#来点二次元")
    public void Img2(GroupMessageEvent event) throws IOException {
        MessagesBuilder messagesBuilder = new MessagesBuilder();
        int i = random.nextInt(3);
        if (i == 1){
            messagesBuilder.image(Resource.of(new URL("https://imgapi.xl0408.top/index.php")));
        }
        if (i == 2){
            messagesBuilder.image(Resource.of(new URL("https://api.oick.cn/random/api.php?type=pc")));
        } else {
            String s = OK3HttpClient.httpGet("https://api.vvhan.com/api/acgimg?type=json", null, null);
            JsonObject jsonObject = new Gson().fromJson(s, JsonObject.class);
            String imgurl = jsonObject.get("imgurl").getAsString();
            messagesBuilder.image(Resource.of(new URL(imgurl)));
        }
        event.getGroup().sendBlocking(messagesBuilder.build());

    }

    @Listener
    @Filter(value = "#来点色图")
    public void ImgR18(FriendMessageEvent event) throws MalformedURLException {
        String s = OK3HttpClient.httpGet("https://api.苏苏.cn/API/R18.php", null, null);
        JsonObject jsonObject = new Gson().fromJson(s, JsonObject.class);
        JsonObject data = jsonObject.getAsJsonObject("data");
        String author = data.get("画家").getAsString();
        String title = data.get("标题").getAsString();
        String img = data.get("图片").getAsString();
        MessagesBuilder messagesBuilder = new MessagesBuilder()
                .append("标题："+title)
                .append("\n画家："+author+"\n")
                .image(Resource.of(new URL(img)));
        event.getFriend().sendBlocking(messagesBuilder.build());
    }

    @Listener
    @Filter(value = "{{str}}",targets = @Filter.Targets(atBot = true))
    @ContentTrim // 当匹配被at时，将'at'这个特殊消息移除后，剩余的文本消息大概率存在前后空格，通过此注解在匹配的时候忽略前后空格
    public void onChannelMessage(GroupMessageEvent event , @FilterValue("str")String str) {// 将要监听的事件类型放在参数里，即代表监听此类型的消息

        if (str.equals("你好")) {
            int i = random.nextInt(2);
            if (i == 1) {
                PathResource resource = Resource.of(Path.of("img/2.png"));
                Messages append = new MessagesBuilder()
                        .text("请问有什么事么^^")
                        .image(resource)
                        .build();
                event.replyBlocking(append);// Java中的阻塞式API
            } else {
                MessagesBuilder image = new MessagesBuilder().image(IMG[1]);
                event.getGroup().sendBlocking(image.build());
            }
        }

        if (tx.contains(str)) {
            event.getGroup().sendBlocking("是那个只喜欢钱,游戏里面都是氪金元素,注册的账号不是自己的账号的投资公司吗");
        }

        if(str.equals("学习资料")) {
            Messages elements = new MessagesBuilder()
                    .text("java基础(尚硅谷):" + "https://www.bilibili.com/video/BV1PY411e7J6" + "\n")
                    .text("Go基础:" + "https://www.bilibili.com/video/BV1Pg41187AS\n")
                    .text("simbot文档:" + "https://simbot.forte.love/\n")
                    .text("mirai文档:" + "https://docs.mirai.mamoe.net/\n")
                    .text("springboot官方文档:"+"https://spring.io/projects/spring-cloud-aws\n")
                    .text("LeetCode官网"+"https://leetcode.cn/problemset/all/\n")
                    .text("Vue文档:"+"https://cn.vuejs.org/")
                    .build();
            event.getGroup().sendBlocking(elements);
        }
    }

    @Listener
    public  void need(GroupMessageEvent event){
        //获得信息
        String plainText = event.getMessageContent().getPlainText();
        //获得用户
        String username = event.getAuthor().getNickOrUsername();
        if (plainText.equals("#求签")){
            event.getSource().sendBlocking("你在祈求什么呢？");
        } else if (plainText.startsWith("#求签")){
            String[] msg = plainText.split(" ");
            String msg1 = msg[1];
            //获取当前时间戳
            Date date = new Date(System.currentTimeMillis());
            int i = random.nextInt(MSG.length);
            int i_img = random.nextInt(IMG_AI.length);
            Messages elements = new MessagesBuilder()
                    .text("今天是" + format.format(date)+"\n"
                            +username+"所求的:"+"\""+msg1+"\"\n\n" +
                            "气运:"+"【"+MSG[i]+"】")
                    .image(IMG_AI[i_img])
                    .build();
            event.getGroup().sendBlocking(elements);
        }
    }

    @Listener
    @Filter("#禁言抽奖")
    public void mute(GroupMessageEvent event){
        //禁言所用时长
        Integer time[] = {1,2,3,15,5,10,20,30,60,144,300};

        Member author = event.getAuthor();
        String nickOrUsername = author.getNickOrUsername();
        ID id = author.getId();
        if (author.isAdmin()){
             event.getGroup().sendBlocking("有本事把你的管理员卸下来");
            return;
        }
            int i = random.nextInt(time.length);
            boolean b = author.muteBlocking(Duration.ofMinutes(i));
            if (b) {
                MessagesBuilder messagesBuilder = new MessagesBuilder()
                        .at(id)
                        .text("恭喜"+nickOrUsername+"抽到"+time[i]+"分钟禁言");
                event.getGroup().sendBlocking(messagesBuilder.build());
            }

    }

    @Listener
    @Filter("#自助禁言 {{time}}")
    public void muteMe(GroupMessageEvent event, @FilterValue("time")String time) {
        Integer TIME = Integer.valueOf(time);
        Member author = event.getAuthor();
        ID id = author.getId();
        if (author.isAdmin()){
            event.getGroup().sendBlocking("有本事把你的管理员卸下来");
            return;
        }
            if(0 < TIME && TIME < 1440) {
                author.muteBlocking(Duration.ofMinutes(TIME));
                Messages builder = new MessagesBuilder()
                        .at(id)
                        .text("恭喜禁言:" + TIME + "分钟")
                        .build();
                event.getGroup().sendBlocking(builder);
            }
            if(0 > TIME) {
                event.getGroup().sendBlocking("会时间倒流是吧");
            } else if (TIME > 1440){
                Messages msg = new MessagesBuilder()
                        .text("活久了? 这就找主人把你踢掉")
                        .at(ID.$("Rooooyiii"))
                        .build();
                event.getGroup().sendAsync(msg);
            }
    }
}