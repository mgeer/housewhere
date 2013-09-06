package com.sjl.housewhere;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.widget.EditText;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.mapapi.map.RouteOverlay;
import com.baidu.mapapi.map.TransitOverlay;
import com.baidu.mapapi.search.MKRoute;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.sjl.housewhere.R;
import com.baidu.mapapi.utils.*;
import com.baidu.mapapi.map.MKEvent;
import com.baidu.mapapi.map.MKMapTouchListener;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPlanNode;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.mapapi.map.*;

import com.sjl.housewhere.database.*;
import com.sjl.housewhere.model.*;

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
	
	//�������
	MKSearch mSearch = null;	// ����ģ�飬Ҳ��ȥ����ͼģ�����ʹ��	
	
	//UI���
	Button mBtnDrive = null;	// �ݳ�����
	Button mBtnTransit = null;	// ��������
	Button mBtnWalk = null;	// ��������
	Button mBtnCusRoute = null; //�Զ���·��
	Button mBtnCusIcon = null ; //�Զ������յ�ͼ��
	
	//���·�߽ڵ����
	Button mBtnPre = null;//��һ���ڵ�
	Button mBtnNext = null;//��һ���ڵ�
	int nodeIndex = -2;//�ڵ�����,������ڵ�ʱʹ��
	MKRoute route = null;//����ݳ�/����·�����ݵı�����������ڵ�ʱʹ��
	TransitOverlay transitOverlay = null;//���湫��·��ͼ�����ݵı�����������ڵ�ʱʹ��
	RouteOverlay routeOverlay = null; 
	boolean useDefaultIcon = false;
	int searchType = -1;//��¼���������ͣ����ּݳ�/���к͹���
	private PopupOverlay   pop  = null;//��������ͼ�㣬����ڵ�ʱʹ��
	private TextView  popupText = null;//����view
	private View viewCache = null;	

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	mBMapMan=new BMapManager(getApplication());
    	mBMapMan.init("A7fceec52518d6b3689b1536d5d0b5b4", null);  
    	//ע�⣺��������setContentViewǰ��ʼ��BMapManager���󣬷���ᱨ��
    	setContentView(R.layout.activity_main);
    	mMapView=(MapView)findViewById(R.id.bmapsView);
    	mMapView.setBuiltInZoomControls(true);
    	//�����������õ����ſؼ�
    	MapController mMapController=mMapView.getController();
    	// �õ�mMapView�Ŀ���Ȩ,�����������ƺ�����ƽ�ƺ�����
    	GeoPoint point =new GeoPoint((int)(39.915* 1E6),(int)(116.404* 1E6));
    	//�ø����ľ�γ�ȹ���һ��GeoPoint����λ��΢�� (�� * 1E6)
    	mMapController.setCenter(point);//���õ�ͼ���ĵ�
    	mMapController.setZoom(12);//���õ�ͼzoom����
    	
        //��ʼ������
        mBtnDrive = (Button)findViewById(R.id.drive);
        mBtnTransit = (Button)findViewById(R.id.transit);
        mBtnWalk = (Button)findViewById(R.id.walk);
        mBtnPre = (Button)findViewById(R.id.pre);
        mBtnNext = (Button)findViewById(R.id.next);
        mBtnPre.setVisibility(View.INVISIBLE);
		mBtnNext.setVisibility(View.INVISIBLE);
		
		View.OnClickListener clickListener = new View.OnClickListener() {			
			public void onClick(View v) {
				//��������
				SearchButtonProcess(v);				
			}
		};
		
        View.OnClickListener nodeClickListener = new View.OnClickListener(){
			public void onClick(View v) {
				//���·�߽ڵ�
				nodeClick(v);
			}
        };		
		
        mBtnDrive.setOnClickListener(clickListener); 
        mBtnTransit.setOnClickListener(clickListener); 
        mBtnWalk.setOnClickListener(clickListener);
        mBtnPre.setOnClickListener(nodeClickListener);
        mBtnNext.setOnClickListener(nodeClickListener);        
        
        // ��ʼ������ģ�飬ע���¼�����
        mSearch = new MKSearch();
        mSearch.init(mBMapMan, new MKSearchListener(){      
			public void onGetDrivingRouteResult(MKDrivingRouteResult res,
					int error) {
				//�����յ������壬��Ҫѡ�����ĳ����б���ַ�б�
				if (error == MKEvent.ERROR_ROUTE_ADDR){
					//�������е�ַ
//					ArrayList<MKPoiInfo> stPois = res.getAddrResult().mStartPoiList;
//					ArrayList<MKPoiInfo> enPois = res.getAddrResult().mEndPoiList;
//					ArrayList<MKCityListInfo> stCities = res.getAddrResult().mStartCityList;
//					ArrayList<MKCityListInfo> enCities = res.getAddrResult().mEndCityList;
					return;
				}
				// ����ſɲο�MKEvent�еĶ���
				if (error != 0 || res == null) {
					Toast.makeText(MainActivity.this, "��Ǹ��δ�ҵ����", Toast.LENGTH_SHORT).show();
					return;
				}
			
				searchType = 0;
			    routeOverlay = new RouteOverlay(MainActivity.this, mMapView);
			    // �˴���չʾһ��������Ϊʾ��
			    routeOverlay.setData(res.getPlan(0).getRoute(0));
			    //�������ͼ��
			    mMapView.getOverlays().clear();
			    //���·��ͼ��
			    mMapView.getOverlays().add(routeOverlay);
			    //ִ��ˢ��ʹ��Ч
			    mMapView.refresh();
			    // ʹ��zoomToSpan()���ŵ�ͼ��ʹ·������ȫ��ʾ�ڵ�ͼ��
			    mMapView.getController().zoomToSpan(routeOverlay.getLatSpanE6(), routeOverlay.getLonSpanE6());
			    //�ƶ���ͼ�����
			    mMapView.getController().animateTo(res.getStart().pt);
			    //��·�����ݱ����ȫ�ֱ���
			    route = res.getPlan(0).getRoute(0);
			    //����·�߽ڵ��������ڵ����ʱʹ��
			    nodeIndex = -1;
			    mBtnPre.setVisibility(View.VISIBLE);
				mBtnNext.setVisibility(View.VISIBLE);
			}

			public void onGetTransitRouteResult(MKTransitRouteResult res,
					int error) {
				//�����յ������壬��Ҫѡ�����ĳ����б���ַ�б�
				if (error == MKEvent.ERROR_ROUTE_ADDR){
					//�������е�ַ
//					ArrayList<MKPoiInfo> stPois = res.getAddrResult().mStartPoiList;
//					ArrayList<MKPoiInfo> enPois = res.getAddrResult().mEndPoiList;
//					ArrayList<MKCityListInfo> stCities = res.getAddrResult().mStartCityList;
//					ArrayList<MKCityListInfo> enCities = res.getAddrResult().mEndCityList;
					return;
				}
				if (error != 0 || res == null) {
					Toast.makeText(MainActivity.this, "��Ǹ��δ�ҵ����", Toast.LENGTH_SHORT).show();
					return;
				}
				
				searchType = 1;
				transitOverlay = new TransitOverlay (MainActivity.this, mMapView);
			    // �˴���չʾһ��������Ϊʾ��
			    transitOverlay.setData(res.getPlan(0));
			  //�������ͼ��
			    mMapView.getOverlays().clear();
			  //���·��ͼ��
			    mMapView.getOverlays().add(transitOverlay);
			  //ִ��ˢ��ʹ��Ч
			    mMapView.refresh();
			    // ʹ��zoomToSpan()���ŵ�ͼ��ʹ·������ȫ��ʾ�ڵ�ͼ��
			    mMapView.getController().zoomToSpan(transitOverlay.getLatSpanE6(), transitOverlay.getLonSpanE6());
			  //�ƶ���ͼ�����
			    mMapView.getController().animateTo(res.getStart().pt);
			  //����·�߽ڵ��������ڵ����ʱʹ��
			    nodeIndex = 0;
			    mBtnPre.setVisibility(View.VISIBLE);
				mBtnNext.setVisibility(View.VISIBLE);
			}

			public void onGetWalkingRouteResult(MKWalkingRouteResult res,
					int error) {
				//�����յ������壬��Ҫѡ�����ĳ����б���ַ�б�
				if (error == MKEvent.ERROR_ROUTE_ADDR){
					//�������е�ַ
//					ArrayList<MKPoiInfo> stPois = res.getAddrResult().mStartPoiList;
//					ArrayList<MKPoiInfo> enPois = res.getAddrResult().mEndPoiList;
//					ArrayList<MKCityListInfo> stCities = res.getAddrResult().mStartCityList;
//					ArrayList<MKCityListInfo> enCities = res.getAddrResult().mEndCityList;
					return;
				}
				if (error != 0 || res == null) {
					Toast.makeText(MainActivity.this, "��Ǹ��δ�ҵ����", Toast.LENGTH_SHORT).show();
					return;
				}

				searchType = 2;
				routeOverlay = new RouteOverlay(MainActivity.this, mMapView);
			    // �˴���չʾһ��������Ϊʾ��
				routeOverlay.setData(res.getPlan(0).getRoute(0));
				//�������ͼ��
			    mMapView.getOverlays().clear();
			  //���·��ͼ��
			    mMapView.getOverlays().add(routeOverlay);
			  //ִ��ˢ��ʹ��Ч
			    mMapView.refresh();
			    // ʹ��zoomToSpan()���ŵ�ͼ��ʹ·������ȫ��ʾ�ڵ�ͼ��
			    mMapView.getController().zoomToSpan(routeOverlay.getLatSpanE6(), routeOverlay.getLonSpanE6());
			  //�ƶ���ͼ�����
			    mMapView.getController().animateTo(res.getStart().pt);
			    //��·�����ݱ����ȫ�ֱ���
			    route = res.getPlan(0).getRoute(0);
			    //����·�߽ڵ��������ڵ����ʱʹ��
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
	 * ����·�߹滮����ʾ��
	 * @param v
	 */
	void SearchButtonProcess(View v) {
		//��������ڵ��·������
		route = null;
		routeOverlay = null;
		transitOverlay = null; 
		mBtnPre.setVisibility(View.INVISIBLE);
		mBtnNext.setVisibility(View.INVISIBLE);
		// ����������ť��Ӧ
		EditText editSt = (EditText)findViewById(R.id.start);
		EditText editEn = (EditText)findViewById(R.id.end);
		
		// ������յ��name���и�ֵ��Ҳ����ֱ�Ӷ����긳ֵ����ֵ�����򽫸��������������
		MKPlanNode stNode = new MKPlanNode();
		stNode.name = editSt.getText().toString();
		MKPlanNode enNode = new MKPlanNode();
		enNode.name = editEn.getText().toString();

		// ʵ��ʹ�����������յ���н�����ȷ���趨
		if (mBtnDrive.equals(v)) {
			mSearch.drivingSearch("����", stNode, "����", enNode);
		} else if (mBtnTransit.equals(v)) {
			mSearch.transitSearch("����", stNode, enNode);
		} else if (mBtnWalk.equals(v)) {
			mSearch.walkingSearch("����", stNode, "����", enNode);
		} 
	} 
	
	/**
	 * �ڵ����ʾ��
	 * @param v
	 */
	public void nodeClick(View v){
		viewCache = getLayoutInflater().inflate(R.layout.custom_text_view, null);
        popupText =(TextView) viewCache.findViewById(R.id.textcache);
		if (searchType == 0 || searchType == 2){
			//�ݳ�������ʹ�õ����ݽṹ��ͬ���������Ϊ�ݳ����У��ڵ����������ͬ
			if (nodeIndex < -1 || route == null || nodeIndex >= route.getNumSteps())
				return;
			
			//��һ���ڵ�
			if (mBtnPre.equals(v) && nodeIndex > 0){
				//������
				nodeIndex--;
				//�ƶ���ָ������������
				mMapView.getController().animateTo(route.getStep(nodeIndex).getPoint());
				//��������
				popupText.setBackgroundResource(R.drawable.popup);
				popupText.setText(route.getStep(nodeIndex).getContent());
				pop.showPopup(BMapUtil.getBitmapFromView(popupText),
						route.getStep(nodeIndex).getPoint(),
						5);
			}
			//��һ���ڵ�
			if (mBtnNext.equals(v) && nodeIndex < (route.getNumSteps()-1)){
				//������
				nodeIndex++;
				//�ƶ���ָ������������
				mMapView.getController().animateTo(route.getStep(nodeIndex).getPoint());
				//��������
				popupText.setBackgroundResource(R.drawable.popup);
				popupText.setText(route.getStep(nodeIndex).getContent());
				pop.showPopup(BMapUtil.getBitmapFromView(popupText),
						route.getStep(nodeIndex).getPoint(),
						5);
			}
		}
		if (searchType == 1){
			//��������ʹ�õ����ݽṹ��������ͬ����˵�������ڵ����
			if (nodeIndex < -1 || transitOverlay == null || nodeIndex >= transitOverlay.getAllItem().size())
				return;
			
			//��һ���ڵ�
			if (mBtnPre.equals(v) && nodeIndex > 1){
				//������
				nodeIndex--;
				//�ƶ���ָ������������
				mMapView.getController().animateTo(transitOverlay.getItem(nodeIndex).getPoint());
				drawEstates(transitOverlay.getItem(nodeIndex).getPoint().getLongitudeE6()/1E6,transitOverlay.getItem(nodeIndex).getPoint().getLatitudeE6()/1E6);
				//��������
				popupText.setBackgroundResource(R.drawable.popup);
				popupText.setText(transitOverlay.getItem(nodeIndex).getTitle());
//				pop.showPopup(BMapUtil.getBitmapFromView(popupText),
//						transitOverlay.getItem(nodeIndex).getPoint(),
//						5);
			}
			//��һ���ڵ�
			if (mBtnNext.equals(v) && nodeIndex < (transitOverlay.getAllItem().size()-2)){
				//������
				nodeIndex++;
				//�ƶ���ָ������������
				mMapView.getController().animateTo(transitOverlay.getItem(nodeIndex).getPoint());
				drawEstates(transitOverlay.getItem(nodeIndex).getPoint().getLongitudeE6()/1E6,transitOverlay.getItem(nodeIndex).getPoint().getLatitudeE6()/1E6);
				//��������
				popupText.setBackgroundResource(R.drawable.popup);
				popupText.setText(transitOverlay.getItem(nodeIndex).getTitle());
//				pop.showPopup(BMapUtil.getBitmapFromView(popupText),
//						transitOverlay.getItem(nodeIndex).getPoint(),
//						5);
			}
		}
		
	}
	/**
	 * ������������ͼ��
	 */
	public void createPaopao(){
		
        //���ݵ����Ӧ�ص�
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
        dlog = dlog * 360 /(2 * Math.PI);        // ����ת���ɽǶ�

        dlat = distance / EARTH_RADIUS;
        dlat = dlat * 360 /(2 * Math.PI);     //����ת���ɽǶ�

        return new LongLatScope(log - dlog, log + dlog, lat + dlat, lat - dlat);
    }

    public void drawTransCircle(GeoPoint geoPoint, int pixelRadius)
    {
        Geometry palaceGeometry = new Geometry();
        //GeoPoint palaceCenter = new GeoPoint((int)(39.924 * 1E6),(int)(116.403 * 1E6));
        //palaceGeometry.setEnvelope(geoPoint1, geoPoint2);
        palaceGeometry.setCircle(geoPoint, pixelRadius);

        Symbol palaceSymbol = new Symbol();//������ʽ
        Symbol.Color palaceColor = palaceSymbol.new Color();//������ɫ
        palaceColor.red = 0;//������ɫ�ĺ�ɫ����
        palaceColor.green = 255;//������ɫ����ɫ����
        palaceColor.blue = 0;//������ɫ����ɫ����
        palaceColor.alpha = 200;//������ɫ��alphaֵ
        palaceSymbol.setSurface(palaceColor,1,3);//������ʽ��������ɫ��palaceColor�Ƿ������Σ�����

        Graphic palaceGraphic = new Graphic(palaceGeometry, palaceSymbol);
        GraphicsOverlay palaceOverlay = new GraphicsOverlay(mMapView);
        long palaceId = palaceOverlay.setData(palaceGraphic);
        //��overlay��ӵ�mapview��
        mMapView.getOverlays().add(palaceOverlay);
        //ˢ�µ�ͼʹ����ӵ�overlay��Ч
        mMapView.refresh();
        //�ƶ������ŵ�ͼ������Ұ
        mMapView.getController().setZoom(16);
        mMapView.getController().setCenter(geoPoint);
    }

    private void drawEstate(Estate estate) {
        drawTransCircle(new GeoPoint((int)(estate.getLatitude()*1E6), (int)(estate.getLongitude()*1E6)), (int)Math.sqrt(estate.getArea() / Math.PI));
    }	
}
