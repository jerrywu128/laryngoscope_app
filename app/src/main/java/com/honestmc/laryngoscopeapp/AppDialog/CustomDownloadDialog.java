package com.honestmc.laryngoscopeapp.AppDialog;

import android.app.AlertDialog;
import android.content.Context;
//import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.honestmc.laryngoscopeapp.Adapter.DownloadManagerAdapter;
import com.honestmc.laryngoscopeapp.Log.AppLog;
import com.honestmc.laryngoscopeapp.R;

/**
 * Created by b.jiang on 2016/3/14.
 */
public class CustomDownloadDialog {
    private static String TAG = "CustomDownloadDialog";
    private TextView message;
    private ListView downloadStatus;
    private ImageButton exit;
    AlertDialog.Builder builder;
    AlertDialog alertDialog;

    public CustomDownloadDialog(){

    }

    public void showDownloadDialog(Context context,DownloadManagerAdapter adapter) {
        builder = new AlertDialog.Builder(context);
        View contentView = View.inflate(context, R.layout.download_content_dialog, null);
        View titleView = View.inflate(context, R.layout.download_dialog_title, null);
        exit = (ImageButton) titleView.findViewById(R.id.exit);
        downloadStatus = (ListView) contentView.findViewById(R.id.downloadStatus);
        downloadStatus.setItemsCanFocus(true);
        downloadStatus.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppLog.d(TAG,"3322ee downloadStatus ListView ClickListener position=" + position);
            }
        });
        message = (TextView) contentView.findViewById(R.id.message);
        downloadStatus.setAdapter(adapter);

        builder.setCustomTitle(titleView);
        builder.setView(contentView);
        builder.setCancelable(false);

        //创建、并显示对话框
        alertDialog = builder.create();
        alertDialog.show();
    }

    public void dismissDownloadDialog(){
        if (alertDialog != null){
            alertDialog.dismiss();
        }
    }

    public void setMessage(String myMessage) {
        message.setText(myMessage);
    }

    public void setBackBtnOnClickListener(View.OnClickListener onClickListener){
        if(onClickListener != null){
            exit.setOnClickListener(onClickListener);
        }
    }

    public void setAdapter(DownloadManagerAdapter adapter){
        downloadStatus.setAdapter(adapter);
    }

}
