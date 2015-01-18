package com.mycompany.myfirstapp;

import android.content.Context;
import android.media.MediaPlayer;

import java.util.Hashtable;

/**
 * Created by timotius02 on 1/17/15.
 */
public class SoundManager {
    private Context context;
    private static MediaPlayer bd1;
    private static MediaPlayer bd2;


    public SoundManager(Context context) {
        this.context = context;

        bd1 = MediaPlayer.create(context, R.raw.bd1);
        bd2 = MediaPlayer.create(context, R.raw.bd2);

    }

    public static void bd1() {
        bd1.start();
    }
    public static void bd2() {
        bd2.start();
    }


}
