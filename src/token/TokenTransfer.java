package token;

import util.Base64ImageUtils;
import util.HttpUtil;

import java.util.Date;

public class TokenTransfer {
    private static final String[] states = {"ENTRANCE", "EXPORT", "VENDING_MACHINE", "EXTRA_QUERY", "CIVILIZED_BEHAVIOR"};
    private static final String hostAddress = "47.106.142.178:8080";
    private String state;

    public void initToken(String certNum, String imagePath) {
        state = states[0];
        request(certNum, imagePath, state);
    }

    public void doNothing(String certNum, String imagePath) {
        state = states[1];
        request(certNum, imagePath, state);
    }

    public void renderGoods(String certNum, String imagePath) {
        state = states[2];
        request(certNum, imagePath, state);
    }

    public void addToken(String certNum, String imagePath) {
        state = states[4];
        request(certNum, imagePath, state);
    }


    private void request(String certNum, String imagePath, String state) {
        String base64Image = Base64ImageUtils.ImageToBase64ByOnline(imagePath);
        Date time = new Date();
        HttpUtil.doPost(hostAddress + "/user/PutUserState",
                "UId=" + certNum + "&Ustate=" + state + "&Uimage=" + base64Image + "&Utime=" + time);
    }
}
