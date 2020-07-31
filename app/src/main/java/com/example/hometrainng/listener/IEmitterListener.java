package com.example.hometrainng.listener;

/**
 * @Package com.example.hometrainng.listener
 * @Description java类作用描述
 * @CreateDate: 2020/7/8 14:18
 */
public interface IEmitterListener {
    //监听结果
    void emitterListenerResult(String key, Object... args);
    //请求结果
    void requestSocketResult(String key, Object... args);


}
