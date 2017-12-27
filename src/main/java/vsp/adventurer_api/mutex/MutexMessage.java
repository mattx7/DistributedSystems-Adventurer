package vsp.adventurer_api.mutex;

import vsp.Application;

public class MutexMessage {

    /**
     * the message: request or reply-ok
     */
    private String msg;

    /**
     * int, the lamport clock
     */
    private int time;

    /**
     * url to the endpoint where responses shall be send
     */
    private String reply;

    /**
     * url to user sending this message
     */
    private String user;

    public MutexMessage(String msg, int time) {
        this.msg = msg;
        this.time = time;
        this.reply = Application.ownIp + ":" + Application.OWN_PORT + "/";
        this.user = "/users/" + Application.user.getName();
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final MutexMessage that = (MutexMessage) o;

        if (time != that.time) return false;
        if (msg != null ? !msg.equals(that.msg) : that.msg != null) return false;
        if (reply != null ? !reply.equals(that.reply) : that.reply != null) return false;
        return user != null ? user.equals(that.user) : that.user == null;
    }

    @Override
    public int hashCode() {
        int result = msg != null ? msg.hashCode() : 0;
        result = 31 * result + time;
        result = 31 * result + (reply != null ? reply.hashCode() : 0);
        result = 31 * result + (user != null ? user.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MutexMessage{" +
                "msg='" + msg + '\'' +
                ", time=" + time +
                ", reply='" + reply + '\'' +
                ", user='" + user + '\'' +
                '}';
    }
}
