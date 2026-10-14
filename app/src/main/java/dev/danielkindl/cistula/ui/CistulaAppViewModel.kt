package dev.danielkindl.cistula.ui

data class CistulaAppContent(
    val title: String,
)

class CistulaAppViewModel(
    title: String,
) {
    val content: CistulaAppContent = CistulaAppContent(title = title)
}
