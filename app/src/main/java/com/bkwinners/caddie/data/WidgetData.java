package com.bkwinners.caddie.data;

import java.io.Serializable;

public class WidgetData implements Serializable {
    private String routeUrl;
    private String device;
    private String target;

    public String getRouteUrl() { return this.routeUrl; }
    public void setRouteUrl(String routeUrl) { this.routeUrl = routeUrl; }
    public String getDevice() { return this.device; }
    public void setDevice(String device) { this.device = device; }
    public String getTarget() { return this.target; }
    public void setTaget(String target) { this.target = target; }

}
