package dev.ubujira.solzbot;

import com.freya02.botcommands.api.CommandsBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.ubujira.solzbot.reactionroles.ReactionRoles;
import dev.ubujira.solzbot.utils.Config;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.IOException;


public class SolzBot {

    @Getter
    private static SolzBot instance;

    @Getter
    private static Gson gson = new GsonBuilder().create();

    @Getter
    private JDA jda;

    @Getter
    private ReactionRoles reactionRoles;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public SolzBot() {
        logger.info("SolzBot - 1.0 - Ubujira");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            logger.error("Somehow failed to sleep after printing startup line????", e);
        }
        logger.info("Starting...");
        Config config = new Config();

        try {
            logger.info("Starting JDA!");
            jda = JDABuilder.createDefault(config.getBotToken()).build();
            try {
                jda.awaitReady();
                logger.info("JDA started!");
            } catch (InterruptedException e) {
                logger.error("Failed to start JDA!", e);
            }

            logger.info("Setting up command system!");
            final CommandsBuilder commandsBuilder = CommandsBuilder.newBuilder(745697970124750948L);

            commandsBuilder.textCommandBuilder(textCommandsBuilder -> textCommandsBuilder
                    .addPrefix("+")
            );

            try {
                commandsBuilder.build(jda, "dev.ubujira.solzbot.commands");
                logger.info("Command system is setup!");

                reactionRoles = new ReactionRoles();
            } catch (IOException e) {
                logger.error("Failed to setup command system!", e);
            }

        } catch (LoginException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new SolzBot();
    }

}
