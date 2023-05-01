package com.jackson.ui;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class SoundEffectsController {

    private Clip clip;
    private AudioInputStream audioInputStream;
    private String filePath;

    private final String baseDir = "src/main/resources/sound/";
    private final String CAPTURE_DIR = baseDir + "capture.wav";
    private final String CASTLE_DIR = baseDir + "castle.wav";
    private final String MOVE_DIR = baseDir + "move.wav";
    private final String WIN_DIR = baseDir + "win.wav";


    public SoundEffectsController() {
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
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            throw new RuntimeException(e);
        }
    }

    public void playMoveEffect() {
        this.filePath = MOVE_DIR;
        resetAudioStream();
//        play();
    }

    public void playCaptureEffect() {
        this.filePath = CAPTURE_DIR;
        resetAudioStream();
//        play();
    }

    public void playCastleEffect() {
        this.filePath = CASTLE_DIR;
        resetAudioStream();
        play();
    }

    public void playWinEffect() {
        this.filePath = WIN_DIR;
        resetAudioStream();
        play();
    }


}
