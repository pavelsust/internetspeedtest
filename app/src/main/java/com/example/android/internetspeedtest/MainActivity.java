package com.example.android.internetspeedtest;

import android.annotation.SuppressLint;
import android.icu.math.BigDecimal;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.example.internet_speed_testing.InternetSpeedBuilder;
import com.example.internet_speed_testing.ProgressionModel;
import com.github.anastr.speedviewlib.PointerSpeedometer;

import fr.bmartel.speedtest.SpeedTestReport;
import fr.bmartel.speedtest.SpeedTestSocket;
import fr.bmartel.speedtest.inter.ISpeedTestListener;
import fr.bmartel.speedtest.model.SpeedTestError;

public class MainActivity extends AppCompatActivity {

    PointerSpeedometer pointerSpeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        pointerSpeed = (PointerSpeedometer) findViewById(R.id.pointerSpeedometer);
        new customCall().execute();

        InternetSpeedBuilder builder = new InternetSpeedBuilder(MainActivity.this);
        builder.setOnEventInternetSpeedListener(new InternetSpeedBuilder.OnEventInternetSpeedListener() {
            @Override
            public void onDownloadProgress(int count, final ProgressionModel progressModel) {
                Log.d("SERVER" , ""+progressModel.getDownloadSpeed());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pointerSpeed.speedTo(progressModel.getProgressDownload());
                    }
                });

            }

            @Override
            public void onUploadProgress(int count, ProgressionModel progressModel) {

            }

            @Override
            public void onTotalProgress(int count, ProgressionModel progressModel) {

            }
        });
        builder.start("http://2.testdebit.info/fichiers/1Mo.dat", 1);

    }




    public class customCall extends AsyncTask<Void , Void , Void> {
        SpeedTestSocket speedTestSocket = new SpeedTestSocket();
        @Override
        protected Void doInBackground(Void... voids) {
            speedTestSocket.startFixedDownload("http://www.ovh.net/files/100Mio.dat", 10000);
            //speedTestSocket.startUpload("http://ipv4.ikoula.testdebit.info/", 1000000);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            speedTestSocket.addSpeedTestListener(new ISpeedTestListener() {

                @Override
                public void onCompletion(SpeedTestReport report) {
                    // called when download/upload is complete


                    Log.d("SPEED" , "[COMPLETED] rate in octet/s : " + report.getTransferRateOctet());
                    Log.d("SPEED" , "[COMPLETED] rate in bit/s   : " + report.getTransferRateBit());
                }

                @Override
                public void onError(SpeedTestError speedTestError, String errorMessage) {
                    // called when a download/upload error occur
                }

                @Override
                public void onProgress(float percent, SpeedTestReport report) {
                    // called to notify download/upload progress
                    System.out.println("[PROGRESS] progress : " + percent + "%");
                    System.out.println("[PROGRESS] rate in octet/s : " + report.getTransferRateOctet());
                    System.out.println("[PROGRESS] rate in bit/s   : " + report.getTransferRateBit());

                    Log.d("SPEED" , "[PROGRESS] progress : " + percent + "%");
                    Log.d("SPEED" , "[PROGRESS] rate in octet/s : " + report.getTransferRateOctet());
                    Log.d("SPEED" , "[PROGRESS] rate in bit/s   : " + report.getTransferRateBit());

                    @SuppressLint({"NewApi", "LocalSuppress"}) BigDecimal bd = new BigDecimal(report.getTransferRateBit());
                    @SuppressLint({"NewApi", "LocalSuppress"}) long speed = bd.setScale(0, BigDecimal.ROUND_HALF_UP).longValue();
                    final long finalSpeed = (speed/8192)/100;

                    Log.d("METER_SPEED", ""+finalSpeed);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pointerSpeed.speedTo(finalSpeed);
                        }
                    });

                }
            });

        }
    }




}
