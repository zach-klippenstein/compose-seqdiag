# compose-seqdiag

[![gradle unit tests](https://github.com/zach-klippenstein/compose-seqdiag/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/zach-klippenstein/compose-seqdiag/actions/workflows/ci.yml)
[![Maven Central](https://img.shields.io/maven-central/v/com.zachklipp.seqdiag/seqdiag.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.zachklipp.seqdiag%22)

A library for rendering [sequence diagrams](https://en.wikipedia.org/wiki/Sequence_diagram) in
Compose UI. The library supports Kotlin Multiplatform and currently supports Android and JVM
targets. Both left-to-right and right-to-left layout directions are supported.

More information is available at the [project website](http://www.zachklipp.com/compose-seqdiag/index.html).

Sequence diagrams made with this library look like this:

<img src=".assets/sample-diagram.png" width="500">

## Usage

[![Maven Central](https://img.shields.io/maven-central/v/com.zachklipp.seqdiag/seqdiag.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.zachklipp.seqdiag%22)

```kotlin
implementation("com.zachklipp.seqdiag:seqdiag:{version}")
```