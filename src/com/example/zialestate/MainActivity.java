package com.example.zialestate;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
//import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;



import java.io.UnsupportedEncodingException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;



public class MainActivity extends Activity {
	 
	private String staddr,city,state;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ImageView img = (ImageView)findViewById(R.id.imageView1);
		img.setOnClickListener(new View.OnClickListener(){
		    public void onClick(View v){
		        Intent intent = new Intent();
		        intent.setAction(Intent.ACTION_VIEW);
		        intent.addCategory(Intent.CATEGORY_BROWSABLE);
		        intent.setData(Uri.parse("http://www.zillow.com"));
		        startActivity(intent);
		    }
		});
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	 private class DownloadPHPTask extends AsyncTask<String, Integer, Integer> {
	     private String jsonstr;
		 protected Integer doInBackground(String... url) {
	         int retVal=0; 
	         String uri=url[0];
	        // Log.v("inDoBg",uri);
	         InputStream ins = null;
	         
	         
			try {
				DefaultHttpClient client = new DefaultHttpClient();
		        //HttpPost post = new HttpPost(uri);
		        HttpGet get = new HttpGet(uri);
		        HttpResponse response;
				response = client.execute(get);
				HttpEntity entity = response.getEntity();
		        ins = entity.getContent(); 
		        BufferedReader reader = new BufferedReader(new InputStreamReader(
		                  ins, "UTF-8"), 8);
		        StringBuilder strb = new StringBuilder();
		        String line = null;
		        while ((line = reader.readLine()) != null) {
		                 strb.append(line + "\n");
		             }
		        ins.close();
		        jsonstr = strb.toString();
		        //Log.v("content",jsonstr);
		        
		        //Log.v("JSONObject=",jsonobj.toString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				retVal=-1;
			}
	          
	         return retVal;
	     }

	     protected void onProgressUpdate(Integer... progress) {
	         //setProgressPercent(progress[0]);
	     }

	     protected void onPostExecute(Integer result) {
	    	 try{
	    	 sendJSONoutput(jsonstr);
	    	 }
	    	 catch(Exception e)
	    	 {
	    		 Toast.makeText(MainActivity.this, "Error:Please check your Internet connection!", Toast.LENGTH_LONG).show();
	    	 }
	    	 //Log.v("DoneWithPostExec",jsonstr);
	     }
	 }
	
		/** Called when the user clicks the Search button */
	public void callAWS(View view) {
	    // Do something in response to button
		TextView tv5,tv6,tv7; 
		tv5=(TextView)findViewById(R.id.textView5);
		tv6=(TextView)findViewById(R.id.textView6);
		tv7=(TextView)findViewById(R.id.textView7);
		EditText ed1,ed2;
		ed1=(EditText)findViewById(R.id.editText1);
		ed2=(EditText)findViewById(R.id.editText2);
		Spinner sp1;
		sp1=(Spinner)findViewById(R.id.spinner1);
		boolean valid=true;
		staddr=ed1.getText().toString();
		city=ed2.getText().toString();
		state=sp1.getSelectedItem().toString();
		if(staddr.isEmpty()){
			tv5.setVisibility(TextView.VISIBLE);
			valid=false;
		}
		else
			tv5.setVisibility(TextView.INVISIBLE);
		if(city.isEmpty()){
			tv6.setVisibility(TextView.VISIBLE);
			valid=false;
		}
		else
			tv6.setVisibility(TextView.INVISIBLE);
		if(state.equals("Choose State")){
			tv7.setVisibility(TextView.VISIBLE);
			valid=false;
		}
		else
			tv7.setVisibility(TextView.INVISIBLE);
		if(valid){
		try{
			
		String estaddr=URLEncoder.encode(staddr,"UTF-8");
		String ecity=URLEncoder.encode(city,"UTF-8");
		String estate=URLEncoder.encode(state,"UTF-8");
		String eurl="http://homework8-env.elasticbeanstalk.com/dvrhw8.php?streetaddress="+estaddr+"&city="+ecity+"&state="+estate;
		//Log.v("encoded url=",eurl);
		new DownloadPHPTask().execute(eurl);
		}catch(UnsupportedEncodingException e)
		{
			
		}
		
		}
	}
	
	

	public void sendJSONoutput(String jstr) {
			
		String temp=jstr;
		
		try {
			TextView tv8=(TextView)findViewById(R.id.textView8);
			JSONObject jsonobj = new JSONObject(temp);
			JSONObject errobj = jsonobj.getJSONObject("result");
			//Log.v("result",errobj.toString());
			String error_code = errobj.getString("error_code");
			//Log.v("error_code",error_code);
			if(error_code.equals("0")){
				tv8.setVisibility(TextView.INVISIBLE);
				Intent intent = new Intent(this, ResultActivity.class);
				intent.putExtra("jsonOUT",jstr);
				startActivity(intent);
				//Log.v("Start","result_activity");
			}
			else{
				//Log.v("else_part","error");
				tv8.setVisibility(TextView.VISIBLE);
			}
		
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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

		/**
		 * A placeholder fragment containing a simple view.
		 */
		public static class PlaceholderFragment extends Fragment {
		
			public PlaceholderFragment() {
			}
		
			@Override
			public View onCreateView(LayoutInflater inflater, ViewGroup container,
					Bundle savedInstanceState) {
				View rootView = inflater.inflate(R.layout.fragment_main, container,
						false);
				return rootView;
			}
		}

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}
}
