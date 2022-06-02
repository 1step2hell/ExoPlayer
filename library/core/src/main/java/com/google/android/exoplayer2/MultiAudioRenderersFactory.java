package com.google.android.exoplayer2;

import android.content.Context;
import android.os.Handler;
import com.google.android.exoplayer2.audio.AudioRendererEventListener;
import com.google.android.exoplayer2.audio.AudioSink;
import com.google.android.exoplayer2.audio.MultiAudioRenderer;
import com.google.android.exoplayer2.mediacodec.MediaCodecSelector;
import com.google.android.exoplayer2.util.Util;
import java.lang.reflect.Constructor;
import java.util.ArrayList;

public class MultiAudioRenderersFactory extends DefaultRenderersFactory {

  private final int audioRenderersCount;
  private final int mediaClockRendererIndex;

  /**
   * @param context A {@link Context}.
   */
  public MultiAudioRenderersFactory(
      Context context,
      int audioRenderersCount,
      int mediaClockRendererIndex) {
    super(context);
    this.audioRenderersCount = Math.max(audioRenderersCount, 1);
    this.mediaClockRendererIndex = mediaClockRendererIndex;
  }

  @Override
  protected void buildAudioRenderers(
      Context context,
      @ExtensionRendererMode int extensionRendererMode,
      MediaCodecSelector mediaCodecSelector,
      boolean enableDecoderFallback,
      AudioSink audioSink,
      Handler eventHandler,
      AudioRendererEventListener eventListener,
      ArrayList<Renderer> out) {
    for (int i = 0; i < audioRenderersCount; i++) {
      AudioSink uniqueAudioSink = buildAudioSink();
      if (Util.SDK_INT >= 21) {
        int audioSessionId = Util.generateAudioSessionIdV21(context);
        uniqueAudioSink.setAudioSessionId(audioSessionId);
      } else {
        // fixme
      }
      boolean provideMediaClock = mediaClockRendererIndex == i;
      MultiAudioRenderer renderer = new MultiAudioRenderer(
          context,
          getCodecAdapterFactory(),
          mediaCodecSelector,
          enableDecoderFallback,
          eventHandler,
          eventListener,
          uniqueAudioSink,
          provideMediaClock
      );
      out.add(renderer);
    }

    if (extensionRendererMode == EXTENSION_RENDERER_MODE_OFF) {
      return;
    }
    int extensionRendererIndex = out.size();
    if (extensionRendererMode == EXTENSION_RENDERER_MODE_PREFER) {
      extensionRendererIndex--;
    }

    try {
      // Full class names used for constructor args so the LINT rule triggers if any of them move.
      Class<?> clazz = Class.forName("com.google.android.exoplayer2.ext.opus.LibopusAudioRenderer");
      Constructor<?> constructor =
          clazz.getConstructor(
              android.os.Handler.class,
              com.google.android.exoplayer2.audio.AudioRendererEventListener.class,
              com.google.android.exoplayer2.audio.AudioSink.class);
      Renderer renderer =
          (Renderer) constructor.newInstance(eventHandler, eventListener, audioSink);
      out.add(extensionRendererIndex++, renderer);
    } catch (ClassNotFoundException e) {
      // Expected if the app was built without the extension.
    } catch (Exception e) {
      // The extension is present, but instantiation failed.
      throw new RuntimeException("Error instantiating Opus extension", e);
    }

    try {
      // Full class names used for constructor args so the LINT rule triggers if any of them move.
      Class<?> clazz = Class.forName("com.google.android.exoplayer2.ext.flac.LibflacAudioRenderer");
      Constructor<?> constructor =
          clazz.getConstructor(
              android.os.Handler.class,
              com.google.android.exoplayer2.audio.AudioRendererEventListener.class,
              com.google.android.exoplayer2.audio.AudioSink.class);
      Renderer renderer =
          (Renderer) constructor.newInstance(eventHandler, eventListener, audioSink);
      out.add(extensionRendererIndex++, renderer);
    } catch (ClassNotFoundException e) {
      // Expected if the app was built without the extension.
    } catch (Exception e) {
      // The extension is present, but instantiation failed.
      throw new RuntimeException("Error instantiating FLAC extension", e);
    }

    try {
      // Full class names used for constructor args so the LINT rule triggers if any of them move.
      Class<?> clazz =
          Class.forName("com.google.android.exoplayer2.ext.ffmpeg.FfmpegAudioRenderer");
      Constructor<?> constructor =
          clazz.getConstructor(
              android.os.Handler.class,
              com.google.android.exoplayer2.audio.AudioRendererEventListener.class,
              com.google.android.exoplayer2.audio.AudioSink.class);
      Renderer renderer =
          (Renderer) constructor.newInstance(eventHandler, eventListener, audioSink);
      out.add(extensionRendererIndex++, renderer);
    } catch (ClassNotFoundException e) {
      // Expected if the app was built without the extension.
    } catch (Exception e) {
      // The extension is present, but instantiation failed.
      throw new RuntimeException("Error instantiating FFmpeg extension", e);
    }
  }
}
