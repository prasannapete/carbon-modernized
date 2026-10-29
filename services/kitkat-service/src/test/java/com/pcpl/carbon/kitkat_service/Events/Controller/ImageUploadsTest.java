package com.pcpl.carbon.kitkat_service.Events.Controller;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ImageUploadsTest {

    private static byte[] bytes(int... values) {
        byte[] out = new byte[values.length];
        for (int i = 0; i < values.length; i++) {
            out[i] = (byte) values[i];
        }
        return out;
    }

    @Test
    void detectsAllowedImagesByContent() {
        assertEquals(".png", ImageUploads.detectExtension(bytes(0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A, 0, 0, 0, 0)));
        assertEquals(".jpg", ImageUploads.detectExtension(bytes(0xFF, 0xD8, 0xFF, 0xE0, 0, 0, 0, 0, 0, 0, 0, 0)));
        assertEquals(".gif", ImageUploads.detectExtension("GIF89a......".getBytes(StandardCharsets.US_ASCII)));
        assertEquals(".gif", ImageUploads.detectExtension("GIF87a......".getBytes(StandardCharsets.US_ASCII)));
        assertEquals(".webp", ImageUploads.detectExtension("RIFF\0\0\0\0WEBP".getBytes(StandardCharsets.US_ASCII)));
    }

    @Test
    void rejectsScriptableAndUnknownContent() {
        assertNull(ImageUploads.detectExtension("<svg xmlns=\"http://www.w3.org/2000/svg\"><script>".getBytes(StandardCharsets.US_ASCII)));
        assertNull(ImageUploads.detectExtension("<!doctype html><script>alert(1)".getBytes(StandardCharsets.US_ASCII)));
        assertNull(ImageUploads.detectExtension("RIFF\0\0\0\0WAVE".getBytes(StandardCharsets.US_ASCII)));
        assertNull(ImageUploads.detectExtension(bytes(0x89, 'P', 'N')));
        assertNull(ImageUploads.detectExtension(new byte[0]));
    }

    @Test
    void onlyImageExtensionsAreServedInline() {
        assertEquals("image/png", ImageUploads.inlineContentType("123_logo.png"));
        assertEquals("image/jpeg", ImageUploads.inlineContentType("123_logo.JPEG"));
        assertEquals("image/webp", ImageUploads.inlineContentType("123_logo.webp"));
        assertNull(ImageUploads.inlineContentType("123_logo.svg"));
        assertNull(ImageUploads.inlineContentType("123_logo.html"));
        assertNull(ImageUploads.inlineContentType("scores.xlsx"));
        assertNull(ImageUploads.inlineContentType("noextension"));
    }
}
