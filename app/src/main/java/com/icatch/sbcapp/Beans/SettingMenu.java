package com.icatch.sbcapp.Beans;

public class SettingMenu {
    public int name;
    public String value;
    public int titleId;

    public int getName() {
        return name;
    }

    public SettingMenu(int name, String value,int titleId) {
        this.name = name;
        this.value = value;
        this.titleId = titleId;
    }

    public String getValue() {
        return value;
    }

    public int getTitleId(){
        return titleId;
    }
}
