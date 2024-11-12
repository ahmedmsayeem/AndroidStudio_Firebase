package com.example.videochat.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.videochat.R;
import com.example.videochat.utils.Message;
import com.example.videochat.utils.MessageAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Messaging extends AppCompatActivity {

    private static final String TAG = "MessagingActivity";

    private TextView tvUsername;
    private TextView tvNoMessages;
    private EditText etMessage;
    private Button btnSend;
    private Button filterButton;
    private EditText filterEditText;
    private RecyclerView recyclerViewMessages;

    private DatabaseReference dbReference;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;
    private String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        // Set up database reference
        dbReference = FirebaseDatabase.getInstance().getReference("messages");

        // Set up UI components
        tvUsername = findViewById(R.id.tvUsername);
        tvNoMessages = findViewById(R.id.tvNoMessages);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        recyclerViewMessages = findViewById(R.id.recyclerViewMessages);

        filterButton = findViewById(R.id.filterBtn);
        filterEditText = findViewById(R.id.filterName);
        Intent intent = getIntent();
        currentUsername = intent.getStringExtra("username");

        if (currentUsername != null) {
            tvUsername.setText(currentUsername);
        } else {
            currentUsername = "Anonymous"; // Fallback if username is null
            tvUsername.setText(currentUsername);
        }

        // Ensure no null views
        if (tvUsername == null || etMessage == null || btnSend == null || recyclerViewMessages == null || tvNoMessages == null) {
            Log.e(TAG, "One or more UI components are missing in the layout file.");
            Toast.makeText(this, "Error: UI components missing", Toast.LENGTH_SHORT).show();
            return;
        }

        // Set up RecyclerView
        messageList = new ArrayList<>();

        messageAdapter = new MessageAdapter(messageList);
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMessages.setAdapter(messageAdapter);


        // Load messages from Firebase
        loadMessages();


        // Send message button click listener
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String query = filterEditText.getText().toString().trim();

                if (TextUtils.isEmpty(query)) {
                    // If filter is empty, load all messages
                    loadMessages();
                } else {
                    // If filter is not empty, filter the messages
                    filterMessages(query);
                }
            }
        });
    }


    private void loadMessages() {
        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Message message = dataSnapshot.getValue(Message.class);
                    if (message != null) {
                        messageList.add(message);
                    } else {
                        Log.w(TAG, "Null message encountered in database snapshot.");
                    }
                }

                // Show or hide the "No messages" text based on the message list
                if (messageList.isEmpty()) {
                    tvNoMessages.setVisibility(View.VISIBLE);
                    recyclerViewMessages.setVisibility(View.GONE);
                } else {
                    tvNoMessages.setVisibility(View.GONE);
                    recyclerViewMessages.setVisibility(View.VISIBLE);
                    messageAdapter.notifyDataSetChanged();
                    recyclerViewMessages.smoothScrollToPosition(messageList.size() - 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load messages: " + error.getMessage());
                Toast.makeText(Messaging.this, "Failed to load messages", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMessage() {
        String messageText = etMessage.getText().toString().trim();
        if (TextUtils.isEmpty(messageText)) {
            Toast.makeText(this, "Cannot send an empty message", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a message object
        Message message = new Message(currentUsername, messageText);

        // Push the message to Firebase
        dbReference.push().setValue(message).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Clear the input
                etMessage.setText("");
            } else {
                Log.e(TAG, "Failed to send message: " + task.getException());
                Toast.makeText(Messaging.this, "Failed to send message", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to filter messages based on a given string (e.g., username or message content)
    private void filterMessages(String query) {

        List<Message> filteredList = new ArrayList<>();

        // Loop through the messages and add matching ones to the filtered list
        for (Message message : messageList) {
            if (message.getUsername().contains(query) || message.getContent().contains(query)) {
                filteredList.add(message);
            }
        }

        // Update the message list with the filtered messages and notify the adapter
        messageAdapter.updateMessageList(filteredList);

        // Notify the user if no messages match the query
        if (filteredList.isEmpty()) {
            tvNoMessages.setVisibility(View.VISIBLE);
            recyclerViewMessages.setVisibility(View.GONE);
        } else {
            tvNoMessages.setVisibility(View.GONE);
            recyclerViewMessages.setVisibility(View.VISIBLE);
        }
    }

}
