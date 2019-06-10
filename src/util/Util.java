package util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.MessageDigest;

public class Util {

    public static boolean strIsEmpty(String str) {
        return str == null || "".equals(str);
    }

    public static boolean containsFlag(int tag, int flag) {
        return (tag & flag) != 0;
    }

    public static int addFlag(int tag, int flag) {
        return (tag | flag);
    }

    public synchronized static String MD5(String s) {
        try {
            byte[] btInput = s.getBytes("UTF-8");
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

    public static String formatFileName(String store, String accessCount) {
        return String.format("%s_%s", store, accessCount);
    }

    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            if (children != null) {
                for (int i = 0; i < children.length; i++) {
                    boolean success = deleteDir(new File(dir, children[i]));
                    if (!success) {
                        return false;
                    }
                }
            }
        }
        return dir.delete();
    }

    public static Object toObject(byte[] bytes) {
        Object object = null;
        try {
            // 创建ByteArrayInputStream对象
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            // 创建ObjectInputStream对象
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            // 从objectInputStream流中读取一个对象
            object = objectInputStream.readObject();
            byteArrayInputStream.close();// 关闭输入流
            objectInputStream.close();// 关闭输入流
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return object;// 返回对象
    }

}
