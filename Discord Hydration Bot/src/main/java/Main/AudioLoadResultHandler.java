package James.Morand;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;


public class AudioLoadResultHandler implements com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler {
    private final AudioPlayer player;

    public AudioLoadResultHandler(AudioPlayer player) {
        this.player = player;
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        player.playTrack(track);
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        // Not used for single file
    }

    @Override
    public void noMatches() {
        System.err.println("No audio file found at the specified path!");
    }

    @Override
    public void loadFailed(FriendlyException exception) {
        System.err.println("Failed to load audio file: " + exception.getMessage());
    }
}
