package com.example.mail;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import static java.sql.Types.NULL;

public class MainActivity extends AppCompatActivity {
    public ArrayList<String> subject;
    public int i=0;
    public String s;
    private ProgressDialog pd;
    private Handler hd;
    private String account;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        Bundle bunde = this.getIntent().getExtras();
        account = bunde.getString("account");
        password = bunde.getString("password");

        try {
            DisplayMail();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void DisplayMail() throws Exception{
        Context mContext = MainActivity.this;
        LinkedList<TxtBox> mData = new LinkedList<TxtBox>();
        ListView list_inbox = (ListView) findViewById(R.id.listView);
        TxtAdapter mAdapter = new TxtAdapter(mData, mContext);

        MyThread thread = new MyThread(mContext, mData, list_inbox, mAdapter, hd);
        thread.start();
        try{
            thread.join();
        }catch (Exception e){
            e.printStackTrace();
        }
        mAdapter = thread.mAdapter;
        list_inbox = (ListView) findViewById(R.id.listView);
        list_inbox.setAdapter(mAdapter);
    }

    public class MyThread extends Thread{
        public Context mContext;
        public LinkedList<TxtBox> mData;
        public ListView list_inbox;
        public TxtAdapter mAdapter;
        public Handler hd;

        MyThread(Context mContext, LinkedList<TxtBox> mData, ListView list_inbox, TxtAdapter mAdapter,Handler hd){
            this.mContext = mContext;
            this.mData = mData;
            this.list_inbox = list_inbox;
            this.mAdapter = mAdapter;
            this.hd = hd;
        }
        @Override
        public void run(){
            LinkedList<TxtBox> mData = new LinkedList<TxtBox>();
            subject = receive();
            while(i < subject.size()) {
                s = subject.get(i++);
                // 截取发送者邮箱字符串
                String sender = s.substring(s.indexOf("<") + 1, s.indexOf(">"));
                // 截取发送日期
                String date = s.substring(s.indexOf("TIME")+4,s.lastIndexOf("time"));
                // 存储链表
                mData.add(new TxtBox(sender, date, sender.substring(0, 1).toUpperCase()));
            }
            // 装入适配器
            mAdapter = new TxtAdapter(mData, mContext);
        }
    }

    public void Send(View view) {
        Intent intent = new Intent(MainActivity.this, Send.class);
        Bundle bundle = new Bundle();
        bundle.putString("account", account);
        bundle.putString("password", password);
        intent.putExtras(bundle);
        startActivityForResult(intent,NULL);
    }

    public ArrayList<String> receive ()
    {
        ArrayList<String> subject = new ArrayList<String>();
        Socket client = null;
        try
        {
            // 创建一个连接到POP3服务程序的套接字
            client = new Socket ("pop.163.com", 110);
            InputStream is = client.getInputStream ();
            // 创建一个BufferedReader对象，以便从套接字读取输出
            BufferedReader bufferSockReader = new BufferedReader (new InputStreamReader (is));
            OutputStream os = client.getOutputStream ();
            // 创建一个PrintWriter对象，以便向套接字写入内容
            PrintWriter bufferSockWriter = new PrintWriter (os, true);

            // 读取握手信息
            bufferSockReader.readLine();
            // 用户标识命令
            String cmd = "user " + account;

            // 写入输入流
            bufferSockWriter.println (cmd);
            // 读取回应消息
            bufferSockReader.readLine();
            // 用户密码写入输入流
            bufferSockWriter.println ("pass " + password);
            // 读取回应消息
            bufferSockReader.readLine();
            // 命令写入输入流
            bufferSockWriter.println ("stat");
            // 读取回应消息
            String reply = bufferSockReader.readLine ();

            for(int i = 1;i < 1000;i++) {
                // 邮件请求命令
                cmd = "retr "+i;
                // 命令写入输入流
                bufferSockWriter.println(cmd);
                // 读取回应消息
                reply = bufferSockReader.readLine();
                // 判断请求是否成功
                if(reply.contains("-ERR Unknown message"))
                    break;

                // 当某一行只有"."元素时终止
                // 套接字读出的输出就是邮件的内容
                if ( cmd.toLowerCase().startsWith("retr") ) {
                    int count = 0;
                    // 一条邮件的所有内容
                    String content = "";
                    do {
                        reply = bufferSockReader.readLine();
                        System.out.println("S:" + reply);
                        if( count == 2){
                            // 给第三行时间内容做标识位
                            content = content +  "TIME" + reply + "time";
                        }else {
                            content = content + reply;
                        }
                        if (reply.length() > 0) {
                            if (reply.equals(".")) {
                                subject.add(content);
                                break;
                            }
                        }
                        count++;
                        // 邮件报文前6行
                    } while (true);
                }
            }
            // 退出命令写入输入流
            bufferSockWriter.println ("quit");
        }
        catch (IOException e)
        {
            System.out.println (e.toString ());
        }
        finally
        {
            try
            {  if (client != null)
                client.close ();
            }
            catch (IOException e)
            {
                System.out.println (e.toString ());
            }
        }
        return subject;
    }
}
