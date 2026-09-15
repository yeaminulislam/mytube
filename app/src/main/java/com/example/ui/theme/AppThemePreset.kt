package com.example.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

data class AppThemePreset(
    val id: String,
    val nameEn: String,
    val nameBn: String,
    val subtitle: String,
    val primary: Color,
    val secondary: Color,
    val background: Color,
    val surface: Color,
    val card: Color,
    val border: Color,
    val onBackground: Color = Color.White,
    val onSurfaceVariant: Color = Color(0xFFAAAAAA),
    val isDark: Boolean = true
) {
    fun toColorScheme(): ColorScheme {
        return if (isDark) {
            darkColorScheme(
                primary = primary,
                onPrimary = if (isDark) Color.Black else Color.White,
                secondary = secondary,
                onSecondary = Color.Black,
                tertiary = secondary,
                background = background,
                onBackground = onBackground,
                surface = surface,
                onSurface = onBackground,
                surfaceVariant = card,
                onSurfaceVariant = onSurfaceVariant,
                outline = border
            )
        } else {
            lightColorScheme(
                primary = primary,
                onPrimary = Color.White,
                secondary = secondary,
                onSecondary = Color.White,
                tertiary = primary,
                background = background,
                onBackground = onBackground,
                surface = surface,
                onSurface = onBackground,
                surfaceVariant = card,
                onSurfaceVariant = onSurfaceVariant,
                outline = border
            )
        }
    }
}

object AppThemePresets {
    val GreenBlack = AppThemePreset(
        id = "emerald_black",
        nameEn = "Emerald & Pitch Black",
        nameBn = "সবুজ ও কালো (Emerald Green)",
        subtitle = "প্রাণবন্ত নিয়ন সবুজ ও পিচ ব্ল্যাক মিক্সিং",
        primary = Color(0xFF00E676),
        secondary = Color(0xFF69F0AE),
        background = Color(0xFF080D0A),
        surface = Color(0xFF101C14),
        card = Color(0xFF16281D),
        border = Color(0xFF213B2A),
        onBackground = Color.White,
        onSurfaceVariant = Color(0xFFA5D6A7),
        isDark = true
    )

    val ClassicRed = AppThemePreset(
        id = "classic_red",
        nameEn = "Classic Red & Dark",
        nameBn = "ক্লাসিক লাল ও কালো (Classic Red)",
        subtitle = "অরিজিনাল ইউটিউব রেড ও ডার্ক ব্যাকগ্রাউন্ড",
        primary = Color(0xFFFF0000),
        secondary = Color(0xFF3EA6FF),
        background = Color(0xFF0F0F0F),
        surface = Color(0xFF1E1E1E),
        card = Color(0xFF272727),
        border = Color(0xFF383838),
        onBackground = Color.White,
        onSurfaceVariant = Color(0xFFAAAAAA),
        isDark = true
    )

    val CyberCyan = AppThemePreset(
        id = "cyber_cyan",
        nameEn = "Cyber Cyan & Midnight",
        nameBn = "সাইবার সায়ান ও মিডনাইট (Cyan Blue)",
        subtitle = "উজ্জ্বল স্কাই সায়ান ও গভীর নেভি স্পেস",
        primary = Color(0xFF00E5FF),
        secondary = Color(0xFF80D8FF),
        background = Color(0xFF070D14),
        surface = Color(0xFF0E1724),
        card = Color(0xFF152236),
        border = Color(0xFF20334E),
        onBackground = Color.White,
        onSurfaceVariant = Color(0xFF90CAF9),
        isDark = true
    )

    val RoyalPurple = AppThemePreset(
        id = "royal_purple",
        nameEn = "Electric Purple & Obsidian",
        nameBn = "রয়্যাল পার্পল ও ওবসিডিয়ান (Royal Purple)",
        subtitle = "নিয়ন ইলেকট্রিক পার্পল ও গভীর নাইট ব্ল্যাক",
        primary = Color(0xFFD500F9),
        secondary = Color(0xFFEA80FC),
        background = Color(0xFF0D0714),
        surface = Color(0xFF181026),
        card = Color(0xFF241838),
        border = Color(0xFF3B265C),
        onBackground = Color.White,
        onSurfaceVariant = Color(0xFFCE93D8),
        isDark = true
    )

    val SunsetGold = AppThemePreset(
        id = "sunset_gold",
        nameEn = "Sunset Gold & Charcoal",
        nameBn = "সোনালী ও কাঠকয়লা (Sunset Amber)",
        subtitle = "সূর্যাস্ত গোল্ডেন অ্যাম্বার ও প্রিমিয়াম কাঠকয়লা",
        primary = Color(0xFFFFD600),
        secondary = Color(0xFFFFAB00),
        background = Color(0xFF121008),
        surface = Color(0xFF1E1B10),
        card = Color(0xFF2B2618),
        border = Color(0xFF453D25),
        onBackground = Color.White,
        onSurfaceVariant = Color(0xFFFFE082),
        isDark = true
    )

    val NeonPink = AppThemePreset(
        id = "neon_pink",
        nameEn = "Hot Magenta & Pitch Dark",
        nameBn = "ম্যাজেন্টা গোলাপি ও ডার্ক (Neon Pink)",
        subtitle = "উজ্জ্বল হট পিঙ্ক ও গভীর নিয়ন ব্ল্যাক",
        primary = Color(0xFFFF2A6D),
        secondary = Color(0xFFFF4081),
        background = Color(0xFF13080E),
        surface = Color(0xFF200F18),
        card = Color(0xFF2E1523),
        border = Color(0xFF472236),
        onBackground = Color.White,
        onSurfaceVariant = Color(0xFFF48FB1),
        isDark = true
    )

    val SolarOrange = AppThemePreset(
        id = "solar_orange",
        nameEn = "Solar Orange & Pitch Ash",
        nameBn = "সোলার অরেঞ্জ ও অ্যাশ (Fiery Orange)",
        subtitle = "উজ্জ্বল ফায়ার কমলা ও গভীর ডার্ক অ্যাশ",
        primary = Color(0xFFFF6D00),
        secondary = Color(0xFFFF9E80),
        background = Color(0xFF120E0A),
        surface = Color(0xFF1E1712),
        card = Color(0xFF2B201A),
        border = Color(0xFF47352B),
        onBackground = Color.White,
        onSurfaceVariant = Color(0xFFFFCC80),
        isDark = true
    )

    val CleanLight = AppThemePreset(
        id = "clean_light",
        nameEn = "Modern Clean White",
        nameBn = "আধুনিক লাইট মোড (Clean White)",
        subtitle = "পরিচ্ছন্ন ঝকঝকে সাদা ও স্লীক গ্রে লুক",
        primary = Color(0xFFFF0000),
        secondary = Color(0xFF065FD4),
        background = Color(0xFFF9F9F9),
        surface = Color(0xFFFFFFFF),
        card = Color(0xFFEDEDED),
        border = Color(0xFFD8D8D8),
        onBackground = Color(0xFF0F0F0F),
        onSurfaceVariant = Color(0xFF606060),
        isDark = false
    )

    val allPresets = listOf(
        GreenBlack,   // সবুজ ও কালো
        ClassicRed,   // ক্লাসিক লাল ও কালো
        CyberCyan,    // সাইবার সায়ান ও মিডনাইট
        RoyalPurple,  // রয়্যাল পার্পল ও ওবসিডিয়ান
        SunsetGold,   // সোনালী ও কাঠকয়লা
        NeonPink,     // ম্যাজেন্টা গোলাপি ও ডার্ক
        SolarOrange,  // সোলার অরেঞ্জ ও অ্যাশ
        CleanLight    // আধুনিক লাইট মোড
    )
}

val LocalAppTheme = compositionLocalOf { AppThemePresets.ClassicRed }
