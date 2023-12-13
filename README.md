# Author

Thierry Vilmart

November 2023

# Summary

- Use of Google's ExoPlayer for playing Video
- Use of a reactive approach with RXJava to update a Video with a movable SeekBar,
- And to update the seekbar every second.
- We can change the parallel tracks using the Exoplayer API
- The lifecycle of the app is used so that the Exoplayer is cleared and reinitialized
when the configuration changes (onStop when the app goes to the background).
