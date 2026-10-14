package dev.danielkindl.cistula.ui

data class CistulaAppContent(
    val title: String,
)

/**
 * Supplies [CistulaAppContent] for the only screen.
 * Plain Kotlin. Not an androidx.lifecycle.ViewModel. The screen has no lifecycle state to store.
 */
class CistulaAppViewModel(
    title: String,
) {
    val content: CistulaAppContent = CistulaAppContent(title = title)
}
