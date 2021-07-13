package com.github.kooroshh;

public class PingMonitor {
    public static void main(String[] args){
        String[] hosts = new String[]{
                "4.2.2.4",
                "8.8.8.8",
                "1.1.1.1",
                "8.8.4.4"
        };
        for (String host : hosts) {
            PingProcessThread thread = new PingProcessThread(host);
            thread.setPingCallback((pingResult,error) -> {
                if(pingResult != null )
                    System.out.printf("Reply From %s, Time = %f , TTL = %d%n", pingResult.getAddress(),pingResult.getTime(),pingResult.getTtl());
                else
                    System.out.printf("Request %s Timed Out = %s!%n",thread.getHostname(),error);
            });
            thread.start();
        }
    }
}
