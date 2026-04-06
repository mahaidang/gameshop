package com.n2.gameshop.network;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public final class CloudinaryUploader {

    private CloudinaryUploader() {
    }

    public static String uploadImage(Context context, Uri imageUri, String cloudName, String uploadPreset)
            throws Exception {
        if (TextUtils.isEmpty(cloudName) || TextUtils.isEmpty(uploadPreset)) {
            throw new IllegalStateException("Thiếu cấu hình Cloudinary");
        }
        if (imageUri == null) {
            throw new IllegalArgumentException("Ảnh không hợp lệ");
        }

        String endpoint = "https://api.cloudinary.com/v1_1/" + cloudName + "/image/upload";
        String boundary = "----GameShopBoundary" + System.currentTimeMillis();

        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(endpoint).openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(30000);
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            DataOutputStream output = new DataOutputStream(connection.getOutputStream());
            writeTextPart(output, boundary, "upload_preset", uploadPreset);
            writeFilePart(context.getContentResolver(), output, boundary, "file", imageUri);
            output.writeBytes("--" + boundary + "--\r\n");
            output.flush();
            output.close();

            int code = connection.getResponseCode();
            InputStream stream = code >= 200 && code < 300
                    ? connection.getInputStream()
                    : connection.getErrorStream();
            String response = readStream(stream);

            if (code < 200 || code >= 300) {
                throw new IOException("Upload thất bại: " + response);
            }

            JSONObject json = new JSONObject(response);
            String secureUrl = json.optString("secure_url");
            if (TextUtils.isEmpty(secureUrl)) {
                throw new IOException("Cloudinary không trả về secure_url");
            }
            return secureUrl;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static void writeTextPart(DataOutputStream output, String boundary, String name, String value)
            throws IOException {
        output.writeBytes("--" + boundary + "\r\n");
        output.writeBytes("Content-Disposition: form-data; name=\"" + name + "\"\r\n\r\n");
        output.write(value.getBytes(StandardCharsets.UTF_8));
        output.writeBytes("\r\n");
    }

    private static void writeFilePart(ContentResolver resolver, DataOutputStream output,
                                      String boundary, String fieldName, Uri uri) throws IOException {
        String mimeType = resolver.getType(uri);
        if (TextUtils.isEmpty(mimeType)) {
            mimeType = "application/octet-stream";
        }

        String fileName = "upload_" + System.currentTimeMillis() + ".jpg";
        output.writeBytes("--" + boundary + "\r\n");
        output.writeBytes("Content-Disposition: form-data; name=\"" + fieldName
                + "\"; filename=\"" + fileName + "\"\r\n");
        output.writeBytes("Content-Type: " + mimeType + "\r\n\r\n");

        InputStream input = resolver.openInputStream(uri);
        if (input == null) {
            throw new IOException("Không thể đọc ảnh đã chọn");
        }
        try {
            byte[] buffer = new byte[8192];
            int count;
            while ((count = input.read(buffer)) != -1) {
                output.write(buffer, 0, count);
            }
        } finally {
            input.close();
        }
        output.writeBytes("\r\n");
    }

    private static String readStream(InputStream input) throws IOException {
        if (input == null) {
            return "";
        }
        try {
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int length;
            while ((length = input.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            return result.toString("UTF-8");
        } finally {
            input.close();
        }
    }
}

