package com.opdar.seed.extra.apns;

/**
 * Created by 俊帆 on 2015/12/21.
 */
public class Payload {
    private Object alert;
    private int badge;
    private String sound;
    private int contentAvailable;
    private String category;

    public static class Alert{
        private String title;
        private String body;
        private String titleLocKey;
        private String[] titleLocArgs;
        private String actionLocKey;
        private String locKey;
        private String[] locArgs;
        private String launchImage;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public String getTitleLocKey() {
            return titleLocKey;
        }

        public void setTitleLocKey(String titleLocKey) {
            this.titleLocKey = titleLocKey;
        }

        public String[] getTitleLocArgs() {
            return titleLocArgs;
        }

        public void setTitleLocArgs(String[] titleLocArgs) {
            this.titleLocArgs = titleLocArgs;
        }

        public String getActionLocKey() {
            return actionLocKey;
        }

        public void setActionLocKey(String actionLocKey) {
            this.actionLocKey = actionLocKey;
        }

        public String getLocKey() {
            return locKey;
        }

        public void setLocKey(String locKey) {
            this.locKey = locKey;
        }

        public String[] getLocArgs() {
            return locArgs;
        }

        public void setLocArgs(String[] locArgs) {
            this.locArgs = locArgs;
        }

        public String getLaunchImage() {
            return launchImage;
        }

        public void setLaunchImage(String launchImage) {
            this.launchImage = launchImage;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            boolean notFirst = false;
            builder.append("{");
            if(title != null){
                notFirst = true;
                builder.append("\"title\":\"" ).append(title).append("\"");
            }
            if(body != null){
                notFirst = checkFirst(notFirst,builder);
                builder.append("\"body\":\"" ).append(body).append("\"");
            }
            if(titleLocKey != null){
                notFirst = checkFirst(notFirst,builder);
                builder.append("\"title-loc-key\":\"" ).append(titleLocKey).append("\"");
            }else{
                notFirst = checkFirst(notFirst,builder);
                builder.append("\"title-loc-key\":null" );
            }
            if(titleLocArgs != null){
                notFirst = checkFirst(notFirst,builder);
                builder.append("\"title-loc-args\":").append(convertStringArray(titleLocArgs));
            }else{
                notFirst = checkFirst(notFirst,builder);
                builder.append("\"title-loc-args\":null" );
            }
            if(actionLocKey != null){
                notFirst = checkFirst(notFirst,builder);
                builder.append("\"action-loc-key\":\"" ).append(actionLocKey).append("\"");
            }else{
                notFirst = checkFirst(notFirst,builder);
                builder.append("\"action-loc-key\":null" );
            }
            if(locKey != null){
                notFirst = checkFirst(notFirst,builder);
                builder.append("\"loc-key\":\"" ).append(locKey).append("\"");
            }
            if(locArgs != null){
                notFirst = checkFirst(notFirst,builder);
                builder.append("\"loc-args\":").append(convertStringArray(locArgs));
            }
            if(launchImage != null){
                notFirst = checkFirst(notFirst,builder);
                builder.append("\"launch-image\":\"" ).append(launchImage).append("\"");
            }
            builder.append("}");
            if(!notFirst){
                throw new RuntimeException("不能传入一个空的对象");
            }
            return  builder.toString();
        }
    }

    public Object getAlert() {
        return alert;
    }

    public void setAlert(Object alert) {
        this.alert = alert;
    }

    public int getBadge() {
        return badge;
    }

    public void setBadge(int badge) {
        this.badge = badge;
    }

    public String getSound() {
        return sound;
    }

    public void setSound(String sound) {
        this.sound = sound;
    }

    public int getContentAvailable() {
        return contentAvailable;
    }

    public void setContentAvailable(int contentAvailable) {
        this.contentAvailable = contentAvailable;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{\"apns\":{" );
        builder.append("\"badge\":").append(badge);
        builder.append(",\"content-available\":").append(contentAvailable);
        if(alert != null){
            if(alert instanceof Alert){
                builder.append(",\"alert\":").append(alert);
            }else{
                builder.append(",\"alert\":\"").append(alert).append("\"");
            }
        }
        if(sound != null){
            builder.append(",\"sound\":\"").append(sound).append("\"");
        }
        if(category != null){
            builder.append(",\"category\":\"").append(category).append("\"");
        }
        builder.append("}}");
        return builder.toString();
    }

    protected static boolean checkFirst(boolean notFirst, StringBuilder builder){
        if(notFirst)
            builder.append(",");
        return true;
    }

    protected static String convertStringArray(String[] a){
        if (a == null)
            return "null";

        int iMax = a.length - 1;
        if (iMax == -1)
            return "[]";

        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0; ; i++) {
            b.append("\"").append(String.valueOf(a[i])).append("\"");
            if (i == iMax)
                return b.append(']').toString();
            b.append(", ");
        }
    }
}
