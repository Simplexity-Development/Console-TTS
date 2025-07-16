# Console Text To Speech
This program is meant to be an intuitive text-to-speech program that runs on the command line.
This was made with streamers who struggle speaking in mind—but is open to use for many other things.
This uses SSML and includes markdown-esque formatting for the effects.

## Quickstart

- **Install Java 17+** if you haven’t already.
  - **Recommended (OpenJDK):** [Adoptium Temurin 17](https://adoptium.net/en-GB/temurin/releases/?version=17)
  - **Oracle JDK 17 (Login Required):** [Oracle JDK 17 Archive](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
  - **Linux/macOS users:** [Install via SDKMAN](https://sdkman.io)
  - Confirm installation with `java -version`
- **Download or compile the program**, then run it.
- When first launched, it will generate default config files.
- Open the generated config files and fill in:
  - Your [AWS IAM](#aws) credentials in `tokens.yml`
- If you intend to use the Twitch integration:
  - Twitch Client ID and Secret in `tokens.yml`
  - Your Twitch username in `twitch-api.username`
  - Follow the instructions in [The Twitch Integration](#twitch-integration) section to set up a Twitch application
- Reload or restart the app and start typing — messages will be read aloud!


### AWS
Using this requires an AWS account: https://aws.amazon.com/

You can use a free account - please check the [limits and pricing](https://aws.amazon.com/polly/pricing/).

1. Go to **IAM / Users / Security Credentials**
2. Create or use an existing user
3. Generate an **Access Key ID** and **Secret Access Key**
4. Enter them into `tokens.yml`:
```yml
aws:
access-key: YOUR_ACCESS_KEY
secret-key: YOUR_SECRET_KEY
```

More info: [Setting up IAM](https://docs.aws.amazon.com/signer/latest/developerguide/iam-setup.html)

---

## Voice Prefixes
The `voice-prefixes` section allows you to define short keywords that change the voice for any message that begins with them.
They are matched case-insensitively and tolerate `:` or `-` afterward.

Example:
```yml
voice-prefixes:
Sal: Salli
Kim: Kimberly
Bri: Brian
```

---

## Default Effects
You can set default prosody effects to apply to speech that doesn't have other effects. 

```yml
default-effects:
type: prosody
volume: medium
pitch: default
rate: medium
```

---

## Speech Effect Markdown
This is where the effects are declared. [SSML Tags Reference](https://docs.aws.amazon.com/polly/latest/dg/supportedtags.html)

These must wrap around the text like `**text**` or `~~text~~` similar to how most markdown works.

Effect declaration format:
```yml
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
```

> Note
> 
> Self-closing tags (e.g., `<break/>`) are not supported yet.

---

## Text Replacements
Run as regex-safe replacements so they don't catch partial words.
```yml
text-replacements:
"<3": "heart emoji"
```

---

## Other Settings
```yml
internal-settings:
server-port: 3000            # Port for local Amazon Polly API server
twitch-auth-port: 8080       # Must match Twitch App's redirect URI port
```

---

## Twitch Integration

### Creating a Twitch App
To use Twitch chat integration:
- Go to the [Twitch Dev Console](https://dev.twitch.tv/console/apps).
- Click **"Register Your Application"**
- Set the **OAuth Redirect URL** to match the value you define in `twitch-auth-port` (i.e., `http://localhost:8080` if the port is 8080).
- Select `Chat Bot` or something similar
- **GO TO THE BOTTOM AND CLICK SAVE - after doing the captcha, idk I managed to miss this and kept not saving the port properly** 
- After creating the app, copy your **Client ID** and **Client Secret**.

Place these values into your `tokens.yml`:
  ```yml
twitch:
  client-id: YOUR_CLIENT_ID
  client-secret: YOUR_CLIENT_SECRET
```

### Twitch Configuration

```yml
twitch-api:
  enable: false             # Whether the integration should be enabled or not
  messages:
    send: false             # Whether messages *you* type should be sent to twitch chat or not
    clean-markdown: true    # If the above is true - whether the messages should be stripped of markdown or not
  channel: ""               # Channel to connect to
  username: ""              # Username to use - NOTE this MUST be the user you authenticated with - or it won't work lol
  
  # This section allows you to set how certain groups' chat will look in the console. You can use any UTF-8 characters,
  # as long as your console supports it. It probably also works with UTF-16 but I haven't checked.   
  # The permissions are the CommandPermissions linked below
  chat:   
    default:
      format: "%user% ➜ %message%"
      weight: 0
      permission: EVERYONE
    mod:
      format: "<br-green>⚔ %user%</color> ➜ %message%"
      weight: 10
      permission: MODERATOR

```
[Twitch4J Permissions Reference](https://twitch4j.github.io/javadoc/com/github/twitch4j/common/enums/CommandPermission.html)

---

## 🎨 Formats
Use these to style your messages. 

### Styles
| Style         | Tag       | Close With     |
|---------------|-----------|----------------|
| Bold          | `<bold>`  | `</bold>`      |
| Italic        | `<i>`     | `</i>`         |
| Underline     | `<u>`     | `</u>`         |
| Strikethrough | `<s>`     | `</s>`         |
| Reset All     | `<reset>` | *(no closing)* |

### Regular Text Colors
| Color  | Tag        | Close With |
|--------|------------|------------|
| Black  | `<black>`  | `</color>` |
| Red    | `<red>`    | `</color>` |
| Green  | `<green>`  | `</color>` |
| Yellow | `<yellow>` | `</color>` |
| Blue   | `<blue>`   | `</color>` |
| Purple | `<purple>` | `</color>` |
| Cyan   | `<cyan>`   | `</color>` |
| White  | `<white>`  | `</color>` |

### Bright Text Colors
| Bright Color | Tag           | Close With |
|--------------|---------------|------------|
| Black        | `<br-black>`  | `</color>` |
| Red          | `<br-red>`    | `</color>` |
| Green        | `<br-green>`  | `</color>` |
| Yellow       | `<br-yellow>` | `</color>` |
| Blue         | `<br-blue>`   | `</color>` |
| Purple       | `<br-purple>` | `</color>` |
| Cyan         | `<br-cyan>`   | `</color>` |
| White        | `<br-white>`  | `</color>` |

### Background Colors / Highlight Colors
| Color  | Tag           | Close With    |
|--------|---------------|---------------|
| Black  | `<black-bg>`  | `</color-bg>` |
| Red    | `<red-bg>`    | `</color-bg>` |
| Green  | `<green-bg>`  | `</color-bg>` |
| Yellow | `<yellow-bg>` | `</color-bg>` |
| Blue   | `<blue-bg>`   | `</color-bg>` |
| Purple | `<purple-bg>` | `</color-bg>` |
| Cyan   | `<cyan-bg>`   | `</color-bg>` |
| White  | `<white-bg>`  | `</color-bg>` |

### Bright Background Colors / Highlight Colors
| Bright BG Color | Tag              | Close With    |
|-----------------|------------------|---------------|
| Black           | `<br-black-bg>`  | `</color-bg>` |
| Red             | `<br-red-bg>`    | `</color-bg>` |
| Green           | `<br-green-bg>`  | `</color-bg>` |
| Yellow          | `<br-yellow-bg>` | `</color-bg>` |
| Blue            | `<br-blue-bg>`   | `</color-bg>` |
| Purple          | `<br-purple-bg>` | `</color-bg>` |
| Cyan            | `<br-cyan-bg>`   | `</color-bg>` |
| White           | `<br-white-bg>`  | `</color-bg>` |

### Resets
| Reset Type       | Tag           |
|------------------|---------------|
| Reset All Styles | `<reset>`     |
| Reset Text Color | `</color>`    |
| Reset Background | `</color-bg>` |

---

## ✅ Todo
- [x] Make closing tags less annoying
- [x] Manage exceptions better
- [x] Probably unify the config into one file
- [x] Add more Polly configuration (how dates are said, numbers, etc)
- [ ] Add configuration for audio output destination
- [ ] Add support for Twitch bits / cheers
- [ ] Add support for highlighted messages
- [ ] Handle deleted messages (optional removal/ignoring)
- [ ] Add channel point redemption triggers
- [ ] Support self-closing SSML tags
- [ ] Add debug/verbose mode toggle
