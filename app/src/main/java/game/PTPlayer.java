package game;

import org.cocos2dx.lib.Cocos2dxActivity;
import org.cocos2dx.lib.Cocos2dxGLSurfaceView;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;
import com.secrethq.store.PTStoreBridge;
import com.google.android.gms.games.GamesActivityResultCodes;

import com.secrethq.ads.*;
import com.secrethq.utils.*;

// - - - SkiADInterstitial code start:

import com.mobiblocks.skippables.Skippables;
import com.mobiblocks.skippables.SkiAdInterstitial;
import com.mobiblocks.skippables.SkiAdRequest;
import com.mobiblocks.skippables.SkiAdListener;
import java.io.*;

// - - - SkiADInterstitial code end:

public class PTPlayer extends Cocos2dxActivity {

    // - - - SkiADInterstitial code start:

    public static SkiAdInterstitial interstitial;

    private JSONObject jsonCreateFromRawResource(int fileID) {

        JSONObject result = null;

        try {
            // Read file into string builder
            InputStream inputStream = getContext().getResources().openRawResource(fileID);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder builder = new StringBuilder();

            for (String line = null; (line = reader.readLine()) != null ; ) {
                builder.append(line).append("\n");
            }

            // Parse into JSONObject
            String resultStr = builder.toString();
            JSONTokener tokener = new JSONTokener(resultStr);
            result = new JSONObject(tokener);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        return result;
    }

    private String jsonGetStringValue(JSONObject json, String paramName, String defaultValue){
        if(null == json) return defaultValue;
        try {
            String _s = json.getString(paramName);
            if(_s.equals(null) || _s.trim().isEmpty()) return defaultValue;
            return _s.trim();
        } catch (JSONException e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    // - - - SkiADInterstitial code end:

	private static native void loadModelController();

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    try {
		    Log.v("----------","onActivityResult: request: " + requestCode + " result: "+ resultCode);
		    if(PTStoreBridge.iabHelper().handleActivityResult(requestCode, resultCode, data)){
		    	Log.v("-----------", "handled by IABHelper");
		    }
		    else if(requestCode == PTServicesBridge.RC_SIGN_IN){
		    	if(resultCode == RESULT_OK){
		    		PTServicesBridge.instance().onActivityResult(requestCode, resultCode, data);
		    	}
		    	else if(resultCode == GamesActivityResultCodes.RESULT_SIGN_IN_FAILED){
		    		int duration = Toast.LENGTH_SHORT;
		    		Toast toast = Toast.makeText(this, "Google Play Services: Sign in error", duration);
		    		toast.show();
		    	}
		    	else if(resultCode == GamesActivityResultCodes.RESULT_APP_MISCONFIGURED){
		    		int duration = Toast.LENGTH_SHORT;
		    		Toast toast = Toast.makeText(this, "Google Play Services: App misconfigured", duration);
		    		toast.show();	    		
		    	}
		    }
	    } catch (Exception e) {
		    	Log.v("-----------", "onActivityResult FAIL on iabHelper : " + e.toString());
		}
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

// - - - SkiADInterstitial code start:

        Skippables.initialize(this);

        PTPlayer.interstitial = new SkiAdInterstitial(this);
        PTPlayer.interstitial.setAdListener(new SkiAdListener() {

            @Override
            public void onAdLoaded() {
                // Called when an ad request loaded an ad.
                Log.d("NICE","Hello loaded");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Called when an ad request failed.
                Log.d("BAD","Hello failed");
            }

            @Override
            public void onAdOpened() {
                // Called just before presenting the user a full screen view.
                Log.d("NICE","Hello loaded");
            }

            @Override
            public void onAdLeftApplication() {
                // Called when the user has left the app.
                Log.d("NICE","Hello loaded");
            }

            @Override
            public void onAdClosed() {
                // Called when the user is about to return
                // to the app after tapping on an ad.
                Log.d("NICE","Hello loaded");
            }

        });

        String _adUnitIdName = "dynamicAdUnitId";
        String _adUnitIdValue = "564dfd67-6046-44db-8fc5-b2c52167b8ff";
        JSONObject _json = jsonCreateFromRawResource(R.raw.skippables);
        _adUnitIdValue = jsonGetStringValue(_json,_adUnitIdName,_adUnitIdValue);
        _json = null;

        Log.println( Log.INFO,"PTPLayer.onCreate","300: interstit. adUnitId: " + _adUnitIdValue);

        if(!_adUnitIdValue.equals("")) {
            PTPlayer.interstitial.setAdUnitId(_adUnitIdValue);
            SkiAdRequest adRequest = SkiAdRequest.builder().setTest(false).build();
            PTPlayer.interstitial.load(adRequest);
        }

        // - - - SkiADInterstitial code end:

		PTServicesBridge.initBridge(this, getString( R.string.app_id ));
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	@Override
	public void onNativeInit(){
			initBridges();				
	}

	private void initBridges(){
		PTStoreBridge.initBridge( this );


		if (PTJniHelper.isAdNetworkActive("kChartboost")) {
			PTAdChartboostBridge.initBridge(this);
		}

		if (PTJniHelper.isAdNetworkActive("kRevMob")) {
			PTAdRevMobBridge.initBridge(this);
		}
		
		if (PTJniHelper.isAdNetworkActive("kAdMob") || PTJniHelper.isAdNetworkActive("kFacebook")) {
			PTAdAdMobBridge.initBridge(this);
		}

		if (PTJniHelper.isAdNetworkActive("kAppLovin")) {
			PTAdAppLovinBridge.initBridge(this);
		}

		if (PTJniHelper.isAdNetworkActive("kLeadBolt")) {
			PTAdLeadBoltBridge.initBridge(this);
		}
		
		if (PTJniHelper.isAdNetworkActive("kFacebook")) {
			PTAdFacebookBridge.initBridge(this);
		}
		
		if (PTJniHelper.isAdNetworkActive("kHeyzap")) {
			PTAdHeyzapBridge.initBridge(this);
		}
	}

	@Override
	public Cocos2dxGLSurfaceView onCreateView() {
		Cocos2dxGLSurfaceView glSurfaceView = new Cocos2dxGLSurfaceView(this);
		glSurfaceView.setEGLConfigChooser(8, 8, 8, 0, 0, 0);

		return glSurfaceView;
	}

	static {
		System.loadLibrary("player");
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (PTJniHelper.isAdNetworkActive("kChartboost")) {
			PTAdChartboostBridge.onResume( this );
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (PTJniHelper.isAdNetworkActive("kChartboost")) {
			PTAdChartboostBridge.onStart( this );
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (PTJniHelper.isAdNetworkActive("kChartboost")) {
			PTAdChartboostBridge.onStop( this );
		}
	}
}
