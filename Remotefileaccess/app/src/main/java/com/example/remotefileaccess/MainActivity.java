package com.example.remotefileaccess;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.appcompat.app.AppCompatActivity;

import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    // declaring required variables
    private Socket socket = new Socket();
    private PrintWriter printwriter;
    private EditText textField_opt;
    private EditText textField_dname;
    private EditText ip;
    private EditText cmd;
    private TextView status;
    private TextView log;
    private TextView Conn;
    private Button send;
    private Button connect;
    private String option;
    private String directory_name;
    private String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // reference to the text field
        textField_opt = (EditText) findViewById(R.id.et_opt);
        textField_dname = (EditText) findViewById(R.id.et_dname);

        // reference to the send button
        send = (Button) findViewById(R.id.bSend);

        status = (TextView) findViewById(R.id.tvstatus);

        log = (TextView) findViewById(R.id.tvlog);

        ip = (EditText) findViewById(R.id.et_ip);

        cmd = (EditText) findViewById(R.id.et_cmd);

        Conn = (TextView) findViewById(R.id.tvconn);

        connect = (Button) findViewById(R.id.bConnect);

        new Thread(new Connectioncheck()).start();

        status.setMovementMethod(new ScrollingMovementMethod());

//// for nested scrollview
        findViewById(R.id.scrollview).setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                status.getParent().requestDisallowInterceptTouchEvent(false);

                return false;
            }
        });

        status.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                status.getParent().requestDisallowInterceptTouchEvent(true);

                return false;
            }
        });
////

status.setHeight(600);

        send.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                // get the text message on the text field
                option = textField_opt.getText().toString();
                directory_name = textField_dname.getText().toString();
                query = option + "\n" + directory_name + "\n" + cmd.getText().toString();
                // start the Thread to send query to server
                new Thread(new Sender(query)).start();

            }
        });

        connect.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // start the Thread to connect to server
                new Thread(new connectSocket()).start();

            }
        });


    }

    // the Sender class performs
    // the query sending networking operations
    class Sender implements Runnable {
        private final String message;

        Sender(String message) {
            this.message = message;
        }

        @Override
        public void run() {
            try {

                //     new Thread(new connectSocket()).start();
                socket = new Socket(ip.getText().toString(), 8080); // connect to server
                runOnUiThread(()->status.setText(""));
                //sending message
                printwriter = new PrintWriter(socket.getOutputStream(), true);
                printwriter.write(message); // write the message to output stream

                printwriter.flush();
                //    printwriter.close();


                //getting status

                BufferedReader in =
                        new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
                String msg;
                String fullmsg = "";

                while ((msg = in.readLine()) != null && socket.isConnected()) {
                    fullmsg += msg;
                    final String fMsg = fullmsg;
                    runOnUiThread(() -> {
                        status.setText(fMsg + "\n");
                    });
                }

                // closing the connection
                socket.close();

            } catch (IOException e) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                runOnUiThread(()->log.setText(sw.toString()));
                try {
                    socket.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }

            // updating the UI
            runOnUiThread(() -> {
                textField_opt.setText("");
                textField_dname.setText("");
            });
        }
    }

    class connectSocket implements Runnable {


        @Override
        public void run() {
            try {
                if (!Connect())
                    socket = new Socket(ip.getText().toString(), 8080); // connect to server
            } catch (SocketException e) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                log.setText(sw.toString());
            } catch (UnknownHostException e) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                log.setText(sw.toString());
            } catch (IOException e) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                log.setText(sw.toString());
            }
        }
    }


    class Connectioncheck implements Runnable {

        @Override
        public void run() {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        Connect();
                    } catch (IOException e) {
                        StringWriter sw = new StringWriter();
                        PrintWriter pw = new PrintWriter(sw);
                        e.printStackTrace(pw);
                        log.setText(sw.toString());
                    }
                }
            }, 0, 3000);


        }
    }

    public boolean Connect() throws IOException {
        //boolean s = InetAddress.getByName(ip.getText().toString()).isReachable(3000);
        //  boolean s = socket.isClosed();
        SocketAddress s = socket.getRemoteSocketAddress();

        if (s == null) {
            Conn.setText("Not Connected");
            Conn.setBackgroundColor(0xFFFF3A32);
            return false;
        } else {
            Conn.setText("Connected");
            Conn.setBackgroundColor(0xFF37FF77);
            return true;
        }

    }

}



