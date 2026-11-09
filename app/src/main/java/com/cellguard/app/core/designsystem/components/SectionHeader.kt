package com.cellguard.app.core.designsystem.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cellguard.app.core.designsystem.theme.LocalCellGuardColors

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    val colors = LocalCellGuardColors.current
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = colors.textSecondary,
        modifier = modifier.padding(start = 12.dp, bottom = 8.dp, top = 20.dp)
    )
}
