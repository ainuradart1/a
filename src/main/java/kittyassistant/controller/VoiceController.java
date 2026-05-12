package kittyassistant.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublishers;

@RestController
@RequestMapping("/api/voice")
public class VoiceController {

    @Value("${groq.api.key:}")
    private String apiKey;

    private static final String GROQ_WHISPER_URL =
            "https://api.groq.com/openai/v1/audio/transcriptions";

    @PostMapping("/transcribe")
    public ResponseEntity<String> transcribe(
            @RequestParam("audio") MultipartFile audio) {

        if (apiKey == null || apiKey.isBlank()) {
            return ResponseEntity.badRequest()
                    .body("API ключ не настроен");
        }

        try {
            String boundary = "----boundary" + System.currentTimeMillis();

            byte[] audioBytes = audio.getBytes();
            String fileName = "audio.webm";

            byte[] body = buildMultipartBody(boundary, audioBytes, fileName);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(GROQ_WHISPER_URL))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type",
                            "multipart/form-data; boundary=" + boundary)
                    .POST(BodyPublishers.ofByteArray(body))
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            String json = response.body();
            String text = extractText(json);

            return ResponseEntity.ok(text);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Ошибка: " + e.getMessage());
        }
    }

    private byte[] buildMultipartBody(String boundary,
                                      byte[] audio,
                                      String fileName) throws Exception {
        String CRLF = "\r\n";
        StringBuilder sb = new StringBuilder();

        sb.append("--").append(boundary).append(CRLF);
        sb.append("Content-Disposition: form-data; name=\"model\"")
                .append(CRLF).append(CRLF);
        sb.append("whisper-large-v3").append(CRLF);

        sb.append("--").append(boundary).append(CRLF);
        sb.append("Content-Disposition: form-data; name=\"language\"")
                .append(CRLF).append(CRLF);
        sb.append("ru").append(CRLF);

        sb.append("--").append(boundary).append(CRLF);
        sb.append("Content-Disposition: form-data; name=\"file\"; filename=\"")
                .append(fileName).append("\"").append(CRLF);
        sb.append("Content-Type: audio/webm").append(CRLF).append(CRLF);

        byte[] prefix = sb.toString().getBytes();
        byte[] suffix = (CRLF + "--" + boundary + "--" + CRLF).getBytes();

        byte[] result = new byte[prefix.length + audio.length + suffix.length];
        System.arraycopy(prefix, 0, result, 0, prefix.length);
        System.arraycopy(audio, 0, result, prefix.length, audio.length);
        System.arraycopy(suffix, 0, result,
                prefix.length + audio.length, suffix.length);

        return result;
    }

    private String extractText(String json) {
        int start = json.indexOf("\"text\"");
        if (start == -1) return "Не удалось распознать речь";
        start = json.indexOf("\"", start + 6) + 1;
        int end = json.indexOf("\"", start);
        return json.substring(start, end);
    }
}