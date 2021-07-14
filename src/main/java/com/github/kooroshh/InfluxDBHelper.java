package com.github.kooroshh;


import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApi;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

public class InfluxDBHelper {
    private String mToken = "";
    private String mBucket = "";
    private String mOrganization = "";
    private String mUrl = "";
    private volatile List<Point> buffer = new ArrayList<>();
    InfluxDBClient mClient;
    Timer timer = new Timer("TimerThread");
    public InfluxDBHelper(String url, String token , String bucket , String organization){
        this.mUrl = url;
        this.mToken = token;
        this.mBucket = bucket;
        this.mOrganization = organization;
        mClient = InfluxDBClientFactory.create(url, token.toCharArray(),organization,bucket);
        scheduleTimer();
    }
    void scheduleTimer(){
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                flush();
                scheduleTimer();
            }
        },10000);
    }
    public void sendData(String address , PingResult data){
        Point point = Point
                .measurement("Ping")
                .addTag("host",address)
                .addField("response_time",data.getTime())
                .addField("ttl",data.getTtl())
                .addField("error",0)

                .time(Instant.now(), WritePrecision.NS);
        push(point);
    }
    public void sendData(String address , int err){
        Point point = Point
                .measurement("Ping")
                .addTag("host",address)
                .addField("response_time",(float) -1)
                .addField("ttl",-1)
                .addField("error",err)
                .time(Instant.now(), WritePrecision.NS);
        push(point);
    }
    private synchronized void push(Point point){
        synchronized (buffer){
            buffer.add(point);
        }
    }
    private synchronized void flush(){
        synchronized (buffer) {
            if(buffer.size() > 0) {
                try (WriteApi writeApi = mClient.getWriteApi()) {
                    writeApi.writePoints(buffer);
                }
                buffer.clear();
            }
        }
    }
}
