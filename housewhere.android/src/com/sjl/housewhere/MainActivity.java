package com.sjl.housewhere;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.*;
import com.baidu.mapapi.search.*;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.sjl.housewhere.model.Estate;
import com.sjl.housewhere.model.EstateRepository;

import java.util.List;

import static java.lang.Math.*;

public class MainActivity<RoutePlanDemo> extends Activity {

    private final int distance = 500;
    //  private double centerLongitude = 116.404;
//  private double centerLatitude = 39.915;
    private double centerLongitude = 116.443016;
    private double centerLatitude = 40.073729;

    BMapManager mBMapMan = null;
    MapView mMapView = null;

    //搜索相关
    MKSearch mSearch = null;	// 搜索模块，也可去掉地图模块独立使用

    //UI相关
    Button mBtnDrive = null;	// 驾车搜索
    Button mBtnTransit = null;	// 公交搜索
    Button mBtnWalk = null;	// 步行搜索
    Button mBtnCusRoute = null; //自定义路线
    Button mBtnCusIcon = null ; //自定义起终点图标

    //浏览路线节点相关
    Button mBtnPre = null;//上一个节点
    Button mBtnNext = null;//下一个节点
    int nodeIndex = -2;//节点索引,供浏览节点时使用
    MKRoute route = null;//保存驾车/步行路线数据的变量，供浏览节点时使用
    TransitOverlay transitOverlay = null;//保存公交路线图层数据的变量，供浏览节点时使用
    RouteOverlay routeOverlay = null;
    boolean useDefaultIcon = false;
    int searchType = -1;//记录搜索的类型，区分驾车/步行和公交
    private PopupOverlay   pop  = null;//弹出泡泡图层，浏览节点时使用
    private TextView  popupText = null;//泡泡view
    private View viewCache = null;
    private GraphicsOverlay previousOverlay;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBMapMan = new BMapManager(getApplication());
        mBMapMan.init("A7fceec52518d6b3689b1536d5d0b5b4", null);
        //注意：请在试用setContentView前初始化BMapManager对象，否则会报错
        setContentView(R.layout.activity_main);
        mMapView=(MapView)findViewById(R.id.bmapsView);
        mMapView.setBuiltInZoomControls(true);
        //设置启用内置的缩放控件
        MapController mMapController=mMapView.getController();
        // 得到mMapView的控制权,可以用它控制和驱动平移和缩放
        GeoPoint point =new GeoPoint((int)(39.915* 1E6),(int)(116.404* 1E6));
        //用给定的经纬度构造一个GeoPoint，单位是微度 (度 * 1E6)
        mMapController.setCenter(point);//设置地图中心点
        mMapController.setZoom(12);//设置地图zoom级别

        //初始化按键
        mBtnDrive = (Button)findViewById(R.id.drive);
        mBtnTransit = (Button)findViewById(R.id.transit);
        mBtnWalk = (Button)findViewById(R.id.walk);
        mBtnPre = (Button)findViewById(R.id.pre);
        mBtnNext = (Button)findViewById(R.id.next);
        mBtnPre.setVisibility(View.INVISIBLE);
        mBtnNext.setVisibility(View.INVISIBLE);

        View.OnClickListener clickListener = new View.OnClickListener() {
            public void onClick(View v) {
                //发起搜索
                SearchButtonProcess(v);
            }
        };

        View.OnClickListener nodeClickListener = new View.OnClickListener(){
            public void onClick(View v) {
                //浏览路线节点
                nodeClick(v);
            }
        };

        mBtnDrive.setOnClickListener(clickListener);
        mBtnTransit.setOnClickListener(clickListener);
        mBtnWalk.setOnClickListener(clickListener);
        mBtnPre.setOnClickListener(nodeClickListener);
        mBtnNext.setOnClickListener(nodeClickListener);

        // 初始化搜索模块，注册事件监听
        mSearch = new MKSearch();
        mSearch.init(mBMapMan, new MKSearchListener(){
            public void onGetDrivingRouteResult(MKDrivingRouteResult res,
                                                int error) {
                //起点或终点有歧义，需要选择具体的城市列表或地址列表
                if (error == MKEvent.ERROR_ROUTE_ADDR){
                    //遍历所有地址
//					ArrayList<MKPoiInfo> stPois = res.getAddrResult().mStartPoiList;
//					ArrayList<MKPoiInfo> enPois = res.getAddrResult().mEndPoiList;
//					ArrayList<MKCityListInfo> stCities = res.getAddrResult().mStartCityList;
//					ArrayList<MKCityListInfo> enCities = res.getAddrResult().mEndCityList;
                    return;
                }
                // 错误号可参考MKEvent中的定义
                if (error != 0 || res == null) {
                    Toast.makeText(MainActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
                    return;
                }

                searchType = 0;
                routeOverlay = new RouteOverlay(MainActivity.this, mMapView);
                // 此处仅展示一个方案作为示例
                routeOverlay.setData(res.getPlan(0).getRoute(0));
                //清除其他图层
                mMapView.getOverlays().clear();
                //添加路线图层
                mMapView.getOverlays().add(routeOverlay);
                //执行刷新使生效
                mMapView.refresh();
                // 使用zoomToSpan()绽放地图，使路线能完全显示在地图上
                mMapView.getController().zoomToSpan(routeOverlay.getLatSpanE6(), routeOverlay.getLonSpanE6());
                //移动地图到起点
                mMapView.getController().animateTo(res.getStart().pt);
                //将路线数据保存给全局变量
                route = res.getPlan(0).getRoute(0);
                //重置路线节点索引，节点浏览时使用
                nodeIndex = -1;
                mBtnPre.setVisibility(View.VISIBLE);
                mBtnNext.setVisibility(View.VISIBLE);
            }

            public void onGetTransitRouteResult(MKTransitRouteResult res,
                                                int error) {
                //起点或终点有歧义，需要选择具体的城市列表或地址列表
                if (error == MKEvent.ERROR_ROUTE_ADDR){
                    //遍历所有地址
//					ArrayList<MKPoiInfo> stPois = res.getAddrResult().mStartPoiList;
//					ArrayList<MKPoiInfo> enPois = res.getAddrResult().mEndPoiList;
//					ArrayList<MKCityListInfo> stCities = res.getAddrResult().mStartCityList;
//					ArrayList<MKCityListInfo> enCities = res.getAddrResult().mEndCityList;
                    return;
                }
                if (error != 0 || res == null) {
                    Toast.makeText(MainActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
                    return;
                }

                searchType = 1;
                transitOverlay = new TransitOverlay (MainActivity.this, mMapView);
                // 此处仅展示一个方案作为示例
                transitOverlay.setData(res.getPlan(0));
                //清除其他图层
                mMapView.getOverlays().clear();
                //添加路线图层
                mMapView.getOverlays().add(transitOverlay);
                //执行刷新使生效
                mMapView.refresh();
                // 使用zoomToSpan()绽放地图，使路线能完全显示在地图上
                mMapView.getController().zoomToSpan(transitOverlay.getLatSpanE6(), transitOverlay.getLonSpanE6());
                //移动地图到起点
                mMapView.getController().animateTo(res.getStart().pt);
                //重置路线节点索引，节点浏览时使用
                nodeIndex = 0;
                mBtnPre.setVisibility(View.VISIBLE);
                mBtnNext.setVisibility(View.VISIBLE);
            }

            public void onGetWalkingRouteResult(MKWalkingRouteResult res,
                                                int error) {
                //起点或终点有歧义，需要选择具体的城市列表或地址列表
                if (error == MKEvent.ERROR_ROUTE_ADDR){
                    //遍历所有地址
//					ArrayList<MKPoiInfo> stPois = res.getAddrResult().mStartPoiList;
//					ArrayList<MKPoiInfo> enPois = res.getAddrResult().mEndPoiList;
//					ArrayList<MKCityListInfo> stCities = res.getAddrResult().mStartCityList;
//					ArrayList<MKCityListInfo> enCities = res.getAddrResult().mEndCityList;
                    return;
                }
                if (error != 0 || res == null) {
                    Toast.makeText(MainActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
                    return;
                }

                searchType = 2;
                routeOverlay = new RouteOverlay(MainActivity.this, mMapView);
                // 此处仅展示一个方案作为示例
                routeOverlay.setData(res.getPlan(0).getRoute(0));
                //清除其他图层
                mMapView.getOverlays().clear();
                //添加路线图层
                mMapView.getOverlays().add(routeOverlay);
                //执行刷新使生效
                mMapView.refresh();
                // 使用zoomToSpan()绽放地图，使路线能完全显示在地图上
                mMapView.getController().zoomToSpan(routeOverlay.getLatSpanE6(), routeOverlay.getLonSpanE6());
                //移动地图到起点
                mMapView.getController().animateTo(res.getStart().pt);
                //将路线数据保存给全局变量
                route = res.getPlan(0).getRoute(0);
                //重置路线节点索引，节点浏览时使用
                nodeIndex = -1;
                mBtnPre.setVisibility(View.VISIBLE);
                mBtnNext.setVisibility(View.VISIBLE);

            }
            public void onGetAddrResult(MKAddrInfo res, int error) {
            }
            public void onGetPoiResult(MKPoiResult res, int arg1, int arg2) {
            }
            public void onGetBusDetailResult(MKBusLineResult result, int iError) {
            }

            public void onGetPoiDetailSearchResult(int arg0, int arg1) {
                // TODO Auto-generated method stub

            }

            public void onGetShareUrlResult(MKShareUrlResult arg0, int arg1,
                                            int arg2) {
                // TODO Auto-generated method stub

            }

            public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {
                // TODO Auto-generated method stub

            }

//			@Override
//			public void onGetSuggestionResult(MKSuggestionResult res, int arg1) {
//			}
//
//			@Override
//			public void onGetPoiDetailSearchResult(int type, int iError) {
//				// TODO Auto-generated method stub
//			}
//
//			@Override
//			public void onGetShareUrlResult(MKShareUrlResult result, int type,
//					int error) {
//				// TODO Auto-generated method stub
//				
//			}
        });
    }

    /**
     * 发起路线规划搜索示例
     * @param v
     */
    void SearchButtonProcess(View v) {
        //重置浏览节点的路线数据
        route = null;
        routeOverlay = null;
        transitOverlay = null;
        mBtnPre.setVisibility(View.INVISIBLE);
        mBtnNext.setVisibility(View.INVISIBLE);
        // 处理搜索按钮响应
        EditText editSt = (EditText)findViewById(R.id.start);
        EditText editEn = (EditText)findViewById(R.id.end);

        // 对起点终点的name进行赋值，也可以直接对坐标赋值，赋值坐标则将根据坐标进行搜索
        MKPlanNode stNode = new MKPlanNode();
        stNode.name = editSt.getText().toString();
        MKPlanNode enNode = new MKPlanNode();
        enNode.name = editEn.getText().toString();

        // 实际使用中请对起点终点城市进行正确的设定
        if (mBtnDrive.equals(v)) {
            mSearch.drivingSearch("北京", stNode, "北京", enNode);
        } else if (mBtnTransit.equals(v)) {
            mSearch.transitSearch("北京", stNode, enNode);
        } else if (mBtnWalk.equals(v)) {
            mSearch.walkingSearch("北京", stNode, "北京", enNode);
        }
    }

    /**
     * 节点浏览示例
     * @param v
     */
    public void nodeClick(View v){
        viewCache = getLayoutInflater().inflate(R.layout.custom_text_view, null);
        popupText =(TextView) viewCache.findViewById(R.id.textcache);
        if (searchType == 0 || searchType == 2){
            //驾车、步行使用的数据结构相同，因此类型为驾车或步行，节点浏览方法相同
            if (nodeIndex < -1 || route == null || nodeIndex >= route.getNumSteps())
                return;

            //上一个节点
            if (mBtnPre.equals(v) && nodeIndex > 0){
                //索引减
                nodeIndex--;
                //移动到指定索引的坐标
                mMapView.getController().animateTo(route.getStep(nodeIndex).getPoint());
                //弹出泡泡
                popupText.setBackgroundResource(R.drawable.popup);
                popupText.setText(route.getStep(nodeIndex).getContent());
                pop.showPopup(BMapUtil.getBitmapFromView(popupText),
                        route.getStep(nodeIndex).getPoint(),
                        5);
            }
            //下一个节点
            if (mBtnNext.equals(v) && nodeIndex < (route.getNumSteps()-1)){
                //索引加
                nodeIndex++;
                //移动到指定索引的坐标
                mMapView.getController().animateTo(route.getStep(nodeIndex).getPoint());
                //弹出泡泡
                popupText.setBackgroundResource(R.drawable.popup);
                popupText.setText(route.getStep(nodeIndex).getContent());
                pop.showPopup(BMapUtil.getBitmapFromView(popupText),
                        route.getStep(nodeIndex).getPoint(),
                        5);
            }
        }
        if (searchType == 1){
            //公交换乘使用的数据结构与其他不同，因此单独处理节点浏览
            if (nodeIndex < -1 || transitOverlay == null || nodeIndex >= transitOverlay.getAllItem().size())
                return;

            //上一个节点
            if (mBtnPre.equals(v) && nodeIndex > 1){
                //索引减
                nodeIndex--;
                //移动到指定索引的坐标
                mMapView.getController().animateTo(transitOverlay.getItem(nodeIndex).getPoint());
                drawEstates(transitOverlay.getItem(nodeIndex).getPoint().getLongitudeE6()/1E6,transitOverlay.getItem(nodeIndex).getPoint().getLatitudeE6()/1E6);
                //弹出泡泡
                popupText.setBackgroundResource(R.drawable.popup);
                popupText.setText(transitOverlay.getItem(nodeIndex).getTitle());
//				pop.showPopup(BMapUtil.getBitmapFromView(popupText),
//						transitOverlay.getItem(nodeIndex).getPoint(),
//						5);
            }
            //下一个节点
            if (mBtnNext.equals(v) && nodeIndex < (transitOverlay.getAllItem().size()-2)){
                //索引加
                nodeIndex++;
                //移动到指定索引的坐标
                mMapView.getController().animateTo(transitOverlay.getItem(nodeIndex).getPoint());
                drawEstates(transitOverlay.getItem(nodeIndex).getPoint().getLongitudeE6()/1E6,transitOverlay.getItem(nodeIndex).getPoint().getLatitudeE6()/1E6);
                //弹出泡泡
                popupText.setBackgroundResource(R.drawable.popup);
                popupText.setText(transitOverlay.getItem(nodeIndex).getTitle());
//				pop.showPopup(BMapUtil.getBitmapFromView(popupText),
//						transitOverlay.getItem(nodeIndex).getPoint(),
//						5);
            }
        }

    }
    /**
     * 创建弹出泡泡图层
     */
    public void createPaopao(){

        //泡泡点击响应回调
        PopupClickListener popListener = new PopupClickListener(){
            @Override
            public void onClickedPopup(int index) {
                Log.v("click", "clickapoapo");
            }
        };
        pop = new PopupOverlay(mMapView,popListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    protected void onDestroy(){
        mMapView.destroy();
        if(mBMapMan!=null){
            mBMapMan.destroy();
            mBMapMan=null;
        }
        super.onDestroy();
    }
    @Override
    protected void onPause(){
        mMapView.onPause();
        if(mBMapMan!=null){
            mBMapMan.stop();
        }
        super.onPause();
    }
    @Override
    protected void onResume(){
        mMapView.onResume();
        if(mBMapMan!=null){
            mBMapMan.start();
        }
        super.onResume();
    }

    private void drawEstates(double longitude, double latitude) {
        GeoPoint centerPoint = new GeoPoint((int) (latitude * 1E6), (int) (longitude * 1E6));
        LongLatScope longLatScope = getLongLatScope(centerPoint, distance);
        Log.i(this.getLocalClassName(), "=====long lat scope:" + longLatScope.toString());
        EstateRepository estateRepository = new EstateRepository(this.getApplication());
        List<Estate> estates = estateRepository.getEstatesByLongLatScope(longLatScope.getMaxLong(), longLatScope.getMinLong(),
                longLatScope.getMaxLat(), longLatScope.getMinLat());
        Log.i("MyActivity", String.format("{%s} estates found!", estates.size()));
        drawEstatesOnMap(estates, centerPoint);
    }

    private void drawEstatesOnMap(List<Estate> estates, GeoPoint centerPoint) {
        if (previousOverlay != null){
            previousOverlay.removeAll();
        }
        GraphicsOverlay palaceOverlay = new GraphicsOverlay(mMapView);
        palaceOverlay.removeAll();
        for(Estate estate : estates){
            GeoPoint geoPoint = new GeoPoint((int) (estate.getLatitude() * 1E6), (int) (estate.getLongitude() * 1E6));
            int pixelRadius = (int) Math.sqrt(estate.getArea() / Math.PI);
            Graphic palaceGraphic = getGraphic(geoPoint, pixelRadius);
            palaceOverlay.setData(palaceGraphic);
        }
        //将overlay添加到mapview中
        mMapView.getOverlays().add(palaceOverlay);
        //刷新地图使新添加的overlay生效
        mMapView.refresh();
        //移动，缩放地图到最视野
        mMapView.getController().setZoom(16);
        mMapView.getController().setCenter(centerPoint);
        previousOverlay = palaceOverlay;
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

    private Graphic getGraphic(GeoPoint geoPoint, int pixelRadius) {
        Geometry palaceGeometry = new Geometry();
        palaceGeometry.setCircle(geoPoint, pixelRadius);

        Symbol palaceSymbol = new Symbol();//创建样式
        Symbol.Color palaceColor = palaceSymbol.new Color();//创建颜色
        palaceColor.red = 0;//设置颜色的红色分量
        palaceColor.green = 255;//设置颜色的绿色分量
        palaceColor.blue = 0;//设置颜色的蓝色分量
        palaceColor.alpha = 200;//设置颜色的alpha值
        palaceSymbol.setSurface(palaceColor,1,3);//设置样式参数，颜色：palaceColor是否填充距形：是线

        return new Graphic(palaceGeometry, palaceSymbol);
    }

}
