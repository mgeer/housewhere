package com.sjl.housewhere;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.MKEvent;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.RouteOverlay;
import com.baidu.mapapi.map.TransitOverlay;
import com.baidu.mapapi.search.*;

import java.util.List;

public class TransitRoute {
    private final MapView mMapView;
    private BMapManager bMapManager;
    private HousewhereApplication application;
    private List<String> busLineIDList = null;
    MKSearch mkSearch = new MKSearch();
    MKRoute route = null;
    int busLineIndex = 0;
    private Activity activity;

    public TransitRoute(Activity activity) {
        this.activity = activity;
        this.application = (HousewhereApplication)activity.getApplication();

        this.bMapManager = application.mBMapManager;

        mMapView = (MapView)activity.findViewById(R.id.bmapsView);
    }

    public void search(String start, String end) {

        mkSearch.init(bMapManager, new MKSearchListener() {
            @Override
            public void onGetPoiResult(MKPoiResult res, int type, int error) {
                // 错误号可参考MKEvent中的定义
                if (error != 0 || res == null) {
                    Toast.makeText(application.getApplicationContext(), "抱歉，未找到结果", Toast.LENGTH_LONG).show();
                    return;
                }

                // 找到公交路线poi node
                MKPoiInfo curPoi = null;
                int totalPoiNum  = res.getCurrentNumPois();
                //遍历所有poi，找到类型为公交线路的poi
                busLineIDList.clear();
                for( int idx = 0; idx < totalPoiNum; idx++ ) {
                    if ( 2 == res.getPoi(idx).ePoiType ) {
                        // poi类型，0：普通点，1：公交站，2：公交线路，3：地铁站，4：地铁线路
                        curPoi = res.getPoi(idx);
                        //使用poi的uid发起公交详情检索
                        busLineIDList.add(curPoi.uid);
                        System.out.println(curPoi.uid);

                    }
                }
                SearchNextBusline();

                // 没有找到公交信息
                if (curPoi == null) {
                    Toast.makeText(application.getApplicationContext(), "抱歉，未找到结果", Toast.LENGTH_LONG).show();
                    return;
                }
                route = null;
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void onGetTransitRouteResult(MKTransitRouteResult res, int error) {
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
                    Toast.makeText(application, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
                    return;
                }

//                searchType = 1;
                TransitOverlay transitOverlay = new TransitOverlay(activity, mMapView);
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
//                nodeIndex = 0;
//                mBtnPre.setVisibility(View.VISIBLE);
//                mBtnNext.setVisibility(View.VISIBLE);
            }

            @Override
            public void onGetDrivingRouteResult(MKDrivingRouteResult mkDrivingRouteResult, int i) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void onGetWalkingRouteResult(MKWalkingRouteResult mkWalkingRouteResult, int i) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void onGetAddrResult(MKAddrInfo mkAddrInfo, int i) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void onGetBusDetailResult(MKBusLineResult result, int iError) {
                if (iError != 0 || result == null) {
                    Toast.makeText(application, "抱歉，未找到结果", Toast.LENGTH_LONG).show();
                    return;
                }

                RouteOverlay routeOverlay = new RouteOverlay(activity, mMapView);
                // 此处仅展示一个方案作为示例
                routeOverlay.setData(result.getBusRoute());
                //清除其他图层
                mMapView.getOverlays().clear();
                //添加路线图层
                mMapView.getOverlays().add(routeOverlay);
                //刷新地图使生效
                mMapView.refresh();
                //移动地图到起点
                mMapView.getController().animateTo(result.getBusRoute().getStart());
                //将路线数据保存给全局变量
                route = result.getBusRoute();
                //重置路线节点索引，节点浏览时使用
//                nodeIndex = -1;
//                mBtnPre.setVisibility(View.VISIBLE);
//                mBtnNext.setVisibility(View.VISIBLE);
//                Toast.makeText(BusLineSearchDemo.this,
//                        result.getBusName(),
//                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onGetSuggestionResult(MKSuggestionResult mkSuggestionResult, int i) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void onGetPoiDetailSearchResult(int i, int i2) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void onGetShareUrlResult(MKShareUrlResult mkShareUrlResult, int i, int i2) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });


        int transitSearchResult = mkSearch.transitSearch("北京", createMKPlanNodeByName(start), createMKPlanNodeByName(end));
        Log.i("TransitRoute", String.format("=====transitSearchResult:%s", transitSearchResult));
    }

    void SearchNextBusline(){
        if ( busLineIndex >= busLineIDList.size()){
            busLineIndex =0;
        }
        if ( busLineIndex >=0 && busLineIndex < busLineIDList.size() && busLineIDList.size() >0){
//            mkSearch.busLineSearch(((EditText)findViewById(R.id.city)).getText().toString(), busLineIDList.get(busLineIndex));
            mkSearch.busLineSearch("北京", busLineIDList.get(busLineIndex));
            busLineIndex ++;
        }

    }
    private MKPlanNode createMKPlanNodeByName(String start) {
        MKPlanNode node = new MKPlanNode();
        node.name = start;
        return node;
    }
}
