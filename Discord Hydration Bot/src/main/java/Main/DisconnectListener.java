package James.Morand;


import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

public class DisconnectListener extends AudioEventAdapter {
    private final AudioManager audioManager;
    private final VoiceChannel channel;

    public DisconnectListener(AudioManager audioManager, VoiceChannel channel) {
        this.audioManager = audioManager;
        this.channel = channel;
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        audioManager.closeAudioConnection();
        System.out.println("Finished playing sound in: " + channel.getName());
    }
}

