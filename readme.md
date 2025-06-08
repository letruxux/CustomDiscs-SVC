# CustomDiscs SVC

Create music discs using mp3, wav and flac files, and play audio from YouTube. Enhance and create a unique atmosphere in your game world.

## Special thanks
[Navoei CustomDiscs](https://github.com/Navoei/CustomDiscs) | [henkelmax AudioPlayer](https://github.com/henkelmax/audio-player) | [sedmelluq lavaplayer](https://github.com/sedmelluq/lavaplayer)

## Fork info
This fork adds `/cd searchyt` functionality, using [piped](https://docs.piped.video/docs/api-documentation/)'s API. (v2.1.10 - v2.1.11)

## Commands
```
/cd - Help for CustomDiscs
/cd download <direct link> <name.extension> - Downloads music file from URL.
/cd create <file name> "<disc name>" - Creates music disc.
/cd createyt <video url> "<disc name>" - Create disc with music from YouTube.
/cd reload - Reloads configuration file.
/cd searchyt <search query> - Searches for videos on YouTube.
```

## Configuration
```yaml
global:
  # Language of the plugin
  # Supported: ru_RU, it_IT, en_US
  # Unknown languages will be replaced with en_US
  locale: en_US
  debug: true
disc:
  # The distance from which music discs can be heard in blocks.
  distance: 64
  # The master volume of music discs from 0-1.
  # You can set values like 0.5 for 50% volume.
  volume: '0.5'
  allow-hoppers: true
providers:
  youtube:
    # This may help if the plugin is not working properly.
    # When you first play the disc after the server starts, you will see an authorization request in the console. Use a secondary account for security purposes.
    use-oauth2: false
    # piped.video URL to access youtube api, needed for /ytsearch and /ytget
    # Default: https://pipedapi.ducks.party
    # List of community-made instances: https://github.com/TeamPiped/documentation/blob/main/content/docs/public-instances/index.md
    piped-base-url: 'https://pipedapi.ducks.party'
    # If you have oauth2 enabled, leave these fields blank.
    # This may help if the plugin is not working properly.
    # https://github.com/lavalink-devs/youtube-source?tab=readme-ov-file#using-a-potoken
    po-token:
      token: ''
      visitor-data: ''
command:
  download:
    # The maximum download size in megabytes.
    max-size: 50
  create:
    custom-model-data:
      enable: false
      value: 0
  createyt:
    custom-model-data:
      enable: false
      value: 0
  distance:
    max: 64
```