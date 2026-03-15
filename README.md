# Serene

Serene is an open-source Android music player for your locally stored music library. It features a signature vinyl turntable Now Playing screen with spinning album art — inspired by YouTube Music — along with a full-featured music library, background playback, and a clean dark purple theme.

## Features

- Vinyl/turntable Now Playing screen with rotating album art disc and animated tonearm
- Browse music by Songs, Albums, Artists, and Playlists
- Background playback via Media3/ExoPlayer with notification controls
- Shuffle, repeat (off / all / one), and seek
- Persistent mini player bar across all screens
- Favorites and playlist management stored locally
- Scans device storage automatically via MediaStore API

## Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose
- **Playback:** AndroidX Media3 (ExoPlayer + MediaSessionService)
- **Architecture:** MVVM + Repository
- **DI:** Hilt
- **Database:** Room (playlists & favorites)
- **Images:** Coil

## Requirements

- Android 8.0 (API 26) or higher
- Storage permission for audio files

## License

MIT License — see [LICENSE](LICENSE)
