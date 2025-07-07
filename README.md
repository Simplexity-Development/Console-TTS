# Console Text To Speech
This program is meant to be an intuitive text-to-speech program that runs on command line. 
  This was made with streamers who struggle speaking in mind - but is open to use for many other things.
  This uses SSML - and uses markdown-esque formatting for the effects.

### AWS
Using this requires an AWS account - https://aws.amazon.com/
You can use a free account - Please do read and check the limits and such though.
You will need to go to Identity and Access Management -> Access Keys
You will get an Access ID and Access Secret from there.

References:
- [Amazon Polly Description of Limits/Price](https://aws.amazon.com/polly/pricing/)
- [Setting up a user and how to use IAM](https://docs.aws.amazon.com/signer/latest/developerguide/iam-setup.html)

## Voice Prefixes

The voice prefixes section allows you to set a prefix that will switch the voice to a specific Polly voice.
Then, if you use that prefix at the beginning of a message, the application will switch to that voice until another prefix is used. 
Prefixes will be matched ignoring case and even if there's a `:` or `-` after it.

## Speech Effect Markdown

This is where the marks for the effects are declared. [SSML Tags Can Be Found Here](https://docs.aws.amazon.com/polly/latest/dg/supportedtags.html) - Currently self-closing tags such as 'break' are not supported
Effect declaration in the config looks like this:

```yml
speech-effect-markdown:
  "!": //The character(s) that will be on either side of the text you want the effect to be used on
    type: tag-name     //This is the tag name, might have 'amazon:' before it
    attribute: value   // Any customization settings go after that, like "volume" and "loud"
```

By default, the markdown will be matched if surrounded by spaces or newlines/end of lines, but not if surrounded by other characters. 
So you can have '.' and '..' as effect markdowns if you want


## Text Replacements

This is generic text replacement - it is run through a regex so that things aren't caught in the middle of a sentence. 

## Other Settings

**Server Port:**
Used for the HTTP server the amazon requests are being made from. You shouldn't need to change this unless you already know what you're doing

### Default configuration

```yml
aws-api:
  access-key: ""
  secret-key: ""
  region: "US_EAST_1"
  default-voice: "Brian"
  voice-prefixes:
    Sal: Salli
    Kim: Kimberly
    Bri: Brian
speech-effect-markdown:
  "**":
    type: prosody
    volume: x-loud
    pitch: low
    rate: slow
  "~~":
    type: amazon:effect
    name: whispered
  "__":
    type: emphasis
    level: strong
  "*":
    type: prosody
    volume: x-loud
    rate: x-fast
    pitch: x-high
  "||":
    type: say-as
    interpret-as: expletive
text-replacements:
    "<3": "heart emoji"
internal-settings:
  server-port: 3000

```

Todo:

- [x] Make closing tags less annoying
- [x] Manage exceptions better
- [x] Probably unify the config into one file
- [ ] Add configuration for audio output destination
- [ ] Add more Polly configuration (how dates are said, numbers, etc)
