package util;

import java.security.MessageDigest;

public class Util {

    public static boolean strIsEmpty(String str){
        return str == null || "".equals(str);
    }

    public static boolean containsFlag(int tag,int flag){
        return (tag & flag) != 0;
    }

    public static int addFlag(int tag,int flag){
        return (tag | flag);
    }

    public synchronized static String MD5(String s) {
        try {
            byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();

            return bytes2Hex(md);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private static String bytes2Hex(byte[] bts) {
        String des = "";
        String tmp;

        for (byte bt : bts) {
            tmp = (Integer.toHexString(bt & 0xFF));
            if (tmp.length() == 1) {
                des += "0";
            }
            des += tmp;
        }
        return des;
    }

}
