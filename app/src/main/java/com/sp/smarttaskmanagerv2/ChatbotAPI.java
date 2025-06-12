package com.sp.smarttaskmanagerv2;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ChatbotAPI {
    @Headers({
            "Authorization: ",
            "Content-Type: application/json"
    })
    @POST("v1/chat/completions")
    Call<ChatResponse> getChatResponse(@Body ChatRequest request);
}
