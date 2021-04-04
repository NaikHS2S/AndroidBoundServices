package com.sec.myapplication;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private CustomService customService;
    private Messenger messenger, receiveMessenger;
    private IService iService;

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            customService = ((CustomService.CustomBinder)service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            customService = null;

        }
    };


    ServiceConnection messengerServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            messenger = new Messenger(service);
            receiveMessenger = new Messenger(mHandler);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            messenger = null;
            receiveMessenger = null;

        }
    };

    ServiceConnection aidlServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            iService =  IService.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            iService = null;
        }
    };


    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MessengerService.MSG_SAY_HELLO_2) {
                Toast.makeText(MainActivity.this, "From Messenger: "+msg.arg1, Toast.LENGTH_SHORT).show();
            } else {
                super.handleMessage(msg);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try(BufferedReader br = new BufferedReader(new FileReader(new File("d:/myfile.txt"))) ;)
        {
            String str;
            while ((str=br.readLine())!=null)
            {
                System.out.println(str);
            }
        }
        catch(IOException e) {
            e.printStackTrace();
        }



        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(customService != null) {
                    try {
                        Snackbar.make(view, "From Bind Service: "+customService.getData(), Snackbar.LENGTH_LONG)
                                .setAction("Action :"+ iService.callAidlFromMe(), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                    }
                                }).show();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }

                if(messenger != null){
                    Message message = Message.obtain(null, MessengerService.MSG_SAY_HELLO, 1, 0);
                    message.replyTo = receiveMessenger;
                    try {
                        messenger.send(message);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }

                Intent intent = new Intent(MainActivity.this, SampleBroadCast.class);
                sendBroadcast(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, CustomService.class);
        Intent intentMessenger = new Intent(this, MessengerService.class);
        Intent aidlServiceIntent = new Intent(this, AidlService.class);

        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
        bindService(intentMessenger, messengerServiceConnection, BIND_AUTO_CREATE);
        bindService(aidlServiceIntent, aidlServiceConnection, BIND_AUTO_CREATE);


        class Task extends AsyncTask<Integer, String, Boolean>{

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Boolean doInBackground(Integer... integers) {
                onProgressUpdate("hi", "hello");

                return null;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
            }

            @Override
            protected void onProgressUpdate(String... values) {
                super.onProgressUpdate(values);
            }

            @Override
            protected void onCancelled() {
                super.onCancelled();
            }

        }

        new Task().execute(10);
        new Task().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, 10);


        class MyHandlerThread extends HandlerThread {

            Handler handler;

            public MyHandlerThread(String name) {
                super(name);
            }

            @Override
            protected void onLooperPrepared() {
                handler = new Handler(getLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                        // process incoming messages here
                        // this will run in non-ui/background thread
                    }
                };
            }
        }

        HandlerThread handlerThread = new HandlerThread("MyHandlerThread");
        handlerThread.start();
        Handler handler = new Handler(handlerThread.getLooper());


    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(serviceConnection);
        unbindService(messengerServiceConnection);
        unbindService(aidlServiceConnection);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class SampleBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "BroadcastReceiver Called ", Toast.LENGTH_LONG).show();
        }
    }


}