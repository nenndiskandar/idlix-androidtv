package com.iskanflix.mobile.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iskanflix.mobile.ui.theme.IskanFlixRed
import com.iskanflix.mobile.ui.theme.SurfaceDark
import com.iskanflix.mobile.ui.theme.SurfaceVariantDark
import com.iskanflix.mobile.ui.theme.TextDim
import com.iskanflix.mobile.ui.theme.TextWhite

data class FilterItem(
    val id: String,
    val label: String
)

@Composable
fun FilterChipRow(
    items: List<FilterItem>,
    selectedId: String,
    onSelect: (id: String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items, key = { it.id }) { item ->
            val isSelected = item.id == selectedId
            FilterChip(
                selected = isSelected,
                onClick = { onSelect(item.id) },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 12.sp,
                        color = if (isSelected) Color.White else TextDim
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = SurfaceDark,
                    labelColor = TextDim,
                    selectedContainerColor = IskanFlixRed,
                    selectedLabelColor = Color.White
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    borderColor = SurfaceVariantDark,
                    selectedBorderColor = IskanFlixRed
                )
            )
        }
    }
}
