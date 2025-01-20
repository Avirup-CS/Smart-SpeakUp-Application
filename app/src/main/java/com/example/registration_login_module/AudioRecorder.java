package com.example.registration_login_module;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;

import com.assemblyai.api.AssemblyAI;
import com.assemblyai.api.resources.transcripts.types.Transcript;
import com.assemblyai.api.resources.transcripts.types.TranscriptStatus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class AudioRecorder {

    private static final int SAMPLE_RATE = 44100;
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private static final int BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);

    private final AssemblyAI assemblyAI;
    private boolean isRecording = false;

    public AudioRecorder(String assemblyAIKey) {
        assemblyAI = AssemblyAI.builder().apiKey(assemblyAIKey).build();
    }

    public File startRecording(Context context) throws IOException {
        File pcmFile = new File(context.getExternalCacheDir(), "recorded_audio.pcm");
        AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, BUFFER_SIZE);

        if (audioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
            throw new IllegalStateException("Failed to initialize AudioRecord");
        }

        audioRecord.startRecording();
        isRecording = true;

        // Record audio in a background thread
        new Thread(() -> {
            try (FileOutputStream outputStream = new FileOutputStream(pcmFile)) {
                byte[] buffer = new byte[BUFFER_SIZE];
                while (isRecording) {
                    int read = audioRecord.read(buffer, 0, buffer.length);
                    if (read > 0) {
                        outputStream.write(buffer, 0, read);
                    }
                }
                audioRecord.stop();
                audioRecord.release();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        return pcmFile;
    }

    public void stopRecording() {
        isRecording = false;
    }

    public File convertPcmToWav(File pcmFile) throws IOException {
        File wavFile = new File(pcmFile.getParent(), "recorded_audio.wav");

        try (FileInputStream pcmIn = new FileInputStream(pcmFile);
             FileOutputStream wavOut = new FileOutputStream(wavFile)) {

            long totalAudioLen = pcmFile.length();
            long totalDataLen = totalAudioLen + 36;
            int channels = 1;
            long byteRate = SAMPLE_RATE * channels * 2;

            writeWavHeader(wavOut, totalAudioLen, totalDataLen, SAMPLE_RATE, channels, byteRate);

            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = pcmIn.read(buffer)) != -1) {
                wavOut.write(buffer, 0, bytesRead);
            }
        }

        return wavFile;
    }

    private void writeWavHeader(FileOutputStream out, long totalAudioLen, long totalDataLen, long sampleRate, int channels, long byteRate) throws IOException {
        byte[] header = new byte[44];
        header[0] = 'R'; // RIFF
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W'; // WAVE
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f'; // fmt
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16; // Subchunk1Size (16 for PCM)
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1; // AudioFormat (1 for PCM)
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) (sampleRate & 0xff);
        header[25] = (byte) ((sampleRate >> 8) & 0xff);
        header[26] = (byte) ((sampleRate >> 16) & 0xff);
        header[27] = (byte) ((sampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (channels * 2); // BlockAlign
        header[33] = 0;
        header[34] = 16; // Bits per sample
        header[35] = 0;
        header[36] = 'd'; // data
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);

        out.write(header, 0, 44);
    }

    public String transcribeAudio(File wavFile) throws Exception {
        Transcript transcript = assemblyAI.transcripts().transcribe(wavFile);
        if (transcript.getStatus() == TranscriptStatus.COMPLETED) {
            return transcript.getText().orElse("No text available");
        } else {
            throw new Exception("Transcription failed: " + transcript.getError().orElse("Unknown error"));
        }
    }
}
