package com.example.yanjiang.stockchart.rxutils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.example.yanjiang.stockchart.api.Constant;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;

public class FileUtils {

    /*返回体写入磁盘*/
    public static boolean writeResponseBodyToDisk(ResponseBody body,Context context) {
        try {
            String patchPath = Constant.EXTERNALPATH + Constant.APATCH_PATH;///storage/emulated/0/out.apatch
            // todo change the file location/name according to your needs
            File futureStudioIconFile = new File(patchPath);

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    Log.e("@@@", "file download: " + fileSizeDownloaded + " of " + fileSize + futureStudioIconFile.getPath());
                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }



}
