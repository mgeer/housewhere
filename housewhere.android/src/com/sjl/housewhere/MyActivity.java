package com.sjl.housewhere;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import com.baidu.mapapi.map.*;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.sjl.housewhere.model.Estate;
import com.sjl.housewhere.model.EstateRepository;

import java.util.List;

import static java.lang.Math.*;

public class MyActivity extends Activity {
    MapView mapView;
    private final int distance = 500;
//    private double centerLongitude = 116.404;
//    private double centerLatitude = 39.915;
    private double centerLongitude = 116.443016;
    private double centerLatitude = 40.073729;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
//        mBMapMan=new BMapManager(getApplication());
//        mBMapMan.init("A7fceec52518d6b3689b1536d5d0b5b4", null);
//        setContentView(R.layout.main);
        mapView = (MapView) findViewById(R.id.bmapsView);
        mapView.setBuiltInZoomControls(true);
        MapController mMapController = mapView.getController();
        GeoPoint point =new GeoPoint((int)(centerLatitude * 1E6),(int)(centerLongitude * 1E6));
        mMapController.setCenter(point);
        mMapController.setZoom(16);

        drawEstates(centerLongitude, centerLatitude);
    }

    private void drawEstates(double longitude, double latitude) {
        LongLatScope longLatScope = getLongLatScope(new GeoPoint((int)(latitude*1E6), (int)(longitude*1E6)), distance);
        Log.i(this.getLocalClassName(), "=====long lat scope:" + longLatScope.toString());
        EstateRepository estateRepository = new EstateRepository(this.getApplication());
        List<Estate> estates = estateRepository.getEstatesByLongLatScope(longLatScope.getMaxLong(), longLatScope.getMinLong(),
                longLatScope.getMaxLat(), longLatScope.getMinLat());
        Log.i("MyActivity", String.format("{%s} estates found!", estates.size()));
        for(Estate estate : estates){
            Log.i("MyActivity", "drawing estate:" + estate.toString());
            drawEstate(estate);
        }
    }

    class LongLatScope{
        private double maxLong;
        private double minLong;
        private double maxLat;
        private double minLat;

        LongLatScope(double maxLong, double minLong, double maxLat, double minLat) {
            this.maxLong = maxLong;
            this.minLong = minLong;
            this.maxLat = maxLat;
            this.minLat = minLat;
        }

        @Override
        public String toString() {
            return String.format("%s:%s - %s:%s", maxLong, minLong, maxLat, minLat);
        }

        double getMaxLong() {
            return maxLong;
        }

        double getMinLong() {
            return minLong;
        }

        double getMaxLat() {
            return maxLat;
        }

        double getMinLat() {
            return minLat;
        }
    }

    private LongLatScope getLongLatScope(GeoPoint point, double distance)
    {
        double dlog,dlat;

        double lat = (double)point.getLatitudeE6() / 1E6;
        double log = (double)point.getLongitudeE6() / 1E6;
        double EARTH_RADIUS = 6371000;

        dlog = 2 * asin(sin(distance / (2 * EARTH_RADIUS)) / cos(lat)) ;
        dlog = dlog * 360 /(2 * Math.PI);        // 弧度转换成角度

        dlat = distance / EARTH_RADIUS;
        dlat = dlat * 360 /(2 * Math.PI);     //弧度转换成角度

        return new LongLatScope(log - dlog, log + dlog, lat + dlat, lat - dlat);
    }

    public void drawTransCircle(GeoPoint geoPoint, int pixelRadius)
    {
        Geometry palaceGeometry = new Geometry();
        //GeoPoint palaceCenter = new GeoPoint((int)(39.924 * 1E6),(int)(116.403 * 1E6));
        //palaceGeometry.setEnvelope(geoPoint1, geoPoint2);
        palaceGeometry.setCircle(geoPoint, pixelRadius);

        Symbol palaceSymbol = new Symbol();//创建样式
        Symbol.Color palaceColor = palaceSymbol.new Color();//创建颜色
        palaceColor.red = 0;//设置颜色的红色分量
        palaceColor.green = 255;//设置颜色的绿色分量
        palaceColor.blue = 0;//设置颜色的蓝色分量
        palaceColor.alpha = 200;//设置颜色的alpha值
        palaceSymbol.setSurface(palaceColor,1,3);//设置样式参数，颜色：palaceColor是否填充距形：是线

        Graphic palaceGraphic = new Graphic(palaceGeometry, palaceSymbol);
        GraphicsOverlay palaceOverlay = new GraphicsOverlay(mapView);
        long palaceId = palaceOverlay.setData(palaceGraphic);
        //将overlay添加到mapview中
        mapView.getOverlays().add(palaceOverlay);
        //刷新地图使新添加的overlay生效
        mapView.refresh();
        //移动，缩放地图到最视野
        mapView.getController().setZoom(16);
        mapView.getController().setCenter(geoPoint);
    }

    private void drawEstate(Estate estate) {
        drawTransCircle(new GeoPoint((int)(estate.getLatitude()*1E6), (int)(estate.getLongitude()*1E6)), (int)Math.sqrt(estate.getArea() / Math.PI));
    }
}
