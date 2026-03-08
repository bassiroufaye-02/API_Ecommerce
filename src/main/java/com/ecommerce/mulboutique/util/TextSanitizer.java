package com.ecommerce.mulboutique.util;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

public final class TextSanitizer {

    private TextSanitizer() {}

    public static String clean(String input) {
        if (input == null) {
            return null;
        }
        String trimmed = input.trim();
        if (trimmed.isEmpty()) {
            return trimmed;
        }
        return Jsoup.clean(trimmed, Safelist.none());
    }
}
