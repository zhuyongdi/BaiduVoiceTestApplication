package com.zyd.baiduvoicetestapplication.voice.recog;

import org.json.JSONException;
import org.json.JSONObject;

public class Volume {

    private int volumePercent = -1;
    private int volume = -1;
    private String originalJson;

    public static Volume parseVolumeJson(String jsonStr) {
        Volume vol = new Volume();
        vol.setOriginalJson(jsonStr);
        try {
            JSONObject json = new JSONObject(jsonStr);
            vol.setVolumePercent(json.getInt("volume-percent"));
            vol.setVolume(json.getInt("volume"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return vol;
    }

    public int getVolumePercent() {
        return volumePercent;
    }

    public void setVolumePercent(int volumePercent) {
        this.volumePercent = volumePercent;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public String getOriginalJson() {
        return originalJson;
    }

    public void setOriginalJson(String originalJson) {
        this.originalJson = originalJson;
    }

    @Override
    public String toString() {
        return "Volume{" +
                "volumePercent=" + volumePercent +
                ", volume=" + volume +
                ", originalJson='" + originalJson + '\'' +
                '}';
    }
}
