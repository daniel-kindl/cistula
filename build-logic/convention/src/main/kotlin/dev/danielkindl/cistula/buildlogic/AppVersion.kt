package dev.danielkindl.cistula.buildlogic

/**
 * Release version parsed from the `cistula.version` Gradle property (`MAJOR.MINOR.PATCH`).
 *
 * The Android `versionCode` is derived from it so that it grows with every SemVer release.
 */
internal data class AppVersion(
    val major: Int,
    val minor: Int,
    val patch: Int,
) {
    val name: String get() = "$major.$minor.$patch"

    /** `MAJOR * 1_000_000 + MINOR * 1_000 + PATCH + 1`. The `+ 1` keeps 0.0.0 above zero, which Android rejects. */
    val versionCode: Int get() = major * MAJOR_FACTOR + minor * MINOR_FACTOR + patch + 1

    companion object {
        private const val MAJOR_FACTOR = 1_000_000
        private const val MINOR_FACTOR = 1_000
        private const val MAX_COMPONENT = 999
        private const val MAX_MAJOR = 2_000
        private val SEMVER = Regex("""^(0|[1-9]\d*)\.(0|[1-9]\d*)\.(0|[1-9]\d*)$""")

        fun parse(text: String): AppVersion {
            val match = requireNotNull(SEMVER.matchEntire(text)) { "cistula.version '$text' is not MAJOR.MINOR.PATCH" }
            val (major, minor, patch) = match.destructured.toList().map(String::toInt)
            require(major <= MAX_MAJOR && minor <= MAX_COMPONENT && patch <= MAX_COMPONENT) {
                "cistula.version '$text' does not fit the versionCode scheme"
            }
            return AppVersion(major, minor, patch)
        }
    }
}
