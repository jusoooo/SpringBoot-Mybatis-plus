package top.jimxu.entity.domain;

import lombok.Data;

@Data
public class R<T> {



    private static final long serialVersionUID = 1L;

    private String code = "0000";


    /**
     * 描述信息
     */
    private String message = "";

    /**
     * //数据
     */
    private T result;

    private R() {
    }

    private R(String message) {
        this.message = message;
    }

    private R(String code, String message) {
        this.code = code;
        this.message = message;
    }

    private R(String code, String message, T result) {
        this.result = result;
        this.code = code;
        this.message = message;
    }


    public static R success() {
        return new R("成功");
    }

    public static R success(String message) {
        return new R(message);
    }

    public static <T> R success(T result) {
        return new R("0000", "成功", result);
    }

    public static R error() {
        return new R("9999", "失败");
    }

    public static R error(String message) {
        return new R("9999", message);
    }

    public static R error(String code, String message) {
        return new R(code, message);
    }

    public static <T> R data(T result) {
        return new R("0000", "成功", result);
    }

    public static <T> R data(String message, T result) {
        return data("0000", message, result);
    }

    public static <T> R data(String code, String message, T result) {
        return new R(code, message, result);
    }

    public static <T> R toResult(int rows) {
        return rows > 0 ? success() : error();
    }


}
