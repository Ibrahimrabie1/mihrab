package com.sk.indiaVideoComp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.*;
import android.app.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.media.*;
import android.net.*;
import android.text.*;
import android.text.style.*;
import android.util.*;
import android.webkit.*;
import android.animation.*;
import android.view.animation.*;
import java.util.*;
import java.util.regex.*;
import java.text.*;
import org.json.*;
import java.util.HashMap;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.EditText;
import android.widget.Button;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.content.ClipData;
import android.content.ClipboardManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.agora.rtc.*;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.DialogFragment;


public class HomeActivity extends AppCompatActivity {
	
	private double e1state = 0;
	private double e2state = 0;
	private String rcode = "";
	private String atoken = "";
	private HashMap<String, Object> response = new HashMap<>();
	
	private TextView textview2;
	private LinearLayout linear1;
	private TextView textview1;
	private EditText channelName;
	private EditText accesstoken;
	private TextView textview3;
	private Button button1;
	private Button button2;
	
	private RequestNetwork rn;
	private RequestNetwork.RequestListener _rn_request_listener;
	private Intent i = new Intent();
	
	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.home);
		initialize(_savedInstanceState);
		initializeLogic();
	}
	
	private void initialize(Bundle _savedInstanceState) {
		textview2 = (TextView) findViewById(R.id.textview2);
		linear1 = (LinearLayout) findViewById(R.id.linear1);
		textview1 = (TextView) findViewById(R.id.textview1);
		channelName = (EditText) findViewById(R.id.channelName);
		accesstoken = (EditText) findViewById(R.id.accesstoken);
		textview3 = (TextView) findViewById(R.id.textview3);
		button1 = (Button) findViewById(R.id.button1);
		button2 = (Button) findViewById(R.id.button2);
		rn = new RequestNetwork(this);
		
		textview2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				channelName.setText("");
				accesstoken.setText("");
				textview1.setText("Status");
				button1.setText("Create Video Call");
				channelName.setVisibility(View.GONE);
				accesstoken.setVisibility(View.GONE);
				textview3.setVisibility(View.GONE);
				button1.setVisibility(View.VISIBLE);
				button2.setVisibility(View.VISIBLE);
				e1state = 0;
				e2state = 0;
				SketchwareUtil.showMessage(getApplicationContext(), "Data Reset Completed");
				button1.setEnabled(true);
				button2.setEnabled(true);
			}
		});
		
		textview3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				((ClipboardManager) getSystemService(getApplicationContext().CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("clipboard", "RoomCode - ".concat(channelName.getText().toString().concat("\n\nAccess Key - ".concat(accesstoken.getText().toString())))));
				SketchwareUtil.showMessage(getApplicationContext(), "Copied ✔️");
			}
		});
		
		button1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				switch((int)e1state) {
					case ((int)0): {
						textview1.setText("Create A Room Code-");
						channelName.setVisibility(View.VISIBLE);
						button1.setText("Create Room");
						button2.setVisibility(View.GONE);
						e1state = 1;
						break;
					}
					case ((int)1): {
						rcode = channelName.getText().toString();
						if (rcode.trim().equals("")) {
							SketchwareUtil.showMessage(getApplicationContext(), "Please enter a room code first");
						}
						else {
							button1.setEnabled(false);
							textview1.setText("Generating key...");
							button1.setText("Please wait...");
							rn.startRequestNetwork(RequestNetworkController.GET, "https://mihrab.herokuapp.com/access_token?channel=".concat(rcode), "Token", _rn_request_listener);
						}
						break;
					}
					case ((int)2): {
						i.setClass(getApplicationContext(), MainActivity.class);
						i.putExtra("cname", rcode.trim());
						i.putExtra("tname", atoken.trim());
						startActivity(i);
						break;
					}
				}
			}
		});
		
		button2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				switch((int)e2state) {
					case ((int)0): {
						channelName.setVisibility(View.VISIBLE);
						accesstoken.setVisibility(View.VISIBLE);
						button1.setVisibility(View.GONE);
						e2state = 1;
						break;
					}
					case ((int)1): {
						rcode = channelName.getText().toString();
						atoken = accesstoken.getText().toString();
						if (rcode.trim().equals("") || atoken.trim().equals("")) {
							SketchwareUtil.showMessage(getApplicationContext(), "Please Enter Required fields");
						}
						else {
							i.setClass(getApplicationContext(), MainActivity.class);
							i.putExtra("cname", rcode.trim());
							i.putExtra("tname", atoken.trim());
							startActivity(i);
						}
						break;
					}
				}
			}
		});
		
		_rn_request_listener = new RequestNetwork.RequestListener() {
			@Override
			public void onResponse(String _param1, String _param2, HashMap<String, Object> _param3) {
				final String _tag = _param1;
				final String _response = _param2;
				final HashMap<String, Object> _responseHeaders = _param3;
				try {
					response = new Gson().fromJson(_response, new TypeToken<HashMap<String, Object>>(){}.getType());
					accesstoken.setVisibility(View.VISIBLE);
					accesstoken.setText(response.get("token").toString());
					atoken = response.get("token").toString();
					textview1.setText("Key Generated.\nNow Share Room Code and Key to Your Friend to Join Your Call");
					button1.setText("Enter to Call");
					e1state = 2;
					button1.setEnabled(true);
					textview3.setVisibility(View.VISIBLE);
				} catch(Exception e) {
					textview1.setText(_response);
				}
			}
			
			@Override
			public void onErrorResponse(String _param1, String _param2) {
				final String _tag = _param1;
				final String _message = _param2;
				
			}
		};
	}
	
	private void initializeLogic() {
		channelName.setVisibility(View.GONE);
		accesstoken.setVisibility(View.GONE);
		textview3.setVisibility(View.GONE);
	}
	
	@Override
	protected void onActivityResult(int _requestCode, int _resultCode, Intent _data) {
		super.onActivityResult(_requestCode, _resultCode, _data);
		switch (_requestCode) {
			
			default:
			break;
		}
	}
	
	@Deprecated
	public void showMessage(String _s) {
		Toast.makeText(getApplicationContext(), _s, Toast.LENGTH_SHORT).show();
	}
	
	@Deprecated
	public int getLocationX(View _v) {
		int _location[] = new int[2];
		_v.getLocationInWindow(_location);
		return _location[0];
	}
	
	@Deprecated
	public int getLocationY(View _v) {
		int _location[] = new int[2];
		_v.getLocationInWindow(_location);
		return _location[1];
	}
	
	@Deprecated
	public int getRandom(int _min, int _max) {
		Random random = new Random();
		return random.nextInt(_max - _min + 1) + _min;
	}
	
	@Deprecated
	public ArrayList<Double> getCheckedItemPositionsToArray(ListView _list) {
		ArrayList<Double> _result = new ArrayList<Double>();
		SparseBooleanArray _arr = _list.getCheckedItemPositions();
		for (int _iIdx = 0; _iIdx < _arr.size(); _iIdx++) {
			if (_arr.valueAt(_iIdx))
			_result.add((double)_arr.keyAt(_iIdx));
		}
		return _result;
	}
	
	@Deprecated
	public float getDip(int _input) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, _input, getResources().getDisplayMetrics());
	}
	
	@Deprecated
	public int getDisplayWidthPixels() {
		return getResources().getDisplayMetrics().widthPixels;
	}
	
	@Deprecated
	public int getDisplayHeightPixels() {
		return getResources().getDisplayMetrics().heightPixels;
	}
}