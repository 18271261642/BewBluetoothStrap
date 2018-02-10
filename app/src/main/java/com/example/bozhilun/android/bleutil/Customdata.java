package com.example.bozhilun.android.bleutil;

import android.content.IntentFilter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Locale;

/**
 * Created by admin on 2017/3/6.
 * <p>
 * 自定义蓝牙的数据类型
 */

public class Customdata {

    //广播
    public static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA);
        intentFilter.addAction(BluetoothLeService.batterylevel);
        intentFilter.addAction(BluetoothLeService.ReadSteps);
        intentFilter.addAction(BluetoothLeService.Shakethecamera);
        intentFilter.addAction(BluetoothLeService.HeartRate);
        intentFilter.addAction(BluetoothLeService.OnekeyMeasurement);
        intentFilter.addAction(BluetoothLeService.Findphone);
        intentFilter.addAction(BluetoothLeService.Currentversionnumber);
        intentFilter.addAction(BluetoothLeService.DailyActivity);
        intentFilter.addAction(BluetoothLeService.Sleep);
        return intentFilter;
    }


    /**
     * byte转16进制
     *
     * @param b
     * @return
     */
    public static String byteToHex(byte b) {
        String result = Integer.toHexString(b & 0xFF);
        if (result.length() == 1) {
            result = '0' + result;
        }
        return result;
    }

    //电池换算方法
    public static int huansuan(int Adc) {
        double string = 0;
        if (Adc < 163) {
            string = 1.0;
        } else if (Adc >= 163) {
            string = -0.00077229 * Adc * Adc * Adc + 0.4513 * Adc * Adc - 85.4906 * Adc + 5292.5;
            if (string > 100) {
                string = 100;
            }
        }return (int) Math.round(string);}


    /**
     * 拼接字节数组
     * @param data1
     * @param data2
     * @return
     */
    public static byte[] addBytes(byte[] data1, byte[] data2) {
        byte[] data3 = new byte[data1.length + data2.length];
        System.arraycopy(data1, 0, data3, 0, data1.length);
        System.arraycopy(data2, 0, data3, data1.length, data2.length);
        return data3;
    }


    //多个数组合并
    public static <T> T[] concatAll(T[] first, T[]... rest) {
        int totalLength = first.length;
        for (T[] array : rest) {
            totalLength += array.length;
        }
        T[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (T[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }



    /**
     * 16进制转二进制
     */

    public static String hexString2binaryString(String hexString) {
        if (hexString == null || hexString.length() % 2 != 0)
            return null;
        String bString = "", tmp;
        for (int i = 0; i < hexString.length(); i++) {
            tmp = "0000"
                    + Integer.toBinaryString(Integer.parseInt(hexString
                    .substring(i, i + 1), 16));
            bString += tmp.substring(tmp.length() - 4);
        }
        return bString;
    }
    /**
     * 十六进制字符串转十进制
     *
     * @param hex 十六进制字符串
     * @return 十进制数值
     */
    public static int hexStringToAlgorism(String hex) {
        hex = hex.toUpperCase();
        int max = hex.length();
        int result = 0;
        for (int i = max; i > 0; i--) {
            char c = hex.charAt(i - 1);
            int algorism = 0;
            if (c >= '0' && c <= '9') {
                algorism = c - '0';
            } else {
                algorism = c - 55;
            }
            result += Math.pow(16, max - i) * algorism;
        }
        return result;
    }


    /**
     * 比较data 的大小
     * @param date1
     * @param date2
     * @return
     */
    public static boolean compare_date(String date1, String date2) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            java.util.Date d1 = df.parse(date1);
            java.util.Date d2 = df.parse(date2);
            if (d1.getTime() < d2.getTime()) {
                return true;
            }
            else if (d1.getTime() >d2.getTime()) {
                return false;
            } else {
                return false;
            }
        } catch (Exception exception) {
            exception.printStackTrace();}
        return false;}


    // byte转十六进制字符串
    public static String bytes2HexString(byte[] bytes) {
        String ret = "";
        for (byte aByte : bytes) {
            String hex = Integer.toHexString(aByte & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            ret += hex.toUpperCase(Locale.CHINA);
        }
        return ret;
    }

    /**
     * byte数组转换为16进制字符串
     * @param src
     * @return
     */
    public static String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }


}
