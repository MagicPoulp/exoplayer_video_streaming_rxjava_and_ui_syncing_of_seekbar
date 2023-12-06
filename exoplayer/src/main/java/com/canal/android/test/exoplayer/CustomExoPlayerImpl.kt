package com.canal.android.test.exoplayer

import android.media.AudioDeviceInfo
import android.os.Looper
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.TextureView
import androidx.media3.common.AudioAttributes
import androidx.media3.common.AuxEffectInfo
import androidx.media3.common.DeviceInfo
import androidx.media3.common.Format
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.PriorityTaskManager
import androidx.media3.common.Timeline
import androidx.media3.common.TrackSelectionParameters
import androidx.media3.common.Tracks
import androidx.media3.common.VideoSize
import androidx.media3.common.text.CueGroup
import androidx.media3.common.util.Clock
import androidx.media3.common.util.Size
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DecoderCounters
import androidx.media3.exoplayer.ExoPlaybackException
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.PlayerMessage
import androidx.media3.exoplayer.Renderer
import androidx.media3.exoplayer.SeekParameters
import androidx.media3.exoplayer.analytics.AnalyticsCollector
import androidx.media3.exoplayer.analytics.AnalyticsListener
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ShuffleOrder
import androidx.media3.exoplayer.source.TrackGroupArray
import androidx.media3.exoplayer.trackselection.TrackSelectionArray
import androidx.media3.exoplayer.trackselection.TrackSelector
import androidx.media3.exoplayer.video.VideoFrameMetadataListener
import androidx.media3.exoplayer.video.spherical.CameraMotionListener

// We use the Adapter design pattern because we cannot inherit ExoPlayerImpl
// we need to add COMMAND_SEEK_IN_CURRENT_MEDIA_ITEM in the available commands
// seekTo() with one argument needs the missing command COMMAND_SEEK_IN_CURRENT_MEDIA_ITEM
// seekTo() with 2 arguments needs the command COMMAND_SEEK_TO_MEDIA_ITEM that we have but the media index is required
@UnstableApi
class CustomExoPlayerImpl(private val playerInstance: ExoPlayer) : ExoPlayer {

    override fun getApplicationLooper(): Looper {
        return playerInstance.applicationLooper
    }

    override fun addListener(listener: Player.Listener) {
        playerInstance.addListener(listener)
    }

    override fun removeListener(listener: Player.Listener) {
        playerInstance.removeListener(listener)
    }

    override fun setMediaItems(mediaItems: MutableList<MediaItem>) {
        playerInstance.setMediaItems(mediaItems)
    }

    override fun setMediaItems(mediaItems: MutableList<MediaItem>, resetPosition: Boolean) {
        playerInstance.setMediaItems(mediaItems, resetPosition)
    }

    override fun setMediaItems(
        mediaItems: MutableList<MediaItem>,
        startIndex: Int,
        startPositionMs: Long
    ) {
        playerInstance.setMediaItems(mediaItems, startIndex, startPositionMs)
    }

    override fun setMediaItem(mediaItem: MediaItem) {
        playerInstance.setMediaItem(mediaItem)
    }

    override fun setMediaItem(mediaItem: MediaItem, startPositionMs: Long) {
        playerInstance.setMediaItem(mediaItem, startPositionMs)
    }

    override fun setMediaItem(mediaItem: MediaItem, resetPosition: Boolean) {
        playerInstance.setMediaItem(mediaItem, resetPosition)
    }

    override fun addMediaItem(mediaItem: MediaItem) {
        playerInstance.addMediaItem(mediaItem)
    }

    override fun addMediaItem(index: Int, mediaItem: MediaItem) {
        playerInstance.addMediaItem(index, mediaItem)
    }

    override fun addMediaItems(mediaItems: MutableList<MediaItem>) {
        playerInstance.addMediaItems(mediaItems)
    }

    override fun addMediaItems(index: Int, mediaItems: MutableList<MediaItem>) {
        playerInstance.addMediaItems(index, mediaItems)
    }

    override fun moveMediaItem(currentIndex: Int, newIndex: Int) {
        playerInstance.moveMediaItem(currentIndex, newIndex)
    }

    override fun moveMediaItems(fromIndex: Int, toIndex: Int, newIndex: Int) {
        playerInstance.moveMediaItems(fromIndex, toIndex, newIndex)
    }

    override fun removeMediaItem(index: Int) {
        playerInstance.removeMediaItem(index)
    }

    override fun removeMediaItems(fromIndex: Int, toIndex: Int) {
        playerInstance.removeMediaItems(fromIndex, toIndex)
    }

    override fun clearMediaItems() {
        playerInstance.clearMediaItems()
    }

    override fun isCommandAvailable(command: Int): Boolean {
        return playerInstance.isCommandAvailable(command)
    }

    override fun canAdvertiseSession(): Boolean {
        return playerInstance.canAdvertiseSession()
    }

    // CUSTOM
    override fun getAvailableCommands(): Player.Commands {
        return playerInstance.availableCommands.buildUpon().addAllCommands().build()
    }

    override fun prepare(mediaSource: MediaSource) {
        playerInstance.prepare(mediaSource)
    }

    override fun prepare(mediaSource: MediaSource, resetPosition: Boolean, resetState: Boolean) {
        playerInstance.prepare(mediaSource, resetPosition, resetState)
    }

    override fun prepare() {
        playerInstance.prepare()
    }

    override fun getPlaybackState(): Int {
        return playerInstance.playbackState
    }

    override fun getPlaybackSuppressionReason(): Int {
        return playerInstance.playbackSuppressionReason
    }

    override fun isPlaying(): Boolean {
        return playerInstance.isPlaying()
    }

    override fun getPlayerError(): ExoPlaybackException? {
        return playerInstance.playerError
    }

    override fun play() {
        playerInstance.play()
    }

    override fun pause() {
        playerInstance.pause()
    }

    override fun setPlayWhenReady(playWhenReady: Boolean) {
        playerInstance.playWhenReady = playWhenReady
    }

    override fun getPlayWhenReady(): Boolean {
        return playerInstance.playWhenReady
    }

    override fun setRepeatMode(repeatMode: Int) {
        playerInstance.repeatMode = repeatMode
    }

    override fun getRepeatMode(): Int {
        return playerInstance.repeatMode
    }

    override fun setShuffleModeEnabled(shuffleModeEnabled: Boolean) {
        playerInstance.shuffleModeEnabled = shuffleModeEnabled
    }

    override fun getShuffleModeEnabled(): Boolean {
        return playerInstance.shuffleModeEnabled
    }

    override fun isLoading(): Boolean {
        return playerInstance.isLoading
    }

    override fun seekToDefaultPosition() {
        playerInstance.seekToDefaultPosition()
    }

    override fun seekToDefaultPosition(mediaItemIndex: Int) {
        playerInstance.seekToDefaultPosition(mediaItemIndex)
    }

    override fun seekTo(positionMs: Long) {
        playerInstance.seekTo(positionMs)
    }

    override fun seekTo(mediaItemIndex: Int, positionMs: Long) {
        playerInstance.seekTo(mediaItemIndex, positionMs)
    }

    override fun getSeekBackIncrement(): Long {
        return playerInstance.seekBackIncrement
    }

    override fun seekBack() {
        playerInstance.seekBack()
    }

    override fun getSeekForwardIncrement(): Long {
        return playerInstance.seekForwardIncrement
    }

    override fun seekForward() {
        playerInstance.seekForward()
    }

    override fun hasPrevious(): Boolean {
        return playerInstance.hasPrevious()
    }

    override fun hasPreviousWindow(): Boolean {
        return playerInstance.hasPreviousWindow()
    }

    override fun hasPreviousMediaItem(): Boolean {
        return playerInstance.hasPreviousMediaItem()
    }

    override fun previous() {
        playerInstance.previous()
    }

    override fun seekToPreviousWindow() {
        playerInstance.seekToPreviousWindow()
    }

    override fun seekToPreviousMediaItem() {
        playerInstance.seekToPreviousMediaItem()
    }

    override fun getMaxSeekToPreviousPosition(): Long {
        return playerInstance.maxSeekToPreviousPosition
    }

    override fun seekToPrevious() {
        playerInstance.seekToPrevious()
    }

    override fun hasNext(): Boolean {
        return playerInstance.hasNext()
    }

    override fun hasNextWindow(): Boolean {
        return playerInstance.hasNextWindow()
    }

    override fun hasNextMediaItem(): Boolean {
        return playerInstance.hasNextMediaItem()
    }

    override fun next() {
        playerInstance.next()
    }

    override fun seekToNextWindow() {
        playerInstance.seekToNextWindow()
    }

    override fun seekToNextMediaItem() {
        playerInstance.seekToNextMediaItem()
    }

    override fun seekToNext() {
        playerInstance.seekToNext()
    }

    override fun setPlaybackParameters(playbackParameters: PlaybackParameters) {
        playerInstance.playbackParameters = playbackParameters
    }

    override fun setPlaybackSpeed(speed: Float) {
        playerInstance.setPlaybackSpeed(speed)
    }

    override fun getPlaybackParameters(): PlaybackParameters {
        return playerInstance.playbackParameters
    }

    override fun stop() {
        playerInstance.stop()
    }

    override fun stop(reset: Boolean) {
        playerInstance.stop(reset)
    }

    override fun release() {
        playerInstance.release()
    }

    override fun getCurrentTracks(): Tracks {
        return playerInstance.currentTracks
    }

    override fun getTrackSelectionParameters(): TrackSelectionParameters {
        return playerInstance.trackSelectionParameters
    }

    override fun setTrackSelectionParameters(parameters: TrackSelectionParameters) {
        playerInstance.trackSelectionParameters = parameters
    }

    override fun getMediaMetadata(): MediaMetadata {
        return playerInstance.mediaMetadata
    }

    override fun getPlaylistMetadata(): MediaMetadata {
        return playerInstance.playlistMetadata
    }

    override fun setPlaylistMetadata(mediaMetadata: MediaMetadata) {
        playerInstance.playlistMetadata = mediaMetadata
    }

    override fun getCurrentManifest(): Any? {
        return playerInstance.currentManifest
    }

    override fun getCurrentTimeline(): Timeline {
        return playerInstance.currentTimeline
    }

    override fun getCurrentPeriodIndex(): Int {
        return playerInstance.currentPeriodIndex
    }

    override fun getCurrentWindowIndex(): Int {
        return playerInstance.getCurrentWindowIndex()
    }

    override fun getCurrentMediaItemIndex(): Int {
        return playerInstance.currentMediaItemIndex
    }

    override fun getNextWindowIndex(): Int {
        return playerInstance.getNextWindowIndex()
    }

    override fun getNextMediaItemIndex(): Int {
        return playerInstance.getNextMediaItemIndex()
    }

    override fun getPreviousWindowIndex(): Int {
        return playerInstance.getPreviousWindowIndex()
    }

    override fun getPreviousMediaItemIndex(): Int {
        return playerInstance.getPreviousMediaItemIndex()
    }

    override fun getCurrentMediaItem(): MediaItem? {
        return playerInstance.getCurrentMediaItem()
    }

    override fun getMediaItemCount(): Int {
        return playerInstance.getMediaItemCount()
    }

    override fun getMediaItemAt(index: Int): MediaItem {
        return playerInstance.getMediaItemAt(index)
    }

    override fun getDuration(): Long {
        return playerInstance.getDuration()
    }

    override fun getCurrentPosition(): Long {
        return playerInstance.getCurrentPosition()
    }

    override fun getBufferedPosition(): Long {
        return playerInstance.getBufferedPosition()
    }

    override fun getBufferedPercentage(): Int {
        return playerInstance.getBufferedPercentage()
    }

    override fun getTotalBufferedDuration(): Long {
        return playerInstance.getTotalBufferedDuration()
    }

    override fun isCurrentWindowDynamic(): Boolean {
        return playerInstance.isCurrentWindowDynamic()
    }

    override fun isCurrentMediaItemDynamic(): Boolean {
        return playerInstance.isCurrentMediaItemDynamic()
    }

    override fun isCurrentWindowLive(): Boolean {
        return playerInstance.isCurrentWindowLive()
    }

    override fun isCurrentMediaItemLive(): Boolean {
        return playerInstance.isCurrentMediaItemLive()
    }

    override fun getCurrentLiveOffset(): Long {
        return playerInstance.getCurrentLiveOffset()
    }

    override fun isCurrentWindowSeekable(): Boolean {
        return playerInstance.isCurrentWindowSeekable()
    }

    override fun isCurrentMediaItemSeekable(): Boolean {
        return playerInstance.isCurrentMediaItemSeekable()
    }

    override fun isPlayingAd(): Boolean {
        return playerInstance.isPlayingAd()
    }

    override fun getCurrentAdGroupIndex(): Int {
        return playerInstance.getCurrentAdGroupIndex()
    }

    override fun getCurrentAdIndexInAdGroup(): Int {
        return playerInstance.getCurrentAdIndexInAdGroup()
    }

    override fun getContentDuration(): Long {
        return playerInstance.getContentDuration()
    }

    override fun getContentPosition(): Long {
        return playerInstance.getContentPosition()
    }

    override fun getContentBufferedPosition(): Long {
        return playerInstance.getContentBufferedPosition()
    }

    override fun getAudioAttributes(): AudioAttributes {
        return playerInstance.getAudioAttributes()
    }

    override fun setVolume(volume: Float) {
        playerInstance.setVolume(volume)
    }

    override fun getVolume(): Float {
        return playerInstance.getVolume()
    }

    override fun clearVideoSurface() {
        playerInstance.clearVideoSurface()
    }

    override fun clearVideoSurface(surface: Surface?) {
        playerInstance.clearVideoSurface(surface)
    }

    override fun setVideoSurface(surface: Surface?) {
        playerInstance.setVideoSurface(surface)
    }

    override fun setVideoSurfaceHolder(surfaceHolder: SurfaceHolder?) {
        playerInstance.setVideoSurfaceHolder(surfaceHolder)
    }

    override fun clearVideoSurfaceHolder(surfaceHolder: SurfaceHolder?) {
        playerInstance.clearVideoSurfaceHolder(surfaceHolder)
    }

    override fun setVideoSurfaceView(surfaceView: SurfaceView?) {
        playerInstance.setVideoSurfaceView(surfaceView)
    }

    override fun clearVideoSurfaceView(surfaceView: SurfaceView?) {
        playerInstance.clearVideoSurfaceView(surfaceView)
    }

    override fun setVideoTextureView(textureView: TextureView?) {
        playerInstance.setVideoTextureView(textureView)
    }

    override fun clearVideoTextureView(textureView: TextureView?) {
        playerInstance.clearVideoTextureView(textureView)
    }

    override fun getVideoSize(): VideoSize {
        return playerInstance.getVideoSize()
    }

    override fun getSurfaceSize(): Size {
        return playerInstance.getSurfaceSize()
    }

    override fun getCurrentCues(): CueGroup {
        return playerInstance.getCurrentCues()
    }

    override fun getDeviceInfo(): DeviceInfo {
        return playerInstance.getDeviceInfo()
    }

    override fun getDeviceVolume(): Int {
        return playerInstance.getDeviceVolume()
    }

    override fun isDeviceMuted(): Boolean {
        return playerInstance.isDeviceMuted()
    }

    override fun setDeviceVolume(volume: Int) {
        playerInstance.setDeviceVolume(volume)
    }

    override fun increaseDeviceVolume() {
        playerInstance.increaseDeviceVolume()
    }

    override fun decreaseDeviceVolume() {
        playerInstance.decreaseDeviceVolume()
    }

    override fun setDeviceMuted(muted: Boolean) {
        playerInstance.setDeviceMuted(muted)
    }

    override fun getAudioComponent(): ExoPlayer.AudioComponent? {
        return playerInstance.getAudioComponent()
    }

    override fun getVideoComponent(): ExoPlayer.VideoComponent? {
        return playerInstance.getVideoComponent()
    }

    override fun getTextComponent(): ExoPlayer.TextComponent? {
        return playerInstance.getTextComponent()
    }

    override fun getDeviceComponent(): ExoPlayer.DeviceComponent? {
        return playerInstance.getDeviceComponent()
    }

    override fun addAudioOffloadListener(listener: ExoPlayer.AudioOffloadListener) {
        playerInstance.addAudioOffloadListener(listener)
    }

    override fun removeAudioOffloadListener(listener: ExoPlayer.AudioOffloadListener) {
        playerInstance.removeAudioOffloadListener(listener)
    }

    override fun getAnalyticsCollector(): AnalyticsCollector {
        return playerInstance.getAnalyticsCollector()
    }

    override fun addAnalyticsListener(listener: AnalyticsListener) {
        playerInstance.addAnalyticsListener(listener)
    }

    override fun removeAnalyticsListener(listener: AnalyticsListener) {
        playerInstance.removeAnalyticsListener(listener)
    }

    override fun getRendererCount(): Int {
        return playerInstance.getRendererCount()
    }

    override fun getRendererType(index: Int): Int {
        return playerInstance.getRendererType(index)
    }

    override fun getRenderer(index: Int): Renderer {
        return playerInstance.getRenderer(index)
    }

    override fun getTrackSelector(): TrackSelector? {
        return playerInstance.getTrackSelector()
    }

    override fun getCurrentTrackGroups(): TrackGroupArray {
        return playerInstance.getCurrentTrackGroups()
    }

    override fun getCurrentTrackSelections(): TrackSelectionArray {
        return playerInstance.getCurrentTrackSelections()
    }

    override fun getPlaybackLooper(): Looper {
        return playerInstance.getPlaybackLooper()
    }

    override fun getClock(): Clock {
        return playerInstance.getClock()
    }

    override fun retry() {
        playerInstance.retry()
    }

    override fun setMediaSources(mediaSources: MutableList<MediaSource>) {
        playerInstance.setMediaSources(mediaSources)
    }

    override fun setMediaSources(mediaSources: MutableList<MediaSource>, resetPosition: Boolean) {
        playerInstance.setMediaSources(mediaSources, resetPosition)
    }

    override fun setMediaSources(
        mediaSources: MutableList<MediaSource>,
        startMediaItemIndex: Int,
        startPositionMs: Long
    ) {
        playerInstance.setMediaSources(
            mediaSources,
            startMediaItemIndex,
            startPositionMs
        )
    }

    override fun setMediaSource(mediaSource: MediaSource) {
        playerInstance.setMediaSource(mediaSource)
    }

    override fun setMediaSource(mediaSource: MediaSource, startPositionMs: Long) {
        playerInstance.setMediaSource(mediaSource, startPositionMs)
    }

    override fun setMediaSource(mediaSource: MediaSource, resetPosition: Boolean) {
        playerInstance.setMediaSource(mediaSource, resetPosition)
    }

    override fun addMediaSource(mediaSource: MediaSource) {
        playerInstance.addMediaSource(mediaSource)
    }

    override fun addMediaSource(index: Int, mediaSource: MediaSource) {
        playerInstance.addMediaSource(index, mediaSource)
    }

    override fun addMediaSources(mediaSources: MutableList<MediaSource>) {
        playerInstance.addMediaSources(mediaSources)
    }

    override fun addMediaSources(index: Int, mediaSources: MutableList<MediaSource>) {
        playerInstance.addMediaSources(index, mediaSources)
    }

    override fun setShuffleOrder(shuffleOrder: ShuffleOrder) {
        playerInstance.setShuffleOrder(shuffleOrder)
    }

    override fun setAudioAttributes(audioAttributes: AudioAttributes, handleAudioFocus: Boolean) {
        playerInstance.setAudioAttributes(audioAttributes, handleAudioFocus)
    }

    override fun setAudioSessionId(audioSessionId: Int) {
        playerInstance.setAudioSessionId(audioSessionId)
    }

    override fun getAudioSessionId(): Int {
        return playerInstance.getAudioSessionId()
    }

    override fun setAuxEffectInfo(auxEffectInfo: AuxEffectInfo) {
        playerInstance.setAuxEffectInfo(auxEffectInfo)
    }

    override fun clearAuxEffectInfo() {
        playerInstance.clearAuxEffectInfo()
    }

    override fun setPreferredAudioDevice(audioDeviceInfo: AudioDeviceInfo?) {
        playerInstance.setPreferredAudioDevice(audioDeviceInfo)
    }

    override fun setSkipSilenceEnabled(skipSilenceEnabled: Boolean) {
        playerInstance.setSkipSilenceEnabled(skipSilenceEnabled)
    }

    override fun getSkipSilenceEnabled(): Boolean {
        return playerInstance.getSkipSilenceEnabled()
    }

    override fun setVideoScalingMode(videoScalingMode: Int) {
        playerInstance.setVideoScalingMode(videoScalingMode)
    }

    override fun getVideoScalingMode(): Int {
        return playerInstance.getVideoScalingMode()
    }

    override fun setVideoChangeFrameRateStrategy(videoChangeFrameRateStrategy: Int) {
        playerInstance.setVideoChangeFrameRateStrategy(videoChangeFrameRateStrategy)
    }

    override fun getVideoChangeFrameRateStrategy(): Int {
        return playerInstance.getVideoChangeFrameRateStrategy()
    }

    override fun setVideoFrameMetadataListener(listener: VideoFrameMetadataListener) {
        playerInstance.setVideoFrameMetadataListener(listener)
    }

    override fun clearVideoFrameMetadataListener(listener: VideoFrameMetadataListener) {
        playerInstance.clearVideoFrameMetadataListener(listener)
    }

    override fun setCameraMotionListener(listener: CameraMotionListener) {
        playerInstance.setCameraMotionListener(listener)
    }

    override fun clearCameraMotionListener(listener: CameraMotionListener) {
        playerInstance.clearCameraMotionListener(listener)
    }

    override fun createMessage(target: PlayerMessage.Target): PlayerMessage {
        return playerInstance.createMessage(target)
    }

    override fun setSeekParameters(seekParameters: SeekParameters?) {
        playerInstance.setSeekParameters(seekParameters)
    }

    override fun getSeekParameters(): SeekParameters {
        return playerInstance.getSeekParameters()
    }

    override fun setForegroundMode(foregroundMode: Boolean) {
        return playerInstance.setForegroundMode(foregroundMode)
    }

    override fun setPauseAtEndOfMediaItems(pauseAtEndOfMediaItems: Boolean) {
        playerInstance.setPauseAtEndOfMediaItems(pauseAtEndOfMediaItems)
    }

    override fun getPauseAtEndOfMediaItems(): Boolean {
        return playerInstance.getPauseAtEndOfMediaItems()
    }

    override fun getAudioFormat(): Format? {
        return playerInstance.getAudioFormat()
    }

    override fun getVideoFormat(): Format? {
        return playerInstance.getVideoFormat()
    }

    override fun getAudioDecoderCounters(): DecoderCounters? {
        return playerInstance.getAudioDecoderCounters()
    }

    override fun getVideoDecoderCounters(): DecoderCounters? {
        return playerInstance.getVideoDecoderCounters()
    }

    override fun setHandleAudioBecomingNoisy(handleAudioBecomingNoisy: Boolean) {
        playerInstance.setHandleAudioBecomingNoisy(handleAudioBecomingNoisy)
    }

    override fun setHandleWakeLock(handleWakeLock: Boolean) {
        playerInstance.setHandleWakeLock(handleWakeLock)
    }

    override fun setWakeMode(wakeMode: Int) {
        playerInstance.setWakeMode(wakeMode)
    }

    override fun setPriorityTaskManager(priorityTaskManager: PriorityTaskManager?) {
        playerInstance.setPriorityTaskManager(priorityTaskManager)
    }

    override fun experimentalSetOffloadSchedulingEnabled(offloadSchedulingEnabled: Boolean) {
        playerInstance.experimentalSetOffloadSchedulingEnabled(offloadSchedulingEnabled)
    }

    override fun experimentalIsSleepingForOffload(): Boolean {
        return playerInstance.experimentalIsSleepingForOffload()
    }

    override fun isTunnelingEnabled(): Boolean {
        return playerInstance.isTunnelingEnabled()
    }
}