package com.modureview.service.utill;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SummarizationService {

  private final Client client;
  private final String modelId;

  /**
   * 단일 생성자만 남기면 스프링이 자동으로 주입해 줍니다.
   */
  public SummarizationService(
      @Value("${genai.api-key}") String apiKey,
      @Value("${genai.model:gemini-2.0-flash}") String modelId
  ) {
    this.client = Client.builder()
        .apiKey(apiKey)
        .build();
    this.modelId = modelId;
  }

  public String summarize(String title, String text) {
    if (text == null || text.isBlank()) {
      return "";
    }
    String prompt = "다음 글을 130자 내외로 " + title + "을 기준으로 한국어로 요약해줘:\n" + text;

    // ★ 여기가 핵심 수정 포인트 ★
    // 잘못된 호출: client.models() → 올바른 호출: client.models
    GenerateContentResponse resp = client.models
        .generateContent(modelId, prompt, null);

    return resp.text().trim();
  }
}