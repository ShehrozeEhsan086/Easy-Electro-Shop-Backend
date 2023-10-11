package com.easyelectroshop.chatbotdataretrievingservice.Model;

import java.util.List;

public record Intents(
        String tag,
        List<String> patterns,
        List<String> responses

) {
}
