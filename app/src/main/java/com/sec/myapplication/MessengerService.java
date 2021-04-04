package com.sec.myapplication;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import androidx.annotation.Nullable;
import java.util.Random;

public class MessengerService extends Service {

    public static int MSG_SAY_HELLO = 1 ;
    public static int MSG_SAY_HELLO_2 = 2 ;

    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_SAY_HELLO) {
                Messenger replyMessenger = msg.replyTo;
                Message message = Message.obtain(null, MessengerService.MSG_SAY_HELLO_2,  new Random().nextInt(10), 0);
                try {
                    replyMessenger.send(message);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }else {
                super.handleMessage(msg);
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Messenger messenger = new Messenger(mHandler);
        return messenger.getBinder();
    }
}
