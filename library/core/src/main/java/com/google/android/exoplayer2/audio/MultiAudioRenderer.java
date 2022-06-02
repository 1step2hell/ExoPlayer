package com.google.android.exoplayer2.audio;

import android.content.Context;
import android.os.Handler;
import androidx.annotation.Nullable;
import com.google.android.exoplayer2.mediacodec.MediaCodecAdapter;
import com.google.android.exoplayer2.mediacodec.MediaCodecSelector;
import com.google.android.exoplayer2.util.MediaClock;

public class MultiAudioRenderer extends MediaCodecAudioRenderer {

  private final boolean provideMediaClock;

  public MultiAudioRenderer(
      Context context,
      MediaCodecAdapter.Factory codecAdapterFactory,
      MediaCodecSelector mediaCodecSelector,
      boolean enableDecoderFallback,
      @Nullable Handler eventHandler,
      @Nullable AudioRendererEventListener eventListener,
      AudioSink audioSink,
      boolean provideMediaClock) {
    super(
        context,
        codecAdapterFactory,
        mediaCodecSelector,
        enableDecoderFallback,
        eventHandler,
        eventListener,
        audioSink);
    this.provideMediaClock = provideMediaClock;
  }

  @Nullable
  @Override
  public MediaClock getMediaClock() {
    return provideMediaClock ? this : null;
  }
}
