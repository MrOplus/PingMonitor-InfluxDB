package com.github.kooroshh;

public class HostInfo {
    public static String getHostType(){
        String os = System.getProperty("os.name");
        // System.out.println("Using SystemUtils: " + os);
        return os;
    }

}
