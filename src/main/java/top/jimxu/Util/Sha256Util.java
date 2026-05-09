package top.jimxu.Util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class Sha256Util {

    public static String sha256(String pwd) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(pwd.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("加密失败", e);
        }
    }

}
