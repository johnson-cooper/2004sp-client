# Java 254 Client

Early Java desktop client for a LostCity/2004Scape revision 254 server.

This is **not a complete game client yet**. The first milestone is a runnable Java shell with the exact login/update protocol pieces pulled from the attached server/webclient:

- revision `254`
- WebSocket transport to `/` on the game server
- `/crc` checksum loading
- 254 login handshaking opcodes `14`, `16`, `18`
- ISAAC cipher implementation
- client/server protocol constants
- a Swing/Canvas 50 TPS game loop

## Run

```bash
mvn package
java -jar target/java-254-client-0.1.0.jar
```

Default server is `localhost:43594`. You can override it:

```bash
java -Drs254.host=127.0.0.1 -Drs254.port=43594 -jar target/java-254-client-0.1.0.jar
```

## Login RSA

The server/webclient bundle has fallback public RSA values in `webclient/bundle.ts`. They are currently mirrored in `LoginClient`. If you regenerate `LOGIN_RSAE` / `LOGIN_RSAN` on the server, update those constants in:

```text
src/main/java/com/gradwahl/rs254/net/LoginClient.java
```

## Next milestones

1. Decode the title/config/media JAG archives from the HTTP endpoints.
2. Render the original 765x503 fixed client layout.
3. Implement inbound packet decoding using `ServerProtocol.SIZES`.
4. Add player/NPC/map update decoding.
5. Port Pix2D/Pix3D/model/scene systems from the TypeScript webclient or original 254 Java source.
