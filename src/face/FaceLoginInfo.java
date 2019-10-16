package face;

public class FaceLoginInfo {
    public static int DEVICE_COUNT = 4;

    public static final String FACE_DEVICE_ONE_ID = "ID0000801941330791520638";
    public static final String FACE_DEVICE_TWO_ID = "ID0000801941312222530880";
    public static final String FACE_DEVICE_THREE_ID = "ID0000801941330872140708";
    public static final String FACE_DEVICE_FOUR_ID = "ID0000801941330872210715";

    public String faceDeviceId;
    public String ip;
    public int port;
    public String userName;
    public String password;

    // 进入抓拍机
    public static final FaceLoginInfo FACE_LOGIN_INFO_ONE;
    public static final FaceLoginInfo FACE_LOGIN_INFO_TWO;
    // 售货机抓拍机
    public static final FaceLoginInfo FACE_LOGIN_INFO_THREE;
    public static final FaceLoginInfo FACE_LOGIN_INFO_FOUR;

    static {
        FACE_LOGIN_INFO_ONE = new FaceLoginInfo(FACE_DEVICE_ONE_ID, "192.168.1.3", 3000, "admin", "admin");
        FACE_LOGIN_INFO_TWO = new FaceLoginInfo(FACE_DEVICE_TWO_ID, "192.168.1.4", 3000, "admin", "admin");
        FACE_LOGIN_INFO_THREE = new FaceLoginInfo(FACE_DEVICE_THREE_ID, "192.168.1.5", 3000, "admin", "admin");
        FACE_LOGIN_INFO_FOUR = new FaceLoginInfo(FACE_DEVICE_FOUR_ID, "192.168.1.6", 3000, "admin", "admin");
    }

    public FaceLoginInfo(String faceDeviceId, String ip, int port, String userName, String password) {
        this.faceDeviceId = faceDeviceId;
        this.ip = ip;
        this.port = port;
        this.userName = userName;
        this.password = password;
    }
}
