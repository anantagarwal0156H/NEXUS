package com.nexus.domain;
public final class GeoCheck {
    private GeoCheck(){}
    public record Result(boolean verified,Long distanceMeters,String reason){}
    public static double distance(double a,double b,double c,double d){
        double p=Math.toRadians(c-a),q=Math.toRadians(d-b);
        double h=Math.pow(Math.sin(p/2),2)+Math.cos(Math.toRadians(a))*Math.cos(Math.toRadians(c))*Math.pow(Math.sin(q/2),2);
        return 6371000*2*Math.asin(Math.sqrt(Math.max(0,Math.min(1,h))));
    }
    public static Result check(CampusCatalog.Site site,Double lat,Double lng,Double accuracy){
        if(site.lat()==null||site.lng()==null)return new Result(false,null,"Building coordinates not verified; manual review required");
        if(lat==null||lng==null||accuracy==null||!Double.isFinite(lat)||!Double.isFinite(lng)||!Double.isFinite(accuracy)||Math.abs(lat)>90||Math.abs(lng)>180||accuracy<0||accuracy>75)return new Result(false,null,"GPS unavailable or accuracy worse than 75 metres");
        double metres=distance(lat,lng,site.lat(),site.lng());
        return new Result(metres+accuracy<=125,Math.round(metres),metres+accuracy<=125?"Within pilot verification area":"Location or accuracy outside 125 metre pilot radius");
    }
}
