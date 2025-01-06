package com.example.bbs.result;

//封装的返回数据类型不确定，故使用泛型
public class CommonResult<T> {
	//作为通用的返回类，故应包含三部分
    private long code;//返回状态码
    private String message;//返回信息
    private T data;//返回数据，将要给客户端的数据

    protected CommonResult(long code,String message,T data){
        this.code=code;
        this.message=message;
        this.data=data;
    }

    @Override
    public String toString() {
        return "CommonResult{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }

    /**
     * 成功返回的结果
     */
    public static <T> CommonResult success(){
        return new CommonResult(ResultCode.SUCCESS.getCode(),ResultCode.SUCCESS.getMessage(),null);
    }
    /**
     * 成功返回的结果
     * @param data  获取的数据
     */
    public static <T> CommonResult success(T data){
        return new CommonResult(ResultCode.SUCCESS.getCode(),ResultCode.SUCCESS.getMessage(),data);
    }

    /**
     *
     * @param message   自定义提示信息
     * @param data  获取的数据
     */
    public static <T> CommonResult success(String message,T data){
        return new CommonResult(ResultCode.SUCCESS.getCode(),message,data);
    }

    /**
     * 失败返回结果
     */
    public static <T> CommonResult failed(){
        return new CommonResult(ResultCode.FAILED.getCode(),ResultCode.FAILED.getMessage(),null);
    }

    /**
     * 失败返回结果
     * @param message   自定义信息
     */
    public static <T> CommonResult failed(String message){
        return new CommonResult(ResultCode.FAILED.getCode(),message,null);
    }

    /**
     * 已存在记录返回结果
     * @param message   自定义信息
     */
    public static <T> CommonResult exist(String message){
        return new CommonResult(ResultCode.EXIST.getCode(),message,null);
    }


    /**
     * 参数验证失败返回结果
     */
    public static <T> CommonResult validateFailed(){
        return new CommonResult(ResultCode.VALIDATE_FAILED.getCode(),ResultCode.VALIDATE_FAILED.getMessage(),null);
    }

    /**
     * 参数验证失败返回结果
     * @param message   自定义提示信息
     */
    public static <T> CommonResult validateFailed(String message){
        return new CommonResult(ResultCode.VALIDATE_FAILED.getCode(),message,null);
    }

    /**
     * 未授权返回结果
     *
     */
    public static <T> CommonResult forbidden(T data){
        return new CommonResult(ResultCode.FORBIDDEN.getCode(),ResultCode.FORBIDDEN.getMessage(),data);
    }

    /**
     * 未登录返回结果
     * @param data  返回的数据
     */
    public static <T> CommonResult unauthorized(T data){
        return new CommonResult(ResultCode.UNAUTHORIZED.getCode(),ResultCode.UNAUTHORIZED.getMessage(),null);
    }

    public long getCode() {
        return code;
    }

    public void setCode(long code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
