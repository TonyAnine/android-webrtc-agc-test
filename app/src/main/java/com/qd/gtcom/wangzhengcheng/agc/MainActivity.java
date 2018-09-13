package com.qd.gtcom.wangzhengcheng.agc;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class MainActivity extends AppCompatActivity {


    //LOCAL_CPPFLAGS += -std=c++11
//    LOCAL_CFLAGS += -DWEBRTC_POSIX
//    APP_STL:=gnustl_static
    private static final boolean DEBUG = true;
    int micOutLevel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (DEBUG){
            doAgc();
//            recordPcm(true);
        }
    }

    private void doAgc (){

        try{

            AgcUtils agcUtils = new AgcUtils();
            agcUtils.setAgcConfig(3,20,1).prepare();


            FileInputStream fInt = new FileInputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + "/99999-wzc-test.pcm");
            FileOutputStream fOut = new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath() +"/wzc-55555.pcm");
            byte[] buffer = new byte[160];
            int bytes;


            while((bytes = fInt.read(buffer)) != -1){
                short[] data = new short[80];
                short[] outData = new short[80];
                short[] processData = new short[80];
                ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(data);
                Log.e("aaaaa" ,"working " + data[50]);
//                short a;
//                for (int i = 0; i<bytes/2 ; i ++){
//                    a = data[i];
//                    agcUtils.agcProcess(a,0,80,outData,0,micOutLevel,0,0);
//                    processData[i] = outData;
//                }

                int aa = agcUtils.agcProcess(data,0,80,outData,0,micOutLevel,0,0);
                Log.e("cccccc ", "-====== aa = " + aa);


                fOut.write(shortArrayToByteArry(outData));

            }

            fInt.close();
            fOut.close();





        }catch (Exception e){
            e.printStackTrace();
        }
    }
    // shortתbyte
    public byte[] shortArrayToByteArry(short[] data) {
        byte[] byteVal = new byte[data.length * 2];
        for (int i = 0; i < data.length; i++) {
            byteVal[i * 2] = (byte) (data[i] & 0xff);
            byteVal[i * 2 + 1] = (byte) ((data[i] & 0xff00) >> 8);
        }
        return byteVal;
    }


    private void recordPcm (boolean isRecording){
        try {
            FileOutputStream fOut = new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + "/99999-wzc-test.pcm");
            int recBufSize = AudioRecord.getMinBufferSize(8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
            AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, 8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, recBufSize);

            audioRecord.startRecording();

            byte[] buffer = new byte[160];

            while (isRecording) {
                audioRecord.read(buffer,0,160);
                fOut.write(buffer);

                Log.e("aaaaa","working.....");


            }

            audioRecord.stop();
            fOut.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
