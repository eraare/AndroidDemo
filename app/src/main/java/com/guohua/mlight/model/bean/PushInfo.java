package com.guohua.mlight.model.bean;

/**
 * 推送消息
 */
public class PushInfo {
    public APS aps;
    public String title;
    public String text;
    public String action;

    public PushInfo() {
    }

    public PushInfo(APS aps, String title, String text, String action) {
        this.aps = aps;
        this.title = title;
        this.text = text;
        this.action = action;
    }

    public APS getAps() {
        return aps;
    }

    public void setAps(APS aps) {
        this.aps = aps;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public class APS {
        public String sound;
        public String alert;
        public int badge;

        public APS() {
        }

        public APS(String sound, String alert, int badge) {
            this.sound = sound;
            this.alert = alert;
            this.badge = badge;
        }

        public String getSound() {
            return sound;
        }

        public void setSound(String sound) {
            this.sound = sound;
        }

        public String getAlert() {
            return alert;
        }

        public void setAlert(String alert) {
            this.alert = alert;
        }

        public int getBadge() {
            return badge;
        }

        public void setBadge(int badge) {
            this.badge = badge;
        }
    }
}
