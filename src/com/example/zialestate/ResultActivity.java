package com.example.zialestate;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
//import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher.ViewFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
//import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.FacebookOperationCanceledException;
import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Session.StatusCallback;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;

public class ResultActivity extends Activity {

	public int mPosition = 0;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);
		getActionBar().setTitle("Results from Zillow");  
		final FrameLayout container = (FrameLayout)findViewById(R.id.container);
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		try {
			Intent intent = getIntent();
			String out = intent.getStringExtra("jsonOUT"); 
			final JSONObject jsonobj = new JSONObject(out);
			//Log.v("GotValue",jsonobj.toString());
			//TextView tv = (TextView) findViewById(R.id.textView1);
			//tv.setText(jsonobj.toString());
			ActionBar myActionBar = getActionBar();
			myActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
			Tab binfo_tab = myActionBar.newTab(); 
			binfo_tab.setTabListener(new ActionBar.TabListener(){
				@Override 
				public void onTabUnselected(Tab tab, FragmentTransaction ft){
					// TODO Auto-generated method stub
					} 
				@Override 
				public void onTabSelected(Tab tab, FragmentTransaction ft) {
					// TODO Auto-generated method stub
					final JSONObject result;
					try {
						result = jsonobj.getJSONObject("result");
					    JSONObject chart = jsonobj.getJSONObject("chart");
					    JSONObject year1chart = chart.getJSONObject("year1");
					    final String fb_name = result.getString("table_header");
					    final String fb_link = result.getString("link_addr");
					    final String fb_chart = year1chart.getString("url");
					    String sign = result.getString("img_src_oc").contains("down")?"-":"+";
					    final String fb_desc = "Last Sold Price: "+result.getString("last_sold_price")+" ,30 Days Overall Change:"+sign+result.getString("overall_change");
					DisplayMetrics displaymetrics = new DisplayMetrics();
					getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
					int screenwidth = displaymetrics.widthPixels;
					int screenheight = displaymetrics.heightPixels;
					int lw = (int) (0.5 * screenwidth);
					int rw = (int) (0.5 * screenwidth);
					ScrollView svw= new ScrollView(ResultActivity.this);
					TableLayout tbl= new TableLayout(ResultActivity.this);
					
					LinearLayout hdr= new LinearLayout(ResultActivity.this);
					TableLayout ftb = new TableLayout(ResultActivity.this);
					TableRow sm = new TableRow(ResultActivity.this);
					//sm.setBackgroundResource(R.layout.white_row);
					TextView seemore = new TextView(ResultActivity.this);
					seemore.setText("See more details on Zillow:");
					seemore.setPadding(10, 5, 0, 0);
					seemore.setWidth((int) (lw*1));
					sm.addView(seemore);
					Button fb= new Button(ResultActivity.this);
					
					
					
					fb.setBackgroundResource(R.drawable.fb_share);
					
					fb.setOnClickListener(new View.OnClickListener() {
					
						@SuppressWarnings("deprecation")
						@Override
						public void onClick(View v) {
								
							AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ResultActivity.this);
					 		alertDialogBuilder.setTitle("Post to Facebook");
					 		alertDialogBuilder
									.setCancelable(false)
									.setPositiveButton("Post Property Details",new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog,int id) {
											Facebook facebook = new Facebook("902534799759525");
											Bundle params = new Bundle();
											
											params.putString("name", fb_name);
											params.putString("caption", "Property Information from Zillow.com");
											params.putString("description", fb_desc);
											params.putString("link", fb_link);
											params.putString("picture", fb_chart);
															
											
											    facebook.dialog(ResultActivity.this,"feed",params, new DialogListener() {

											@Override
											public void onFacebookError(FacebookError e) {
											// TODO Auto-generated method stub
												Toast.makeText(ResultActivity.this, 
								                        "Facebook Error!", 
								                        Toast.LENGTH_SHORT).show();
											}

											@Override
											public void onError(DialogError e) {
											// TODO Auto-generated method stub
												Toast.makeText(ResultActivity.this, 
								                        "Posting Error!", 
								                        Toast.LENGTH_SHORT).show();
											}

											@Override
											public void onComplete(Bundle values) {
											// TODO Auto-generated method stub
												if(values.getString("post_id")==null)
												{
													Toast.makeText(ResultActivity.this, 
									                        "Post Cancelled", 
									                        Toast.LENGTH_SHORT).show();
												}
												else{
												Toast.makeText(ResultActivity.this, 
								                        "Posted Story, ID:"+values.getString("post_id"), 
								                        Toast.LENGTH_LONG).show();}
												
											}

											@Override
											public void onCancel() {
											// TODO Auto-generated method stub
												Toast.makeText(ResultActivity.this, 
								                        "Post Cancelled", 
								                        Toast.LENGTH_SHORT).show();
											}
											});
										}
									  })
									.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog,int id) {
											// if this button is clicked, just close
											// the dialog box and do nothing
											dialog.cancel();
											Toast.makeText(ResultActivity.this, 
							                        "Post Cancelled", 
							                        Toast.LENGTH_SHORT).show();
										}
									});
					 									
									AlertDialog alertDialog = alertDialogBuilder.create();
					 									
									alertDialog.show();

					
						}
					});
					
					
					sm.addView(fb);
					ftb.addView(sm);
					
					TableRow linkrow = new TableRow(ResultActivity.this);
					//linkrow.setBackgroundResource(R.layout.white_row);
					TextView link = new TextView(ResultActivity.this);
					
					link.setText(Html.fromHtml("<a href=\""+result.getString("link_addr")+"\">"+result.getString("table_header")+"</a>"));
					link.setMovementMethod(LinkMovementMethod.getInstance());
					link.setPadding(10, 5, 10, 5);
					link.setWidth((int) (screenwidth*0.82));
					linkrow.addView(link);
					ftb.addView(linkrow);
					hdr.addView(ftb);
					
					
					TableRow tr1 = new TableRow(ResultActivity.this);
					tr1.setBackgroundResource(R.layout.grey_row);
					TextView prop_type = new TextView(ResultActivity.this);
					TextView prop_type_value = new TextView(ResultActivity.this);
					prop_type.setText("Property Type:");
					prop_type.setPadding(10, 10, 10, 10);
					prop_type.setWidth(lw);
					prop_type_value.setText(result.getString("prop_type"));
					prop_type_value.setPadding(10, 10, 10, 10);
					prop_type_value.setWidth(rw);
					prop_type_value.setGravity(Gravity.RIGHT);
					tr1.addView(prop_type);
					tr1.addView(prop_type_value);
					tbl.addView(tr1);
					
					TableRow tr2 = new TableRow(ResultActivity.this);
					tr2.setBackgroundResource(R.layout.white_row);
					TextView yr_built = new TextView(ResultActivity.this);
					TextView yr_built_value = new TextView(ResultActivity.this);
					yr_built.setText("Year Built:");
					yr_built.setPadding(10, 10, 10, 10);
					yr_built.setWidth(lw);
					yr_built_value.setText(result.getString("year_built"));
					yr_built_value.setPadding(10, 10, 10, 10);
					yr_built_value.setWidth(rw);
					yr_built_value.setGravity(Gravity.RIGHT);
					tr2.addView(yr_built);
					tr2.addView(yr_built_value);
					tbl.addView(tr2);
					
					TableRow tr3 = new TableRow(ResultActivity.this);
					tr3.setBackgroundResource(R.layout.grey_row);
					TextView lot_size = new TextView(ResultActivity.this);
					TextView lot_size_value = new TextView(ResultActivity.this);
					lot_size.setText("Lot Size:");
					lot_size.setPadding(10, 10, 10, 10);
					lot_size.setWidth(lw);
					lot_size_value.setText(result.getString("lot_size"));
					lot_size_value.setPadding(10, 10, 10, 10);
					lot_size_value.setWidth(rw);
					lot_size_value.setGravity(Gravity.RIGHT);
					tr3.addView(lot_size);
					tr3.addView(lot_size_value);
					tbl.addView(tr3);
					
					TableRow tr4 = new TableRow(ResultActivity.this);
					tr4.setBackgroundResource(R.layout.white_row);
					TextView fin_area = new TextView(ResultActivity.this);
					TextView fin_area_value = new TextView(ResultActivity.this);
					fin_area.setText("Finished Area:");
					fin_area.setPadding(10, 10, 10, 10);
					fin_area.setWidth(lw);
					fin_area_value.setText(result.getString("fin_area"));
					fin_area_value.setPadding(10, 10, 10, 10);
					fin_area_value.setWidth(rw);
					fin_area_value.setGravity(Gravity.RIGHT);
					tr4.addView(fin_area);
					tr4.addView(fin_area_value);
					tbl.addView(tr4);
					
					TableRow tr5 = new TableRow(ResultActivity.this);
					tr5.setBackgroundResource(R.layout.grey_row);
					TextView bathrooms = new TextView(ResultActivity.this);
					TextView bathrooms_value = new TextView(ResultActivity.this);
					bathrooms.setText("Bathrooms:");
					bathrooms.setPadding(10, 10, 10, 10);
					bathrooms.setWidth(lw);
					bathrooms_value.setText(result.getString("bathrooms"));
					bathrooms_value.setPadding(10, 10, 10, 10);
					bathrooms_value.setWidth(rw);
					bathrooms_value.setGravity(Gravity.RIGHT);
					tr5.addView(bathrooms);
					tr5.addView(bathrooms_value);
					tbl.addView(tr5);
					
					
					TableRow tr6 = new TableRow(ResultActivity.this);
					tr6.setBackgroundResource(R.layout.white_row);
					TextView bedrooms = new TextView(ResultActivity.this);
					TextView bedrooms_value = new TextView(ResultActivity.this);
					bedrooms.setText("Bedrooms:");
					bedrooms.setPadding(10, 10, 10, 10);
					bedrooms.setWidth(lw);
					bedrooms_value.setText(result.getString("bedrooms"));
					bedrooms_value.setPadding(10, 10, 10, 10);
					bedrooms_value.setWidth(rw);
					bedrooms_value.setGravity(Gravity.RIGHT);
					tr6.addView(bedrooms);
					tr6.addView(bedrooms_value);
					tbl.addView(tr6);
					
					TableRow tr7 = new TableRow(ResultActivity.this);
					tr7.setBackgroundResource(R.layout.grey_row);
					TextView tax_ass_year = new TextView(ResultActivity.this);
					TextView tax_ass_year_value = new TextView(ResultActivity.this);
					tax_ass_year.setText("Tax Assessment Year:");
					tax_ass_year.setPadding(10, 10, 10, 10);
					tax_ass_year.setWidth(lw);
					tax_ass_year_value.setText(result.getString("tax_ass_year"));
					tax_ass_year_value.setPadding(10, 10, 10, 10);
					tax_ass_year_value.setWidth(rw);
					tax_ass_year_value.setGravity(Gravity.RIGHT);
					tr7.addView(tax_ass_year);
					tr7.addView(tax_ass_year_value);
					tbl.addView(tr7);
					
					TableRow tr8 = new TableRow(ResultActivity.this);
					tr8.setBackgroundResource(R.layout.white_row);
					TextView tax_ass = new TextView(ResultActivity.this);
					TextView tax_ass_value = new TextView(ResultActivity.this);
					tax_ass.setText("Tax Assessment:");
					tax_ass.setPadding(10, 10, 10, 10);
					tax_ass.setWidth(lw);
					tax_ass_value.setText(result.getString("tax_ass"));
					tax_ass_value.setPadding(10, 10, 10, 10);
					tax_ass_value.setWidth(rw);
					tax_ass_value.setGravity(Gravity.RIGHT);
					tr8.addView(tax_ass);
					tr8.addView(tax_ass_value);
					tbl.addView(tr8);
									
					TableRow tr9 = new TableRow(ResultActivity.this);
					tr9.setBackgroundResource(R.layout.grey_row);
					TextView last_sold_price = new TextView(ResultActivity.this);
					TextView last_sold_price_value = new TextView(ResultActivity.this);
					last_sold_price.setText("Last Sold Price:");
					last_sold_price.setPadding(10, 10, 10, 10);
					last_sold_price.setWidth(lw);
					last_sold_price_value.setText(result.getString("last_sold_price"));
					last_sold_price_value.setPadding(10, 10, 10, 10);
					last_sold_price_value.setWidth(rw);
					last_sold_price_value.setGravity(Gravity.RIGHT);
					tr9.addView(last_sold_price);
					tr9.addView(last_sold_price_value);
					tbl.addView(tr9);
					
					TableRow tr10 = new TableRow(ResultActivity.this);
					tr10.setBackgroundResource(R.layout.white_row);
					TextView last_sold_date = new TextView(ResultActivity.this);
					TextView last_sold_date_value = new TextView(ResultActivity.this);
					last_sold_date.setText("Last Sold Date:");
					last_sold_date.setPadding(10, 10, 10, 10);
					last_sold_date.setWidth(lw);
					last_sold_date_value.setText(result.getString("last_sold_date"));
					last_sold_date_value.setPadding(10, 10, 10, 10);
					last_sold_date_value.setWidth(rw);
					last_sold_date_value.setGravity(Gravity.RIGHT);
					tr10.addView(last_sold_date);
					tr10.addView(last_sold_date_value);
					tbl.addView(tr10);
					
					TableRow tr11 = new TableRow(ResultActivity.this);
					tr11.setBackgroundResource(R.layout.grey_row);
					TextView zest_amt = new TextView(ResultActivity.this);
					TextView zest_amt_value = new TextView(ResultActivity.this);
					zest_amt.setText("Zestimate ® Property Estimate as of "+result.getString("zest_date")+":");
					zest_amt.setPadding(10, 10, 10, 10);
					zest_amt.setWidth(lw);
					zest_amt_value.setText(result.getString("zest_amt"));
					zest_amt_value.setPadding(10, 10, 10, 10);
					zest_amt_value.setWidth(rw);
					zest_amt_value.setGravity(Gravity.RIGHT);
					tr11.addView(zest_amt);
					tr11.addView(zest_amt_value);
					tbl.addView(tr11);

					TableRow tr12 = new TableRow(ResultActivity.this);
					tr12.setBackgroundResource(R.layout.white_row);
					TextView overall_change = new TextView(ResultActivity.this);
					TextView overall_change_value = new TextView(ResultActivity.this);
					SpannableString spStr = new SpannableString(" "+ result.getString("overall_change"));
					String image_type = result.getString("img_src_oc").toString();
					Drawable d = null;
					try{
					if(image_type.contains("down"))
					{
						d = getResources().getDrawable(R.drawable.down_r); 
					}
					if(image_type.contains("up")) {
						d = getResources().getDrawable(R.drawable.up_g); 
					}
						
					//d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight()); 
					d.setBounds(0, 0, 20, 30);
					ImageSpan imgSpan = new ImageSpan(d, ImageSpan.ALIGN_BASELINE); 
					spStr.setSpan(imgSpan, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE); 
					overall_change_value.setText(spStr);
					}
					catch(Exception e)
					{
						overall_change_value.setText("N/A");
					}
					
					overall_change.setText("30 Days Overall change:       ");
					overall_change.setPadding(10, 10, 10, 10);
					overall_change.setWidth(lw);
					
					overall_change_value.setPadding(0, 10, 10, 10);
					overall_change_value.setWidth(rw);
					overall_change_value.setGravity(Gravity.RIGHT);
					tr12.addView(overall_change);
					tr12.addView(overall_change_value);
					tbl.addView(tr12); 
					
					
					
					
					TableRow tr13 = new TableRow(ResultActivity.this);
					tr13.setBackgroundResource(R.layout.grey_row);
					TextView all_time_prop = new TextView(ResultActivity.this);
					TextView all_time_prop_value = new TextView(ResultActivity.this);
					all_time_prop.setText("All Time Property Range:");
					all_time_prop.setPadding(10, 10, 10, 10);
					all_time_prop.setWidth(lw);
					all_time_prop_value.setText(result.getString("all_time_prop"));
					all_time_prop_value.setPadding(10, 10, 10, 10);
					all_time_prop_value.setWidth(rw);
					all_time_prop_value.setGravity(Gravity.RIGHT);
					tr13.addView(all_time_prop);
					tr13.addView(all_time_prop_value);
					tbl.addView(tr13);
					
					
					
					TableRow tr14 = new TableRow(ResultActivity.this);
					tr14.setBackgroundResource(R.layout.white_row);
					TextView rent_amt = new TextView(ResultActivity.this);
					TextView rent_amt_value = new TextView(ResultActivity.this);
					rent_amt.setText("Rent Zestimate ® value as of "+result.getString("rent_date")+":");
					rent_amt.setPadding(10, 10, 10, 10);
					rent_amt.setWidth(lw);
					rent_amt_value.setText(result.getString("rent_amt"));
					rent_amt_value.setPadding(10, 10, 10, 10);
					rent_amt_value.setWidth(rw);
					rent_amt_value.setGravity(Gravity.RIGHT);
					tr14.addView(rent_amt);
					tr14.addView(rent_amt_value);
					tbl.addView(tr14);

					
					
					TableRow tr15 = new TableRow(ResultActivity.this);
					tr15.setBackgroundResource(R.layout.grey_row);
					TextView rent_change = new TextView(ResultActivity.this);
					TextView rent_change_value = new TextView(ResultActivity.this);
					SpannableString spStr2 = new SpannableString(" "+ result.getString("rent_change"));
					String image_typ = result.getString("img_src_rc").toString();
					Drawable d2 = null;
					try{
					if(image_typ.contains("down"))
					{
						d2 = getResources().getDrawable(R.drawable.down_r); 
					}
					if(image_typ.contains("up")) {
						d2 = getResources().getDrawable(R.drawable.up_g); 
					}
					d2.setBounds(0, 0, 20, 30); 
					ImageSpan imgSpan2 = new ImageSpan(d2, ImageSpan.ALIGN_BASELINE); 
					spStr2.setSpan(imgSpan2, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
					rent_change_value.setText(spStr2);
					}
					catch(Exception e)
					{
						rent_change_value.setText("N/A");	
					}
						
					 
					rent_change.setText("30 Days Rent change:       ");
					rent_change.setPadding(10, 10, 10, 10);
					rent_change.setWidth(lw);
					
					rent_change_value.setPadding(0, 10, 10, 10);
					rent_change_value.setWidth(rw);
					rent_change_value.setGravity(Gravity.RIGHT);
					tr15.addView(rent_change);
					tr15.addView(rent_change_value);
					tbl.addView(tr15); 
					  
					 
					
					
					TableRow tr16 = new TableRow(ResultActivity.this); 
					tr16.setBackgroundResource(R.layout.white_row);
					TextView all_time_rent = new TextView(ResultActivity.this);
					TextView all_time_rent_value = new TextView(ResultActivity.this);
					all_time_rent.setText("All Time Rent Range:");
					all_time_rent.setPadding(10, 10, 10, 10);
					all_time_rent.setWidth(lw);
					all_time_rent_value.setText(result.getString("all_time_rent"));
					all_time_rent_value.setPadding(10, 10, 10, 10);
					all_time_rent_value.setWidth(rw);
					all_time_rent_value.setGravity(Gravity.RIGHT);
					tr16.addView(all_time_rent);
					tr16.addView(all_time_rent_value);
					tbl.addView(tr16);
					
					
					
							
					svw.addView(tbl);
					LayoutParams sparams = new LayoutParams(LayoutParams.WRAP_CONTENT,(int) (screenheight*0.55));
					svw.setLayoutParams(sparams);
					container.removeAllViews();
					LinearLayout main = new LinearLayout(ResultActivity.this);
					TableLayout maintab = new TableLayout(ResultActivity.this);
					TableRow mainrow1 = new TableRow(ResultActivity.this);
					//TableRow mainrow2 = new TableRow(ResultActivity.this);
					TableLayout distab = new TableLayout(ResultActivity.this);
					TableRow mainrow3 = new TableRow(ResultActivity.this);
					TableRow mainrow4 = new TableRow(ResultActivity.this);
					TableRow mainrow5 = new TableRow(ResultActivity.this);
					 
					
					TextView disclaimer = new TextView(ResultActivity.this);
					TextView disclaimer2 = new TextView(ResultActivity.this);
					TextView disclaimer3 = new TextView(ResultActivity.this);
					
					disclaimer.setText("\u00A9 Zillow, Inc., 2006-2014.");
					disclaimer.setGravity(Gravity.CENTER);
					disclaimer.setWidth(lw+rw);
					disclaimer2.setText(Html.fromHtml("<html>Use is subject to <a href=\"http://www.zillow.com/corp/Terms.htm\">Terms of Use</a></html>"));
					disclaimer2.setGravity(Gravity.CENTER);
					disclaimer3.setText(Html.fromHtml("<a href=\"http://www.zillow.com/zestimate/\">What's a Zestimate?</a>"));
					disclaimer3.setGravity(Gravity.CENTER);
					disclaimer.setMovementMethod(LinkMovementMethod.getInstance());
					disclaimer2.setMovementMethod(LinkMovementMethod.getInstance());
					disclaimer3.setMovementMethod(LinkMovementMethod.getInstance());
					
					
					
					
					mainrow1.addView(hdr);
					maintab.addView(mainrow1);
					maintab.addView(svw);
					
					mainrow3.addView(disclaimer);
					mainrow4.addView(disclaimer2);
					mainrow5.addView(disclaimer3);
					//maintab.addView(mainrow2);
					distab.addView(mainrow3);
					distab.addView(mainrow4);
					distab.addView(mainrow5);
					maintab.addView(distab);
					main.addView(maintab);
					
					container.addView(main);
					
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
					
					} 
					@Override public void onTabReselected(Tab tab, FragmentTransaction ft) { 
						// TODO Auto-generated method stub 
						} 
					});
			Tab hist_tab = myActionBar.newTab(); 
			hist_tab.setTabListener(new ActionBar.TabListener(){
				@Override 
				public void onTabUnselected(Tab tab, FragmentTransaction ft){
					// TODO Auto-generated method stub
					} 
				@Override public void onTabSelected(Tab tab, FragmentTransaction ft) {
					DisplayMetrics displaymetrics = new DisplayMetrics();
					getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
					final int screenheight = displaymetrics.heightPixels;
					final ImageSwitcher imageSwitcher=new ImageSwitcher(ResultActivity.this);
					
					class Histogram extends AsyncTask<String, Bitmap, Bitmap> {

						@Override
						protected Bitmap doInBackground(String... params) {
							
							InputStream is=null;
							//Log.v("in background execute","success");
							 DefaultHttpClient client = new DefaultHttpClient();
					            //HttpPost post = new HttpPost(params[0]);
							 	HttpGet get = new HttpGet(params[0]);
					            HttpResponse response = null;
								try {
									response = client.execute(get);
								} catch (ClientProtocolException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
					            HttpEntity entity = response.getEntity();
					            try {
									is = entity.getContent();
								} catch (IllegalStateException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								
							
							 Bitmap bitmap = BitmapFactory.decodeStream(is);
						      
							return bitmap;
						}
						 @Override
					      protected void onPostExecute(Bitmap bitmap) {
							 Drawable drawable =new BitmapDrawable(bitmap);
								
								imageSwitcher.setImageDrawable(drawable);
								//Log.v("in post execute","success");
						 	}
						 }	
					// TODO Auto-generated method stub
					try{
						  JSONObject resultJSON;
						  JSONObject chartJSON;
						  JSONObject oneyear;
						  JSONObject fiveyear;
						  JSONObject tenyear;
						  
						  resultJSON = jsonobj.getJSONObject("result");
						  chartJSON = jsonobj.getJSONObject("chart");
						  oneyear = chartJSON.getJSONObject("year1");
						  fiveyear = chartJSON.getJSONObject("year5");
						  tenyear = chartJSON.getJSONObject("year10");
						  final List<String> imagesList= new ArrayList<String>();
						  final List<String>  textList= new ArrayList<String>();
						  
						  imagesList.add(oneyear.getString("url"));
						  imagesList.add(fiveyear.getString("url"));
						  imagesList.add(tenyear.getString("url"));
						  
						  textList.add("Historical Zestimate for the past 1 year");
						  textList.add("Historical Zestimate for the past 5 years");
						  textList.add("Historical Zestimate for the past 10 years");
						  
						  final String address=resultJSON.getString("table_header");

						  
						  LinearLayout linearLayout =new LinearLayout(ResultActivity.this) ;
						  linearLayout.setOrientation(LinearLayout.VERTICAL);
						  LinearLayout buttonLinearLayout =new LinearLayout(ResultActivity.this) ;
						
						Button nextButton = new Button(ResultActivity.this);
						Button prevButton = new Button(ResultActivity.this);
						Button dummy1Button = new Button(ResultActivity.this);
						Button dummy2Button = new Button(ResultActivity.this);
						nextButton.setText("Next");
						//nextButton.setWidth(100);
						prevButton.setText("Prev");
						//prevButton.setWidth(100);
						final TextSwitcher boldTextSwitcher = new TextSwitcher(ResultActivity.this);
						boldTextSwitcher.setFactory(new ViewFactory() {
							@Override
							public View makeView() {
								TextView textView = new TextView(ResultActivity.this);
								textView.setGravity(Gravity.CENTER);
								textView.setTypeface(Typeface.DEFAULT_BOLD);
									
								return textView;
							}
						});
						
						boldTextSwitcher.setInAnimation(ResultActivity.this, android.R.anim.fade_in);
						boldTextSwitcher.setOutAnimation(ResultActivity.this, android.R.anim.fade_out);
				    	final TextSwitcher textSwitcher=new TextSwitcher(ResultActivity.this);
						textSwitcher.setFactory(new ViewFactory() {
							@Override
							public View makeView() {
								
								TextView textView = new TextView(ResultActivity.this);
								textView.setGravity(Gravity.CENTER);
												
								return textView;
							}
						});
						
						textSwitcher.setInAnimation(ResultActivity.this, android.R.anim.fade_in);
						textSwitcher.setOutAnimation(ResultActivity.this, android.R.anim.fade_out);	
						
						imageSwitcher.setFactory(new ViewFactory() {
							@Override
							public View makeView() {
								ImageView imgView = new ImageView(ResultActivity.this);
								imgView.setScaleType(ImageView.ScaleType.FIT_XY);
								imgView.setLayoutParams(new ImageSwitcher.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
				                imgView.setPadding(0, 40, 0, 40);
				                imgView.getLayoutParams().height = (int) (screenheight * 0.5);
				                
								
								return imgView;
							}
						});
						imageSwitcher.setInAnimation(ResultActivity.this, android.R.anim.fade_in);
						imageSwitcher.setOutAnimation(ResultActivity.this, android.R.anim.fade_out);
						
						boldTextSwitcher.setText(textList.get(0));
						textSwitcher.setText(address);
						new Histogram().execute(imagesList.get(0));
						 
						nextButton.setOnClickListener(new View.OnClickListener() {
				            public void onClick(View v) {
				            mPosition = (mPosition + 1) % imagesList.size();	 
				            
							new Histogram().execute(imagesList.get(mPosition));
							boldTextSwitcher.setText(textList.get(mPosition));
							textSwitcher.setText(address);
							//Log.v("next", Integer.toString(mPosition));
			                  }
				            });
						
						prevButton.setOnClickListener(new View.OnClickListener() {
				             public void onClick(View v) {
				            mPosition = ((mPosition + imagesList.size() -1) % imagesList.size());
				            
							
							new Histogram().execute(imagesList.get(mPosition));
							boldTextSwitcher.setText(textList.get(mPosition));
							textSwitcher.setText(address);
										
			                 }
				            });
						
						linearLayout.addView(boldTextSwitcher);
						linearLayout.addView(textSwitcher);
						linearLayout.addView(imageSwitcher);
						
						
						dummy1Button.setVisibility(Button.INVISIBLE);
						dummy2Button.setVisibility(Button.INVISIBLE);
						buttonLinearLayout.addView(dummy1Button);
						buttonLinearLayout.addView(dummy2Button);
						buttonLinearLayout.addView(prevButton);
						buttonLinearLayout.addView(nextButton);
						
						linearLayout.addView(buttonLinearLayout);
						TextView disclaimer = new TextView(ResultActivity.this);
						TextView disclaimer2 = new TextView(ResultActivity.this);
						TextView disclaimer3 = new TextView(ResultActivity.this);
						disclaimer.setText("\u00A9 Zillow, Inc., 2006-2014.");
						disclaimer.setGravity(Gravity.CENTER);
						disclaimer2.setText(Html.fromHtml("<html>Use is subject to <a href=\"http://www.zillow.com/corp/Terms.htm\">Terms of Use</a></html>"));
						disclaimer2.setGravity(Gravity.CENTER);
						disclaimer3.setText(Html.fromHtml("<a href=\"http://www.zillow.com/zestimate/\">What's a Zestimate?</a>"));
						disclaimer3.setGravity(Gravity.CENTER);
						disclaimer.setMovementMethod(LinkMovementMethod.getInstance());
						disclaimer2.setMovementMethod(LinkMovementMethod.getInstance());
						disclaimer3.setMovementMethod(LinkMovementMethod.getInstance());
						linearLayout.addView(disclaimer);
						linearLayout.addView(disclaimer2);
						linearLayout.addView(disclaimer3);
						container.removeAllViews();
						container.addView(linearLayout);
						
						  }catch(Exception e){
							  e.printStackTrace();
						  }
		
					} 
					@Override public void onTabReselected(Tab tab, FragmentTransaction ft) { 
						// TODO Auto-generated method stub 
						} 
					});
	
			binfo_tab.setText("Basic Info"); 
			myActionBar.addTab(binfo_tab);
			hist_tab.setText("Historical Zestimates"); 
			myActionBar.addTab(hist_tab);
			
			
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

		
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.result, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}
			
		String fb_name = null;
		String fb_desc = null;
		String fb_link = null;
		String fb_chart = null;
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_result,
					container, false);
			//publishFeedDialog();
			return rootView;
		}
		
		@SuppressWarnings("deprecation")
		public void publishFeedDialog(){
			Facebook facebook = new Facebook("902534799759525");
			Bundle params = new Bundle();
			
			params.putString("name", fb_name);
			params.putString("caption", "Property Information from Zillow.com");
			
			params.putString("description", fb_desc);
			
			params.putString("link", fb_link);
			
			params.putString("picture", fb_chart);
			    facebook.dialog(getActivity().getApplicationContext(),"feed",params, new DialogListener() {

			    	@Override
					public void onFacebookError(FacebookError e) {
					// TODO Auto-generated method stub
						Toast.makeText(getActivity(), 
		                        "Facebook Error!", 
		                        Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onError(DialogError e) {
					// TODO Auto-generated method stub
						Toast.makeText(getActivity(), 
		                        "Posting Error!", 
		                        Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onComplete(Bundle values) {
					// TODO Auto-generated method stub
						Toast.makeText(getActivity(), 
		                        "Posted Story Successfully, ID:"+values.getString("post_id"), 
		                        Toast.LENGTH_LONG).show();
					}

					@Override
					public void onCancel() {
					// TODO Auto-generated method stub
						Toast.makeText(getActivity(), 
		                        "Posting Cancelled", 
		                        Toast.LENGTH_SHORT).show();
					}
					});
		}
	}
}

