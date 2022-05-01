package com.honestmc.laryngoscopeapp.View.Activity;

import android.os.Handler;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.honestmc.laryngoscopeapp.Adapter.DownloadManagerAdapter;
import com.honestmc.laryngoscopeapp.AppDialog.CustomDownloadDialog;
import com.honestmc.laryngoscopeapp.BaseItems.DownloadInfo;
import com.honestmc.laryngoscopeapp.Log.AppLog;
import com.honestmc.laryngoscopeapp.R;
import com.honestmc.laryngoscopeapp.Tools.ConvertTools;
import com.honestmc.laryngoscopeapp.Tools.WifiCheck;
import com.icatch.wificam.customer.type.ICatchFile;
import com.icatch.wificam.customer.type.ICatchFileType;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

public class TestActivity extends AppCompatActivity {
    Button testBtn;
    LinkedList<ICatchFile> iCatchFiles;
    DownloadManagerAdapter downloadManagerAdapter;
    CustomDownloadDialog customDownloadDialog;
    Handler handler = new Handler();
    private HashMap<ICatchFile, DownloadInfo> downloadInfoMap = new HashMap<ICatchFile, DownloadInfo>();
    private Timer downloadProgressTimer;
    EditText editText;
    TextView textView ;
    WifiCheck wifiCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppLog.enableAppLog();
        setContentView(R.layout.activity_test);
        testBtn = (Button)findViewById(R.id.test01);
        editText = (EditText) findViewById(R.id.editText);
        textView = (TextView) findViewById(R.id.text);
        testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
////                        String add = editText.getText().toString();
////                        System.out.println("start isReachable" + add);
////                        final boolean value = InetAddressUtils.isReachable(add);
////                        handler.post(new Runnable() {
////                            @Override
////                            public void run() {
////                                textView.setText(value ? "Host is reachable" : "Host is NOT reachable");
////                            }
////                        });
//
////                        wifiCheck.connectWifiTest("AndroidAP","12345678",WifiCheck.WIFICIPHER_WAP);
//
//                    }
//                }).start();
                LinkedList<Integer> linkedList = new LinkedList<Integer>();
                linkedList.add(0x4000000a);
                linkedList.add(0xC0000011);
                linkedList.add(0x00000001);
                linkedList.add(0x0000000a);
                linkedList.add(0xffffffff);
                linkedList.add(0x00ffffff);
                String aa = "";
                for (Integer temp: linkedList
                     ) {
                    aa = aa + ConvertTools.getExposureCompensation(temp) + "\n";
                }
                textView.setText(aa);


//                WifiCheck wifiCheck = new WifiCheck(TestActivity.this);
//                wifiCheck.connectWifiTest("AndroidAP","12345678",WifiCheck.WIFICIPHER_WAP);

//                downloadManagerAdapter = new DownloadManagerAdapter(TestActivity.this, downloadInfoMap, iCatchFiles, handler);
//                customDownloadDialog = new CustomDownloadDialog();
//                customDownloadDialog.showDownloadDialog(TestActivity.this, downloadManagerAdapter);
//                downloadProgressTimer = new Timer();
//                downloadProgressTimer.schedule(new DownloadProgressTask(), 0, 300);

            }
        });
        initData();
        wifiCheck = new WifiCheck(TestActivity.this);
    }

    private void initData(){
        if(iCatchFiles==null){
            iCatchFiles = new LinkedList<>();
        }
        for (int ii =0;ii < 10; ii++){
            ICatchFile iCatchFile = new ICatchFile(ii, ICatchFileType.ICH_TYPE_IMAGE,"20160822_"+ ii,100);
            iCatchFiles.add(iCatchFile);
        }

        for (int ii = 0; ii < iCatchFiles.size(); ii++) {
            DownloadInfo downloadInfo = new DownloadInfo(iCatchFiles.get(ii), iCatchFiles.get(ii).getFileSize(), 0, 0, false);
            downloadInfoMap.put(iCatchFiles.get(ii), downloadInfo);
        }


    }

    class DownloadProgressTask extends TimerTask {

        @Override
        public void run() {

            final ICatchFile iCatchFile = iCatchFiles.getFirst();

            final DownloadInfo downloadInfo = downloadInfoMap.get(iCatchFile);
            if(downloadInfo.curFileLength < 100){
                downloadInfo.curFileLength = downloadInfo.curFileLength + 2;
            }
            if(downloadInfo.progress < 100){
                downloadInfo.progress = downloadInfo.progress + 2;
            }

            if(downloadInfo.progress >=100){
                iCatchFiles.removeFirst();
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    downloadInfoMap.put(iCatchFile, downloadInfo);
                    downloadManagerAdapter.notifyDataSetInvalidated();
//                    downloadManagerAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        customDownloadDialog.dismissDownloadDialog();
        finish();
    }
}
