package com.github.kooroshh;

public class AppConfig {
    String[] hosts;
    Influx influx;
    static class Influx {
        String url,secret,org,bucket;
    }
}
