package com.sjl.housewhere;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import com.baidu.mapapi.map.*;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.sjl.housewhere.model.Estate;
import com.sjl.housewhere.model.EstateRepository;

import java.util.List;

public class EstatesInCityActivity extends Activity {
    private MapView mMapView;

    private double centerLongitude = 116.404;
    private double centerLatitude = 39.915;
    private double targetPrice = 40000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.
        setContentView(R.layout.estates_in_city);

        showMap();
//        addDarkBackround();
        drawEstates(targetPrice);
    }

    private void addDarkBackround() {
        GraphicsOverlay overlay = new GraphicsOverlay(mMapView);
        Geometry palaceGeometry = new Geometry();
        int latitudeSpan = 1;
        int longitudeSpan = 1;
        int upLeftLon = (int)(centerLongitude * 1E6 - longitudeSpan / 2);
        int upLeftLat = (int)(centerLatitude * 1E6 + latitudeSpan / 2);
        int downRightLon = (int)(centerLongitude * 1E6 + longitudeSpan / 2);
        int downRightLat = (int)(centerLatitude * 1E6 - longitudeSpan / 2);
        GeoPoint upLeftPoint = new GeoPoint(upLeftLat, upLeftLon);
        GeoPoint downRightPoint = new GeoPoint(downRightLat, downRightLon);
        palaceGeometry.setEnvelope(upLeftPoint, downRightPoint);
        Symbol symbol = new Symbol();
        Symbol.Color color = symbol.new Color();
        color.red = 0x00;
        color.green = 0x00;
        color.blue = 0x00;
        color.alpha = 200;
        Graphic graphic = new Graphic(palaceGeometry, symbol);
        overlay.setData(graphic);
    }

    private void showMap() {
        mMapView=(MapView)findViewById(R.id.bmapsView);
        mMapView.setBuiltInZoomControls(true);
        MapController mMapController = mMapView.getController();
        GeoPoint point =new GeoPoint((int)(centerLatitude * 1E6),(int)(centerLongitude * 1E6));
        mMapController.setCenter(point);
        mMapController.setZoom(12);
    }

    private void drawEstates(double targetPrice) {
        EstateRepository estateRepository = new EstateRepository(this.getApplication());
        List<Estate> estates = estateRepository.getAllEstates();
        Log.i("EstatesInCityActivity", String.format("=====>%d estates found as all!", estates.size()));
        GraphicsOverlay estatesOverlay = new GraphicsOverlay(mMapView);

        for (Estate estate : estates) {
            Graphic circle = createCircle(estate, targetPrice);
            estatesOverlay.setData(circle);
        }

        GeoPoint centerPoint = new GeoPoint((int)(centerLatitude * 1E6), (int)(centerLongitude * 1E6));

        refreshOverlay(estatesOverlay, centerPoint);
    }

    private Graphic createCircle(Estate estate, double targetPrice) {
        Geometry palaceGeometry = new Geometry();
        GeoPoint geoPoint = new GeoPoint((int) (estate.getLatitude() * 1E6), (int) (estate.getLongitude() * 1E6));
        int radius = (int) Math.sqrt(estate.getArea() / Math.PI);
        palaceGeometry.setCircle(geoPoint, radius);
        Symbol estateSymbol = getEstateSymbol(estate, targetPrice);
        return new Graphic(palaceGeometry, estateSymbol);
    }

    private Symbol getEstateSymbol(Estate estate, double targetPrice) {
        Symbol symbol = new Symbol();
        Symbol.Color color = symbol.new Color();

        if (greatMoreThanTarget(estate.getPrice(), targetPrice)){
            color.red = 0xFF;
            color.green = 0x00;
            color.blue = 0x00;
        }
        else if (littleMoreThanTarget(estate.getPrice(), targetPrice)){
            color.red = 0xFF;
            color.green = 0x99;
            color.blue = 0x00;
        }
        else if (moreOrLess(estate.getPrice(), targetPrice)){
            color.red = 0x33;
            color.green = 0xFF;
            color.blue = 0x00;
        }
        else{
            color.red = 0x000;
            color.green = 0x66;
            color.blue = 0xFF;
        }
        color.alpha = 240;
        symbol.setSurface(color, 1, 3);
        return symbol;
    }

    private boolean moreOrLess(double price, double targetPrice) {
        double gap = price - targetPrice;
        return  gap <= 0 && gap > -10000;
    }

    private boolean littleMoreThanTarget(double price, double targetPrice) {
        double gap = price - targetPrice;
        return gap > 0 && gap <= 10000;
    }

    private boolean greatMoreThanTarget(double price, double targetPrice) {
        return price - targetPrice > 10000;
    }

    private void refreshOverlay(GraphicsOverlay estatesOverlay, GeoPoint centerPoint) {
        mMapView.getOverlays().add(estatesOverlay);
        mMapView.refresh();
        mMapView.getController().setZoom(12);
        mMapView.getController().setCenter(centerPoint);
    }

    @Override
    protected void onDestroy(){
        mMapView.destroy();
//        if(mBMapMan!=null){
//            mBMapMan.destroy();
//            mBMapMan=null;
//        }
        super.onDestroy();
    }

    @Override
    protected void onPause(){
        mMapView.onPause();
//        if(mBMapMan!=null){
//            mBMapMan.stop();
//        }
        super.onPause();
    }
    @Override
    protected void onResume(){
        mMapView.onResume();
//        if(mBMapMan!=null){
//            mBMapMan.start();
//        }
        super.onResume();
    }
}
