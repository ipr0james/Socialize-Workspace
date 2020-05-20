package net.thenova.socialize.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Copyright 2019 ipr0james
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public final class UString {

    private static final Map<String, String> subscript = new HashMap<>();

    static {

        // ᵃ ᵇ ᶜ ᵈ ᵉ ᶠ ᵍ ʰ ᶦ ʲ ᵏ ˡ ᵐ ᵒ ᵖ ᵠ ʳ ˢ ᵗ ᵘ ᵛ ʷ ˣ ʸ ᶻ
        subscript.put("a", "ᵃ");
        subscript.put("b", "ᵇ");
        subscript.put("c", "ᶜ");
        subscript.put("d", "ᵈ");
        subscript.put("e", "ᵉ");
        subscript.put("f", "ᶠ");
        subscript.put("g", "ᵍ");
        subscript.put("h", "ʰ");
        subscript.put("i", "ᶦ");
        subscript.put("j", "ʲ");
        subscript.put("k", "ᵏ");
        subscript.put("l", "ˡ");
        subscript.put("m", "ᵐ");
        subscript.put("n", "ⁿ");
        subscript.put("o", "ᵒ");
        subscript.put("p", "ᵖ");
        subscript.put("q", "ᵠ");
        subscript.put("r", "ʳ");
        subscript.put("s", "ˢ");
        subscript.put("t", "ᵗ");
        subscript.put("u", "ᵘ");
        subscript.put("v", "ᵛ");
        subscript.put("w", "ʷ");
        subscript.put("x", "ˣ");
        subscript.put("y", "ʸ");
        subscript.put("z", "ᶻ");

        // ᴬ ᴮ ᶜ ᴰ ᴱ ᶠ ᴳ ᴴ ᴵ ᴶ ᴷ ᴸ ᴹ ᴺ ᴼ ᴾ ᵠ ᴿ ˢ ᵀ ᵁ ⱽ ᵂ ˣ ʸ ᶻ

        subscript.put("A", "ᴬ");
        subscript.put("B", "ᴮ");
        subscript.put("C", "ᶜ");
        subscript.put("D", "ᴰ");
        subscript.put("E", "ᴱ");
        subscript.put("F", "ᶠ");
        subscript.put("G", "ᴳ");
        subscript.put("H", "ᴴ");
        subscript.put("I", "ᴵ");
        subscript.put("J", "ᴶ");
        subscript.put("K", "ᴷ");
        subscript.put("L", "ᴸ");
        subscript.put("M", "ᴹ");
        subscript.put("N", "ᴺ");
        subscript.put("O", "ᴼ");
        subscript.put("P", "ᴾ");
        subscript.put("Q", "ᵠ");
        subscript.put("R", "ᴿ");
        subscript.put("S", "ˢ");
        subscript.put("T", "ᵀ");
        subscript.put("U", "ᵁ");
        subscript.put("V", "ⱽ");
        subscript.put("W", "ᵂ");
        subscript.put("X", "ˣ");
        subscript.put("Y", "ʸ");
        subscript.put("Z", "ᶻ");

        // ⁰ ¹ ² ³ ⁴ ⁵ ⁶ ⁷ ⁸ ⁹

        subscript.put("0", "⁰");
        subscript.put("1", "¹");
        subscript.put("2", "²");
        subscript.put("3", "³");
        subscript.put("4", "⁴");
        subscript.put("5", "⁵");
        subscript.put("6", "⁶");
        subscript.put("7", "⁷");
        subscript.put("8", "⁸");
        subscript.put("9", "⁹");
    }

    /**
     * Concatenates a string array.
     * @param input The string array.
     * @param delim The delimiter.
     * @param start The start index.
     * @return A concatenated string.
     */
    public static String concat(String[] input, String delim, int start) {
        if(input == null) {
            throw new IllegalArgumentException("input null");
        }
        if(start >= input.length) {
            throw new IllegalArgumentException("start > max index");
        }
        if(delim == null) {
            delim = "";
        }
        StringBuilder builder = new StringBuilder();
        for(int i = start; i < input.length; i++) {
            builder.append(input[i])
                    .append(delim);
        }
        String result = builder.toString();
        if(result.endsWith(delim)) {
            result = result.substring(0, result.length() - delim.length());
        }
        return result;
    }

    /**
     * Turns a string into a long.
     * @param input The string.
     * @return Null if error, otherwise a long.
     */
    public static Long toLong(String input) {
        try {
            return Long.valueOf(input);
        } catch(IllegalArgumentException exception) {
            return null;
        }
    }

    /**
     * Strips all mass mentions.
     * @param input The input.
     * @return Input with all the mass mentions stripped.
     */
    public static String stripMassMentions(String input) {
        if(input == null) {
            throw new IllegalArgumentException("input null");
        }
        return input
                .replace("@everyone", "@\u200Beveryone")
                .replace("@here", "@\u200Bhere");
    }

    /**
     * Escapes all formatting.
     * @param input The input.
     * @return Input with all formatting escaped.
     */
    public static String escapeFormatting(String input) {
        if(input == null) {
            throw new IllegalArgumentException("input null");
        }
        return input.replace("*", "\\*")
                .replace("_", "\\_")
                .replace("~", "\\~")
                .replace("`", "\\`");
    }

    /**
     * Convert a string in to Superscript
     *
     * @param input - input string
     * @return - Convert to superscript
     */
    public static String superscript(String input) {
        for(Map.Entry<String, String> subscripts : subscript.entrySet()) {
            input = input.replace(subscripts.getKey(), subscripts.getValue());
        }

        return input;
    }

}
