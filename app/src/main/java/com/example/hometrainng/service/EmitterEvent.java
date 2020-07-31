package com.example.hometrainng.service;

import com.example.hometrainng.listener.IEmitterListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import io.socket.client.Manager;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * @Package com.example.hometrainng.service
 * @Description java类作用描述
 * @CreateDate: 2020/7/8 15:18
 */
public class EmitterEvent {
    private Map<String, Emitter.Listener> emitterEventMap = new HashMap<>();

    public EmitterEvent() {
        emitterEventMap.put(Manager.EVENT_TRANSPORT, null);
        emitterEventMap.put(Socket.EVENT_CONNECT_ERROR, null);
        emitterEventMap.put(Socket.EVENT_CONNECT_TIMEOUT, null);
        emitterEventMap.put(Socket.EVENT_CONNECT, null);
        emitterEventMap.put(Socket.EVENT_DISCONNECT, null);
        emitterEventMap.put(Socket.EVENT_ERROR, null);
        emitterEventMap.put(Socket.EVENT_RECONNECT, null);
        emitterEventMap.put(Socket.EVENT_RECONNECT_ATTEMPT, null);
        emitterEventMap.put(Socket.EVENT_RECONNECT_ERROR, null);
        emitterEventMap.put(Socket.EVENT_RECONNECT_FAILED, null);
        emitterEventMap.put(Socket.EVENT_RECONNECTING, null);

//        emitterEventMap.put(IConstants.LOGIN, null);
//        emitterEventMap.put(IConstants.NEW_MESSAGE, null);
//        emitterEventMap.put(IConstants.USER_JOINED, null);
//        emitterEventMap.put(IConstants.USER_LEFT, null);
//        emitterEventMap.put(IConstants.TYPING, null);
//        emitterEventMap.put(IConstants.STOP_TYPING, null);

    }

    public void onEmitterEvent(Socket socket, final IEmitterListener emitterListener) {
        Iterator iterator = emitterEventMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            final String event = (String) entry.getKey();
            Emitter.Listener listener;
            listener = new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    emitterListener.emitterListenerResult(event, args);
                }
            };
            emitterEventMap.put(event, listener);
            socket.on(event, listener);

        }
    }

    public void offEmitterEvent(Socket socket) {
        Iterator iterator = emitterEventMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            String event = (String) entry.getKey();
            Emitter.Listener el = (Emitter.Listener) entry.getValue();
            socket.off(event, el);
        }
    }
}
