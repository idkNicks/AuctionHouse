package net.starly.auctionhouse.context;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public class MessageContent {

    private static MessageContent instance;
    private final Map<MessageType, Map<String, String>> messageMap = new HashMap<>();

    private MessageContent() {}

    public static MessageContent getInstance() {
        if (instance == null) instance = new MessageContent();
        return instance;
    }

    public void initialize(FileConfiguration file) {
        messageMap.clear();
        for (MessageType type : MessageType.values()) {
            ConfigurationSection configSection = file.getConfigurationSection(type.key);
            if (configSection != null) {
                initializeMessages(type, configSection);
            }
        }
    }

    private void initializeMessages(MessageType type, ConfigurationSection configSection) {
        Map<String, String> messages = messageMap.computeIfAbsent(type, key -> new HashMap<>());
        for (String key : configSection.getKeys(true)) {
            messages.put(key, ChatColor.translateAlternateColorCodes('&', configSection.getString(key)));
        }
    }

    public Optional<String> getMessage(MessageType type, String key) {
        return Optional.ofNullable(messageMap.getOrDefault(type, Collections.emptyMap()).get(key));
    }

    public Optional<String> getMessageAfterPrefix(MessageType type, String key) {
        String prefix = getMessage(MessageType.NORMAL, "prefix").orElse("");
        return getMessage(type, key).map(message -> prefix + message);
    }

    public List<String> getMessages(MessageType type, String path) {
        Map<String, String> messageTypeMap = messageMap.getOrDefault(type, Collections.emptyMap());
        String messagesString = messageTypeMap.getOrDefault(path, "");
        String[] messagesArray = messagesString.split("\\\\n");
        return Arrays.asList(messagesArray);
    }

    public int getInt(MessageType type, String key) {
        String value = messageMap.getOrDefault(type, Collections.emptyMap()).get(key);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) { e.printStackTrace(); }
        }
        return 0;
    }

    public float getFloat(MessageType type, String key) {
        String value = messageMap.getOrDefault(type, Collections.emptyMap()).get(key);
        if (value != null) {
            try {
                return Float.parseFloat(value);
            } catch (NumberFormatException e) { e.printStackTrace(); }
        }
        return 1;
    }
}
