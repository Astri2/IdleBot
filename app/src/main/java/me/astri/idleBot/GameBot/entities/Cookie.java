package me.astri.idleBot.GameBot.entities;

public class Cookie {
    private int cookies;
    private long lastCookie;

    public Cookie() {
        this.cookies = 0;
    }

    public void giveCookie() {
        cookies++;
    }

    public int getNumber() {
        return cookies;
    }

    public void setCookies(int cookies) {
        this.cookies = cookies;
    }

    public void setLastCookie() {
        lastCookie = System.currentTimeMillis();
    }

    public long getLastCookie() {
        return lastCookie;
    }
}
