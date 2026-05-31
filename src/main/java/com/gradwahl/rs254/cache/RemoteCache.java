package com.gradwahl.rs254.cache;

import com.gradwahl.rs254.ClientConfig;
import com.gradwahl.rs254.io.PacketBuffer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public final class RemoteCache {
    private final ClientConfig config;
    private final HttpClient client = HttpClient.newHttpClient();

    public RemoteCache(ClientConfig config) {
        this.config = config;
    }

    public int[] fetchCrcs() throws IOException, InterruptedException {
        byte[] data = fetch("/crc");
        if (data.length < 36) {
            throw new IOException("/crc returned " + data.length + " bytes, expected 36");
        }
        PacketBuffer buf = new PacketBuffer(data);
        int[] crcs = new int[9];
        for (int i = 0; i < crcs.length; i++) crcs[i] = buf.g4();
        return crcs;
    }

    public byte[] fetch(String path) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(config.httpBaseUri() + path)).GET().build();
        HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
        if (response.statusCode() != 200) {
            throw new IOException(path + " returned HTTP " + response.statusCode());
        }
        return response.body();
    }
}
