package com.example.yanjiang.stockchart.event;

public class ProgressUpdateEvent {
    private long bytesRead,contentLength;
    private boolean done;

    public ProgressUpdateEvent(long bytesRead, long contentLength, boolean done){
        this.bytesRead = bytesRead;
        this.contentLength=contentLength;
        this.done=done;
    }
    public long getbytesRead(){
        return bytesRead;
    }
    public long getcontentLength(){
        return contentLength;
    }
    public boolean getdone(){
        return done;
    }

}
