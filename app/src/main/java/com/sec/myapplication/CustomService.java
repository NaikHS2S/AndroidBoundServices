package com.sec.myapplication;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import androidx.annotation.Nullable;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class CustomService extends Service {

    public class CustomBinder extends Binder{
        public CustomService getService(){
            return CustomService.this;
        }
    }

    public IBinder iBinder = new CustomBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public int getData(){
        int value = 10;
        ExecutorService executorService =  Executors.newFixedThreadPool(10);
        Future<Integer> futureData = executorService.submit(new Callable<Integer>() {

            @Override
            public Integer call() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    //Do Nothing
                }
                return new Random().nextInt(10);
            }
        });


        try {
            value = futureData.get();
        } catch (InterruptedException | ExecutionException e) {
          //Do Nothing
        }
        return value;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }
}
