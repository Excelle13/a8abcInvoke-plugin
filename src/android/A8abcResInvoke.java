package com.ttebd.a8abcResInvoke;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.zacloud.deviceservice.aidl.IDeviceService;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.LOG;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class echoes a string called from JavaScript.
 */
public class A8abcResInvoke extends CordovaPlugin {

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    try {
      System.out.println("params-" + args);
      Runnable logRb = new Runnable() {
        @Override
        public void run() {
          JSONObject job = args.optJSONObject(0);
          String method = "";
//          initDevicesPlugin();
          try {
            method = job.getString("method");
            switch (method) {
              case "abcInvoke":
                com.ttebd.a8abcResInvoke.PrintMain printMain = new com.ttebd.a8abcResInvoke.PrintMain(cordova.getActivity().getApplicationContext(),callbackContext);

                printMain.print(job.getJSONArray("params"));

//                String message = args.getString(0);

//                coolMethod(message, callbackContext);
                break;
              default:
                LOG.e("log调用", "没有找到此方法");
            }
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      };
      cordova.getThreadPool().execute(logRb);
      return true;
    } catch (Exception e) {
      e.printStackTrace();
    }


    return false;
  }

  private IDeviceService deviceService;

  public void initDevicesPlugin() {
    bindDeviceService();
    try {
      com.ttebd.a8abcResInvoke.DeviceHelper.initDevices(deviceService);
    } catch (RemoteException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (com.ttebd.a8abcResInvoke.DeviceSecurityException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * 绑定设备服务
   */
  public void bindDeviceService() {
    if (null != deviceService) {
      return;
    }

    Intent intent = new Intent();
    intent.setAction("com.zacloud.device_service");
    intent.setPackage("com.zacloud.deviceservice");

    this.cordova.getActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE);

  }

  ServiceConnection connection = new ServiceConnection() {

    @Override
    public void onServiceDisconnected(ComponentName name) {
      deviceService = null;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
      deviceService = IDeviceService.Stub.asInterface(service);

      try {
        com.ttebd.a8abcResInvoke.DeviceHelper.initDevices(deviceService);

      } catch (RemoteException e) {
        e.printStackTrace();
      } catch (com.ttebd.a8abcResInvoke.DeviceSecurityException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

      linkToDeath(service);

    }

    private void linkToDeath(IBinder service) {
      try {
        service.linkToDeath(new IBinder.DeathRecipient() {
          @Override
          public void binderDied() {
            deviceService = null;
            bindDeviceService();
          }
        }, 0);
      } catch (RemoteException e) {
        e.printStackTrace();
      }
    }
  };



  private void coolMethod(String message, CallbackContext callbackContext) {
    System.out.println("进入打印方法");

/*    com.ttebd.a8abcResInvoke.PrintMain printMain = new com.ttebd.a8abcResInvoke.PrintMain(this.cordova.getActivity().getApplicationContext(),callbackContext);

    printMain.print();*/
//    Activity activity = new Activity();
//    activity.finish();
//      finish();
    if (message != null && message.length() > 0) {
//      callbackContext.success(message);
    } else {
      callbackContext.error("Expected one non-empty string argument.");
    }

  }

}
