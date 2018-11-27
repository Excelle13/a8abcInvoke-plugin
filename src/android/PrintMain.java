package com.ttebd.a8abcResInvoke;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.util.Log;

import com.zacloud.deviceservice.aidl.IPrinter;
import com.zacloud.deviceservice.aidl.PrinterListener;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.LOG;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

public class PrintMain {
  private IPrinter printer;
  // 打印机状态
  private static final int ERROR_NONE = 0x00;// 状态正常</li>
  private static final int ERROR_PAPERENDED = 0xF0;// 缺纸，不能打印</li>
  private static final int ERROR_HARDERR = 0xF2;// 硬件错误</li>
  private static final int ERROR_OVERHEAT = 0xF3;// 打印头过热</li>
  private static final int ERROR_BUFOVERFLOW = 0xF5;// 缓冲模式下所操作的位置超出范围 </li>
  private static final int ERROR_LOWVOL = 0xE1;// 低压保护 </li>
  private static final int ERROR_PAPERENDING = 0xF4;// 纸张将要用尽，还允许打印(单步进针打特有返回值)</li>
  private static final int ERROR_MOTORERR = 0xFB;// 打印机芯故障(过快或者过慢)</li>
  private static final int ERROR_PENOFOUND = 0xFC;// 自动定位没有找到对齐位置,纸张回到原来位置
  //
  private static final int ERROR_PAPERJAM = 0xEE;// 卡纸</li>
  private static final int ERROR_NOBM = 0xF6;// 没有找到黑标</li>
  private static final int ERROR_BUSY = 0xF7;// 打印机处于忙状态</li>
  private static final int ERROR_BMBLACK = 0xF8;// 黑标探测器检测到黑色信号</li>
  private static final int ERROR_WORKON = 0xE6;// 打印机电源处于打开状态</li>
  private static final int ERROR_LIFTHEAD = 0xE0;// 打印头抬起(自助热敏打印机特有返回值)</li>
  private static final int ERROR_CUTPOSITIONERR = 0xE2;// 切纸刀不在原位(自助热敏打印机特有返回值)</li>
  private static final int ERROR_LOWTEMP = 0xE3;// 低温保护或AD出错(自助热敏打印机特有返回值)</li>

  private static final int ERROR_REMOTE_EXCEPTION = 0xE4;// 服务异常</li>

  // 字体
  private static final int FONT_SMALL = 0;
  private static final int FONT_NORMAL = 1;
  private static final int FONT_LARGE = 2;
  // 位置
  private static final int LEFT = 0;
  private static final int CENTER = 1;
  private static final int RIGHT = 2;

  protected CallbackContext _callbackContext = null;
  protected Context _context = null;

  public PrintMain(Context context, CallbackContext callbackContext) {
    _callbackContext = callbackContext;
    _context = context;

  }

  void addText(String str, int fontSize, int align) {
    Bundle format = new Bundle();
    format.putInt("font", fontSize);
    format.putInt("align", align);
    try {
      printer.addText(format, str);
    } catch (RemoteException e) {
      e.printStackTrace();
    }
  }

  void addQrCode(String qrCodeStr, int offset, int expectedHeight) {
    Bundle format = new Bundle();
    format.putInt("offset", offset); //50
    format.putInt("expectedHeight", expectedHeight);  //300
//    String printInfo = "1234567890ABCDEF1234567890ABCDEF";

    try {
      printer.addQrCode(format, qrCodeStr);
    } catch (RemoteException e) {
      e.printStackTrace();
    }
  }

  void addBarCode(String barCodeStr, int align, int height, int width) {
    Bundle format = new Bundle();
    format.putInt("align", align);
    format.putInt("height", height);
    format.putInt("width", width);
//    String printInfo1 = "ABCDEF1234567890";
    try {
      printer.addBarCode(format, barCodeStr);
    } catch (RemoteException e) {
      e.printStackTrace();
    }
  }

  void addImage(String imgSrc, int offset, int width, int height) {
    Bundle format = new Bundle();
    format.putInt("offset", offset);
    format.putInt("width", width);
    format.putInt("height", height);
    try {
      printer.addImage(format, getFromAssets(imgSrc));
//      printer.addBarCode(format, "");
    } catch (RemoteException e) {
      e.printStackTrace();
    }
  }


  public void print(JSONArray json) {
    try {
      printer = com.ttebd.a8abcResInvoke.DeviceHelper.getPrinter();
    } catch (RemoteException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (com.ttebd.a8abcResInvoke.DeviceSecurityException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    System.out.println("start print");


    try {
      int status = printer.getStatus();
      System.out.println("打印機狀態：" + status);
      if (status == ERROR_NONE) {
        printer.setGray(5);

        for (int i = 0; i < json.length(); i++) {
          JSONObject obj = null;
          try {
            obj = json.getJSONObject(i);
            Iterator iterator = obj.keys();
            while (iterator.hasNext()) {
              String key = (String) iterator.next();
              // 打印文本
              if (key.indexOf("txt") == 0) {
                JSONObject txtObj = obj.getJSONObject(key);
                addText(txtObj.optString("data"), txtObj.optInt("font"), txtObj.optInt("align"));
              }

              // 打印图片
              if (key.indexOf("img") == 0) {
                JSONObject imgObj = obj.getJSONObject(key);
                File file = new File(Environment.getExternalStorageDirectory(),
                        imgObj.optString("imgSrc"));
                if (file.isDirectory()) {
                  LOG.e("农行打印", "打印logo不存在");
                } else {
                  addImage(imgObj.optString("imgSrc"), imgObj.optInt("offset"), imgObj.optInt("width"), imgObj.optInt("height"));
                }
              }

              // 打印条形码
              if (key.indexOf("barCode") == 0) {
                JSONObject barCodeObj = obj.getJSONObject(key);
                addBarCode(barCodeObj.optString("data"), barCodeObj.optInt("align"), barCodeObj.optInt("height"), barCodeObj.optInt("width"));
              }

              // 打印二维码
              if (key.indexOf("qrCode") == 0) {
                JSONObject barCodeObj = obj.getJSONObject(key);
                addQrCode(barCodeObj.optString("data"), barCodeObj.optInt("offset"), barCodeObj.optInt("expectedHeight"));
              }
              if (key.indexOf("feedLine") == 0) {
                JSONObject feedLineObj = obj.getJSONObject(key);
                printer.feedLine(feedLineObj.optInt("line"));
              }

            }
          } catch (JSONException e) {
            e.printStackTrace();
          }

        }



      /*  Bundle format = new Bundle();
        format.putInt("offset", 0);
        format.putInt("width", 384);
        format.putInt("height", 104);
        printer.addImage(format, getFromAssets("PrintLogo/0180012903.bmp"));

        Bundle format0 = new Bundle();
        format0.putInt("font", FONT_SMALL);
        format0.putInt("align", LEFT);
        printer.addText(format0, "打印测试一");

        Bundle format1 = new Bundle();
        format1.putInt("font", FONT_NORMAL);
        format1.putInt("align", CENTER);
        printer.addText(format1, "打印测试二");

        Bundle format2 = new Bundle();
        format2.putInt("font", FONT_LARGE);
        format2.putInt("align", RIGHT);
        printer.addText(format2, "打印测试三");

        Bundle format9 = new Bundle();
        format9.putInt("font", FONT_LARGE);
        format9.putInt("align", RIGHT);
        printer.addText(format2, "测试打印成功");

        Bundle format3 = new Bundle();
        format3.putInt("offset", 50);
        format3.putInt("expectedHeight", 300);
        String printInfo = "1234567890ABCDEF1234567890ABCDEF";
        printer.addQrCode(format3, printInfo);

        Bundle format4 = new Bundle();
        format4.putInt("align", CENTER);
        format4.putInt("height", 80);
        format4.putInt("width", 2);
        String printInfo1 = "ABCDEF1234567890";
        printer.addBarCode(format4, printInfo1);


        Bundle format01 = new Bundle();
        format01.putInt("font", FONT_NORMAL);
        format01.putInt("align", LEFT);
        printer.addText(format01, " - - - - X - - - - - - X - - - - ");
*/
        printer.feedLine(4);

        printer.startPrint(new PrinterListener.Stub() {

          @Override
          public void onFinish() throws RemoteException {
            // TODO Auto-generated method stub
            JSONObject successMessageObj = new JSONObject();

            try {
              successMessageObj.put("code", "0000");
              successMessageObj.put("message", "打印成功");
            } catch (JSONException e) {
              e.printStackTrace();
            }
            _callbackContext.success(successMessageObj);
            Log.i("test", "打印成功");
          }

          @Override
          public void onError(int arg0) throws RemoteException {
            JSONObject errMessageObj = new JSONObject();
            try {
              errMessageObj.put("code", "0001");
              errMessageObj.put("message", getErrorInfo(arg0));
              _callbackContext.error(errMessageObj);
            } catch (JSONException e) {
              // TODO
              e.printStackTrace();
            }
            // TODO Auto-generated method stub
            Log.i("test", "打印失败2:" + getErrorInfo(arg0));
          }
        });
      } else {
        JSONObject errMessageObj = new JSONObject();
        try {
          errMessageObj.put("code", "0001");
          errMessageObj.put("message", getErrorInfo(status));
          _callbackContext.error(errMessageObj);
        } catch (JSONException e) {
          // TODO
          e.printStackTrace();
        }
        Log.i("test", "打印失败:" + getErrorInfo(status));
//        Toast.makeText(this.cordova.getActivity().getApplicationContext(), "打印失败:" + getErrorInfo(status), Toast.LENGTH_SHORT);
      }
    } catch (RemoteException e) {
      e.printStackTrace();
    }
  }

  private String getErrorInfo(int errorCode) {
    switch (errorCode) {
      case ERROR_PAPERENDED:
        return "缺纸，不能打印";
      case ERROR_HARDERR:
        return "硬件错误";
      case ERROR_OVERHEAT:
        return "打印头过热";
      case ERROR_LOWVOL:
        return "低压保护";
      case ERROR_MOTORERR:
        return "打印机芯故障";
      case ERROR_BUSY:
        return "打印机忙";
      case ERROR_BMBLACK:
      case ERROR_WORKON:
      case ERROR_LIFTHEAD:
      case ERROR_CUTPOSITIONERR:
      case ERROR_LOWTEMP:
      case ERROR_NOBM:
      case ERROR_PENOFOUND:
      case ERROR_PAPERJAM:
      case ERROR_PAPERENDING:
      case ERROR_BUFOVERFLOW:
      case ERROR_REMOTE_EXCEPTION:
      default:
        return "打印异常，请检查";
    }
  }

  public byte[] getFromAssets(String imgSrc) {
    byte[] buffer = null;
    InputStream in = null;

//    try {
////                    in = context.getResources().getAssets().open(imgSrc);
//      File file = new File(Environment.getExternalStorageDirectory(),
//        imgSrc);
//      in = new FileInputStream(file);
//      printer.printImage(offset, in);
//    } catch (Exception e) {
////                    callbackContext.error("打印图片异常");
//      Log.e("打印图片异常", e.getMessage());
//      e.printStackTrace();
//    }

    try {

//      in = _context.getResources().getAssets().open(imgSrc);
      File file = new File(Environment.getExternalStorageDirectory(),
              imgSrc);
      in = new FileInputStream(file);
//      in = getResources().getAssets().open(fileName);
      // 获取文件的字节数
      int lenght = in.available();
      // 创建byte数组
      buffer = new byte[lenght];
      // 将文件中的数据读到byte数组中
      in.read(buffer);

      in.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return buffer;
  }

  public static void judeFileExists(File file) {

    if (file.exists()) {
      System.out.println("file exists");
    } else {
      System.out.println("file not exists, create it ...");
      try {
        file.createNewFile();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

  }
}
