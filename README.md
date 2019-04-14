# MacroKeys Server &middot; [![Build Status](https://travis-ci.org/SimoneCorazza/MacroKeysServer.svg?branch=master)](https://travis-ci.org/SimoneCorazza/MacroKeysServer) [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=com.macrokeys%3Amacro-key-server&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.macrokeys%3Amacro-key-server)

Cross platform desktop MacroKeys server.

## Features

- Receive macro keys from the [Android App](https://github.com/SimoneCorazza/MacroKeysAndroid)
- Communicate with Bluetooth and Wifi
- Customize your keyboards with the [editor](https://github.com/SimoneCorazza/MacroKeysEditor)
- Trace every keystroke sent
- Encrypted protocol for added layer of security

## Getting Started

### Prerequisites

This project is built with [maven](https://maven.apache.org/) so you need to install the [maven cli](https://maven.apache.org/download.cgi) to run it.

### Executing

To execute simply use

```
mvn exec:java
```

### Packaging

```
mvn package
```

The output `.jar` is in the `./target` directory.

## License

This project is licensed under the GPL License see the [LICENSE.md](LICENSE.md) file for details