package com.android.lua.framework;

import org.keplerproject.luajava.LuaException;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;

import com.android.lua.core.LuaEngine;

/**
 * Lua base activity.
 * @author lizhennian
 * @version 0.0.1 2015-02-13
 */
public abstract class LuaActivity extends Activity {
    // public so we can play with these from Lua

    private String mLuaMainModuleName;

    /** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            this.getMetaData();
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        try {
            LuaEngine.getInstance().executeString(
                    "require('" + this.mLuaMainModuleName + "')");
        } catch (LuaException e) {
            e.printStackTrace();
        }

        this.attachContext();

        this.callLuaModuleMethod("onCreate", savedInstanceState);

    }

    @Override
    protected void onStart() {
        super.onStart();
        this.callLuaModuleMethod("onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.callLuaModuleMethod("onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.callLuaModuleMethod("onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.callLuaModuleMethod("onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.callLuaModuleMethod("onDestroy");
    }

    @Override
    public void onBackPressed() {
        this.callLuaModuleMethod("onBackPressed");
    }

    private void getMetaData() throws NameNotFoundException {
        ActivityInfo info = this.getPackageManager().getActivityInfo(
                this.getComponentName(), PackageManager.GET_META_DATA);
        if (info.metaData != null) {
            this.mLuaMainModuleName = info.metaData
                    .getString("lua_main_module_name");
        }
    }

    private void attachContext() {
        this.callLuaModuleMethod("attachContext", this);
    }

    protected void callLuaModuleMethod(String method, Object... args) {
        try {
            LuaEngine.getInstance().executeModuleFunction(
                    this.mLuaMainModuleName, method, args);
        } catch (LuaException e) {
            e.printStackTrace();
        }
    }
}