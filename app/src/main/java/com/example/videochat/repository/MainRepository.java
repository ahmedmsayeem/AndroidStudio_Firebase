package com.example.videochat.repository;

import com.example.videochat.remote.FirebaseClient;
import com.example.videochat.utils.DataModel;
import com.example.videochat.utils.DataModelType;
import com.example.videochat.utils.ErrorCallback;
import com.example.videochat.utils.NewEventCallBack;
import com.example.videochat.utils.SuccessCallback;

public class MainRepository {
    private FirebaseClient firebaseClient;
    private static MainRepository instance;
    private String currentUsername;

    private void updateCurrentUsername(String username){
        this.currentUsername=username;
    }
    private MainRepository(){
        this.firebaseClient=new FirebaseClient();
    }
    public static MainRepository getInstance(){
        if(instance==null){
            instance=new MainRepository();
        }
        return instance;

    }

    public void login(String username, SuccessCallback callback){
        firebaseClient.login(username,()->{
            updateCurrentUsername(username);
            callback.onSuccess();
        });
    }

    public void sendCallRequest(String target, ErrorCallback errorCallback){
        firebaseClient.sendMessageToOtherUser(new DataModel(target,currentUsername,null, DataModelType.StartCall),errorCallback);


    }
    public void subscribeForLatestEvents(NewEventCallBack callback){
        firebaseClient.observeIncomingLatestEvent(model -> {
            switch (model.getType()) {
                case Offer:

                    break;
                case Answer:
                    break;
                case IceCandidate:
                    break;
                case StartCall:
                    break;
            }
        });
    }
}
