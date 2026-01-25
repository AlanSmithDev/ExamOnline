package com.sdut.examonline.user.util;

import com.baidu.aip.face.AipFace;
import com.sdut.examonline.common.constant.FaceConstants;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Chen
 * @since 2022/3/17 18:28
 */
public class BaiduFaceUtils {

    /**
     * 检测时传递faceToken
     */
    private static final String IMAGE_TYPE = "BASE64";

    /**
     * 人脸分组
     */
    private static final String GROUP = "login";

    /**
     * 客户端单例
     */
    private static final AipFace CLIENT = new AipFace(FaceConstants.APP_ID, FaceConstants.API_KEY, FaceConstants.SECRET_KEY);

    static {
        CLIENT.setConnectionTimeoutInMillis(2000);
        CLIENT.setSocketTimeoutInMillis(20000);
    }

    /**
     * 是否存在用户的人脸信息
     *
     * @param userId 用户ID
     */
    public static boolean isFaceExists(Long userId) {
        JSONObject response = CLIENT.faceGetlist(String.valueOf(userId), GROUP, null);
        System.out.println(response.toString());
        if (response.has("error_code") && response.getInt("error_code") == 0) {
            JSONObject result = response.getJSONObject("result");
            return result.has("face_list") && result.getJSONArray("face_list").length() > 0;
        }
        return false;
    }

    /**
     * 检测图片中有且仅有一张人脸
     *
     * @param image base64图片
     */
    public static boolean detectFace(String image) {
        JSONObject response = CLIENT.detect(image, IMAGE_TYPE, null);
        return response.has("error_code") && response.getInt("error_code") == 0;
    }

    /**
     * 检测人脸
     *
     * @param image base64图片
     * @return 用户ID
     */
    public static String checkFace(String image) {
        JSONObject response = CLIENT.search(image, IMAGE_TYPE, GROUP, null);
        if (response.has("error_code") && response.getInt("error_code") == 0) {
            JSONObject result = response.getJSONObject("result");
            if (result.has("user_list")) {
                JSONArray userList = result.getJSONArray("user_list");
                if (userList.length() > 0) {
                    JSONObject jsonObject = userList.getJSONObject(0);
                    if (jsonObject.has("score") && jsonObject.has("user_id")
                            && jsonObject.getDouble("score") > FaceConstants.PASS_SCORE) {
                        return jsonObject.getString("user_id");
                    }
                }
            }
        }
        return null;
    }

    /**
     * 注册人脸
     *
     * @param image  base64图片
     * @param userId 用户ID
     */
    public static boolean registerFace(String image, Long userId) {
        JSONObject response = CLIENT.addUser(image, IMAGE_TYPE, GROUP, String.valueOf(userId), null);
        return response.has("error_code") && response.getInt("error_code") == 0;
    }

    /**
     * 更新人脸
     *
     * @param image  base64图片
     * @param userId 用户ID
     */
    public static boolean updateFace(String image, Long userId) {
        JSONObject response = CLIENT.updateUser(image, IMAGE_TYPE, GROUP, String.valueOf(userId), null);
        return response.has("error_code") && response.getInt("error_code") == 0;
    }

}
