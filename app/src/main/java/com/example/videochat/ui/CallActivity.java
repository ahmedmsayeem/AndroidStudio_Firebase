package com.example.videochat.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.videochat.databinding.ActivityCallBinding;

import com.example.videochat.repository.MainRepository;
import com.example.videochat.utils.DataModelType;

public class CallActivity extends AppCompatActivity {
     private ActivityCallBinding views;
     private MainRepository mainRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        views = ActivityCallBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(views.getRoot());

        init();

    }


    private void init(){
        mainRepository = MainRepository.getInstance();
       views.callBtn.setOnClickListener(v -> {
             mainRepository.sendCallRequest(views.targetUserNameEt.getText().toString(),()->{

                 Toast.makeText(this, "couldnt find target", Toast.LENGTH_SHORT).show();
             });
       });
        mainRepository.subscribeForLatestEvents(data->{
            if (data.getType()== DataModelType.StartCall){
                runOnUiThread(()->{
                    views.incomingNameTV.setText(data.getSender()+" is Calling you");
                    views.incomingCallLayout.setVisibility(View.VISIBLE);
                    views.acceptButton.setOnClickListener(v->{
                        //star the call here
//                        mainRepository.startCall(data.getSender());
//                        views.incomingCallLayout.setVisibility(View.GONE);
                    });
                    views.rejectButton.setOnClickListener(v->{
                        views.incomingCallLayout.setVisibility(View.GONE);
                    });
                    views.rejectButton.setOnClickListener(v -> {
                        views.incomingCallLayout.setVisibility(View.GONE);
                    });
                });
            }
        });
    }
}