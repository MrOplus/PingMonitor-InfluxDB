package com.github.kooroshh;

import java.io.IOException;
import java.util.HashMap;

public class PingMonitor {
    public static void main(String[] args) throws IOException {
        HashMap<String,String> parsedArgs = CliParser.parse(args);
        AppConfig appConfig = ConfigParser.parseConfigFile(parsedArgs.get(CliParser.CONFIG));
        InfluxDBHelper helper = new InfluxDBHelper(
                appConfig.influx.url,
                appConfig.influx.secret,
                appConfig.influx.bucket,
                appConfig.influx.org);
        for (String host : appConfig.hosts) {
            PingProcessThread thread = new PingProcessThread(host);
            thread.setPingCallback((pingResult,error) -> {
                if(pingResult != null ) {
                    System.out.printf("Reply From %s, Time = %f , TTL = %d%n", pingResult.getAddress(), pingResult.getTime(), pingResult.getTtl());
                    helper.sendData(thread.getHostname(), pingResult);
                }else {
                    System.out.printf("Request %s Timed Out = %s!%n", thread.getHostname(), error);
                    helper.sendData(thread.getHostname(),error);
                }
            });
            thread.start();
        }
    }
}
