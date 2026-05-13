package com.xfs.xfsbackend.common;



import lombok.Data;

/**
 * 统一接口响应对象。
 * Controller 层统一返回该结构，前端通过 code、msg、data 判断请求是否成功以及读取业务数据。
 */
@Data
public class Result<T> {
    private Integer code;
    private String msg;
    private T data;

    /**
     * 构造成功响应。
     * code 固定为 200，data 存放真正返回给前端的数据。
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMsg("请求成功");
        result.setData(data);
        return result;
    }

    /**
     * 构造失败响应。
     * 当前项目业务失败统一返回 code=500，msg 存放给前端展示的错误原因。
     */
    public static Result error(String msg) {
        Result result = new Result();
        result.setCode(500);
        result.setMsg(msg);
        return result;
    }
}
