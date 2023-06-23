package com.meedan;

import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;

import com.facebook.react.ReactActivity;
import com.facebook.react.ReactActivityDelegate;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint;
import com.facebook.react.defaults.DefaultReactActivityDelegate;
import android.content.Intent;
import com.facebook.react.bridge.ReadableMap;

import android.text.TextUtils;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ShareActivity extends ReactActivity {
  private static final String SHARE_SCHEME_KEY = "share-scheme";
  private String appUrlScheme = null;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(null);
    try {
      ActivityInfo ai = getPackageManager()
              .getActivityInfo(this.getComponentName(), PackageManager.GET_META_DATA);
      appUrlScheme = ai.metaData.getString(SHARE_SCHEME_KEY);
    } catch (PackageManager.NameNotFoundException e) {
      throw new RuntimeException(e);
    }
  }
  /**
   * Returns the name of the main component registered from JavaScript. This is used to schedule
   * rendering of the component.
   */
  @Override
  protected String getMainComponentName() {
    return "ShareMenuModuleComponent";
  }

  /**
   * Returns the instance of the {@link ReactActivityDelegate}. Here we use a util class {@link
   * DefaultReactActivityDelegate} which allows you to easily enable Fabric and Concurrent React
   * (aka React 18) with two boolean flags.
   */
  @Override
  protected ReactActivityDelegate createReactActivityDelegate() {
    return new DefaultReactActivityDelegate(
        this,
        getMainComponentName(),
        // If you opted-in for the New Architecture, we enable the Fabric Renderer.
        DefaultNewArchitectureEntryPoint.getFabricEnabled(), // fabricEnabled
        // If you opted-in for the New Architecture, we enable Concurrent React (i.e. React 18).
        DefaultNewArchitectureEntryPoint.getConcurrentReactEnabled() // concurrentRootEnabled
        );
  }
  
  public void openApp() {
    if (this.appUrlScheme != null && !this.appUrlScheme.equals("")) {
      Intent intent = getPackageManager().getLaunchIntentForPackage(appUrlScheme);
      startActivity(intent);
    }
  }

  private static JSONObject convertMapToJson(ReadableMap readableMap) throws JSONException {
    JSONObject object = new JSONObject();
    ReadableMapKeySetIterator iterator = readableMap.keySetIterator();
    while (iterator.hasNextKey()) {
      String key = iterator.nextKey();
      switch (readableMap.getType(key)) {
        case Null:
          object.put(key, JSONObject.NULL);
          break;
        case Boolean:
          object.put(key, readableMap.getBoolean(key));
          break;
        case Number:
          object.put(key, readableMap.getDouble(key));
          break;
        case String:
          object.put(key, readableMap.getString(key));
          break;
        case Map:
          object.put(key, convertMapToJson(readableMap.getMap(key)));
          break;
        case Array:
          object.put(key, convertArrayToJson(readableMap.getArray(key)));
          break;
      }
    }
    return object;
  }

  private static JSONArray convertArrayToJson(ReadableArray readableArray) throws JSONException {
    JSONArray array = new JSONArray();
    for (int i = 0; i < readableArray.size(); i++) {
      switch (readableArray.getType(i)) {
        case Null:
          break;
        case Boolean:
          array.put(readableArray.getBoolean(i));
          break;
        case Number:
          array.put(readableArray.getDouble(i));
          break;
        case String:
          array.put(readableArray.getString(i));
          break;
        case Map:
          array.put(convertMapToJson(readableArray.getMap(i)));
          break;
        case Array:
          array.put(convertArrayToJson(readableArray.getArray(i)));
          break;
      }
    }
    return array;
  }

  public void continueInApp(ReadableMap readableMap) {
    if (this.appUrlScheme != null && !this.appUrlScheme.equals("")) {
      Uri uri = Uri.parse(appUrlScheme);
      Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
      if (readableMap != null) {
        try {
          JSONObject data = convertMapToJson(readableMap);
          mapIntent.putExtra("data", data.toString());
        } catch (JSONException e) {
          throw new RuntimeException(e);
        }
      }
      PackageManager packageManager = getPackageManager();
      List<ResolveInfo> activities =
              packageManager.queryIntentActivities(mapIntent, 0);
      boolean isIntentSafe = activities.size() > 0;
      if (isIntentSafe) {
        startActivity(mapIntent);
      }
    }
  }


  public void dismissExtension(String error) {
    if (error != null && !error.equals("")) {
      Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }
    this.finish();
  }
}
