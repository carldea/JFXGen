package carlfx.gameengine;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Responsible for loading sound media to be played using an id or key.
 * Contains all sounds for use later.
 * <p/>
 * User: cdea
 */
public class SoundManager {
    ExecutorService soundPool = Executors.newFixedThreadPool(2);
    Map<String, String> soundMap = new HashMap<>();

    /**
     * Constructor to create a simple thread pool.
     *
     * @param numberOfThreads - number of threads to use media players in the map.
     */
    public SoundManager(int numberOfThreads) {
        soundPool = Executors.newFixedThreadPool(numberOfThreads);
    }

    /**
     * Load a sound into a map to later be played based on the id.
     *
     * @param id  - The identifier for a sound.
     * @param url - The url location of the media or audio resource. Usually in src/main/resources directory.
     */
    public void loadSound(String id, URL url) {
        soundMap.put(id, url.toExternalForm());
    }

    /**
     * Lookup a name resource to play sound based on the id.
     *
     * @param id identifier for a sound to be played.
     */
    public void playSound(final String id) {
        Runnable soundPlay = new Runnable() {
            @Override
            public void run() {
                Media sound = new Media(soundMap.get(id));
                final MediaPlayer mediaPlayer = new MediaPlayer(sound);
                mediaPlayer.setOnReady(new Runnable() {
                    @Override
                    public void run() {
                        mediaPlayer.play();
                    }
                });
            }
        };
        soundPool.execute(soundPlay);
    }

    /**
     * Stop all threads and media players.
     */
    public void shutdown() {
        soundPool.shutdown();
    }

}
