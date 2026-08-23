package dio.budgeting.infrastructure.ai;

import org.springframework.ai.audio.tts.TextToSpeechModel;
import org.springframework.ai.audio.tts.TextToSpeechPrompt;
import org.springframework.ai.audio.tts.TextToSpeechResponse;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.ai.audio.transcription.TranscriptionModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Fluxo completo do desafio: áudio -> texto -> intenção -> tool calling -> áudio de resposta.
 */
@RestController
@RequestMapping("/api/voice-commands")
public class VoiceCommandController {

    private final ChatClient chatClient;
    private final TranscriptionModel transcriptionModel;
    private final TextToSpeechModel textToSpeechModel;

    public VoiceCommandController(ChatClient chatClient,
                                   TranscriptionModel transcriptionModel,
                                   TextToSpeechModel textToSpeechModel) {
        this.chatClient = chatClient;
        this.transcriptionModel = transcriptionModel;
        this.textToSpeechModel = textToSpeechModel;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = "audio/mpeg")
    public ResponseEntity<byte[]> handle(@RequestParam("audio") MultipartFile audio) throws IOException {
        // 1. Transcrição
        AudioTranscriptionResponse transcription = transcriptionModel.call(
                new AudioTranscriptionPrompt(new ByteArrayResource(audio.getBytes())));
        String transcript = transcription.getResult().getOutput();

        // 2. Intenção + Tool Calling
        String textResponse = chatClient.prompt()
                .user(transcript)
                .call()
                .content();

        // 3. Texto -> voz
        TextToSpeechResponse speechResponse = textToSpeechModel.call(new TextToSpeechPrompt(textResponse));
        byte[] audioBytes = speechResponse.getResult().getOutput();

        return ResponseEntity.ok().contentType(MediaType.valueOf("audio/mpeg")).body(audioBytes);
    }
}
