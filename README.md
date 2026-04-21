Setup Instructions

1. Add JDA dependency to your project:
For Maven (pom.xml):
<dependencies>
    <dependency>
        <groupId>net.dv8tion</groupId>
        <artifactId>JDA</artifactId>
        <version>5.0.0-beta.20</version>
    </dependency>
    <dependency>
        <groupId>com.sedmelluq</groupId>
        <artifactId>lavaplayer</artifactId>
        <version>1.3.77</version>
    </dependency>
</dependencies>

For Gradle (build.gradle):
dependencies {
    implementation 'net.dv8tion:JDA:5.0.0-beta.20'
    implementation 'com.sedmelluq:lavaplayer:1.3.77'
}


Create the Project Structure
Create these folders and files:
hydration-bot/

 ├── pom.xml
 ├── config.properties (optional, for external config)
 
 └── src/
     └── main/
        ├── java/
        │   └── com/
        │       └── hydrationbot/
        │           ├── HydrationBot.java
        │           ├── AudioPlayerSendHandler.java
        |           ├── DisconnectListener.java
        │           └── AudioLoadResultHandler.java
        └── resources/
            ├── config.properties
            └── sounds/
                └── hydration_reminder.mp3


Configure the Bot
Edit src/main/resources/config.properties:
propertiesbot.token=YOUR_ACTUAL_BOT_TOKEN
sound.file.path=sounds/hydration_reminder.mp3

Add Your Sound File
Place your audio file in src/main/resources/sounds/hydration_reminder.mp3

Build the Project
bashmvn clean package
This creates an executable JAR with all dependencies in target/discord-hydration-bot-1.0.0.jar

Run the Bot
bashjava -jar target/discord-hydration-bot-1.0.0.jar
Or during development:
bashmvn exec:java -Dexec.mainClass="com.hydrationbot.HydrationBot"


2. Create a Discord Bot:

Go to https://discord.com/developers/applications
Create a new application
Go to the "Bot" section and create a bot
Copy the bot token and replace YOUR_BOT_TOKEN_HERE in the code
Enable these Privileged Gateway Intents: Server Members Intent, Presence Intent

3. Invite the bot to your server:

Go to OAuth2 → URL Generator
Select scopes: bot
Select permissions: Send Messages, Read Messages/View Channels
Copy and visit the generated URL

Setup Instructions

Update bot permissions - When inviting the bot, add these additional permissions:

Connect (to join voice channels)
Speak (to play audio)


Add your sound file:

Place your audio file (MP3, WAV, etc.) in your project directory
Update SOUND_FILE_PATH to point to your file (e.g., "sounds/hydration_reminder.mp3")
You can use a full path like "C:/path/to/your/file.mp3" or a relative path



How It Works Now

Every hour, the bot checks all voice channels
If users are in a channel, it:

Joins the voice channel
Plays your sound file
Automatically disconnects after the sound finishes
Sends DMs to all users in the channel



The bot will play the sound once per channel (not once per user), so everyone hears it together!

You can adjust the reminder interval by changing the TimeUnit.HOURS parameter in the scheduleAtFixedRate method.


Features

Properly organized Maven project structure

Configuration file for easy bot token management

Executable JAR generation with all dependencies included

Logging support with SLF4J

Checks voice channels every hour

Plays sound file and sends DMs to users

The bot will load the configuration and sound file from the resources folder automatically!




