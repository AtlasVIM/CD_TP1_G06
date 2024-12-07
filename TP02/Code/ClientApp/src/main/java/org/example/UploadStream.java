package org.example;

import io.grpc.stub.StreamObserver;
import svcclientstubs.UploadResponse;

public class UploadStream implements StreamObserver<UploadResponse> {
    boolean completed=false;


    @Override
    public void onNext(UploadResponse uploadResponse) {
        System.out.println("Upload Stream onNext.");



    }

    @Override
    public void onError(Throwable throwable) {
        System.out.println("Upload Stream onError. Details: "+throwable.getMessage());
        throwable.printStackTrace();
        completed=true;

    }

    @Override
    public void onCompleted() {
        System.out.println("Upload Stream onCompleted called.");
        completed=true;
    }

    public boolean isCompleted() {
        return completed;
    }
}

