package io.github.lokka30.papichatformatter.utils;

import io.github.lokka30.papichatformatter.PAPIChatFormatter;

import java.util.Arrays;
import java.util.List;

public class Utils {

    public Utils() {
    }

    public List<String> getSupportedServerVersions() {
        return Arrays.asList(
                "1.15",
                "1.14",
                "1.13",
                "1.12",
                "1.11",
                "1.10",
                "1.9",
                "1.8",
                "1.7"
        );
    }

    public int getLatestSettingsFileVersion() { return 1; }
    public int getLatestMessagesFileVersion() { return 1; }
}
