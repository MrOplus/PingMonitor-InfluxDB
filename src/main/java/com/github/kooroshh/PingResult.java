package com.github.kooroshh;

public class PingResult {
    private final float time ;
    private final int ttl ;
    private final String mAdress ;

    public float getTime() {
        return time;
    }
    public String getAddress() {
        return mAdress;
    }

    public int getTtl() {
        return ttl;
    }

    public PingResult(String address , float time, int ttl) {
        this.time = time;
        this.ttl = ttl;
        mAdress = address;
    }
}
