package com.sp.smarttaskmanagerv2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatbotFragment extends Fragment {

    private EditText userInput;
    private Button sendButton;
    private TextView chatOutput;
    private ImageView chatIcon;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chatbot, container, false);

        userInput = view.findViewById(R.id.userInput);
        sendButton = view.findViewById(R.id.sendButton);
        chatOutput = view.findViewById(R.id.chatOutput);
        chatIcon = view.findViewById(R.id.ic_chat);

        sendButton.setOnClickListener(v -> {
            String message = userInput.getText().toString().trim();
            if (!message.isEmpty()) {
                sendMessageToChatbot(message);
            }
        });

        return view;
    }

    private void sendMessageToChatbot(String message) {
        ChatbotAPI api = RetrofitClient.getInstance().create(ChatbotAPI.class);
        ChatRequest request = new ChatRequest(message);
        Call<ChatResponse> call = api.getChatResponse(request);

        call.enqueue(new Callback<ChatResponse>() {
            @Override
            public void onResponse(Call<ChatResponse> call, Response<ChatResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    chatOutput.setText(response.body().getReply());
                } else {
                    chatOutput.setText("Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ChatResponse> call, Throwable t) {
                chatOutput.setText("Error: " + t.getMessage());
            }
        });
    }
}
