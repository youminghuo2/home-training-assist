package com.example.hometrainng.tools;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.hometrainng.db.TimeDate;
import com.example.hometrainng.retrofit.Constants;
import com.tamsiree.rxkit.RxEncodeTool;
import com.tamsiree.rxkit.RxTimeTool;

import org.litepal.LitePal;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static androidx.constraintlayout.widget.Constraints.TAG;

/**
 * @Package com.example.hometrainng.Utils
 * @Description java类作用描述
 * @CreateDate: 2020/5/7 4:36 PM
 */
public class Utils {

    public static String formatMonthString(String month) {
        return String.valueOf(month.charAt(0)).equals("0") ? month.replace("0", "") : month;
    }

    public static String formatDayString(String date){
        return String.valueOf(date.charAt(0)).equals("0") ? date.replace("0","") : date;
    }

    public static void playNotificationRing(Context context) {
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        if (uri == null) {
            return;
        }
        Ringtone ringtone = RingtoneManager.getRingtone(context, uri);
        ringtone.play();
    }

    public static Date strToDate(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    /**
     * 返回XXXX.X.X~1.XX时间格式数据，1号到最后一天,text返回
     *
     * @param date
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String dataMinus(LocalDate date) {
        //获取月的最后一天
        LocalDate firstday = date.with(TemporalAdjusters.lastDayOfMonth());
        String time = date.getYear() + "." + date.getMonthValue() + ".1~" + date.getMonthValue() + "." + firstday.getDayOfMonth();
        return time;
    }

    /**
     * 根据传入年份和上半下班，输出XXXX.X.1~X.XX
     */

    public static List<String> getSpinnerTextDate(int year, String desc, LocalDate endDate) {
        int month = endDate.getMonthValue();
        int day = endDate.getDayOfMonth();
        String date="";
        if (day<10){
            date="0"+day;
        }else {
            date=""+day;
        }

        List<TimeDate> timeDateList = LitePal.select("*").limit(1).find(TimeDate.class);
        TimeDate timeDate = timeDateList.get(0);
        //两种格式，一种yyyy-M-d,一种yyyy-MM-dd
            LocalDate beginDate = LocalDate.parse(timeDate.getBeginDate(), DateTimeFormatter.ofPattern("yyyy-M-d"));
            LocalDate beginDate2=LocalDate.parse(timeDate.getBeginDate(),DateTimeFormatter.ISO_LOCAL_DATE);
        List<String> getSpinner = new ArrayList<>();
        String text = null;
        String start = null;
        String end = null;

        if (year==beginDate.getYear()){
            if (year==endDate.getYear()){
                if(desc.equals("前半")){
                    if (Utils.getMsg(beginDate2).equals("前半")){
                       int lastday=beginDate.with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth();
                        text=year+"."+beginDate.getMonthValue()+"."+beginDate.getDayOfMonth()+"~"+beginDate.getMonthValue()+"."+lastday;
                        start=beginDate2.format(DateTimeFormatter.ISO_LOCAL_DATE);
                        end=(beginDate2.with(TemporalAdjusters.lastDayOfMonth())).format(DateTimeFormatter.ISO_LOCAL_DATE);
                    }
                }else if (desc.equals("後半")){
                    if (endDate.getMonthValue()==7){
                        text=year+".7.1~7."+endDate.getDayOfMonth();
                        start=year+"-07-01";
                        end=endDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
                    }else {
                        text = year + ".7.1~7.31";
                        start = year + "-07-01";
                        end = year + "-07-31";
                    }
                }
            }else {
                if (desc.equals("前半")) {
                    if (Utils.getMsg(beginDate2).equals("前半")){
                        int lastday=beginDate.with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth();
                        text=year+"."+beginDate.getMonthValue()+"."+beginDate.getDayOfMonth()+"~"+beginDate.getMonthValue()+"."+lastday;
                        start=beginDate2.format(DateTimeFormatter.ISO_LOCAL_DATE);
                        end=(beginDate2.with(TemporalAdjusters.lastDayOfMonth())).format(DateTimeFormatter.ISO_LOCAL_DATE);
                    }else if (Utils.getMsg(beginDate2).equals("後半")){
                        text = year + ".1.1~1.31";
                        start = year + "-01-01";
                        end = year + "-01-31";
                    }

                } else if (desc.equals("後半")) {
                        text = year + ".7.1~7.31";
                        start = year + "-07-01";
                        end = year + "-07-31";
                }
            }
        }else {
            if (desc.equals("前半")) {
                if (month == 1) {
                    text = year + ".1.1~1." + day;
                    start = year + "-01-01";
                    end = year + "-01-" + date;
                } else {
                    text = year + ".1.1~1.31";
                    start = year + "-01-01";
                    end = year + "-01-31";
                }
            } else if (desc.equals("後半")) {
                if (month == 7) {
                    text = year + ".7.1~7." + day;
                    start = year + "-07-01";
                    end = year + "-07-" + date;
                } else {
                    text = year + ".7.1~7.31";
                    start = year + "-07-01";
                    end = year + "-07-31";
                }

            }
        }
        getSpinner.add(text);
        getSpinner.add(start);
        getSpinner.add(end);
        return getSpinner;
    }



    //录像筛选

    public static List<String> getSpinnerTextDateVideo(int year, String desc, LocalDate endDate) {
        int month = endDate.getMonthValue();
        int day = endDate.getDayOfMonth();

        List<TimeDate> timeDateList = LitePal.select("*").limit(1).find(TimeDate.class);
        TimeDate timeDate = timeDateList.get(0);
        LocalDate beginDate = LocalDate.parse(timeDate.getVideoDate(), DateTimeFormatter.ofPattern("yyyy-M-d"));
        LocalDate beginDate2=LocalDate.parse(timeDate.getVideoDate(),DateTimeFormatter.ISO_LOCAL_DATE);

        List<String> getSpinner = new ArrayList<>();
        String text = null;
        String start = null;
        String end = null;

        if (year==beginDate.getYear()){
            if (year==endDate.getYear()){
                if(desc.equals("前半")){
                    if (Utils.getMsg(beginDate2).equals("前半")){
                        int lastday=beginDate.with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth();
                        text=year+"."+beginDate.getMonthValue()+"."+beginDate.getDayOfMonth()+"~"+beginDate.getMonthValue()+"."+lastday;
                        start=beginDate2.format(DateTimeFormatter.ISO_LOCAL_DATE);
                        end=(beginDate2.with(TemporalAdjusters.lastDayOfMonth())).format(DateTimeFormatter.ISO_LOCAL_DATE);
                    }
                }else if (desc.equals("後半")){
                    if (endDate.getMonthValue()==7){
                        text=year+".7.1~7."+endDate.getDayOfMonth();
                        start=year+"-07-01";
                        end=endDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
                    }else {
                        text = year + ".7.1~7.31";
                        start = year + "-07-01";
                        end = year + "-07-31";
                    }
                }
            }else {
                if (desc.equals("前半")) {
                    if (Utils.getMsg(beginDate2).equals("前半")){
                        int lastday=beginDate.with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth();
                        text=year+"."+beginDate.getMonthValue()+"."+beginDate.getDayOfMonth()+"~"+beginDate.getMonthValue()+"."+lastday;
                        start=beginDate2.format(DateTimeFormatter.ISO_LOCAL_DATE);
                        end=(beginDate2.with(TemporalAdjusters.lastDayOfMonth())).format(DateTimeFormatter.ISO_LOCAL_DATE);
                    }else if (Utils.getMsg(beginDate2).equals("後半")){
                        text = year + ".1.1~1.31";
                        start = year + "-01-01";
                        end = year + "-01-31";
                    }

                } else if (desc.equals("後半")) {
                    text = year + ".7.1~7.31";
                    start = year + "-07-01";
                    end = year + "-07-31";
                }
            }
        }else {
            if (desc.equals("前半")) {
                if (month == 1) {
                    text = year + ".1.1~1." + day;
                    start = year + "-01-01";
                    end = year + "-01-" + day;
                } else {
                    text = year + ".1.1~1.31";
                    start = year + "-01-01";
                    end = year + "-01-31";
                }
            } else if (desc.equals("後半")) {
                if (month == 7) {
                    text = year + ".7.1~7." + day;
                    start = year + "-07-01";
                    end = year + "-07-" + day;
                } else {
                    text = year + ".7.1~7.31";
                    start = year + "-07-01";
                    end = year + "-07-31";
                }

            }
        }
        getSpinner.add(text);
        getSpinner.add(start);
        getSpinner.add(end);
        return getSpinner;
    }


    /**
     * 将XXXX-XX-XX时间变为XXXX.XX.XX格式
     */
    public static String getToDate(String date) {
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        int year = localDate.getYear();
        int month = localDate.getMonthValue();
        int day = localDate.getDayOfMonth();
        String reDate = year + "." + month + "." + day;
        return reDate;
    }

    public static String getDatePoint(String date) {
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String newDate = localDate.format(DateTimeFormatter.ofPattern("yyyy.M.d"));
        return newDate;
    }

    //返还字符串时间
    public static String getStringDate(LocalDate localDate) {
        return localDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    //返还localtime时间
    public static LocalDate getLocalDate(String date) {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public static LocalDate getLocalDate2(String date) {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy.M.d"));
    }

    /**
     * 根据传入的时间，返回前半还是后半
     */

    public static String getMsg(LocalDate date) {
        String isWhich;
        if (date.getMonthValue() < 7) {
            isWhich = "前半";
        } else {
            isWhich = "後半";
        }
        return isWhich;
    }

    /**
     * 将某个月变成某个月最后一天 yyyy-MM-dd
     */
    public static String lastdate(String date) {
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate lastDate = localDate.with(TemporalAdjusters.lastDayOfMonth());
        String lastday = lastDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return lastday;
    }


    /**
     * 返回筛选框
     */
    public static List<String> getDesc(LocalDate startDate, LocalDate endDate) {
        List<String> city = new ArrayList<>();
        int startYear = startDate.getYear();
        int startMonth = startDate.getMonthValue();
        int endYear = endDate.getYear();
        int endMonth = endDate.getMonthValue();
        int number = endYear - startYear;
        //年份相同
        if (startYear == endYear) {
            //月份不同
            if (startMonth != endMonth) {
                //同上半年,同下半年，一个上半年，一个下半年
                if (startMonth < 7 && endMonth < 7) {
                    city.add(startYear + "年/前半");
                } else if (startMonth > 6 && endMonth > 6) {
                    city.add(startYear + "年/後半");
                } else {
                    city.add(startYear + "年/前半");
                    city.add(startYear + "年/後半");
                }
            } else {
                //月份相同
                if (startMonth < 7) {
                    city.add(startYear + "年/前半");
                } else {
                    city.add(startYear + "年/後半");
                }

            }
        } else {
            //年份不同，开始时间是上半年
            if (startMonth < 7) {
                //结束时间是上半年
                if (endMonth < 7) {
                    for (int i = 0; i < number; i++) {
                        city.add(startYear + i + "年/前半");
                        city.add(startYear + i + "年/後半");
                    }
                    city.add(startYear + number + "年/前半");
                } else {
                    //结束时间是下半年
                    for (int i = 0; i <= number; i++) {
                        city.add(startYear + i + "年/前半");
                        city.add(startYear + i + "年/後半");
                    }
                }
            } else {
                //开始时间是下半年。结束时间是上半年
                if (endMonth < 7) {
                    city.add(startYear + "年/後半");
                    for (int i = 1; i < number; i++) {
                        city.add(startYear + i + "年/前半");
                        city.add(startYear + i + "年/後半");
                    }
                    city.add(startYear + number + "年/前半");
                } else {
                    //开始时间是下半年，结束时间是下半年
                    city.add(startYear + "年/後半");
                    for (int i = 1; i <= number; i++) {
                        city.add(startYear + i + "年/前半");
                        city.add(startYear + i + "年/後半");
                    }
                }
            }
        }
        return city;
    }


    /**
     * 传入时间，返回这个月有多少天
     */

    public static int dayNuber(String date) {
        LocalDate data = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
        int length = data.lengthOfMonth();
        return length;
    }

    /**
     * 根据传入的星期返回日本星期
     */
    public static String weekOf(String week) {
        String[][] strArray = {{"MONDAY", "月"}, {"TUESDAY", "火"}, {"WEDNESDAY", "水"}, {"THURSDAY", "木"}, {"FRIDAY", "金"}, {"SATURDAY", "土"}, {"SUNDAY", "日"}};
        for (int i = 0; i < strArray.length; i++) {
            if (week.equals(strArray[i][0])) {
                week = strArray[i][1];
                break;
            }
        }
        return week;
    }

    /**
     * 获取视频总时间,转换为mm:ss
     */
    public static String getVideoDuration(String path) {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(path);
        String duration = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        mediaMetadataRetriever.release();
        return RxTimeTool.formatTime(Long.parseLong(duration));
    }

    /**
     * 获取视频某一帧图片
     */
    public static String getVideoPhotoPath(String videoPath, Long systemTime) {
        Bitmap bitmap;
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(videoPath);
        bitmap = mediaMetadataRetriever.getFrameAtTime();
        mediaMetadataRetriever.release();
        File file = Environment.getExternalStorageDirectory();
        File dir = new File(file, "_MyPhoto");
        if (!dir.exists()) {
            dir.mkdir();
        }
        String path = dir.getAbsolutePath() + "/video_" + systemTime + ".jpg";
        File file1 = new File(path);
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(file1);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
        } catch (IOException e) {
            PLog.e(TAG + "/getVideoPhotoPath", e.toString());
        }

        return file1.getAbsolutePath();
    }

    /**
     * 将HH-mm-ss转换为hh.m.ss格式
     */
    public static String TimeToHHMMSS(String date) {
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String year = localDate.getYear() + ".";
        String month = localDate.getMonthValue() + ".";
        String day = localDate.getDayOfMonth() + "";
        String dateOfString = year + month + day;
        return dateOfString;

    }

    //解密AES,iv和key格式的
    public static String setBaseAECMsg(String srcFileBase64) throws Exception {
        if (TextUtils.isEmpty(srcFileBase64)) {
            return "";
        }
        byte[] encryptedBytes = RxEncodeTool.base64Decode(srcFileBase64);
        String key = "751f621ea5c8f930";
        String iv = "2624750004598718";
        String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
        byte[] enCodeFormat = key.getBytes();
        SecretKeySpec secretKey = new SecretKeySpec(enCodeFormat, "AES");
        byte[] initParam = iv.getBytes();
        IvParameterSpec ivParameterSpec = new IvParameterSpec(initParam);
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
        byte[] result = cipher.doFinal(encryptedBytes);
        return new String(result, "UTF-8");

    }

    //将byte[]转换成map
    public static Map<String, Object> changeByteToMap(byte[] bytes) {
        ByteArrayInputStream byteInt = new ByteArrayInputStream(bytes);
        ObjectInputStream objInt = null;
        try {
            objInt = new ObjectInputStream(byteInt);
            Map<String, Object> result = (Map<String, Object>) objInt.readObject();//byte[]转map
            return result;
        } catch (IOException | ClassNotFoundException e) {
            PLog.e(TAG, e.toString());
        }
        return new HashMap<>();
    }

    public static String getFileName() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String date = format.format(new Date(System.currentTimeMillis()));
        return date;
    }

    public static String getDateEN() {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date1 = format1.format(new Date(System.currentTimeMillis()));
        return date1;
    }

    public static String getWeek(int year, String month, String date) {
        LocalDate localDate = LocalDate.parse(year + "-" + month + "-" + date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return localDate.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.JAPANESE);
    }

    public static void showMyToast(final Toast toast, final int cnt) {
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                toast.show();
            }
        }, 0, 3000);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                toast.cancel();
                timer.cancel();
            }
        }, cnt);
    }

    public static boolean counselingDateIsToday(String month, String date) {
        int year = LocalDate.now().getYear();
        String tempDate = year + "-" + month + "-" + date;
        LocalDate counselingDate = LocalDate.parse(tempDate.trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return counselingDate.isEqual(LocalDate.now());
    }

    public static String counselingDate(String month, String date, String times) {
        int year = LocalDate.now().getYear();
        return year + "-" + month + "-" + date + " " + times;
    }

    public static String counselingEndDate(String month, String date, String endTime) {
        int year = LocalDate.now().getYear();
        return year + "-" + month + "-" + date + " " + endTime;
    }


    // 两次点击按钮之间的点击间隔不能少于1000毫秒
    private static final int MIN_CLICK_DELAY_TIME = 1000;
    private static long lastClickTime;

    public static boolean isFastClick() {
        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            flag = true;
        }
        lastClickTime = curClickTime;
        return flag;
    }


    // 两次点击按钮之间的点击间隔不能少于2000毫秒
    private static final int MIN_CLICK_DELAY_TIME2 = 1000;
    private static long lastClickTime2;

    public static boolean isFastClick2() {
        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime2) >= MIN_CLICK_DELAY_TIME2) {
            flag = true;
        }
        lastClickTime2 = curClickTime;
        return flag;
    }


    // 正则验证url
    public static boolean IsUrl(String str) {
        String regex = "http(s)?://([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?";
        return match(regex, str);
    }

    private static boolean match(String regex, String str) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    public static String checkUrl(String url) {
        String temp = url.replace("https://", "").replace("http://", "");
        return temp;
    }


    @SuppressLint("WrongConstant")
    public static boolean checkNet(Context context) {
        // 判断是否具有可以用于通信渠道
        boolean mobileConnection = isMobileConnection(context);
        boolean wifiConnection = isWIFIConnection(context);
        if ( mobileConnection == false && wifiConnection == false ) {
            Toast.makeText(context,"ネットワークに接続できません", Constants.Toast_Length).show();
            return false;
        }
        return true;
    }

    @SuppressLint("WrongConstant")
    public static boolean checkNet2(Context context) {
        // 判断是否具有可以用于通信渠道
        boolean mobileConnection = isMobileConnection(context);
        boolean wifiConnection = isWIFIConnection(context);
        if ( mobileConnection == false && wifiConnection == false ) {
            return false;
        }
        return true;
    }


    /**
     * 判断手机接入点（APN）是否处于可以使用的状态
     *
     * @param context
     * @return
     */
    public static boolean isMobileConnection(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if ( networkInfo != null && networkInfo.isConnected() ) {
            return true;
        }
        return false;
    }

    /**
     * 判断当前wifi是否是处于可以使用状态
     *
     * @param context
     * @return
     */
    public static boolean isWIFIConnection(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if ( networkInfo != null && networkInfo.isConnected() ) {
            return true;
        }
        return false;
    }

    public static  String getPhotoUrl(String name,String token){
     String url=Constants.DEBUG_URL + "image/show?imageName=" + name + "&token=" + token;
     return url;
    }

}


