package com.github.kooroshh;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PingProcess {
    Process mProcess;
    List<String> mProcessArgs = new ArrayList<>();
    IPingResult mPingCallback;
    String mAddress;
    public PingProcess(String hostname){
        mAddress = hostname;
        if (HostInfo.getHostType().toLowerCase(Locale.ROOT).contains("windows")){
            mProcessArgs.add(System.getenv("WINDIR") + "\\system32\\" + "ping.exe");
            mProcessArgs.add(hostname);
            mProcessArgs.add("-t"); // ping 4 ever
            mProcessArgs.add("-w");// timeout
            mProcessArgs.add("700");// timeout
        }else if (HostInfo.getHostType().toLowerCase(Locale.ROOT).contains("linux")){
            mProcessArgs.add("/usr/bin/ping");
            mProcessArgs.add(hostname);
            mProcessArgs.add("-O"); // ping 4 ever
            mProcessArgs.add("-W");// timeout
            mProcessArgs.add("700");// timeout
        }
    }
    public void setPingCallback(IPingResult callback){
        mPingCallback = callback;
    }
    public int startPingProcess() throws IOException, InterruptedException {
        mProcess = new ProcessBuilder(mProcessArgs).start();
        Thread stdOutThread = stdOutThread();
        Thread stdErrThread = stdErrThread();
        stdOutThread.start();
        stdErrThread.start();
        int exitCode = mProcess.waitFor();
        stdOutThread.join();
        stdOutThread.join();
        return exitCode;
    }
    private Thread stdOutThread(){
        return new Thread(() -> {
            BufferedReader is = new BufferedReader(new InputStreamReader(mProcess.getInputStream()));
            String line ;
            while(mProcess.isAlive()){
                try {
                    line = is.readLine();
                    if(line != null)
                        parsePingLine(line);
                }catch (IOException ioException ) {
                    ioException.printStackTrace();
                }
            }
        });
    }
    private Thread stdErrThread(){
        return new Thread(() -> {
            BufferedReader es = new BufferedReader(new InputStreamReader(mProcess.getErrorStream()));
            String line ;
            while(mProcess.isAlive()){
                try {
                    line = es.readLine();
                    if(line != null)
                        parsePingLine(line);
                }catch (IOException ioException ) {
                    ioException.printStackTrace();
                }
            }
        });
    }
    private synchronized void parsePingLine(String line){
        if(mPingCallback == null ){
            return;
        }
        if(line.contains("no answer yet") || line.contains("Request timed out")) {
            mPingCallback.OnPingResult(null,1);
        }else if(line.contains("Name or service not known") || line.contains("Ping request could not find host")) {
            mPingCallback.OnPingResult(null,2);
        }
        Pattern patternWin = Pattern.compile("Reply from (.*): bytes=(.*) time=(.*)ms TTL=(.*)");
        Pattern patternNix = Pattern.compile("(.*) bytes from (.*): icmp_seq=(.*) ttl=(.*) time=(.*) ms");
        Matcher matcherWin = patternWin.matcher(line);
        Matcher matcherNix = patternNix.matcher(line);
        if(matcherNix.matches()){
            //NIX
            String time = matcherNix.group(4);
            String ttl = matcherNix.group(5);
            mPingCallback.OnPingResult(new PingResult(mAddress,Float.parseFloat(time),Integer.parseInt(ttl)),0);
        }else if (matcherWin.matches()) {
            //WIN
            String time = matcherWin.group(3);
            String ttl = matcherWin.group(4);
            mPingCallback.OnPingResult(new PingResult(mAddress,Float.parseFloat(time),Integer.parseInt(ttl)),0);
        }
    }
}
