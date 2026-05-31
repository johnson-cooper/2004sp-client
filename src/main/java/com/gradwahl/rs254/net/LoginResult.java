package com.gradwahl.rs254.net;

public record LoginResult(int response, int staffModLevel, boolean mouseTracking) {
    public boolean success() { return response == 2; }

    @Override
    public String toString() {
        return switch (response) {
            case 2 -> "Login OK. staff=" + staffModLevel + ", mouseTracking=" + mouseTracking;
            case 3 -> "Invalid username or password.";
            case 4 -> "Account disabled.";
            case 5 -> "Already logged in.";
            case 6 -> "Client out of date / revision or CRC mismatch.";
            case 7 -> "World full.";
            case 16 -> "Too many login attempts.";
            default -> "Login response " + response;
        };
    }
}
