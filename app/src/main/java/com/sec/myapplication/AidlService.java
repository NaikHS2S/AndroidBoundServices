package com.sec.myapplication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import androidx.annotation.Nullable;

import java.util.Random;

public class AidlService extends Service {

    IService.Stub iService = new IService.Stub() {
        @Override
        public int callAidlFromMe() throws RemoteException {
            return new Random().nextInt(10);
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return iService;
    }
}
