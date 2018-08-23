package com.ora.blockchain.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EosUtils {
    public static String getValue(Map<String,Object> src, String path){

        if(!path.contains("/")){
            return src.get(path).toString();
        }

        String path1 = path.substring(0, path.indexOf("/"));
        String path2 = path.substring(path.indexOf("/")+1);

        Map<String, Object> map = (Map)src.get(path1);
        return getValue(map, path2);

    }

    public static List getValueList(Map<String,Object> src, String path){

        if(!path.contains("/")){
            return (List)src.get(path);
        }

        String path1 = path.substring(0, path.indexOf("/"));
        String path2 = path.substring(path.indexOf("/")+1);

        Map<String, Object> map = (Map)src.get(path1);
        return getValueList(map, path2);

    }

    public static ArrayList<String> listToArrayList(List list){
        ArrayList<String> tmpList = new ArrayList<String>();
        for (int i = 0; i < list.size(); i++){
            tmpList.add(list.get(i).toString());
        }
        return  tmpList;
    }

}
