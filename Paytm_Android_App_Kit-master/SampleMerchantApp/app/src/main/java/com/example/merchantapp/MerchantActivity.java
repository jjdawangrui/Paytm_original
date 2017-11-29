package com.example.merchantapp;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONObject;

import java.util.TreeMap;

/**
 * This is the sample app which will make use of the PG SDK. This activity will
 * show the usage of Paytm PG SDK API's.
 **/

public class MerchantActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.merchantapp);
		//initOrderId();
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
	}
	
	//This is to refresh the order id: Only for the Sample App's purpose.
	@Override
	protected void onStart(){
		super.onStart();
		//initOrderId();
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
	}
	

	/*private void initOrderId() {
		Random r = new Random(System.currentTimeMillis());
		String orderId = "ORDER" + (1 + r.nextInt(2)) * 10000
				+ r.nextInt(10000);
		EditText orderIdEditText = (EditText) findViewById(R.id.order_id);
		orderIdEditText.setText(orderId);
	}*/

	public void onStartTransaction(View view) {

		final String orderid= System.currentTimeMillis()+"";
		String mobile_no = "7836042510";
		HttpUtils http = new HttpUtils();
		String url = "http://payment.xender.com/payment/checksum?orderid="+orderid+"&custid=2&txnamount=2.00&mobileno="+mobile_no+"&email=test@gamil.com";

		http.send(HttpRequest.HttpMethod.POST, url, new RequestCallBack<Object>() {
			@Override
			public void onSuccess(ResponseInfo<Object> responseInfo) {

				String result = (String) responseInfo.result;
				Log.e("MerchantActivity", "<> " + result);
				TreeMap paramMap = getTreeMap(result);

				PaytmOrder Order = new PaytmOrder(paramMap);

				PaytmPGService Service = PaytmPGService.getProductionService();
				Service.initialize(Order, null);

				Service.startPaymentTransaction(MerchantActivity.this, true, true,
						new PaytmPaymentTransactionCallback() {

							@Override
							public void someUIErrorOccurred(String inErrorMessage) {
								// Some UI Error Occurred in Payment Gateway Activity.
								// // This may be due to initialization of views in
								// Payment Gateway Activity or may be due to //
								// initialization of webview. // Error Message details
								// the error occurred.
							}

							@Override
							public void onTransactionResponse(Bundle inResponse) {
								Log.e("MerchantActivity", "Payment Transaction : " + inResponse);
								Toast.makeText(getApplicationContext(), "Payment Transaction response "+inResponse.toString(), Toast.LENGTH_LONG).show();


//								new Thread(new Runnable() {
//									@Override
//									public void run() {
//										try {
//											String result = new XenderTopClient().sendGetRequest("http://payment.xender.com/payment/verify","orderid="+orderid);
//											Log.e("------------", "<> " + result);
//										} catch (IOException e) {
//											e.printStackTrace();
//										} catch (Exception e) {
//											e.printStackTrace();
//										}
//									}
//								}).start();
							}

							@Override
							public void networkNotAvailable() {
								// If network is not
								// available, then this
								// method gets called.
							}

							@Override
							public void clientAuthenticationFailed(String inErrorMessage) {
								// This method gets called if client authentication
								// failed. // Failure may be due to following reasons //
								// 1. Server error or downtime. // 2. Server unable to
								// generate checksum or checksum response is not in
								// proper format. // 3. Server failed to authenticate
								// that client. That is value of payt_STATUS is 2. //
								// Error Message describes the reason for failure.
							}

							@Override
							public void onErrorLoadingWebPage(int iniErrorCode,
															  String inErrorMessage, String inFailingUrl) {

							}

							// had to be added: NOTE
							@Override
							public void onBackPressedCancelTransaction() {
								// TODO Auto-generated method stub
							}

							@Override
							public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
								Log.d("MerchantActivity", "Payment Transaction Failed " + inErrorMessage);
								Toast.makeText(getBaseContext(), "Payment Transaction Failed ", Toast.LENGTH_LONG).show();
							}

						});

			}

			@Override
			public void onFailure(HttpException e, String s) {

			}
		});

	}

	private TreeMap getTreeMap(String result) {
		TreeMap<String, String> paramMap = new TreeMap<>();
		if (!TextUtils.isEmpty(result)) {
			try {
				JSONObject data = new JSONObject(result);
//				JSONObject data = jo.getJSONObject("data");
				paramMap.put("CALLBACK_URL", data.getString("CALLBACK_URL"));
				paramMap.put("CHANNEL_ID", data.getString("CHANNEL_ID"));
				paramMap.put("CHECKSUMHASH", data.getString("CHECKSUMHASH"));
				paramMap.put("CUST_ID", data.getString("CUST_ID"));
				paramMap.put("INDUSTRY_TYPE_ID", data.getString("INDUSTRY_TYPE_ID"));
				paramMap.put("MID", data.getString("MID"));
				paramMap.put("MOBILE_NO", data.getString("MOBILE_NO"));
				paramMap.put("ORDER_ID", data.getString("ORDER_ID"));
				paramMap.put("TXN_AMOUNT", data.getString("TXN_AMOUNT"));
				paramMap.put("WEBSITE", data.getString("WEBSITE"));
				paramMap.put( "EMAIL" , data.getString("EMAIL"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		Log.e("MerchantActivity", paramMap.toString());
		return paramMap;
	}
}
