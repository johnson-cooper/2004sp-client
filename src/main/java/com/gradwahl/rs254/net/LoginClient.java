package com.gradwahl.rs254.net;

import com.gradwahl.rs254.ClientConfig;
import com.gradwahl.rs254.cache.RemoteCache;
import com.gradwahl.rs254.io.IsaacCipher;
import com.gradwahl.rs254.io.PacketBuffer;

import java.math.BigInteger;
import java.util.Random;

public final class LoginClient {
    // Fallback values from the attached server's webclient/bundle.ts.
    private static final BigInteger RSA_EXPONENT = new BigInteger("58778699976184461502525193738213253649000149147835990136706041084440742975821");
    private static final BigInteger RSA_MODULUS = new BigInteger("7162900525229798032761816791230527296329313291232324290237849263501208207972894053929065636522363163621000728841182238772712427862772219676577293600221789");

    private final ClientConfig config;
    private final Random random = new Random();
    private IsaacCipher outboundCipher;
    private IsaacCipher inboundCipher;

    public LoginClient(ClientConfig config) {
        this.config = config;
    }

    public LoginResult login(String username, String password, boolean reconnecting) throws Exception {
        int[] crcs = new RemoteCache(config).fetchCrcs();

        try (GameConnection connection = GameConnection.open(config.websocketUri())) {
            long userhash = usernameHash(username);
            int loginServer = (int) ((userhash >>> 16) & 0x1f);

            PacketBuffer handshake = new PacketBuffer(2);
            handshake.p1(14);
            handshake.p1(loginServer);
            connection.send(handshake.bytes());

            connection.readBytes(8); // server sends eight zero bytes first
            int response = connection.read();
            if (response != 0) {
                return new LoginResult(response, 0, false);
            }

            long serverSeed = new PacketBuffer(connection.readBytes(8)).g8();
            int[] seed = {
                random.nextInt(99_999_999),
                random.nextInt(99_999_999),
                (int) (serverSeed >>> 32),
                (int) serverSeed
            };

            PacketBuffer rsa = new PacketBuffer(256);
            rsa.p1(10);
            for (int value : seed) rsa.p4(value);
            rsa.p4(1337); // uid, matching current webclient
            rsa.pjstr(username);
            rsa.pjstr(password);
            rsa.rsaenc(RSA_MODULUS, RSA_EXPONENT);

            PacketBuffer login = new PacketBuffer(1 + 1 + 1 + 1 + 36 + rsa.pos);
            login.p1(reconnecting ? 18 : 16);
            login.p1(rsa.pos + 36 + 1 + 1);
            login.p1(config.revision());
            login.p1(0); // low memory flag
            for (int crc : crcs) login.p4(crc);
            login.pdata(rsa.data, 0, rsa.pos);

            outboundCipher = new IsaacCipher(seed.clone());
            int[] inboundSeed = seed.clone();
            for (int i = 0; i < inboundSeed.length; i++) inboundSeed[i] += 50;
            inboundCipher = new IsaacCipher(inboundSeed);

            connection.send(login.bytes());
            response = connection.read();
            if (response == 2) {
                int staff = connection.read();
                boolean mouseTracking = connection.read() == 1;
                return new LoginResult(response, staff, mouseTracking);
            }
            return new LoginResult(response, 0, false);
        }
    }

    public IsaacCipher outboundCipher() { return outboundCipher; }
    public IsaacCipher inboundCipher() { return inboundCipher; }

    private static long usernameHash(String username) {
        String clean = username.toLowerCase().trim();
        long hash = 0L;
        for (int i = 0; i < clean.length() && i < 12; i++) {
            char c = clean.charAt(i);
            hash *= 37L;
            if (c >= 'a' && c <= 'z') hash += 1 + c - 'a';
            else if (c >= '0' && c <= '9') hash += 27 + c - '0';
        }
        while (hash % 37L == 0L && hash != 0L) hash /= 37L;
        return hash;
    }
}
