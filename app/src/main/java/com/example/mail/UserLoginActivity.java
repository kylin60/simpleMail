package com.example.mail;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class UserLoginActivity extends AppCompatActivity{
    EditText e1;
    EditText e2;
    ImageView m1, m2;
    Handler hd;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.user_login);
        hd = new Handler() {
            @Override
            public void publish(LogRecord logRecord) {
                String msg = logRecord.getMessage();
                if( msg.equals("200")){
                    pd.dismiss();
                    finish();
                }
            }

            @Override
            public void flush() {

            }

            @Override
            public void close() throws SecurityException {

            }
        };
        Init();
    }

    private void Init() {
        // TODO Auto-generated method stub
        e1 = (EditText) findViewById(R.id.e_mail);
        e2 = (EditText) findViewById(R.id.e_password);
        m1 = (ImageView) findViewById(R.id.i_del_mail);
        m2 = (ImageView) findViewById(R.id.i_del_password);
        // 添加清楚按钮监听器
        EditTextClearTools.addclerListener(e1, m1);
        EditTextClearTools.addclerListener(e2, m2);
    }

    public void Login(View view) {
        e1= (EditText) findViewById(R.id.e_mail);
        e2= (EditText) findViewById(R.id.e_password);
        String sender = e1.getText().toString();
        String password = e2.getText().toString();
        if(sender.length() == 0||password.length() == 0){
            Toast.makeText(this,"输入不能为空",Toast.LENGTH_SHORT).show();
        }
        else{
            pd = ProgressDialog.show(UserLoginActivity.this, "登录", "正在登录");
            new Thread(){
                public void run() {
                    try {
                        Context context=UserLoginActivity.this;
                        String sender = e1.getText().toString();
                        String password = e2.getText().toString();

                        // 截取出“cnsmtp01”并转码
                        String user = android.util.Base64.
                                encodeToString(sender.substring(0, sender.indexOf("@")).getBytes(), Base64.NO_WRAP);
                        // 加密 “computer”
                        String pass = android.util.Base64.
                                encodeToString(password.getBytes(), Base64.NO_WRAP);
                        // 连接服务端
                        Socket socket = new Socket("smtp.163.com", 25);
                        InputStream inputStream = socket.getInputStream();
                        OutputStream outputStream = socket.getOutputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                        PrintWriter writter = new PrintWriter(outputStream,true);

                        writter.println("HELO 1");
                        reader.readLine();
                        writter.println("auth login");
                        reader.readLine();
                        writter.println(user);
                        reader.readLine();
                        writter.println(pass);
                        reader.readLine();
                        writter.println("mail from:<" + sender +">");
                        String buffer=reader.readLine();
                        if(buffer.contains("235 Authentication successful")){
                            LogRecord lr = new LogRecord(Level.INFO, "OK");
                            // 成功状态码
                            lr.setMessage("200");
                            hd.publish(lr);
                            // 登录成功后跳转
                            Intent intent = new Intent(UserLoginActivity.this, MainActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("account",sender);
                            bundle.putString("password",password);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        } else{
                            // eat it
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }
}
