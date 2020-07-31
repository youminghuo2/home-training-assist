package com.example.hometrainng.tools;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class MqConsume {
    private String token;
    private byte[] body;

    public MqConsume(String token, byte[] body) {
        this.token = token;
        this.body = body;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public void execute() throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Map<String, Object> map = Utils.changeByteToMap(getBody());
        String type = map.get("type").toString();
        String entity = map.get("entity").toString();
        String className = "com.example.hometrainng.db.executor." + entity.substring(0, 1).toUpperCase() + entity.substring(1) + "Executor";
        Class cls = Class.forName(className);
        Method method = cls.getDeclaredMethod("executor", new Class[]{String.class, String.class, String.class});
        method.invoke(cls.newInstance(), type, token, map.get("id").toString());
    }
}
