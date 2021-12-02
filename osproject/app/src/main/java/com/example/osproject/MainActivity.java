package com.example.osproject;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


//import in.co.bhadreshtech.osproject.R;

public class MainActivity extends AppCompatActivity {

    private static OutputStream os;
    private static BufferedReader remoteIn;
    private static String filename;
    private static BufferedInputStream bis;
    private static Socket socket ;
    private static final int SELECT_PICTURE = 1;
    private static String sts = new String("");

    private String selectedFilePath;
   // private ImageView img;
    private EditText ip_edt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ip_edt = (EditText) findViewById(R.id.ip_edt);
        System.out.println("34");
        //img = (ImageView) findViewById(R.id.ivPic);
        System.out.println("36");
        ((Button) findViewById(R.id.bBrowse))
                .setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) {
                        System.out.println("40");

                        Intent chooseFile = new Intent();
                        chooseFile.setType("*/*");

                        //android real file picker
                        chooseFile.setAction(Intent.ACTION_GET_CONTENT);

                        chooseFile = Intent.createChooser(chooseFile,"Choose a file");
                        startActivityForResult( chooseFile, 8778);
                     /*   ActivityResultLauncher<Intent> launcher =  registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),new ActivityResultCallback<ActivityResult>(){
                            @Override
                            public void onActivityResult(ActivityResult result) {
                                if(result.getResultCode() == Activity.RESULT_OK){
                                    Intent data = result.getData();
                                    Uri content_describer =  data.getData();
                                    selectedFilePath = content_describer.getPath();

                                    TextView path = (TextView) findViewById(R.id.tvPath);
                                    path.setText("File Path : " + selectedFilePath);
                                    filename = content_describer.getLastPathSegment();

                                    TextView fname = (TextView) findViewById(R.id.tvfilename);
                                    fname.setText("File Name: "+filename);

                                    //img.setImageURI(selectedImageUri);
                                }
                            }
                        }); */

                       // launcher.launch(chooseFile);

                    //    startActivityForResult(
                     //           Intent.createChooser(chooseFile, "Select Picture"),
                       //         SELECT_PICTURE);
                        System.out.println("47");
                    }
                });
        ;
        System.out.println("51");
        Button send = (Button) findViewById(R.id.bSend);
        final TextView status = (TextView) findViewById(R.id.tvStatus);

    //    ip_edt.setText("192.168.55.26");
        ip_edt.setText("192.168.158.26");

        send.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                //TextView tv_sts = (TextView) findViewById(R.id.tvStatus);
               // tv_sts.setText("1");

               Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                 //           tv_sts.setText("2");
                            socket = new Socket(ip_edt.getText().toString(), 8010);
                            remoteIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            //                tv_sts.setText("3");
                         //   File myFile = new File(selectedFilePath.trim());
              //              tv_sts.setText("13");

                            bis = new BufferedInputStream(new FileInputStream( selectedFilePath));
                        ///    bis = new BufferedInputStream(new FileInputStream(Environment.getExternalStorageDirectory() +"//Notebloc_PDF//CS19B1020_DS_CT02.pdf"));
                //            tv_sts.setText(selectedFilePath);
                        //    bis = new BufferedInputStream(new FileInputStream(myFile));
                  //          tv_sts.setText(Environment.getExternalStorageDirectory() +"//Notebloc_PDF//CS19B1020_DS_CT02.pdf");
                      //      int  file_size = (int) myFile.length();
                            int file_size = bis.available();
                            byte[] b = new byte[100];
                            filename = new String(selectedFilePath.substring(selectedFilePath.lastIndexOf("/")+1));
                            System.arraycopy(filename.getBytes(StandardCharsets.UTF_8 ), 0, b, 0, filename.length());
                            os = socket.getOutputStream();
                            BufferedOutputStream bos = new BufferedOutputStream(os);
                            bos.write(b,0,100);
                            bos.flush();
                            byte[] s = intToByteArray(file_size);
                            bos.write(s,0,4);
                            bos.flush();

                       /*     byte[] file_byte = new byte[file_size];
                            bis.read(file_byte,0,file_size);
                            bos.write(file_byte,0,file_size);
                            bos.flush(); */
                            int count;
                            byte[] file_byte = new byte[1024];
                            while((count =bis.read(file_byte)) > 0){
                                bos.write(file_byte,0,count);
                                bos.flush();

                            }


String sd;
                            while( (sd = remoteIn.readLine()) == null );

                                bis.close();
                                bos.close();
                                remoteIn.close();
                                socket.close();

                    //        tv_sts.setText(sd);
//tv_sts.setText("g");

                            //socket.close();
                        } catch (IOException e) {
                            TextView stts = (TextView) findViewById(R.id.summa);
                            stts.setText(e.getMessage());
                            e.printStackTrace();
                        }
                        System.out.println("Connecting...");

                    }
                });
                thread.start();
                // start sending file


            }
        });
    }



    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 8778) {
                Uri content_describer = data.getData();
              ////  selectedFilePath = content_describer.getPath();
                selectedFilePath = getPathFromUri.getPathFromUri(MainActivity.this,content_describer);
           //  selectedFilePath = getPath(content_describer);

                TextView path = (TextView) findViewById(R.id.tvPath);
                path.setText("File Path : " + selectedFilePath);
                filename = content_describer.getLastPathSegment();

                TextView fname = (TextView) findViewById(R.id.tvfilename);
                fname.setText("File Name: " + filename);

                /*
                Uri selectedImageUri = data.getData();
                selectedImagePath = getPath(selectedImageUri);
                TextView path = (TextView) findViewById(R.id.tvPath);
                path.setText("Image Path : " + selectedImagePath);
                img.setImageURI(selectedImageUri);

                 */
            }
        }
    }


    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


    public static byte[] intToByteArray(int a)
    {
        return new byte[] {
                (byte) ((a >> 24) & 0xFF),
                (byte) ((a >> 16) & 0xFF),
                (byte) ((a >> 8) & 0xFF),
                (byte) (a & 0xFF)
        };
    }

}

