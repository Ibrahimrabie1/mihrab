package com.sk.indiaVideoComp;

import com.sk.indiaVideoComp.HomeActivity;
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
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;
import android.widget.RelativeLayout;
import android.widget.FrameLayout;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.SurfaceView;
import android.text.TextUtils;
import android.os.Bundle;
import android.content.pm.PackageManager;
import android.Manifest;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.view.View;
import io.agora.rtc.*;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.DialogFragment;


public class MainActivity extends AppCompatActivity {
	
	private LinearLayout linear1;
	private RelativeLayout remote_video_view_container;
	private LinearLayout linear2;
	private LinearLayout linear3;
	private FrameLayout local_video_view_container;
	private ImageView btnmute;
	private ImageView image_videobtn;
	private ImageView btn_call;
	private ImageView btnswitch;
	
	private AlertDialog.Builder dial;
	private Intent in = new Intent();
	private AlertDialog.Builder d2;
/*
	private SkIndia VideoCall Vcall;
*/
	private static final String TAG = "This Activity";
	private static final int PERMISSION_REQ_ID = 22;
	
	    private static final String[] REQUESTED_PERMISSIONS = {
			            Manifest.permission.RECORD_AUDIO,
			            Manifest.permission.CAMERA
			    };
	   
	    private boolean videna = true;
	    private RtcEngine mRtcEngine;
	    private boolean mCallEnd;
	    private boolean mMuted;
	    private FrameLayout mLocalContainer;
	    private RelativeLayout mRemoteContainer;
	    private VideoCanvas mLocalVideo;
	    private VideoCanvas mRemoteVideo;
	
	private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
			
			@Override
			        public void onJoinChannelSuccess(final String channel, final int uid, final int elapsed) {
					        
					  runOnUiThread(new Runnable() {
							                @Override
							                public void run() {
					
									                
									              onJoinSucc(channel, uid, elapsed);
									
									                
									                }
							            });
					        }
		//
		@Override
			        public void onUserEnableLocalVideo(final int uid, final boolean enabled) {
					            runOnUiThread(new Runnable() {
							                @Override
							                public void run() {
					
					if (enabled) {
						
						setupRemoteVideo(uid);
						
					}
					  else {
							        removeFromParent(mRemoteVideo);
							        mRemoteVideo = null;
										
					}
					onRemoEnableVideo(uid, enabled);  
					
									                }
							            });
					        }
		
		//
			  @Override
			        public void onUserJoined(final int uid, final int elapsed) {
					            runOnUiThread(new Runnable() {
							                @Override
							                public void run() {
									                 
									setupRemoteVideo(uid);
								onRemoJoined(uid, elapsed);
									                }
							            });
					        }
			
			 @Override
			        public void onUserOffline(final int uid, final int reason) {
					            runOnUiThread(new Runnable() {
							                @Override
							                public void run() {
									                   
									onRemoOff(uid, reason);
									
									                }
							            });
					        }
			private void setupRemoteVideo(int uid) {
					        ViewGroup parent = mRemoteContainer;
					        if (parent.indexOfChild(mLocalVideo.view) > -1) {
							            parent = mLocalContainer;
							        }
					
					        if (mRemoteVideo != null) {
							            return;
							        }
					
					     
					  SurfaceView view = RtcEngine.CreateRendererView(getBaseContext());
					        view.setZOrderMediaOverlay(parent == mLocalContainer);
					        parent.addView(view);
					        mRemoteVideo = new VideoCanvas(view, VideoCanvas.RENDER_MODE_HIDDEN, uid);
					     
					        mRtcEngine.setupRemoteVideo(mRemoteVideo);
					    }
			
		private void onRemoteUserLeft(int uid) {
					        if (mRemoteVideo != null && mRemoteVideo.uid == uid) {
							            
						
							removeFromParent(mRemoteVideo);
										            mRemoteVideo = null;
				onRemoLeft(uid);
				
							    }
					
			}
			
	};
	
	private AlertDialog.Builder diala;
	
	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.main);
		initialize(_savedInstanceState);
		initializeLogic();
	}
	
	private void initialize(Bundle _savedInstanceState) {
		linear1 = (LinearLayout) findViewById(R.id.linear1);
		remote_video_view_container = (RelativeLayout) findViewById(R.id.remote_video_view_container);
		linear2 = (LinearLayout) findViewById(R.id.linear2);
		linear3 = (LinearLayout) findViewById(R.id.linear3);
		local_video_view_container = (FrameLayout) findViewById(R.id.local_video_view_container);
		btnmute = (ImageView) findViewById(R.id.btnmute);
		image_videobtn = (ImageView) findViewById(R.id.image_videobtn);
		btn_call = (ImageView) findViewById(R.id.btn_call);
		btnswitch = (ImageView) findViewById(R.id.btnswitch);
		dial = new AlertDialog.Builder(this);
		d2 = new AlertDialog.Builder(this);
		if (checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID) &&
		                checkSelfPermission(REQUESTED_PERMISSIONS[1], PERMISSION_REQ_ID)) {
				         
			
				        }
		
		 setLRViews();
		diala = new AlertDialog.Builder(this);
		
		local_video_view_container.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				switchView(mLocalVideo);
				        switchView(mRemoteVideo);
			}
		});
		
		btnmute.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				if (mMuted) {
					mMuted = !mMuted;
					
					try {
						mRtcEngine.muteLocalAudioStream(false);
					} catch(Exception e) {
					}
					btnmute.setImageResource(R.drawable.micon);
				}
				else {
					mMuted = !mMuted;
					
					try {
						mRtcEngine.muteLocalAudioStream(true);
					} catch(Exception e) {
					}
					btnmute.setImageResource(R.drawable.micoff);
				}
			}
		});
		
		image_videobtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				if (videna) {
					videna = !videna;
					try {
						mRtcEngine.enableLocalVideo(false);
						
						if (false) {
							
							setupLocalVideo();
							
								     } 
						
						else {
							
							removeFromParent(mLocalVideo);
								        mLocalVideo = null;
						}
						
						
					} catch(Exception e) {
					}
					image_videobtn.setImageResource(R.drawable.ic_videocam_off_white);
				}
				else {
					videna = !videna;
					try {
						mRtcEngine.enableLocalVideo(true);
						
						if (true) {
							
							setupLocalVideo();
							
								     } 
						
						else {
							
							removeFromParent(mLocalVideo);
								        mLocalVideo = null;
						}
						
						
					} catch(Exception e) {
					}
					image_videobtn.setImageResource(R.drawable.ic_videocam_white);
				}
			}
		});
		
		btn_call.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				  removeFromParent(mLocalVideo);
					        mLocalVideo = null;
					        removeFromParent(mRemoteVideo);
					        mRemoteVideo = null;
					        leaveChannel();
				            mCallEnd = true;
				            
				finish();
			}
		});
		
		btnswitch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				try {
					mRtcEngine.switchCamera();
				} catch(Exception e) {
				}
			}
		});
	}
	
	private void initializeLogic() {
		mCallEnd = false;
		initEngineAndJoinChannel();
	}
	
	@Override
	protected void onActivityResult(int _requestCode, int _resultCode, Intent _data) {
		super.onActivityResult(_requestCode, _resultCode, _data);
		switch (_requestCode) {
			
			default:
			break;
		}
	}
	
	
	private void onJoinSucc(final String _channel, final int _uid, final int _elapsed) {
		
		SketchwareUtil.showMessage(getApplicationContext(), String.valueOf((long)(_uid)).concat(" joined - ".concat(_channel)));
	}
	
	private void onRemoJoined(final int _uid, final int _elapsed) {
		
		SketchwareUtil.showMessage(getApplicationContext(), String.valueOf((long)(_uid)).concat(" Joined You"));
	}
	
	private void onRemoOff(final int _uid, final int _reason) {
		
		SketchwareUtil.showMessage(getApplicationContext(), String.valueOf((long)(_uid)).concat(" is offline,\nReason - ".concat(String.valueOf((long)(_reason)))));
	}
	
	private void onRemoLeft(final int _uid) {
		
		SketchwareUtil.showMessage(getApplicationContext(), String.valueOf((long)(_uid)).concat(" left Call"));
		
	}
	
	private void setLRViews() {
		mLocalContainer = findViewById(R.id.local_video_view_container);
		        mRemoteContainer = findViewById(R.id.remote_video_view_container);
		
	}
	
	private boolean checkSelfPermission(String permission, int requestCode) {
			        if (ContextCompat.checkSelfPermission(this, permission) !=
			                PackageManager.PERMISSION_GRANTED) {
					            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, requestCode);
					            return false; 
					        } 
			
			        return true;
			    }
	
	@Override
	    public void onRequestPermissionsResult(int requestCode,
	                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
			                                           
			                                       
			                                           
			        if (requestCode == PERMISSION_REQ_ID) {
					            if (grantResults[0] != PackageManager.PERMISSION_GRANTED ||
					                    grantResults[1] != PackageManager.PERMISSION_GRANTED) {
							                
							                finish();
							                return;
							            }
					
					        
					             
					            
					        }
			    }
	private void initEngineAndJoinChannel() {
			      
			       initializeEngine();
			setupVideoConfig();
			setupLocalVideo();
			joinChannel();
			    }
	private void initializeEngine() {
			        
		        try {
			        mRtcEngine = RtcEngine.create(getBaseContext(), "fc0408786329413483d92762bb7fb44d", mRtcEventHandler);
			        } catch (Exception e) {
			           
			SketchwareUtil.showMessage(getApplicationContext(), "invalid Agora App id...\ncannot make any Call");
			        
			        }
			    }
	
	private void setupLocalVideo() {
			        try {
					SurfaceView view = RtcEngine.CreateRendererView(getBaseContext());
					        view.setZOrderMediaOverlay(true);
					        mLocalContainer.addView(view);
					        mLocalVideo = new VideoCanvas(view, VideoCanvas.RENDER_MODE_HIDDEN, 0);
					        mRtcEngine.setupLocalVideo(mLocalVideo);
			} catch(Exception e) {
					
			}
			    }
	
	
	private void joinChannel() {
			    
			      try {
			String token = getIntent().getStringExtra("tname");
			        if (TextUtils.isEmpty(token) || TextUtils.equals(token, "jdid9rj")) {
				            token = null; 
				        }
			        mRtcEngine.joinChannel(token, getIntent().getStringExtra("cname"), "extra", 0);
		} catch(Exception e) {
			
		}
			    }
	private void leaveChannel() {
			        
			try {
					
					mRtcEngine.leaveChannel();
			} catch(Exception e) {
			}
			       
			    }
	private ViewGroup removeFromParent(VideoCanvas canvas) {
			        if (canvas != null) {
					            ViewParent parent = canvas.view.getParent();
					            if (parent != null) {
							                ViewGroup group = (ViewGroup) parent;
							                group.removeView(canvas.view);
							                return group;
							            }
					        }
			        return null;
			    }
	
	private void switchView(VideoCanvas canvas) {
			        ViewGroup parent = removeFromParent(canvas);
			        if (parent == mLocalContainer) {
					            if (canvas.view instanceof SurfaceView) {
							                ((SurfaceView) canvas.view).setZOrderMediaOverlay(false);
							            }
					            mRemoteContainer.addView(canvas.view);
					        } else if (parent == mRemoteContainer) {
					            if (canvas.view instanceof SurfaceView) {
							                ((SurfaceView) canvas.view).setZOrderMediaOverlay(true);
							            }
					            mLocalContainer.addView(canvas.view);
					        }
			    }
	
	
	private void setupVideoConfig() {
			
			try {
					    mRtcEngine.enableVideo();
			mRtcEngine.setVideoEncoderConfiguration(new VideoEncoderConfiguration(
					                
			VideoEncoderConfiguration.VD_640x360,
			VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_10,
			VideoEncoderConfiguration.STANDARD_BITRATE,
			VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT
			
			));
			 
			} catch(Exception e) {
					
			}
			    }
	
	private void onRemoEnableVideo(final int _uid, final boolean _isEnabled) {
		if (_isEnabled) {
			SketchwareUtil.showMessage(getApplicationContext(), "Remote User Enabled Video");
		}
		else {
			SketchwareUtil.showMessage(getApplicationContext(), "Remote user Diabled video");
		}
	}
	
	@Override
	public void onBackPressed() {
		diala.setTitle("Video Call is ongoing...");
		diala.setIcon(R.drawable.logo6_8_195938);
		diala.setMessage("Do you want to exit current page ?");
		diala.setPositiveButton("yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface _dialog, int _which) {
				finish();
			}
		});
		diala.setNegativeButton("No", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface _dialog, int _which) {
				
			}
		});
		diala.create().show();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			if (!mCallEnd) {
					            leaveChannel();
					        }
			        RtcEngine.destroy();
		} catch(Exception e) {
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