package util;

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

}
