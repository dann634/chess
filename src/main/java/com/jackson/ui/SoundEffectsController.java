package com.jackson.ui;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SoundEffectsController {

    private Clip clip;
    private AudioInputStream audioInputStream;
    private String filePath;

    private final String baseDir = "src/main/resources/sound/";
    private final String MOVE_DIR = baseDir + "move.wav";


    public SoundEffectsController() { // FIXME: 11/05/2023 Sometimes piece is stuck waiting for sound
        try {
            this.filePath = MOVE_DIR;
            audioInputStream = AudioSystem.getAudioInputStream(new File(filePath).getAbsoluteFile());
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);

        } catch (UnsupportedAudioFileException e) {
            System.err.println("Error: Unsupported File Format");
        } catch (IOException e) {
            System.err.println("Error: Music Couldn't be played");
        } catch (LineUnavailableException e) {
            System.err.print("Error: Music couldn't be played");
        }
    }



    private void play() {
        clip.start();
    }

    private void stop() {
        clip.stop();
        clip.close();
    }

    private void resetAudioStream() {
        try {
            stop();
            audioInputStream = AudioSystem.getAudioInputStream(new File(filePath).getAbsoluteFile());
            clip.open(audioInputStream);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException | IllegalStateException ignored) {
        }
    }

    public void playSound(String soundEffectName) {
        Map<String, String> map = new HashMap<>();
        map.put("move", MOVE_DIR);
        String CAPTURE_DIR = baseDir + "capture.wav";
        map.put("capture", CAPTURE_DIR);
        String CASTLE_DIR = baseDir + "castle.wav";
        map.put("castle", CASTLE_DIR);
        String WIN_DIR = baseDir + "win.wav";
        map.put("win", WIN_DIR);

        this.filePath = map.get(soundEffectName);
        resetAudioStream();
        play();

    }


}
