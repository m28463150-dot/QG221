package com.example

import com.example.data.model.EqualizerProfile
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testEqualizerPresetsExistAndValid() {
        val presets = EqualizerProfile.PRESETS
        assertTrue(presets.isNotEmpty())

        val mbalax = presets.find { it.id == "mbalax" }
        assertNotNull(mbalax)
        assertEquals("Mbalax", mbalax?.name)
        assertEquals(5, mbalax?.bandsDb?.size)

        val hiphop = presets.find { it.id == "hiphop" }
        assertNotNull(hiphop)
        assertEquals("Hip-Hop", hiphop?.name)
        assertEquals(5, hiphop?.bandsDb?.size)

        val acoustique = presets.find { it.id == "acoustique" }
        assertNotNull(acoustique)
        assertEquals("Acoustique", acoustique?.name)
        assertEquals(5, acoustique?.bandsDb?.size)
    }

    @Test
    fun testEqualizerBandsWithinDbLimits() {
        EqualizerProfile.PRESETS.forEach { profile ->
            profile.bandsDb.forEach { gainDb ->
                assertTrue("Gain $gainDb out of bounds in ${profile.name}", gainDb in -12f..12f)
            }
            assertTrue(profile.bassBoostPercent in 0..100)
        }
    }
}

