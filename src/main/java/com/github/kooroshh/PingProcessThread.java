package com.github.kooroshh;

public class PingProcessThread extends Thread {
    public String getHostname() {
        return mHostname;
    }

    private final String mHostname;
    private final PingProcess mProcess;
    private IPingResult mCallback = null;
    public void setPingCallback(IPingResult callback){
        this.mCallback = callback;
    }
    public PingProcessThread(String hostname){
        this.mHostname = hostname;
        this.mProcess = new PingProcess(hostname);
    }
    @Override
    public void run() {
        super.run();
        try {
            mProcess.setPingCallback((pingResult,error) -> {
                if(mCallback != null)
                    mCallback.OnPingResult(pingResult,error);
            });
            mProcess.startPingProcess();
        }catch (Exception exception){
            exception.printStackTrace();
        }
    }
    public void stopProcess(){
        mProcess.stop();
    }
}
