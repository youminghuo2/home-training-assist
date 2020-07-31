package com.example.hometrainng.service;

import android.app.Service;
import android.text.TextUtils;
import android.util.Log;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ProcessUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.Utils;
import com.example.hometrainng.activity.BaseActivity;
import com.example.hometrainng.activity.MainHomeActivity;
import com.example.hometrainng.events.MessageEvent;
import com.example.hometrainng.listener.IEmitterListener;
import com.example.hometrainng.retrofit.Constants;
import com.example.hometrainng.tools.PLog;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

import java.net.URISyntaxException;
import java.util.Arrays;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.engineio.client.transports.WebSocket;

public class RoomService {

    private static final String TAG = "RoomService";

    private Socket socket;
    private String callee;
    private String caller;
    private String roomName;
    private Boolean currentConnectState = true;
    private Boolean disConnectedFlag = true;

    private String[] transports = new String[]{WebSocket.NAME};

    private int timeout = -1;
    private boolean reconnection = true;
    private int reconnectionAttempts = 100;
    private int reconnectionDelay = 3000;
    private int reconnectionDelayMax = 3000;

    private boolean forceNew = false;
    private IEmitterListener emitterListener;
    private String socketHost = null;


    public String getCallee() {
        return callee;
    }

    public void setCallee(String callee) {
        this.callee = callee;
    }

    public String getCaller() {
        return caller;
    }

    public void setCaller(String caller) {
        this.caller = caller;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    private static class RoomHolder {
        private static RoomService INSTANCE = new RoomService();
    }

    public static RoomService getInstance() {
        return RoomHolder.INSTANCE;
    }


    private void initEmitterEvent(IEmitterListener iEmitterListener){
        if (emitterListener==null){
            emitterListener=new IEmitterListener() {
                @Override
                public void emitterListenerResult(String key, Object... args) {

                }

                @Override
                public void requestSocketResult(String key, Object... args) {

                }
            };

        }
    }



    public void init(String callee) {
        IO.Options options = new IO.Options();
        options.timeout = timeout;
        options.reconnection = reconnection;
        options.reconnectionAttempts = reconnectionAttempts;
        options.reconnectionDelay = reconnectionDelay;
        options.reconnectionDelayMax = reconnectionDelayMax;
        options.forceNew = forceNew;
        options.transports = transports;
        try {
            socket = IO.socket(Constants.ROOM_URL,options);
            initEmitterEvent(emitterListener);
            socket.connect();
            setCallee(callee);
            if (getCallee().equals("-1")) {
                return;
            }
            socket.emit(Constants.ROOM_EVENT_LOGIN, Constants.ROOM_EVENT_CONNECTION_KEY, Constants.ROOM_USER_ROLE, getCallee());
            socket.on(Constants.ROOM_EVENT_CONNECT, args -> {
                socket.emit(Constants.ROOM_EVENT_REFRESH, Constants.ROOM_USER_ROLE, getCallee());
                PLog.d("RoomService", "connect" + socket.id());
                if (!currentConnectState) {
                    currentConnectState = true;
                    EventBus.getDefault().post(new MessageEvent(Constants.ROOM_EVENT_REPEAT_JOIN_MEETING, Constants.ROOM_TYPE));
                }

            });
            socket.on(Constants.ROOM_EVENT_DISCONNECT, args -> {
                PLog.d("RoomService", "disconnect" + socket.id());
                currentConnectState = false;
                if (disConnectedFlag) {
                    EventBus.getDefault().post(new MessageEvent(Constants.ROOM_MSG_EVENT_NETWORK_CLOSE, Constants.ROOM_TYPE));
                }
            });
            socket.on(Constants.ROOM_EVENT_GO_AWAY, args -> {
                EventBus.getDefault().post(new MessageEvent(Constants.ROOM_MSG_EVENT_GO_AWAY, Constants.ROOM_TYPE));
            });
            socket.on(Constants.ROOM_EVENT_OUT_AGE, args -> {
                EventBus.getDefault().post(new MessageEvent(Constants.ROOM_MSG_EVENT_OUT_AGE, Constants.ROOM_TYPE));
            });
            socket.on(Constants.ROOM_EVENT_STOP_CALL, args -> {
                EventBus.getDefault().post(new MessageEvent(Constants.ROOM_EVENT_DISABLE_START_BUTTON, Constants.ROOM_TYPE));
            });
            socket.on(Constants.ROOM_EVENT_CALL, args -> {
                EventBus.getDefault().post(new MessageEvent(Constants.ROOM_EVENT_ENABLE_START_BUTTON, Constants.ROOM_TYPE));
            });
            socket.on(Constants.ROOM_EVENT_CALLING, args -> {
                PLog.d("RoomService", "calling" + Arrays.asList(args));
                setRoomName(String.valueOf(args[0]));
                setCaller(String.valueOf(args[1]));
                EventBus.getDefault().post(new MessageEvent(Constants.ROOM_EVENT_ENABLE_START_BUTTON, Constants.ROOM_TYPE, String.valueOf(args[1])));
//                if (!AppUtils.isAppForeground()){
//                    AppUtils.launchApp("com.example.hometrainng");
//                }

            });
            socket.on(Constants.ROOM_EVENT_JOINED, args -> {
                EventBus.getDefault().post(new MessageEvent(Constants.ROOM_EVENT_C_PEER_DATA, Constants.ROOM_TYPE));
            });
            socket.on(Constants.ROOM_EVENT_OTHER_JOIN, args -> {
                EventBus.getDefault().post(new MessageEvent(Constants.ROOM_EVENT_OTHER_JOIN, Constants.ROOM_TYPE));
                EventBus.getDefault().post(new MessageEvent(Constants.ROOM_EVENT_REPEAT_JOIN_MEETING_AGAIN, Constants.ROOM_TYPE));
                EventBus.getDefault().post(new MessageEvent(Constants.ROOM_EVENT_CREATE_DATA_CHANEL, Constants.ROOM_TYPE));
                EventBus.getDefault().post(new MessageEvent(Constants.ROOM_EVENT_CREATE_OFFER, Constants.ROOM_TYPE));
            });

            socket.on(Constants.ROOM_EVENT_LEAVE, args -> {
                disConnectedFlag = false;
                socket.emit(Constants.ROOM_EVENT_LEAVING, getRoomName(), getCallee());
            });
            socket.on(Constants.ROOM_EVENT_LEAVED, args -> {
                EventBus.getDefault().post(new MessageEvent(Constants.ROOM_MSG_EVENT_LEAVE, Constants.ROOM_TYPE));
            });
            socket.on(Constants.ROOM_EVENT_EMIT_MESSAGE, args -> {
                try {
                    JSONObject data = (JSONObject) args[1];
                    String type = data.getString("type");
                    if (type.equalsIgnoreCase("offer")) {
                        EventBus.getDefault().post(new MessageEvent(Constants.ROOM_EVENT_MSG_RECEIVED_OFFER, Constants.ROOM_TYPE, data));
                    } else if (type.equalsIgnoreCase("answer")) {
                        EventBus.getDefault().post(new MessageEvent(Constants.ROOM_EVENT_ANSWER_RECEIVED, Constants.ROOM_TYPE, data));
                    } else if (type.equalsIgnoreCase("candidate")) {
                        EventBus.getDefault().post(new MessageEvent(Constants.ROOM_EVENT_ICE_CANDIDATE_RECEIVED, Constants.ROOM_TYPE, data));
                    }
                } catch (JSONException e) {
                    PLog.e(TAG, e.toString());
                }
            });
            socket.on(Constants.ROOM_EVENT_CONNECTION_CHECK_DONE, args -> {
                // 可能会收到服务端告知web在线
                if (Constants.ROOM_EVENT_CONNECTION_OK.equals(args[3])) {
                    EventBus.getDefault().post(new MessageEvent(Constants.ROOM_EVENT_CONNECTION_OK, Constants.ROOM_TYPE));
                }
            });
        } catch (URISyntaxException e) {
            PLog.e(TAG, e.toString());
        }

    }

    public void emitCalled() {
        socket.emit(Constants.ROOM_EVENT_CALLED, getRoomName(), getCaller(), getCallee());
    }

    public void emitLeaving() {
        socket.emit(Constants.ROOM_EVENT_LEAVING, getRoomName(), getCallee());
    }

    public void emitConnectionCheck() {
        socket.emit(Constants.ROOM_EVENT_CONNECTION_CHECK, getRoomName(), getCaller(), getCallee());
    }

    public void emitJoin() {
        socket.emit(Constants.ROOM_EVENT_JOIN, getRoomName(), getCallee());
    }

    public void emitMessage(SessionDescription message) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("type", message.type.canonicalForm());
            obj.put("sdp", message.description);
            socket.emit(Constants.ROOM_EVENT_EMIT_MESSAGE, getRoomName(), obj);
        } catch (JSONException e) {
            PLog.e(TAG, e.toString());
        }
    }

    public void emitIceCandidate(IceCandidate iceCandidate) {
        try {
            JSONObject object = new JSONObject();
            object.put("type", "candidate");
            object.put("label", iceCandidate.sdpMLineIndex);
            object.put("id", iceCandidate.sdpMid);
            object.put("candidate", iceCandidate.sdp);
            socket.emit("message", getRoomName(), object);
        } catch (Exception e) {
            PLog.e(TAG, e.toString());
        }
    }

    public void close() {
        socket.disconnect();
        socket.close();
    }

}
