package com.honestmc.laryngoscopeapp.Adapter;

/**
 * Created by b.jiang on 2016/1/27.
 */

import android.content.Context;
import android.os.Handler;
//import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;

import com.honestmc.laryngoscopeapp.ExtendComponent.MyToast;
import com.honestmc.laryngoscopeapp.MyCamera.MyCamera;
import com.honestmc.laryngoscopeapp.PropertyId.PropertyId;
import com.honestmc.laryngoscopeapp.SdkApi.CameraProperties;
import com.honestmc.laryngoscopeapp.AppInfo.AppInfo;
import com.honestmc.laryngoscopeapp.Beans.SettingMenu;
import com.honestmc.laryngoscopeapp.GlobalApp.GlobalInfo;
import com.honestmc.laryngoscopeapp.Log.AppLog;
import com.honestmc.laryngoscopeapp.Message.AppMessage;
import com.honestmc.laryngoscopeapp.R;

import java.util.List;

public class SettingListAdapter extends BaseAdapter {
    private static final String TAG = "SettingListAdapter";
    private Context context;
    private List<SettingMenu> menuList;
    private Handler handler;
    private MyCamera currentCamera;

    OnItemClickListener onItemClickListener;

    public SettingListAdapter(Context context, List<SettingMenu> menuList, Handler handler, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.menuList = menuList;
        this.handler = handler;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getCount() {
        return menuList.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        currentCamera = GlobalInfo.getInstance().getCurrentCamera();
        if (menuList.get(position).name == R.string.setting_auto_download) {
            convertView = LayoutInflater.from(context).inflate(R.layout.auto_download_layout, null);
            final CheckBox toggleButton = (CheckBox) convertView.findViewById(R.id.switcher);

            toggleButton.setChecked(AppInfo.autoDownloadAllow);
            toggleButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    handler.obtainMessage(AppMessage.SETTING_OPTION_AUTO_DOWNLOAD, toggleButton.isChecked()).sendToTarget();
                }
            });

            return convertView;
        } else if (menuList.get(position).name == R.string.setting_audio_switch) {
            convertView = LayoutInflater.from(context).inflate(R.layout.audio_switch_layout, null);
            final CheckBox toggleButton = (CheckBox) convertView.findViewById(R.id.switcher);

            toggleButton.setChecked(!AppInfo.disableAudio);
            toggleButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    boolean temp = false;
                    // TODO Auto-generated method stub
                    AppInfo.disableAudio = !AppInfo.disableAudio;
                    AppLog.d(TAG, "toggleButton.setOnClickListener disableAudio=" + AppInfo.disableAudio);
                }
            });
            return convertView;
        } else if (menuList.get(position).name == R.string.setting_auto_download_size_limit) {
            convertView = LayoutInflater.from(context).inflate(R.layout.auto_download_layout_size, null);
            final TextView autoDownloadSize = (TextView) convertView.findViewById(R.id.download_size);
            RelativeLayout itemLayout = (RelativeLayout) convertView.findViewById(R.id.item_layout);
            itemLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(position);
                }
            });
            autoDownloadSize.setText(AppInfo.autoDownloadSizeLimit + "GB");
            return convertView;

        } else if (menuList.get(position).name == R.string.setting_title_power_on_auto_record) {
            convertView = LayoutInflater.from(context).inflate(R.layout.setting_switch_layout, null);
            TextView textView = (TextView) convertView.findViewById(R.id.item_name);
            textView.setText(R.string.setting_title_power_on_auto_record);
            int curValue = CameraProperties.getInstance().getCurrentPropertyValue(PropertyId.POWER_ON_AUTO_RECORD);
            final SwitchCompat switchCompat = (SwitchCompat) convertView.findViewById(R.id.switchCompat);
            boolean isCheched = curValue == 0 ? false : true;
            switchCompat.setChecked(isCheched);
            switchCompat.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    int value = switchCompat.isChecked() ? 1 : 0;
                    CameraProperties.getInstance().setPropertyValue(PropertyId.POWER_ON_AUTO_RECORD, value);
                    // read one more time
                    int retValue = CameraProperties.getInstance().getCurrentPropertyValue(PropertyId.POWER_ON_AUTO_RECORD);
                    boolean isChecked = retValue == 0 ? false : true;
                    switchCompat.setChecked(isChecked);
                }
            });
            return convertView;
        } else if (menuList.get(position).name == R.string.setting_title_image_stabilization) {
            convertView = LayoutInflater.from(context).inflate(R.layout.setting_switch_layout, null);
            TextView textView = (TextView) convertView.findViewById(R.id.item_name);
            final SwitchCompat switchCompat = (SwitchCompat) convertView.findViewById(R.id.switchCompat);
            textView.setText(R.string.setting_title_image_stabilization);
            int curValue = CameraProperties.getInstance().getCurrentPropertyValue(PropertyId.IMAGE_STABILIZATION);
            List<Integer> supportValues = CameraProperties.getInstance().getSupportedPropertyValues(PropertyId.IMAGE_STABILIZATION);
            if (supportValues == null || supportValues.size() <= 1) {
                switchCompat.setEnabled(false);
            }
            boolean isCheched = curValue == 0 ? false : true;
            switchCompat.setChecked(isCheched);
            switchCompat.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    if (!switchCompat.isEnabled()) {
                        MyToast.show(context, R.string.current_size_not_support_image_stabilization);
                    } else {
                        int value = switchCompat.isChecked() ? 1 : 0;
                        CameraProperties.getInstance().setPropertyValue(PropertyId.IMAGE_STABILIZATION, value);
                        // read one more time
                        int retValue = CameraProperties.getInstance().getCurrentPropertyValue(PropertyId.IMAGE_STABILIZATION);
                        boolean isChecked = retValue == 0 ? false : true;
                        switchCompat.setChecked(isChecked);
                    }

                }
            });
            return convertView;
        } else if (menuList.get(position).name == R.string.setting_title_wind_noise_reduction) {
            convertView = LayoutInflater.from(context).inflate(R.layout.setting_switch_layout, null);
            TextView textView = (TextView) convertView.findViewById(R.id.item_name);
            textView.setText(R.string.setting_title_wind_noise_reduction);
            int curValue = CameraProperties.getInstance().getCurrentPropertyValue(PropertyId.WIND_NOISE_REDUCTION);
            final SwitchCompat switchCompat = (SwitchCompat) convertView.findViewById(R.id.switchCompat);
            boolean isCheched = curValue == 0 ? false : true;
            switchCompat.setChecked(isCheched);
            switchCompat.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    int value = switchCompat.isChecked() ? 1 : 0;
                    CameraProperties.getInstance().setPropertyValue(PropertyId.WIND_NOISE_REDUCTION, value);
                    // read one more time
                    int retValue = CameraProperties.getInstance().getCurrentPropertyValue(PropertyId.WIND_NOISE_REDUCTION);
                    boolean isChecked = retValue == 0 ? false : true;
                    switchCompat.setChecked(isChecked);
                }
            });
            return convertView;
        }
        convertView = LayoutInflater.from(context).inflate(R.layout.setting_menu_item, null);
        holder = new ViewHolder();
        int curTitleId = menuList.get(position).titleId;
        holder.title = (TextView) convertView.findViewById(R.id.item_text);
        holder.text = (TextView) convertView.findViewById(R.id.item_value);
        holder.settingTitle = (TextView) convertView.findViewById(R.id.setting_title);
        holder.itemLayout = (LinearLayout) convertView.findViewById(R.id.item_layout);
        convertView.setTag(holder);

        if (position == 0 || (menuList.get(position - 1).titleId != curTitleId)) {
            holder.settingTitle.setVisibility(View.VISIBLE);
//            holder.settingTitle.setText(menuList.get(position).titleId);
        } else {
            holder.settingTitle.setVisibility(View.GONE);
        }

        holder.title.setText(menuList.get(position).name);
        if (menuList.get(position).value == "") {
            holder.text.setVisibility(View.GONE);
        } else {
            holder.text.setText(menuList.get(position).value);
        }

        int tempName = menuList.get(position).name;

        if (tempName == R.string.setting_app_version || tempName == R.string.setting_product_name
                || tempName == R.string.setting_firmware_version) {
            holder.title.setTextColor(context.getResources().getColor(R.color.secondary_text));
            holder.text.setTextColor(context.getResources().getColor(R.color.secondary_text));
        } else {
            holder.title.setTextColor(context.getResources().getColor(R.color.primary_text));
            holder.text.setTextColor(context.getResources().getColor(R.color.cambridge_blue));
            holder.itemLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(position);
                }
            });
        }
        return convertView;
    }

    public final class ViewHolder {
        public TextView title;
        public TextView text;
        public TextView settingTitle;
        public LinearLayout itemLayout;
    }

    public interface OnItemClickListener {
        public void onItemClick(int position);

    }
}

