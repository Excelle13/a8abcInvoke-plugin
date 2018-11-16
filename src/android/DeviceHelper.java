package com.ttebd.a8abcResInvoke;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.RemoteException;
import android.util.Log;

import com.zacloud.deviceservice.aidl.IBeeper;
import com.zacloud.deviceservice.aidl.IDeviceInfo;
import com.zacloud.deviceservice.aidl.IDeviceService;
import com.zacloud.deviceservice.aidl.IInsertCardReader;
import com.zacloud.deviceservice.aidl.ILed;
import com.zacloud.deviceservice.aidl.IMagCardReader;
import com.zacloud.deviceservice.aidl.IPBOC;
import com.zacloud.deviceservice.aidl.IPinpad;
import com.zacloud.deviceservice.aidl.IPrinter;
import com.zacloud.deviceservice.aidl.IRFCardReader;
import com.zacloud.deviceservice.aidl.IScanner;
import com.zacloud.deviceservice.aidl.ISerialPort;
public class DeviceHelper {
  private static IBeeper beeper;
  private static IPrinter printer;
  private static ISerialPort serialPort;
  private static IScanner scanner;
  private static IMagCardReader magCardReader;
  private static IInsertCardReader insertCardReader;
  private static IRFCardReader rfCardReader;

  private static IDeviceService deviceService;

  private static int lastCameraId = 1;

  @SuppressLint("NewApi")
  public static void initDevices(IDeviceService devService) throws RemoteException, RemoteException, com.ttebd.a8abcResInvoke.DeviceSecurityException {
    DeviceHelper.deviceService = devService;

    if (deviceService != null) {
      try {
        beeper = getBeeper();

        printer = getPrinter();

        magCardReader = getMagCardReader();

        insertCardReader = getInsertCardReader();

        rfCardReader = getRFCardReader();


      } catch (RemoteException e) {
        e.printStackTrace();
        throw e;
      }
    } else {
      reset();
      throw new RemoteException("服务连接失败，请重启应用");

    }
  }

  /**
   * 打印服务
   * @return
   * @throws RemoteException
   * @throws DeviceSecurityException
   */
  @SuppressLint("NewApi")
  public static IPrinter getPrinter() throws RemoteException, com.ttebd.a8abcResInvoke.DeviceSecurityException {
    if (printer == null) {

      try {
        printer = IPrinter.Stub.asInterface(deviceService.getPrinter());//获取打印服务
        return printer;
      } catch (RemoteException e) {
        throw new RemoteException("打印服务获取失败，请稍后重试");
      } catch (SecurityException e) {
        e.printStackTrace();
        throw new com.ttebd.a8abcResInvoke.DeviceSecurityException(e.getMessage());
      }
    } else {
      return printer;
    }
  }

  /**
   * 蜂鸣服务
   * @return
   * @throws RemoteException
   * @throws DeviceSecurityException
   */
  @SuppressLint("NewApi")
  public static IBeeper getBeeper() throws RemoteException, com.ttebd.a8abcResInvoke.DeviceSecurityException {
    if (beeper == null) {

      try {
        beeper = IBeeper.Stub.asInterface(deviceService.getBeeper());//获取蜂鸣服务
        return beeper;
      } catch (RemoteException e) {
        throw new RemoteException("蜂鸣服务获取失败，请稍后重试");
      } catch (SecurityException e) {
        e.printStackTrace();
        throw new com.ttebd.a8abcResInvoke.DeviceSecurityException(e.getMessage());
      }
    } else {
      return beeper;
    }
  }

  /**
   * 非接读卡器服务
   * @return
   * @throws RemoteException
   * @throws DeviceSecurityException
   */
  @SuppressLint("NewApi")
  public static IRFCardReader getRFCardReader() throws RemoteException, com.ttebd.a8abcResInvoke.DeviceSecurityException {
    if (rfCardReader == null) {

      try {
        rfCardReader = IRFCardReader.Stub.asInterface(deviceService.getRFCardReader());//获取非接读卡器服务
        return rfCardReader;
      } catch (RemoteException e) {
        throw new RemoteException("非接读卡器服务获取失败，请稍后重试");
      } catch (SecurityException e) {
        e.printStackTrace();
        throw new com.ttebd.a8abcResInvoke.DeviceSecurityException(e.getMessage());
      }
    } else {
      return rfCardReader;
    }
  }

  /**
   * 接触式读卡器服务
   * @return
   * @throws RemoteException
   * @throws DeviceSecurityException
   */
  @SuppressLint("NewApi")
  public static IInsertCardReader getInsertCardReader() throws RemoteException, com.ttebd.a8abcResInvoke.DeviceSecurityException {
    if (insertCardReader == null) {

      try {
        insertCardReader = IInsertCardReader.Stub.asInterface(deviceService.getInsertCardReader());//获取接触式读卡器服务
        return insertCardReader;
      } catch (RemoteException e) {
        throw new RemoteException("接触式读卡器服务获取失败，请稍后重试");
      } catch (SecurityException e) {
        e.printStackTrace();
        throw new com.ttebd.a8abcResInvoke.DeviceSecurityException(e.getMessage());
      }
    } else {
      return insertCardReader;
    }
  }

  /**
   * 磁条卡读卡器服务
   * @return
   * @throws RemoteException
   * @throws DeviceSecurityException
   */
  @SuppressLint("NewApi")
  public static IMagCardReader getMagCardReader() throws RemoteException, com.ttebd.a8abcResInvoke.DeviceSecurityException {
    if (magCardReader == null) {

      try {
        magCardReader = IMagCardReader.Stub.asInterface(deviceService.getMagCardReader());//获取磁条卡读卡器服务
        return magCardReader;
      } catch (RemoteException e) {
        throw new RemoteException("磁条卡读卡器服务获取失败，请稍后重试");
      } catch (SecurityException e) {
        e.printStackTrace();
        throw new com.ttebd.a8abcResInvoke.DeviceSecurityException(e.getMessage());
      }
    } else {
      return magCardReader;
    }
  }

  /**
   * 扫码服务
   * @param cameraId 0前置扫码器，1后置扫码器
   * @return
   * @throws RemoteException
   * @throws DeviceSecurityException
   */
  @SuppressLint("NewApi")
  public static IScanner getScanner(int cameraId) throws RemoteException, com.ttebd.a8abcResInvoke.DeviceSecurityException {
    if (scanner == null || lastCameraId != cameraId) {

      try {
        lastCameraId = cameraId;
        scanner = IScanner.Stub.asInterface(deviceService.getScanner(cameraId));//获取扫码服务
        return scanner;
      } catch (RemoteException e) {
        throw new RemoteException("扫码服务获取失败，请稍后重试");
      } catch (SecurityException e) {
        e.printStackTrace();
        throw new com.ttebd.a8abcResInvoke.DeviceSecurityException(e.getMessage());
      }
    } else {
      return scanner;
    }
  }

  /**
   * 初始化对象
   */
  public static void reset() {
    beeper = null;
    printer = null;
    scanner = null;
    magCardReader = null;
    insertCardReader = null;
    rfCardReader = null;
  }
}
