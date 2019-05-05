package com.uetty.cloud.utils;

/**
 * 代码块耗时计算
 * <p>
 * 原理： 通过实现AutoCloseable，在关闭资源的时候进行计算与输出
 * <p>
 * 使用方法： try(){}  在try中创建MethodCount对象，try结束后会自动回收资源
 *
 * 代码段推荐直接在try中new，不修改的代码可以使用Runnable
 */
public class MethodCount implements AutoCloseable {

    /**
     * 开始时间
     */
    private Long startTime;

    public MethodCount() {
        this.startTime = System.currentTimeMillis();
    }

    public MethodCount(Runnable runnable) {
        this.startTime = System.currentTimeMillis();
        try (MethodCount c = this) {
            runnable.run();
        }
    }

    public static void count(Runnable runnable){
        new MethodCount(runnable);
    }

    @Override
    public void close() {
        //堆栈信息
        StackTraceElement ste = new Throwable().getStackTrace()[1];
        //在idea中这样可以快速定位代码
        System.out.println(ste.getClassName() + "(" + ste.getFileName() + ":" + ste.getLineNumber() + ")" + "[耗时]：" + (System.currentTimeMillis() - startTime) + "ms");
    }

    public static void main(String[] args) {
         MethodCount.count(()-> System.out.println(1));
    }
}
