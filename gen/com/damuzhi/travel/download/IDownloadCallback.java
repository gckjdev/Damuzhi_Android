/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: E:\\Damuzhi_Android\\src\\com\\damuzhi\\travel\\download\\IDownloadCallback.aidl
 */
package com.damuzhi.travel.download;
public interface IDownloadCallback extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.damuzhi.travel.download.IDownloadCallback
{
private static final java.lang.String DESCRIPTOR = "com.damuzhi.travel.download.IDownloadCallback";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.damuzhi.travel.download.IDownloadCallback interface,
 * generating a proxy if needed.
 */
public static com.damuzhi.travel.download.IDownloadCallback asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.damuzhi.travel.download.IDownloadCallback))) {
return ((com.damuzhi.travel.download.IDownloadCallback)iin);
}
return new com.damuzhi.travel.download.IDownloadCallback.Stub.Proxy(obj);
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
case TRANSACTION_onTaskStatusChanged:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _arg1;
_arg1 = data.readInt();
this.onTaskStatusChanged(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_onTaskProcessStatusChanged:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
long _arg1;
_arg1 = data.readLong();
long _arg2;
_arg2 = data.readLong();
long _arg3;
_arg3 = data.readLong();
this.onTaskProcessStatusChanged(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.damuzhi.travel.download.IDownloadCallback
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
public void onTaskStatusChanged(java.lang.String strkey, int status) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(strkey);
_data.writeInt(status);
mRemote.transact(Stub.TRANSACTION_onTaskStatusChanged, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void onTaskProcessStatusChanged(java.lang.String strkey, long speed, long totalBytes, long curPos) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(strkey);
_data.writeLong(speed);
_data.writeLong(totalBytes);
_data.writeLong(curPos);
mRemote.transact(Stub.TRANSACTION_onTaskProcessStatusChanged, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_onTaskStatusChanged = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_onTaskProcessStatusChanged = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
}
public void onTaskStatusChanged(java.lang.String strkey, int status) throws android.os.RemoteException;
public void onTaskProcessStatusChanged(java.lang.String strkey, long speed, long totalBytes, long curPos) throws android.os.RemoteException;
}
