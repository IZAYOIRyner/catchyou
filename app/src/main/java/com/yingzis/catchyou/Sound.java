package com.yingzis.catchyou;
import java.util.HashMap;
import android.media.SoundPool;
import android.media.AudioManager;
import android.content.Context;
import android.util.Log;

public class Sound {
    private static SoundPool sp;
    private static HashMap<Integer, Integer> sounddata;
    private static Boolean loaded = false;
    private static Context context;

    public static void init(Context c) {
        context = c;
        sp = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        sounddata = new HashMap<>();
        sounddata.put(1, sp.load(context, R.raw.getnet, 1));
        sounddata.put(2, sp.load(context, R.raw.hit, 1));
        sounddata.put(3, sp.load(context, R.raw.net, 1));
        sounddata.put(4, sp.load(context, R.raw.tied, 1));
        sounddata.put(5, sp.load(context, R.raw.shieldon, 1));
        sounddata.put(6, sp.load(context, R.raw.shieldoff, 1));
        sp.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener(){
            @Override
            public void onLoadComplete(SoundPool sound,int sampleId,int status){
                loaded=true;
                Log.d("Sound", "Loaded");
            }
        });
    }
    public static void play(int sound, int number) {
        if(loaded){
            AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            float audioMaxVolumn = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            float volumnCurrent = am.getStreamVolume(AudioManager.STREAM_MUSIC);
            float volumnRatio = volumnCurrent / audioMaxVolumn;

            sp.play(sounddata.get(sound), volumnRatio, volumnRatio,1, number,1);
            Log.d("Sound", "Play!");
        }
    }
    public static boolean getLoaded() {
        return loaded;
    }

}
