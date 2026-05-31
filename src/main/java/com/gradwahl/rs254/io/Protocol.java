package com.gradwahl.rs254.io;

public final class Protocol {
    private Protocol() {}

    public static final class Client {
        public static final int NO_TIMEOUT = 239;
        public static final int MOVE_GAMECLICK = 6;
        public static final int MESSAGE_PUBLIC = 83;
        public static final int CLIENT_CHEAT = 86;
        public static final int FRIENDLIST_ADD = 9;
        public static final int FRIENDLIST_DEL = 84;
        public static final int CHAT_SETMODE = 129;
    }

    public static final class Server {
        public static final int LOGOUT = 21;
        public static final int MESSAGE_GAME = 73;
        public static final int PLAYER_INFO = 87;
        public static final int NPC_INFO = 123;
        public static final int REBUILD_NORMAL = 209;

        public static final int[] SIZES = {
            6, 0, 0, 4, 0, 0, 0, 0, 7, 0, 0, 0, 0, 0, 4, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 3, 5, 0, 6, -2, 0, 4, 0,
            0, 0, 0, 0, 0, 15, 4, 0, 0, -2, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 6, 0, 0, 1, 0, -1, -2, 0, -2,
            6, 0, 0, 0, 0, 0, 4, 0, 0, -1, 0, 1, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 2, 0, -2, 2, 0, 0, 3, 0, 0, 1, 4, 0, 0,
            7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9, 0, 0, 6, 3,
            0, 0, 0, 0, 5, 0, 0, -2, 0, 0, 0, 6, 0, 0, 0, 0, 0, 0,
            0, 0, 6, 0, 1, 0, 0, 2, 0, 2, 0, 0, 10, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 2, 0, 2, 0, 2, 2, 0, 0, 0, 2, 0,
            -2, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 2,
            0, 0, 0, 0, 0, 0, 0, 0, 6, 2, 0, 0, 0, 0, 0, 0, -1, 0,
            0, 0, 0, 4, 0, 4, 0, 3, 0, 0, 0, 0, 14, 0, 0, 0, 6, 0,
            0, 4, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0,
            4, 0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0, 1, 0
        };
    }
}
