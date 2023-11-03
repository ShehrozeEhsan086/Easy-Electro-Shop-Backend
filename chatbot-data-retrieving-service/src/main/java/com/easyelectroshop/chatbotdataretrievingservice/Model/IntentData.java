package com.easyelectroshop.chatbotdataretrievingservice.Model;

import java.util.List;

public record IntentData(String tag, List<String> patterns, List<String> responses) {
}
