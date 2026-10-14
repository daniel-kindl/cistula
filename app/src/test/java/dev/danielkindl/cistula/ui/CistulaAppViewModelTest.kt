package dev.danielkindl.cistula.ui

import org.junit.Assert.assertEquals
import org.junit.Test

class CistulaAppViewModelTest {
    @Test
    fun exposesTheSuppliedTitle() {
        val viewModel = CistulaAppViewModel(title = CistulaAppFixture.TITLE)

        assertEquals(CistulaAppFixture.TITLE, viewModel.content.title)
    }
}
