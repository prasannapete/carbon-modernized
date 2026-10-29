package com.pcpl.carbon.kitkat_service.Events.Controller;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

// Logo uploads are served back from the frontend's origin, so only raster images are accepted
// (no SVG/HTML, which can carry script) and the type is decided from the file's content, never
// from the name the client sends.
final class ImageUploads {

    // Enough bytes for every signature below (WebP needs 12).
    static final int HEADER_LENGTH = 12;

    private static final Map<String, String> INLINE_TYPES = Map.of(
            "png", "image/png",
            "jpg", "image/jpeg",
            "jpeg", "image/jpeg",
            "gif", "image/gif",
            "webp", "image/webp");

    private ImageUploads() {
    }

    // Extension (with the dot) for a PNG/JPEG/GIF/WebP header, or null for anything else.
    static String detectExtension(byte[] header) {
        if (startsWith(header, 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A)) {
            return ".png";
        }
        if (startsWith(header, 0xFF, 0xD8, 0xFF)) {
            return ".jpg";
        }
        if (startsWithAscii(header, 0, "GIF87a") || startsWithAscii(header, 0, "GIF89a")) {
            return ".gif";
        }
        if (startsWithAscii(header, 0, "RIFF") && startsWithAscii(header, 8, "WEBP")) {
            return ".webp";
        }
        return null;
    }

    // Content type for files that may be shown inline (images only); null means serve as a download.
    static String inlineContentType(String fileName) {
        int dot = fileName.lastIndexOf('.');
        if (dot < 0) {
            return null;
        }
        return INLINE_TYPES.get(fileName.substring(dot + 1).toLowerCase(Locale.ROOT));
    }

    private static boolean startsWith(byte[] data, int... signature) {
        if (data.length < signature.length) {
            return false;
        }
        for (int i = 0; i < signature.length; i++) {
            if ((data[i] & 0xFF) != signature[i]) {
                return false;
            }
        }
        return true;
    }

    private static boolean startsWithAscii(byte[] data, int offset, String text) {
        byte[] expected = text.getBytes(StandardCharsets.US_ASCII);
        return data.length >= offset + expected.length
                && Arrays.equals(data, offset, offset + expected.length, expected, 0, expected.length);
    }
}
