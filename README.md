# mytube

Android (Kotlin + Jetpack Compose) build of the MyTube YouTube-clone app.

## Release build

The release build produces a signed **APK** (sideload / direct install) and an
**AAB** (Google Play upload):

```bash
./scripts/build-release.sh        # APK + AAB
./scripts/build-release.sh apk    # APK only
./scripts/build-release.sh aab    # AAB only
```

Requirements: JDK 17+ (JDK 21 recommended), Android SDK with *Platform 36.1*
and *Build-Tools 36.0.0*, and Gradle 9.3.1 (fetched by `./gradlew`).

Outputs:

| Artefact | Path |
| --- | --- |
| APK | `app/build/outputs/apk/release/app-release.apk` |
| AAB | `app/build/outputs/bundle/release/app-release.aab` |

### Signing

`app/build.gradle.kts` signs the release variant with the `release` signing
config, which reads three environment variables and expects the alias `upload`:

| Variable | Meaning | Default |
| --- | --- | --- |
| `KEYSTORE_PATH` | keystore file | `<repo root>/my-upload-key.jks` |
| `STORE_PASSWORD` | keystore password | — (required) |
| `KEY_PASSWORD` | key password | — (required) |

`scripts/build-release.sh` generates that keystore for you the first time it
runs and stores the passwords in the git-ignored `release-keystore.env`.

> Back the keystore up. Play Store updates for an app must be signed with the
> same upload key, and a lost key cannot be recovered.

To sign in CI with your own key instead of a generated one, add three
repository secrets: `KEYSTORE_BASE64` (`base64 -w0 my-upload-key.jks`),
`STORE_PASSWORD`, and `KEY_PASSWORD`.

### Continuous integration

`.github/workflows/android-release.yml` builds `assembleRelease` +
`bundleRelease` on every push to `main` (and on `arena/**` branches), verifies
the output with `apksigner verify` and `aapt2 dump badging`, and uploads the
APK and AAB as a workflow artefact. Open the run under
**Actions → Android Release Build → Artifacts** to download them.

Pushing a `v*` tag additionally publishes a GitHub release with both files:

```bash
git tag v1.0.0 && git push origin v1.0.0
```
