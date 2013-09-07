(function(){
	window.EstateCircle = {
		create : function(price, area, lon, lat, expectedPrice){
			var color = mapColor(price, expectedPrice);
			//var size = Math.log(parseInt(area)) * 20;
			var size = Math.sqrt(parseInt(area) / Math.PI);
			var point = {lng:parseFloat(lon), lat:parseFloat(lat)};
			var circle = new BMap.Circle(point, size);
			circle.setStrokeColor(color);
			circle.setStrokeWeight(1);
			circle.setFillColor(color);
			circle.setStrokeStyle("solid");
			circle.setStrokeOpacity(0.8);
			circle.setFillOpacity(0.8);
			return circle;
		}
	}
	
	function mapColor(price, expectedPrice){
		var red = "#FF0000";
		var orange = "#FF9900";
		var green = "#33FF00";
		var blue = "#0066FF";
		
		var priceFloat = parseFloat(price);
		var expectedPriceFloat = parseFloat(expectedPrice);
		//if( moreOrLess(priceFloat, expectedPriceFloat) ){
			//return yellow;
		//}
		if( littleLessThanExpected(priceFloat, expectedPriceFloat) ){
			return green;
		}		
		if ( littleMoreThanExpected(priceFloat, expectedPriceFloat) ){
			return orange;
		}
		if ( greatLessThanExpected(priceFloat, expectedPriceFloat) ){
			return blue;
		}
		if ( greatMoreThanExpected(priceFloat, expectedPriceFloat) ){
			return red;
		}
	}
	
	function greatLessThanExpected(price, expected){
		return expected - price > 10000;
	}
	
	function littleLessThanExpected(price, expected){
		var gap = expected - price;
		return gap <= 10000 && gap >= 0;
	}
	
	function moreOrLess(price, expected){
		var gap = expected - price;
		return gap >= -5000 && gap <= 5000;
	}
	
	function littleMoreThanExpected(price, expected){
		var gap = price - expected;
		return gap > 0 && gap <= 10000;
	}
	
	function greatMoreThanExpected(price, expected){
		return price - expected > 10000;
	}
})();
