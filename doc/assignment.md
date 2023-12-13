## Little explanation about the player architecture of the test

- 2 player modules are present (player and exoplayer) because we separate from the app modules all exoplayer/media3 dependencies in case of we may want to use another player technology)
- We want to handle player events/action the reactive way

## (1) The candidate have to build a 'clean' ui by using the player architecture from the test
- Respect video ratio of playback stream (see AnalyticsListener from Exoplayer)
- implement all remaining PlayerAction
- Display a seekBar (with readable position/duration updated every seconds)
- Seeking the thumb of seekBar seeks the stream at correct position
- Display title/subtitles/image on top of player (be creative)
- Display a ui element in order to be able to select a distinct audio and text track from stream playlist
- Respect playerFragment lifeCycle (player should be released onStop, and should playback at previous position at onStart)
- Handle exoplayer onPlayerError in order to display a simple dialog in ui when happening

## (2) The candidate have to play 'Paddington DRM test vod stream' when clicking a program

- You can use a harcoded pageMedia response like this one:

private val paddingtonMediaJson =  
"{\"currentPage\":{\"displayTemplate\":\"mediaUrls\"},\"detail\":{\"informations\":{\"consumptionPlatform\":\"HAPI\",\"contentID\":\"4940838_40099\",\"idVideoPub\":\"4940838_40099\",\"noPub\":true,\"title\":\"Paddington\",\"subtitle\":\"Film Jeunesse\",\"URLImage\":\"https://thumb.canalplus.pro/http/unsafe/{resolutionXY}/filters:quality({imageQualityPercentage})/img-hapi.canalplus.pro:80/ServiceImage/ImageID/104307909\",\"channelID\":\"40128\",\"duration\":\"5040000\",\"sharingURL\":\"https://mycan.al/h/4940838\",\"availableInVoDOnDevice\":true,\"availableInD2GOnDevice\":true,\"isInOfferForD2G\":true,\"isInOfferForVoD\":true,\"videoURLs\":[{\"videoURL\":\"https://dash-od-aka-canalplus.akamaized.net/MDRM/MKPC/CLEAR_AUDIO/HEVC/SDR_CRYPT/index.mpd\",\"encryption\":\"widevine\",\"drmURL\":\"https://secure-webtv.canal-bis.com/WebPortal-vabf/TestDRMDASH/api/Widevine?mode=MKPL\"}]}},\"tracking\":{\"omniture\":{\"channel\":\"MyCAndroid\",\"prop4\":\"MyCAndroid - Lecture prog\",\"prop5\":\"MyCAndroid - Lecture prog - Best of - Part 1_1501162\",\"pageName\":\"MyCAndroid - Lecture prog - Best of - Part 1_1501162\",\"prop10\":\"Contenu\",\"prop11\":\"Autres\",\"prop12\":\"Consumer\"}}}"

- Use Widevine DRM, POST to the drm ws, with a payload containing the challenge encoded in base64, you will get the license in base 64 format

## (3) In this step the candidate will have to describe what happens when playing paddington in an android emulator

- Why there is no sound playing (give some ideas how we could remedy on that ?)
- Why the stream is throwing this exception at some time of playback ?  
  Playback error  
  com.google.android.exoplayer2.ExoPlaybackException: MediaCodecVideoRenderer error, index=0, format=Format(Video1.1.4.1, null, null, video/hevc, hvc1.1.6.L93.B0, 2502584, null, [1280, 720, 25.0], [-1, -1]), format_supported=YES  
  at com.google.android.exoplayer2.ExoPlayerImplInternal.handleMessage(ExoPlayerImplInternal.java:571)
- What could we do to make the stream playback continue on the android emulator when getting this exception
- Try to assume/explain why we set .setMultiSession(false) for instantiation of DefaultDrmSessionManager

## (4) Bonus for candidate

- Try to persist the license of paddington and reuse it for all future playbacks

