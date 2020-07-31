package com.example.hometrainng.tools;

import android.content.Context;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;


/**
 * @Package com.example.hometrainng.tools
 * @Description PLog
 * @CreateDate: 2020/5/28 9:33 PM
 */
public class PLog {
    private static Boolean PLOG_SWITCH = true; // Log file switch
    private static Boolean PLOG_WRITE_TO_FILE = true;// log file write flag
    private static char PLOG_TYPE = 'v';// Enter the log type, w means to output only alarm information, etc., v means to output all information
    private static String PLOG_PATH_SDCARD_DIR = "/storage/emulated/0/Android/data/com.example.hometrainng/WgLog";//The path of the log file in the sdcard
    private static int PLOG_FILE_SAVE_DAYS = 14;// Maximum number of days to save log files in sd card
    private static String LOGFILEName = "LogWG.txt";//The name of the log file output by this class
    private static SimpleDateFormat myLogSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// Log output format
    private static SimpleDateFormat logfile = new SimpleDateFormat("yyyy-MM-dd");//Log file format
    public Context context;

    public static void w(String tag, Object msg) { // 警告信息
        log(tag, msg.toString(), 'w');
    }

    public static void e(String tag, Object msg) { // 错误信息
        log(tag, msg.toString(), 'e');
    }

    public static void d(String tag, Object msg) {// 调试信息
        log(tag, msg.toString(), 'd');
    }

    public static void i(String tag, Object msg) {//
        log(tag, msg.toString(), 'i');
    }

    public static void v(String tag, Object msg) {
        log(tag, msg.toString(), 'v');
    }

    public static void w(String tag, String text) {
        log(tag, text, 'w');
    }

    public static void e(String tag, String text) {
        log(tag, text, 'e');
    }

    public static void d(String tag, String text) {
        log(tag, text, 'd');
    }

    public static void i(String tag, String text) {
        log(tag, text, 'i');
    }

    public static void v(String tag, String text) {
        log(tag, text, 'v');
    }

    /**
     * According to tag, msg and level, output log
     *
     * @param tag
     * @param msg
     * @param level
     */
    private static void log(String tag, String msg, char level) {
        if (PLOG_SWITCH) {//Log file master switch
            if ('e' == level && ('e' == PLOG_TYPE || 'v' == PLOG_TYPE)) { //Output error message
                Log.e(tag, msg);
            } else if ('w' == level && ('w' == PLOG_TYPE || 'v' == PLOG_TYPE)) {
                Log.w(tag, msg);
            } else if ('d' == level && ('d' == PLOG_TYPE || 'v' == PLOG_TYPE)) {
                Log.d(tag, msg);
            } else if ('i' == level && ('i' == PLOG_TYPE || 'v' == PLOG_TYPE)) {
                Log.i(tag, msg);
            } else {
                Log.v(tag, msg);
            }
            if (PLOG_WRITE_TO_FILE)//Log write file switch
                writeLogtoFile(String.valueOf(level), tag, msg);
        }
    }

    /**
     * Open the log file and write to the log
     *
     * @param plogtype
     * @param tag
     * @param text
     */
    private static void writeLogtoFile(String plogtype, String tag, String text) {// Create or open a log file
        Date nowtime = new Date();
        String needWriteFile = logfile.format(nowtime);
        String needWriteMessage = myLogSdf.format(nowtime) + "    " + plogtype + "    " + tag + "    " + text;

        File dirsFile = new File(PLOG_PATH_SDCARD_DIR);
        if (!dirsFile.exists()) {
            dirsFile.mkdirs();
        }

        File file = new File(dirsFile.toString(), needWriteFile + LOGFILEName);// MYLOG_PATH_SDCARD_DIR
        if (!file.exists()) {
            boolean boo = false;
            try {
                //Create a file in the specified folder
                boo = file.createNewFile();
            } catch (Exception e) {
                Log.e("PLog_writeLogtoFile", "writeLogtoFile_error");
            }
            if (!boo) {
                Log.d("writeLogtoFile", "create fail");
            }

        }

        try(FileWriter filerWriter = new FileWriter(file, true);
            BufferedWriter bufWriter = new BufferedWriter(filerWriter);){
            bufWriter.write(needWriteMessage);
            bufWriter.newLine();
        } catch (IOException e) {
            Log.e("PLog_writeLogtoFile", "FileWriter_error");
        }
    }

    /**
     * Delete the specified log file
     */
    public static void delFile() {//Delete log files
        String needDelFile = logfile.format(getDateBefore());
        File dirsFile = new File(PLOG_PATH_SDCARD_DIR);
        File file = new File(dirsFile, needDelFile + LOGFILEName);// MYLOG_PATH_SDCARD_DIR
        boolean boo = false;
        if (file.exists()) {
            boo = file.delete();
        }
        if (!boo) {
            Log.e("delFile", "delFile_error");
        }
    }

    /**
     * Get the date a few days before the current time, used to get the log file name to be deleted
     */
    private static Date getDateBefore() {
        Date nowtime = new Date();
        Calendar now = Calendar.getInstance();
        now.setTime(nowtime);
        now.set(Calendar.DATE, now.get(Calendar.DATE) - PLOG_FILE_SAVE_DAYS);
        return now.getTime();
    }
}
