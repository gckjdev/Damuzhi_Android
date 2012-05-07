/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: E:\\Damuzhi_Android\\src\\com\\damuzhi\\travel\\download\\IDownloadService.aidl
 */
package com.damuzhi.travel.download;
public interface IDownloadService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.damuzhi.travel.download.IDownloadService
{
private static final java.lang.String DESCRIPTOR = "com.damuzhi.travel.download.IDownloadService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.damuzhi.travel.download.IDownloadService interface,
 * generating a proxy if needed.
 */
public static com.damuzhi.travel.download.IDownloadService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.damuzhi.travel.download.IDownloadService))) {
return ((com.damuzhi.travel.download.IDownloadService)iin);
}
return new com.damuzhi.travel.download.IDownloadService.Stub.Proxy(obj);
}
public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_addTask:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
java.lang.String _arg2;
_arg2 = data.readString();
boolean _result = this.addTask(_arg0, _arg1, _arg2);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setMaxTaskCount:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setMaxTaskCount(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_pauseTask:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.pauseTask(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_cancelTask:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.cancelTask(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_regCallback:
{
data.enforceInterface(DESCRIPTOR);
com.damuzhi.travel.download.IDownloadCallback _arg0;
_arg0 = com.damuzhi.travel.download.IDownloadCallback.Stub.asInterface(data.readStrongBinder());
this.regCallback(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_unregCallback:
{
data.enforceInterface(DESCRIPTOR);
com.damuzhi.travel.download.IDownloadCallback _arg0;
_arg0 = com.damuzhi.travel.download.IDownloadCallback.Stub.asInterface(data.readStrongBinder());
this.unregCallback(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.damuzhi.travel.download.IDownloadService
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
public boolean addTask(java.lang.String strKey, java.lang.String strURL, java.lang.String strSavePath) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(strKey);
_data.writeString(strURL);
_data.writeString(strSavePath);
mRemote.transact(Stub.TRANSACTION_addTask, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
     *use this function to set how many tasks can download at the same time 
     *if you don't use the function .the default is 3 
          */
public void setMaxTaskCount(int count) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(count);
mRemote.transact(Stub.TRANSACTION_setMaxTaskCount, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void pauseTask(java.lang.String strKey) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(strKey);
mRemote.transact(Stub.TRANSACTION_pauseTask, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void cancelTask(java.lang.String strkey) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(strkey);
mRemote.transact(Stub.TRANSACTION_cancelTask, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void regCallback(com.damuzhi.travel.download.IDownloadCallback cb) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((cb!=null))?(cb.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_regCallback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void unregCallback(com.damuzhi.travel.download.IDownloadCallback cb) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((cb!=null))?(cb.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_unregCallback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_addTask = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_setMaxTaskCount = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_pauseTask = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_cancelTask = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_regCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_unregCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
}
public boolean addTask(java.lang.String strKey, java.lang.String strURL, java.lang.String strSavePath) throws android.os.RemoteException;
/** 
     *use this function to set how many tasks can download at the same time 
     *if you don't use the function .the default is 3 
          */
public void setMaxTaskCount(int count) throws android.os.RemoteException;
public void pauseTask(java.lang.String strKey) throws android.os.RemoteException;
public void cancelTask(java.lang.String strkey) throws android.os.RemoteException;
public void regCallback(com.damuzhi.travel.download.IDownloadCallback cb) throws android.os.RemoteException;
public void unregCallback(com.damuzhi.travel.download.IDownloadCallback cb) throws android.os.RemoteException;
}
