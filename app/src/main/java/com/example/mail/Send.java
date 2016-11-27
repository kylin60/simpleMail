package com.example.mail;

import android.app.Notification;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
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
import java.util.HashMap;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;


public class Send extends AppCompatActivity {
    private boolean sendLock;
    private ProgressDialog pd;
    private String account;
    private String pass;

    private Handler handler = new Handler() {
        @Override
        public void publish(LogRecord logRecord) {
            String msg = logRecord.getMessage();
            if( msg.equals("200")){
                pd.dismiss();
            }
        }

        @Override
        public void flush() {

        }

        @Override
        public void close() throws SecurityException {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        Bundle bundle = this.getIntent().getExtras();
        account = bundle.getString("account");
        pass = bundle.getString("password");

        Button send = (Button)findViewById(R.id.btn_send);
        Button back = (Button)findViewById(R.id.imageView);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    SendEmail();
                }catch (IOException e){
                    Toast.makeText(Send.this,"发送失败!",Toast.LENGTH_SHORT).show();
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Back();
            }
        });

        sendLock = false;
    }

    public void SendEmail() throws  IOException{
        pd = ProgressDialog.show(Send.this, "发送", "正在发送");
        new Thread() {
            @Override
            public void run() {
                try {
                    SubSend();
                    LogRecord lr = new LogRecord(Level.INFO, "OK");
                    // 成功状态码
                    lr.setMessage("200");
                    handler.publish(lr);
                } catch (IOException e) {
                    Toast.makeText(Send.this,"发送失败!",Toast.LENGTH_SHORT).show();
                }
            }
        }.start();
        /*
        if(sendLock) {
            Toast.makeText(Send.this, "发送成功", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Send.this, MainActivity.class);
            startActivity(intent);
            Send.this.finish();
        }*/
    }

    void SubSend() throws IOException{
        EditText e_sender = (EditText)findViewById(R.id.e_sender);
        EditText e_subject = (EditText)findViewById(R.id.e_subject);
        EditText e_content = (EditText)findViewById(R.id.e_content);

        // 账号密码
        String sender = account ;
        String password = pass;

        // base64转码
        String user = android.util.Base64.
                encodeToString(sender.substring(0, sender.indexOf("@")).getBytes(), Base64.NO_WRAP);
        String pass = android.util.Base64.
                encodeToString(password.getBytes(),Base64.NO_WRAP);

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
        reader.readLine();
        writter.println("rcpt to:<" + e_sender.getText().toString() +">");
        reader.readLine();
        writter.println("data");
        reader.readLine();
        writter.println("subject:"+e_subject.getText().toString());
        writter.println("from:" + sender);
        writter.println("to:" + e_sender.getText().toString());
        // 报文格式
        writter.println("Content-Type: text/plain;charset=\"UTF-8\"");
        writter.println();
        // 添加邮件内容
        writter.println(e_content.getText().toString());
        writter.println(".");
        writter.println("");
        reader.readLine();
        writter.println("rset");
        reader.readLine();
        writter.println("quit");
        reader.readLine();
    }

    public void Back() {
        finish();
        /*
        Intent intent = new Intent(Send.this, MainActivity.class);
        startActivity(intent);
        Send.this.finish();
        */
    }
}
