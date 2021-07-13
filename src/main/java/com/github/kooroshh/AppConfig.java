package com.github.kooroshh;

public class AppConfig {
    String[] hosts;
    Influx influx;
    class Influx {
        String url,secret,org,bucket;
    }
}
