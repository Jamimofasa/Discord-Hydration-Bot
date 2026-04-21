package James.Morand;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.requests.GatewayIntent;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer;


import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HydrationBot {
    private static JDA jda;
    private static AudioPlayerManager playerManager;
    private static String botToken;
    private static String soundFilePath;

    public static void main(String[] args) {
        try {
            // Load configuration
            loadConfig();

            // Initialize audio player manager
            playerManager = new DefaultAudioPlayerManager();
            playerManager.getConfiguration().setFrameBufferFactory(NonAllocatingAudioFrameBuffer::new);
            AudioSourceManagers.registerLocalSource(playerManager);

            // Build the JDA instance
            jda = JDABuilder.createDefault(botToken)
                    .enableIntents(GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_MEMBERS)
                    .build();

            // Wait for JDA to be ready
            jda.awaitReady();
            System.out.println("Bot is ready!");

            // Schedule the hydration check task to run every hour
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            scheduler.scheduleAtFixedRate(() -> checkVoiceChannels(), 0, 1, TimeUnit.HOURS);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void loadConfig() {
        Properties props = new Properties();
        try {
            // Try to load from file first
            try (FileInputStream fis = new FileInputStream("config.properties")) {
                props.load(fis);
            } catch (Exception e) {
                // If file doesn't exist, try to load from resources
                try (InputStream is = HydrationBot.class.getClassLoader().getResourceAsStream("config.properties")) {
                    if (is != null) {
                        props.load(is);
                    }
                }
            }

            botToken = props.getProperty("bot.token", "YOUR_BOT_TOKEN_HERE");
            soundFilePath = props.getProperty("sound.file.path", "sounds/hydration_reminder.mp3");

            System.out.println("Configuration loaded successfully");
        } catch (Exception e) {
            System.err.println("Error loading config: " + e.getMessage());
            // Use defaults
            botToken = "YOUR_BOT_TOKEN_HERE";
            soundFilePath = "sounds/hydration_reminder.mp3";
        }
    }

    private static void checkVoiceChannels() {
        System.out.println("Checking voice channels for hydration reminders...");

        for (Guild guild : jda.getGuilds()) {
            List<VoiceChannel> voiceChannels = guild.getVoiceChannels();

            for (VoiceChannel channel : voiceChannels) {
                List<Member> members = channel.getMembers();

                // Check if there are any non-bot members
                boolean hasUsers = members.stream().anyMatch(m -> !m.getUser().isBot());

                if (hasUsers) {
                    // Play sound in the voice channel
                    playSoundInChannel(guild, channel);

                    // Also send DMs to users
                    for (Member member : members) {
                        if (!member.getUser().isBot()) {
                            sendHydrationReminder(member);
                        }
                    }
                }
            }
        }
    }

    private static void playSoundInChannel(Guild guild, VoiceChannel channel) {
        AudioManager audioManager = guild.getAudioManager();
        AudioPlayer player = playerManager.createPlayer();

        // Set up audio sender
        AudioPlayerSendHandler sendHandler = new AudioPlayerSendHandler(player);
        audioManager.setSendingHandler(sendHandler);

        // Add event listener to disconnect after playing
        player.addListener(new DisconnectListener(audioManager, channel));

        // Connect to voice channel
        audioManager.openAudioConnection(channel);
        System.out.println("Playing hydration reminder in: " + channel.getName());

        // Load and play the audio file
        playerManager.loadItem(soundFilePath, new AudioLoadResultHandler(player));
    }

    private static void sendHydrationReminder(Member member) {
        member.getUser().openPrivateChannel().queue(channel -> {
            channel.sendMessage("💧 **Hydration Reminder!** 💧\n\n" +
                    "Hey " + member.getEffectiveName() + "! You've been in a voice channel for a while. " +
                    "Don't forget to drink some water and stay hydrated! 🚰").queue(
                    success -> System.out.println("Sent hydration reminder to: " + member.getEffectiveName()),
                    error -> System.err.println("Failed to send message to: " + member.getEffectiveName())
            );
        }, error -> {
            System.err.println("Could not open private channel with: " + member.getEffectiveName());
        });
    }
}
