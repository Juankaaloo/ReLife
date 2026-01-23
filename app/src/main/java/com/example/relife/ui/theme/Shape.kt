package com.relife.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

// Custom shapes for specific components
val CardShape = RoundedCornerShape(16.dp)
val ButtonShape = RoundedCornerShape(12.dp)
val ChipShape = RoundedCornerShape(20.dp)
val BottomSheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
val DialogShape = RoundedCornerShape(24.dp)
val AvatarShape = RoundedCornerShape(50)
val SearchBarShape = RoundedCornerShape(24.dp)
val FABShape = RoundedCornerShape(16.dp)
