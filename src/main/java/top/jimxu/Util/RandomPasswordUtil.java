package top.jimxu.Util;

import java.security.SecureRandom;

public class RandomPasswordUtil {
    // 定义字符池：小写 + 大写 + 数字 + 特殊字符
    private static final String LOWERS = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NUMS = "0123456789";
    private static final String SYMBOLS = "!@#$%&*_?";
    private static final String ALL_CHARS = LOWERS + UPPERS + NUMS + SYMBOLS;

    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * 生成随机密码
     * @param length 密码长度 1~10
     * @return 包含大小写、数字、特殊字符的密码
     */
    public static String generatePwd(int length) {
        // 限制最大10位
        if (length < 1 || length > 10) {
            length = 10;
        }

        StringBuilder pwd = new StringBuilder(length);
        // 先保证四类字符至少各出现一个
        pwd.append(LOWERS.charAt(RANDOM.nextInt(LOWERS.length())));
        pwd.append(UPPERS.charAt(RANDOM.nextInt(UPPERS.length())));
        pwd.append(NUMS.charAt(RANDOM.nextInt(NUMS.length())));
        pwd.append(SYMBOLS.charAt(RANDOM.nextInt(SYMBOLS.length())));

        // 剩余位数随机填充
        for (int i = 4; i < length; i++) {
            int index = RANDOM.nextInt(ALL_CHARS.length());
            pwd.append(ALL_CHARS.charAt(index));
        }

        // 打乱顺序，避免前四位固定类型
        return shuffleStr(pwd.toString());
    }

    /**
     * 打乱字符串顺序
     */
    private static String shuffleStr(String str) {
        char[] arr = str.toCharArray();
        for (int i = arr.length - 1; i > 0; i--) {
            int j = RANDOM.nextInt(i + 1);
            char temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
        return new String(arr);
    }
}
