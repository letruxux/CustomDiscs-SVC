package io.github.subkek.customdiscs;

import lombok.Getter;

@Getter
public class YoutubeVideo {
    private final String title;
    private final int duration;
    private final String uploaderName;
    private final String url;

    public YoutubeVideo(String title, int duration, String uploaderName, String url) {
        this.title = title;
        this.duration = duration;
        this.uploaderName = uploaderName;
        this.url = url;
    }

}