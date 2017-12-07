package android.simple.toolbox.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.io.Serializable;

/**
 * Created by Sikang on 2017/3/21.
 */

public class IntentBuilder {
    private Intent intent;
    private Context context;
    Bundle bundle;

    private IntentBuilder(Context from, Class target) {
        intent = new Intent(from, target);
        bundle = new Bundle();
        this.context = from;
    }

    private IntentBuilder(Context from, String action) {
        intent = new Intent(action);
        bundle = new Bundle();
        this.context = from;
    }

    public static IntentBuilder build(Context from, Class target) {
        return new IntentBuilder(from, target);
    }

    public static IntentBuilder build(Context from, String action) {
        return new IntentBuilder(from, action);
    }

    public IntentBuilder put(String key, String value) {
        bundle.putString(key, value);
        return this;
    }

    public IntentBuilder put(String key, int value) {
        bundle.putInt(key, value);
        return this;
    }


    public IntentBuilder put(String key, boolean value) {
        bundle.putBoolean(key, value);
        return this;
    }

    public IntentBuilder put(String key, Serializable obj) {
        bundle.putSerializable(key, obj);
        return this;
    }

    public void start() {
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    public void sendBroadcast() {
        intent.putExtras(bundle);
        context.sendOrderedBroadcast(intent, null);
    }

}
