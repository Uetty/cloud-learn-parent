package com.uetty.cloud.feign.api.entity;

public class Config {

    private String name;
    private String propValue;
    private String note;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setValue(String propValue) {
        this.propValue = propValue;
    }
    public int getIntValue(int defaultValue) {
        return propValue != null && !"".equals(propValue.trim()) ? Integer.parseInt(propValue) : defaultValue;
    }
    public long getIntValue(long defaultValue) {
        return propValue != null && !"".equals(propValue.trim()) ? Long.parseLong(propValue) : defaultValue;
    }
    public String getStringValue() {
        return this.propValue;
    }
    public double getDoubleValue(double defaultValue) {
        return propValue != null && !"".equals(propValue.trim()) ? Double.parseDouble(propValue) : defaultValue;
    }
    public String getPropValue() {
        return propValue;
    }
    public void setPropValue(String propValue) {
        this.propValue = propValue;
    }
    public String getNote() {
        return note;
    }
    public void setNote(String note) {
        this.note = note;
    }
}
