package com.xdlr.camera.token;

import com.xdlr.camera.util.Base64ImageUtils;
import com.xdlr.camera.util.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TokenTransfer {
    private static Logger logger = LoggerFactory.getLogger(TokenTransfer.class);
    private static final String[] states = {"ENTRANCE", "EXPORT", "VENDING_MACHINE", "EXTRA_QUERY", "CIVILIZED_BEHAVIOR"};
    private static final String hostAddress = "http://47.106.142.178:8080";
    private String state;

    public void initToken(String certNum, String imagePath) {
        state = states[0];
        logger.info("初始化用户");
        request(certNum, imagePath, state);
    }

    public void doNothing(String certNum, String imagePath) {
        state = states[1];
        logger.info("出门");
        request(certNum, imagePath, state);
    }

    public void renderGoods(String certNum, String imagePath) {
        state = states[2];
        logger.info("售货机消费积分");
        request(certNum, imagePath, state);
    }

    public void addToken(String certNum, String imagePath) {
        state = states[4];
        logger.info("增加积分");
        request(certNum, imagePath, state);
    }

    private void request(String certNum, String imagePath, String state) {
        String base64Image = Base64ImageUtils.ImageToBase64ByLocal(imagePath);
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        String res = HttpUtil.doPost(hostAddress + "/user/PutUserState",
                "Uid=" + certNum + "&Ustate=" + state + "&Uimage=" + base64Image + "&Utime=" + time);
        System.out.println("ready to send request");
        System.out.println(res);
    }
}
