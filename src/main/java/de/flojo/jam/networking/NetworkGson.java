package de.flojo.jam.networking;

import java.util.Objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.flojo.jam.networking.messages.MessageContainer;

public class NetworkGson {
    
    private NetworkGson() {
        throw new UnsupportedOperationException();
    }

    private static final Gson GSON_MAIN = new GsonBuilder().serializeNulls().create();

    public static Gson gson() {
        return GSON_MAIN;
    }

    public static MessageContainer getContainer(final String json) {
        if (json == null || json.isBlank())
            return null;
        return GSON_MAIN.fromJson(json, MessageContainer.class);
    }

    @SuppressWarnings("unchecked")
    public static <T extends MessageContainer> T getMessage(String json) {
        final MessageContainer container = Objects.requireNonNull(getContainer(json), "The container wasn't valid for: " + json);
        return (T) gson().fromJson(json, container.getType().getTargetClass());
    }

}
