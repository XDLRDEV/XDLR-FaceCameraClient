import face.FaceLoginInfo;
import face.FaceManager;
import face.NVSSDK;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import token.TokenTransfer;


//人脸参数相关
public class FaceClient {
    private static final Logger logger = LoggerFactory.getLogger(FaceClient.class);
    private FaceManager[] faceManagers = new FaceManager[FaceLoginInfo.DEVICE_COUNT];
    private TokenTransfer tokenTransfer;

    public static void main(String args[]) {
        logger.debug("Start camera-client");
        FaceClient client = new FaceClient();
        try {
            logger.debug("Init camera-client");
            client.init();
            while (true) {
                Thread.sleep(200);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 程序退出
            client.exit();
        }

    }

    private void init() {
        // 初始化积分工具
        logger.debug("Init tokenTransfer");
        tokenTransfer = new TokenTransfer();

        // 初始化抓拍机
        for (int i = 0; i < FaceLoginInfo.DEVICE_COUNT; i++) {
            FaceManager faceManager;
            if (i == 0) {
                faceManager = new FaceManager(FaceLoginInfo.FACE_LOGIN_INFO_ONE);
            } else if (i == 1) {
                faceManager = new FaceManager(FaceLoginInfo.FACE_LOGIN_INFO_TWO);
            } else if (i == 2) {
                faceManager = new FaceManager(FaceLoginInfo.FACE_LOGIN_INFO_THREE);
            } else {
                faceManager = new FaceManager(FaceLoginInfo.FACE_LOGIN_INFO_FOUR);
            }

            faceManagers[i] = faceManager;
            // 初始化SDK
            logger.debug("Init SDK");
            faceManager.SDKInit();
            // 登录设备
            logger.debug("Login Device");
            faceManager.LogonDevice();
            if (faceManager.getLogonID() < 0) {
                logger.info("LoginID < 0, log back");
                continue;
            }

            // 开启智能分析
            logger.debug("开启智能分析");
            faceManager.SetVcaStatue(NVSSDK.VCA_SUSPEND_STATUS_RESUME);

            // 开启图片流
            logger.debug("开启图片流");
            logger.debug("创建抓拍目录");
            faceManager.createPicDir(); //创建抓拍目录、
            logger.debug("连接图片流通道");
            faceManager.StartSnap();    //连接图片流通道，接收人脸识别(报警)结果
            faceManager.FaceLibraryQuery();
            faceManager.registerSnapListener(
                    new FaceManager.SnapNotifyListener() {
                        @Override
                        public void snapNotify(boolean isStranger, String faceDeviceId, String certNum, String negativePicturePath) {
                            logger.debug("收到抓拍回调通知，开启新线程处理用户信息");
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    logger.debug("处理用户积分");
                                    handleToken(isStranger, faceDeviceId, certNum, negativePicturePath);
                                }
                            }).start();
                        }

                        @Override
                        public void vcaSuspendSucceed(FaceManager faceManager) {
                            logger.debug("智能分析暂停成功");
                            new Thread(() -> {
                                logger.debug("开启新线程，添加人脸底图");
                                faceManager.FacePictureAdd(lastestNegativePicture.faceId, lastestNegativePicture.path);
                                logger.debug("恢复智能分析");
                                faceManager.SetVcaStatue(NVSSDK.VCA_SUSPEND_STATUS_RESUME);  // 恢复智能分析
                            }).start();

                        }
                    }

            );
        }
    }


    private NegativePicture lastestNegativePicture;

    // 底图
    static class NegativePicture {
        String faceId;
        String path;

        public NegativePicture(String faceId, String path) {
            this.faceId = faceId;
            this.path = path;
        }
    }

    void handleToken(boolean isStranger, String faceDeviceId, String certNum, String negativePicturePath) {
        if (isStranger) {
            // 进入抓拍机
            if (FaceLoginInfo.FACE_DEVICE_ONE_ID.equals(faceDeviceId)) {
                // 本地生成id
                for (FaceManager faceManager : faceManagers) {
                    System.out.println("请等待智能分析暂停结果!");
                    faceManager.SetVcaStatue(NVSSDK.VCA_SUSPEND_STATUS_PAUSE); // 暂停
                    lastestNegativePicture = new NegativePicture(certNum, negativePicturePath);
                }
                System.out.println("抓拍到新人");
                tokenTransfer.initToken(certNum, negativePicturePath);
            }
        } else {
            if (FaceLoginInfo.FACE_DEVICE_THREE_ID.equals(faceDeviceId)) {
                // 售货机
                System.out.println("出货");
                tokenTransfer.renderGoods(certNum, negativePicturePath);
            } else if (FaceLoginInfo.FACE_DEVICE_TWO_ID.equals(faceDeviceId)) {
                // 出口
                System.out.println("出口");
                tokenTransfer.doNothing(certNum, negativePicturePath);
            } else if (FaceLoginInfo.FACE_DEVICE_FOUR_ID.equals(faceDeviceId)) {
                System.out.println("文明行为");
                tokenTransfer.addToken(certNum, negativePicturePath);
            }
        }
    }

    private void exit() {
        if (faceManagers != null) {
            for (FaceManager faceManager : faceManagers) {
                faceManager.Exit();
            }
        }
    }
}
