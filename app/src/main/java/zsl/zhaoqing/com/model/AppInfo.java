package zsl.zhaoqing.com.model;

import android.graphics.drawable.Drawable;

/**
 * Created by Administrator on 2015/12/17.
 */
public class AppInfo {

    private String appPkgName;
    private String AppLauncherClassName;
    private String AppName;
    private Drawable appIcon;

    public AppInfo() {
    }

    public String getAppPkgName() {
        return appPkgName;
    }

    public void setAppPkgName(String appPkgName) {
        this.appPkgName = appPkgName;
    }

    public String getAppLauncherClassName() {
        return AppLauncherClassName;
    }

    public void setAppLauncherClassName(String appLauncherClassName) {
        AppLauncherClassName = appLauncherClassName;
    }

    public String getAppName() {
        return AppName;
    }

    public void setAppName(String appName) {
        AppName = appName;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }
}
