package org.cat.pmk.events.api;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

public class PmkEventsRestApi {

    public static String sessionId = "tbd";

    private static final String BASE_URL = "http://www.pmk-bielefeld.de";

    private static AsyncHttpClient client = new AsyncHttpClient();



    public static void getEventsByGeo(float geoLat, float geoLon, int distanceIdx, AsyncHttpResponseHandler responseHandler) {
        System.out.println("Selected distance (index):  " + distanceIdx);
        int distance = 25;
        switch (distanceIdx) {
            case 0:
                distance = 25;
                break;
            case 1:
                distance = 50;
                break;
            case 2:
                distance = 100;
                break;
            case 3:
                distance = 200;
                break;
        }

        System.out.println("GEO lat: " + geoLat);
        System.out.println("GEO lon: " + geoLon);
        System.out.println("GEO dis: " + distance);


        RequestParams params = null;
        String url = getAbsoluteUrl("/pmkEvents/api/getEventsByGeo.php?days=14&lat="+ String.valueOf(geoLat)+"&lon="+String.valueOf(geoLon)+ "&dis="+String.valueOf(distance));

        System.out.println("REST URL: " + url);
        client.get(url, params, responseHandler);
    }


    public static void createOrder(String installedBaseID, String service, ResponseHandlerInterface responseHandler) {
        String url = getAbsoluteUrl("/services/apexrest/WSSimpleOrder/" + service + "/" + installedBaseID);

        client.addHeader("Authorization", "Bearer " + sessionId);

        RequestParams params = new RequestParams();


        System.out.println("REST URL: " + url);
        client.post(url, params, responseHandler);

        // -d '{  "name" : "001D000000IqhSLIAZ",  "phone" : "ss",  "website" : true}'
    }

    public static void getAppointments(String orderID, AsyncHttpResponseHandler responseHandler) {
        client.addHeader("Authorization", "Bearer " + sessionId);
        RequestParams params = null;
        String url = getAbsoluteUrl("/services/apexrest/WSSimpleAppointment/" + orderID);


        System.out.println("REST URL: " + url);
        client.get(url, params, responseHandler);
    }


    public static void saveAppointment(String MessageID, String orderID, String index, AsyncHttpResponseHandler responseHandler) {
        client.addHeader("Authorization", "Bearer " + sessionId);
        client.addHeader("Content-Type", "application/json");
        client.addHeader("Accept", "*/*");

        String url = getAbsoluteUrl("/services/apexrest/WSSimpleAppointment/");
        System.out.println("REST URL: " + url);

        RequestParams params = new RequestParams();
        params.put("MessageId", MessageID);
        params.put("mode", "mode");
        params.put("OrderId", orderID);
        params.put("Index", index);
        client.post(url, params, responseHandler);
    }


    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.addHeader("Authorization", "Bearer " + sessionId);
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.addHeader("Authorization", "Bearer " + sessionId);
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    private static String encode(String query) {
        return query.replace(" ", "+");
        //.replace(")","\\)").replace("(","\\(");
    }


}
