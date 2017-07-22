package com.yanxiu.gphone.student.base;

import android.Manifest;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.yanxiu.gphone.student.R;
import com.yanxiu.gphone.student.util.ActivityManger;
import com.yanxiu.gphone.student.util.PermissionUtil;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by 戴延枫 on 2017/5/9.
 */
public class YanxiuBaseActivity extends FragmentActivity implements EasyPermissions.PermissionCallbacks {
    private final String TAG = "YanxiuBaseActivity";
    private static final int RC_CAMERA_PERM = 123;
    private static final int RC_WRITE_READ_PERM = 124;
    private static final int RC_OTHER_PERM = 125;
    private static OnPermissionCallback mPermissionCallback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManger.addActicity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {

        ActivityManger.destoryActivity(this);
        mPermissionCallback = null;
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    /**
     * 请求照相机权限
     */
    public static void requestCameraPermission(OnPermissionCallback onPermissionCallback) {
        mPermissionCallback = onPermissionCallback;
        ArrayList<String> permsList = new ArrayList<>();
        permsList.add(Manifest.permission.CAMERA);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) { //6.0系统以下
            if (PermissionUtil.cameraIsCanUse()) {
                mPermissionCallback.onPermissionsGranted(permsList);
            } else {
                mPermissionCallback.onPermissionsDenied(permsList);
            }
        } else {
            if (EasyPermissions.hasPermissions(ActivityManger.getTopActivity(), Manifest.permission.CAMERA)) {
                // Have permission, do the thing!
                mPermissionCallback.onPermissionsGranted(permsList);
            } else {
                // Ask for one permission
                EasyPermissions.requestPermissions(ActivityManger.getTopActivity(), ActivityManger.getTopActivity().getString(R.string.rationale_camera), RC_CAMERA_PERM, Manifest.permission.CAMERA);
            }
        }
    }

    /**
     * 请求读写权限
     */
    public static void requestWriteAndReadPermission(OnPermissionCallback onPermissionCallback) {
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        mPermissionCallback = onPermissionCallback;
        ArrayList<String> permsList = new ArrayList<>();
        for (int i = 0; i < perms.length; i++) {
            permsList.add(perms[i]);
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) { //6.0系统以下
            if (PermissionUtil.checkWritePermission() && PermissionUtil.checkReadPermission()) {
                mPermissionCallback.onPermissionsGranted(permsList);
            } else {
                mPermissionCallback.onPermissionsDenied(permsList);
            }
        } else {
            if (EasyPermissions.hasPermissions(ActivityManger.getTopActivity(), perms)) {
                // Have permission, do the thing!
                mPermissionCallback.onPermissionsGranted(permsList);
            } else {
                // Ask for one permission
                EasyPermissions.requestPermissions(ActivityManger.getTopActivity(), ActivityManger.getTopActivity().getString(R.string.rationale_write_read), RC_WRITE_READ_PERM, perms);
            }
        }
    }

    /**
     * 请求权限
     *
     * @param perms {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_CONTACTS};
     */
    public static void requestPermissions(String[] perms, OnPermissionCallback onPermissionCallback) {
        mPermissionCallback = onPermissionCallback;
        if (EasyPermissions.hasPermissions(ActivityManger.getTopActivity(), perms)) {
            // Have permissions, do the thing!
            ArrayList<String> permsList = new ArrayList<>();
            for (int i = 0; i < perms.length; i++) {
                permsList.add(perms[i]);
            }
            mPermissionCallback.onPermissionsGranted(permsList);
        } else {
            // Ask for both permissions
            EasyPermissions.requestPermissions(ActivityManger.getTopActivity(), ActivityManger.getTopActivity().getString(R.string.rationale_other), RC_OTHER_PERM, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Log.d(TAG, "onPermissionsGranted:" + requestCode + ":" + perms.size());
        mPermissionCallback.onPermissionsGranted(perms);
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.d(TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size());
        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
//        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
//            new AppSettingsDialog.Builder(this).build().show();
//        }
        mPermissionCallback.onPermissionsDenied(perms);
    }
}
