package com.example.yanjiang.stockchart.rxutils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.example.yanjiang.stockchart.api.Constant;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lizhi on 2016/2/23.
 */
public class CommonUtil {

    /**
     * 获取设备id；
     * 获取用户设备的IMEI，通过IMEI和mac来唯一的标识用户。
     * ACCESS_WIFI_STATE(必须)	获取用户设备的mac地址，
     * <p/>
     * 在平板设备上，无法通过IMEI标示设备，我们会将mac地址作为用户的唯一标识
     * 在模拟器中运行时，IMEI返回总是000000000000000。
     *
     * @param context
     * @return
     */
    public static String getUUID(Context context) {
        if (context == null) {
            return "";
        }
        final TelephonyManager tm = (TelephonyManager) context.
                getSystemService(Context.TELEPHONY_SERVICE);
        String tmDeviceId = "";
        String androidId = "";
        String mac = "";

        String serial = Build.SERIAL; //12位；
        String time = Build.TIME + "";//13位；

        if (tm != null) {
            tmDeviceId = "" + tm.getDeviceId();
        }

        androidId = "" + Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
//        mac = "" + getMacAdress(context);

        Log.e("@@@","tmDeviceId=" + tmDeviceId + ",\nandroidId=" + androidId
                + ",\nmac=" + mac);

        long uuidParam2;

        uuidParam2 = (long) (androidId.hashCode() << 32 | (serial + time).hashCode());

        UUID deviceUuid = new UUID(tmDeviceId.hashCode(), uuidParam2);
        //生成32位的识别码；
        String uniqueId = deviceUuid.toString();
        String encryption = encryption(uniqueId);
        Log.e("@@@","encryption====>" + encryption);
        return encryption;
    }

    /**
     * 获取mac地址；
     *
     * @param c
     * @return
     */
    public static String getMacAdress(Context c) {
        ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null) {
            return "";
        } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {
            return getWifiAddress(c);
        } else {
            return getmobileAddress(c);
        }
    }

    public static String getmobileAddress(Context c) {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();

                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("@@@","获取移动网络的mac地址失败");
        }
        return "";
    }

    public static String getWifiAddress(Context c) {
        WifiManager wifiManager = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo != null) {
            return wifiInfo.getMacAddress();
        }
        return "";
    }


    public static String encryption(String plainText) {
        String re_md5 = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte b[] = md.digest();
            int i = 0;

            StringBuilder sb = new StringBuilder("");
            for (int offset = 0, len = b.length; offset < len; offset++) {
                i = b[offset];
                if (i < 0) {
                    i += 256;
                }
                if (i < 16) {
                    sb.append("0");
                }
                sb.append(Integer.toHexString(i));
            }
            re_md5 = sb.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return re_md5;
    }


    /**
     * 获取设备信息
     *
     * @return
     */
    public static String getDeviceInfo(Context context) {
        StringBuffer sb = new StringBuffer();

        String androidId = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        sb.append("deviceId：" + tm.getDeviceId());

        sb.append("\nandroidId：" + androidId);
        sb.append(
                " \n修订版本列表：" + Build.ID);
        sb.append(
                "硬件序列号：" + Build.SERIAL);
        sb.append(
                "\n TIME:" + Build.TIME);
        return sb.toString();
    }

    /**
     * 获取CPU名字
     *
     * @return
     */
    public static String getCpuName() {
        try {
            FileReader fr = new FileReader("/proc/cpuinfo");
            BufferedReader br = new BufferedReader(fr);
            String text = br.readLine();
            String[] array = text.split(":\\s+", 2);

            for (int i = 0; i < array.length; i++) {

            }
            return array[1];
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static boolean isEmpty(String text) {
        if (TextUtils.isEmpty(text)) {
            return true;
        }
        return false;
    }

    public static boolean isNotEmpty(String text) {
        if (!TextUtils.isEmpty(text)) {
            return true;
        }
        return false;
    }

    /**
     * 电信号段:133/153/180/181/189/177；              1  3578   01379
     * 联通号段:130/131/132/155/156/185/186/145/176；  1  34578  01256
     * 移动号段：134/135/136/137/138/139/150/151/152/157/158/159/182/183/184/187/188/147/178。
     * <p/>
     * 13      0123456789
     * 14       57
     * 15       012356789
     * 17       678
     * 18       0123456789
     *
     * @param phoneNum
     * @return
     */
    public static boolean isMobilePhone(String phoneNum) {
        if (isNotEmpty(phoneNum)) {
            //11位；
            Pattern p = Pattern.compile("^((13[0-9])|(14[5,7])|(15[0,1,2,3,5,6,7,8,9])|(17[6,7,8])|(18[0-9]))\\d{8}$");
            boolean m = p.matcher(phoneNum).matches();
            Log.e("@@@","手机号验证===>" + m);
            return m;
        }
        return false;
    }


    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int getScreenWidth(Context context) {
        return (int) (context.getResources().getDisplayMetrics().widthPixels + 0.5);
    }

    public static int getScreenHeight(Context context) {
        return (int) (context.getResources().getDisplayMetrics().heightPixels + 0.5);
    }

    /**
     * 获取版本号；
     *
     * @param context
     * @return
     */
    public static String getAppVersionName(Context context) {
        if (context != null) {
            try {
                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                if (packageInfo != null) {
                    return packageInfo.versionName;
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public static boolean isSDCardAvailable() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 删除特定的文件;
     *
     * @param filePath
     * @return
     */
    public static boolean deleteSpicificFile(String filePath) {
        if (!isEmpty(filePath) && isSDCardAvailable()) {
            try {
                File file = new File(filePath);
                if (file.exists() && file.isFile()) {
                    return file.delete();
                }
            } catch (Exception e) {

                if (e != null && isNotEmpty(e.getMessage())) {
                    Log.e("@@@",e.getMessage());
                }
            }
        }
        return false;
    }

    /**
     * 获得磁盘的缓存目录；
     *
     * @param context
     * @return
     */
    public static String getDiskCacheDir(Context context) {
        String cachePath = null;
        if (context != null && CommonUtil.isSDCardAvailable()) {
            File cacheDirTemp = context.getExternalCacheDir();
            if (cacheDirTemp != null) {
                cachePath = cacheDirTemp.getPath();
            }
        }
        return cachePath;
    }

    /**
     * 获得磁盘的缓存目录；
     *
     * @param context
     * @return
     */
    public static String getImageCacheDir(Context context) {
        String cachePath = null;
        if (context != null && CommonUtil.isSDCardAvailable()) {
            File cacheDirTemp = context.getExternalCacheDir();
            if (cacheDirTemp != null) {
                cachePath = cacheDirTemp.getAbsolutePath() + File.separator + "image_cache";
            }
        }
        return cachePath;
    }

    /**
     * 磁盘下载路径；
     *
     * @param context
     * @return
     */
    public static String getDiskDownloadDir(Context context) {
        String downloadPath = null;
        if (context != null && CommonUtil.isSDCardAvailable()) {
            File downloadDirTemp = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
            if (downloadDirTemp != null) {
                downloadPath = downloadDirTemp.getAbsolutePath();
            }
        }
        return downloadPath;
    }

    /**
     * 获取补丁路径；
     *
     * @param context
     * @return
     */

    public static String getApatchDownloadPath(Context context) {
        String downDir = getDiskDownloadDir(context);
        if (isNotEmpty(downDir)) {
            //当没有apatch文件夹时得建文件夹（httpUrlconnection中文件夹必须存在）
            File patchdir = new File(downDir + File.separator+"apatch");
            if(!patchdir.exists()){
                patchdir.mkdir();
            }
            return downDir + File.separator +"apatch" + File.separator + Constant.APATCH_PATH;
        }
        return "";
    }

    /**
     * 获取apk的下载目录(不带apk文件名称)；
     *
     * @param context
     * @return
     */

    public static String getApkDownloadPath(Context context) {
        String downDir = getDiskDownloadDir(context);
        if (isNotEmpty(downDir)) {
            //当没有apk文件夹时得建文件夹（httpUrlconnection中文件夹必须存在）
            File apkdir = new File(downDir + File.separator + "apk");
            if(!apkdir.exists()){
                apkdir.mkdir();
            }
            return downDir + File.separator + "apk" ;
        }
        return "";
    }

    public static int getAppVersionCode(Context context) {
        if (context != null) {
            PackageManager pm = context.getPackageManager();
            try {
                PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
                if (packageInfo != null) {
                    return packageInfo.versionCode;
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    /**
     * 拨号；
     */
    public static void dialNumber(Activity context, String phoneNumber) {
        if (context == null) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        context.startActivity(intent);
    }

    public static String getDirSizeWithUnit(File file) {
        long dirSize = getDirSize(file);
        return getDataWithUnit(dirSize);
    }


    /**
     * 根据字节数进行单位转换；
     *
     * @param size
     * @return
     */
    public static String getDataWithUnit(long size) {
        long kb = 1024;
        long mb = kb * 1024;

        if (size >= mb) {
            float f = (float) size / mb;
            return String.format("%.2f MB", f);
        } else if (size >= kb) {
            float f = (float) size / kb;
            return String.format("%.2f KB", f);
        }

        return "0KB";
    }

    // 校验，只能是数字和英文，且长度为6-9;
    public static boolean isValidCharacters(String s) {
        Pattern p = Pattern.compile("^[0-9a-zA-Z]{6,9}$");
        Matcher m = p.matcher(s);
        return m.matches();
    }

    /**
     * 金额验证，最多精确到小数点后2位；
     * 功能：判断输入框内容是否超出小数点后2位；
     *
     * @param s
     * @return true:已超出；false:未超出；
     */
    public static boolean verifyMore2NumAfterPoint(CharSequence s) {

        if (s != null && !isEmpty(s.toString()) && s.toString().contains(".")) {
            Pattern p = Pattern.compile("^[0-9]+.[0-9]{3,}$");
            boolean m = p.matcher(s).matches();

            Log.e("@@@","正则验证===>" + m);
            return m;
        }
        return false;
    }

    /**
     * 只能输入英文和数字；
     *
     * @param s
     * @return
     */
    public static boolean verifyPointAndEn(CharSequence s) {
        if (s != null && !isEmpty(s.toString())) {
            Pattern p = Pattern.compile("^[a-zA-Z0-9]+$");
            boolean m = p.matcher(s).matches();

            Log.e("@@@","正则验证===>" + m);
            return m;
        }
        return false;
    }

    /**
     * 验证金额格式的合法性；
     * <p/>
     * 1,无小数点的整数；
     * 2，有小数点且点位数最大为2位；
     *
     * @param s
     * @return
     */
    public static boolean verifyDataFormat(CharSequence s) {
        if (s != null && !isEmpty(s.toString())) {
            Pattern p1 = Pattern.compile("^[0-9]+$");
            Pattern p2 = Pattern.compile("^[0-9]+.[0-9]{1,2}$");

            boolean m1 = p1.matcher(s).matches();
            boolean m2 = p2.matcher(s).matches();

            Log.e("@@@","m1--->" + m1 + ",m2--->" + m2);

            return m1 || m2;
        }
        return false;
    }

    /**
     * 验证字符长度的合法性；
     *
     * @param s
     * @param minLen
     * @return
     */
    public static boolean verifyLen(CharSequence s, int minLen) {
        if (s != null && !isEmpty(s.toString()) && s.toString().length() >= minLen) {
            return true;
        }
        return false;
    }


    /**
     * 格式化金额数字，精确到小数点后2位，超出部分裁剪；
     *
     * @param s
     * @return
     */
    public static float formatJeNumber(CharSequence s) {
        if (s != null && !TextUtils.isEmpty(s.toString())) {
            BigDecimal bigDecimal = new BigDecimal(s.toString());
            bigDecimal.setScale(2);
//            setScale(2);//表示保留2位小数，默认用四舍五入方式
//            setScale(2,BigDecimal.ROUND_DOWN);//直接删除多余的小数位  11.116约=11.11
//            setScale(2,BigDecimal.ROUND_UP);//临近位非零，则直接进位；临近位为零，不进位。11.114约=11.12
//            setScale(2,BigDecimal.ROUND_HALF_UP);//四舍五入 2.335约=2.33，2.3351约=2.34
//            setScaler(2,BigDecimal.ROUND_HALF_DOWN);//四舍五入；2.335约=2.33，2.3351约=2.34，11.117约11.12
            return bigDecimal.floatValue();
        }
        return 0.00f;
    }

    /**
     * 隐藏字符串的中间部分长度；
     *
     * @param chars      整个字符串；
     * @param prepostLen 开头和结尾需要显示的部分的长度；
     * @return 处理后的String；
     */
    public static String hideYhkCenterPart(String chars, int prepostLen) {

        if (isEmpty(chars)) {
            return "";
        } else if (chars.length() > prepostLen * 2) {
            int replaceCharsLen = chars.length() - prepostLen * 2;

            String preStr = chars.substring(0, prepostLen);
            StringBuilder sb = new StringBuilder(preStr);
            for (int i = 0; i < replaceCharsLen; i++) {
                sb.append("*");
            }
            String postStr = chars.substring(prepostLen + replaceCharsLen);
            sb.append(postStr);
            return sb.toString();
        }
        return chars;
    }

    /**
     * 扫描目录下所有文件的大小；
     *
     * @param file
     */

    public static long getDirSize(File file) {
        long totalSize = 0;

        if (file != null && file.exists()) {

            if (file.isDirectory()) {
                File[] files = file.listFiles();

                for (int i = 0; i < files.length; i++) {

                    //是目录；
                    if (file.isDirectory()) {
                        totalSize += getDirSize(files[i]);
                    } else {
                        totalSize += file.length();
                    }
                }
            } else {
                totalSize += file.length();
            }
        }

        return totalSize;
    }

    /**
     * 测试网络连接问题；
     *
     * @param context
     * @return
     */
    public static boolean isConnected(Context context) {
        if (context != null) {
            ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (conn != null) {
                NetworkInfo info = conn.getActiveNetworkInfo();
                return (info != null && info.isConnected());
            }
        }
        return false;
    }

    /**
     * 时间格式化；
     *
     * @param timeIns 单位为：秒；
     * @return 格式："x天xx小时xx秒分xx秒";
     */
    public static int[] timeFormat(int timeIns) {
        int dayMs = 24 * 3600;
        int hourMs = 3600;
        int minuteMs = 60;

        int lastDays = 0;
        int lastHours = 0;
        int lastminutes = 0;
        int lastMs = 0;

//        StringBuilder sb = new StringBuilder();
        //天数；
        if (timeIns > dayMs) {
            lastDays = timeIns / dayMs;
            timeIns = timeIns % dayMs;
        }

        //小时；
        if (timeIns > hourMs) {
            lastHours = timeIns / hourMs;
            timeIns = timeIns % hourMs;
        }

        //分钟；
        if (timeIns > minuteMs) {
            lastminutes = timeIns / minuteMs;
            timeIns = timeIns % minuteMs;
        }

        //秒；
        lastMs = timeIns;
//        sb.append(lastDays + "天" + lastHours + "小时" + lastminutes + "分" + lastMs + "秒");
        int[] dhms = new int[4];
        dhms[0] = lastDays;
        dhms[1] = lastHours;
        dhms[2] = lastminutes;
        dhms[3] = lastMs;
        return dhms;
    }

    /**
     * 实时更新tv的值；
     *
     * @param tv
     * @param timeIns
     */
    public static void updateTimeLabel(TextView tv, int timeIns) {
        if (tv == null) {
            return;
        }
        if (timeIns <= 0) {
            timeIns = 0;
        }
        int[] dhms = CommonUtil.timeFormat(timeIns);
        Spanned spanned = Html.fromHtml("<font color=\"#c3130f\">" + dhms[0] + "</font>天" +
                "<font color=\"#c3130f\">" + dhms[1] + "</font>小时" + "<font color=\"#c3130f\">"
                + dhms[2] + "</font>分" + "<font color=\"#c3130f\">" + dhms[3] + "</font>秒");
        tv.setText(spanned);
    }


    /**
     * 删除某个目录下的所有文件；
     *
     * @param cacehDir
     */
    public static void clearCacheDir(File cacehDir) {
        if (cacehDir != null) {

            if (cacehDir.isDirectory()) {
                File[] files = cacehDir.listFiles();

                if (files != null && files.length > 0) {

                    for (File itemFile : files) {

                        //是文件，则删除；
                        if (itemFile.isFile()) {
                            itemFile.delete();
                        } else {
                            clearCacheDir(itemFile);
                        }
                    }
                }

            } else {
                cacehDir.delete();
            }
        }
    }

    /**
     * 验证金额不能为0,0.0,0.00;
     *
     * @param je
     * @return
     */
    public static boolean verifyNoZero(String je) {
        if (isNotEmpty(je)) {
            if ("0".equals(je) || "0.0".equals(je) || "0.00".equals(je)) {
                return true;
            }
        }
        return false;
    }
}
