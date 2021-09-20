package com.icatch.sbcapp.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.icatch.sbcapp.BaseItems.FileType;
import com.icatch.sbcapp.BaseItems.MultiPbItemInfo;
import com.icatch.sbcapp.Log.AppLog;
import com.icatch.sbcapp.Mode.OperationMode;
import com.icatch.sbcapp.R;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by b.jiang on 2015/12/18.
 */

public class MultiPbPhotoWallListAdapter extends BaseAdapter{
    private String TAG = "MultiPbPhotoWallListAdapter";
    private Context context;
    private List<MultiPbItemInfo> list;
    private OperationMode curMode = OperationMode.MODE_BROWSE;
    LruCache<Integer, Bitmap> mLruCache;
    private FileType fileType;

    public MultiPbPhotoWallListAdapter(Context context, List<MultiPbItemInfo> list, LruCache<Integer, Bitmap> mLruCache,FileType fileType) {
        this.context = context;
        this.list = list;
        this.mLruCache = mLruCache;
        this.fileType = fileType;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int fileHandle = list.get(position).getFileHandle();
        String curFileDate = list.get(position).getFileDate();
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_local_photo_wall_list, null);
        } else {
            view = convertView;
        }
        ImageView imageView = (ImageView) view.findViewById(R.id.local_photo_thumbnail_list);
        TextView mTextView = (TextView) view.findViewById(R.id.photo_wall_header);
        RelativeLayout mLayout = (RelativeLayout) view.findViewById(R.id.local_photo_wall_header_layout);
        TextView imageNameTextView = (TextView) view.findViewById(R.id.local_photo_name);
        TextView imageSizeTextView = (TextView) view.findViewById(R.id.local_photo_size);
        TextView imageDateTextView = (TextView) view.findViewById(R.id.local_photo_date);
        TextView imageDurationView = (TextView) view.findViewById(R.id.local_video_duration);
        ImageView mCheckImageView = (ImageView) view.findViewById(R.id.local_photo_wall_list_edit);
        ImageView videoSignImageView = (ImageView) view.findViewById(R.id.video_sign);
        imageNameTextView.setText(list.get(position).getFileName());
        imageSizeTextView.setText(list.get(position).getFileSize());
        imageDateTextView.setText(list.get(position).getFileDateMMSS());
//
        if (fileType == FileType.FILE_PHOTO){
            videoSignImageView.setVisibility(View.GONE);
            imageDurationView.setVisibility(View.GONE);
        }else{
            videoSignImageView.setVisibility(View.VISIBLE);
            imageDurationView.setText(list.get(position).getFileDuration());
            imageDurationView.setVisibility(View.VISIBLE);
        }
        if(curMode == OperationMode.MODE_EDIT){
            mCheckImageView.setVisibility(View.VISIBLE);
            if (list.get(position).isItemChecked){
                mCheckImageView.setImageResource(R.drawable.ic_check_box_blue);
            }else {
                mCheckImageView.setImageResource(R.drawable.ic_check_box_blank_grey);
            }
        }else {
            mCheckImageView.setVisibility(View.GONE);
        }
        imageView.setTag(fileHandle);
        if (position == 0 || !list.get(position - 1).getFileDate().equals(curFileDate)) {
            mLayout.setVisibility(View.VISIBLE);
            mTextView.setText(list.get(position).getFileDate());
        } else {
            mLayout.setVisibility(View.GONE);
        }
        Bitmap bitmap =  mLruCache.get(fileHandle);
        if(bitmap != null){
            imageView.setImageBitmap(bitmap);

        }else {
            imageView.setImageResource(R.drawable.pictures_no);
        }
        return view;
    }

    public void setOperationMode(OperationMode operationMode){
        this.curMode = operationMode;
    }

    public void changeSelectionState(int position)
    {
        list.get(position).isItemChecked = list.get(position).isItemChecked==true?false:true;
        this.notifyDataSetChanged();
    }

    public List<MultiPbItemInfo> getSelectedList(){
        LinkedList<MultiPbItemInfo> checkedList = new LinkedList<MultiPbItemInfo>();

        for (int ii = 0; ii < list.size();ii ++){
            if(list.get(ii).isItemChecked){
                checkedList.add(list.get(ii));
            }
        }
        return checkedList;
    }

    public void quitEditMode(){
        this.curMode = OperationMode.MODE_BROWSE;
        for (int ii = 0; ii < list.size();ii ++){
            list.get(ii).isItemChecked = false;
        }
        this.notifyDataSetChanged();
    }
    public void selectAllItems(){
        for (int ii = 0; ii < list.size();ii ++){
            list.get(ii).isItemChecked = true;
        }
        this.notifyDataSetChanged();
    }
    public void cancelAllSelections(){
        for (int ii = 0; ii < list.size();ii ++){
            list.get(ii).isItemChecked = false;
        }
        this.notifyDataSetChanged();
    }

    public int getSelectedCount(){
        int checkedNum = 0;
        for (int ii = 0; ii < list.size();ii ++){
            if(list.get(ii).isItemChecked){
                checkedNum ++;
            }
        }
        return checkedNum;
    }
}
