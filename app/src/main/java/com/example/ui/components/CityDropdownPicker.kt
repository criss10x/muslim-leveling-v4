package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.IndonesianCities
import com.example.ui.theme.*

/**
 * A searchable dropdown picker for Indonesian cities.
 *
 * Uses Material3 [ExposedDropdownMenuBox] — the official, crash-safe way to
 * build a combo-box style picker in Jetpack Compose.
 *
 * Design language (Arena Hikmah):
 * - DarkBackground container, IslamicGreen (teal) border on focus
 * - TextLight text, TextMuted placeholder
 * - Dropdown: DarkSurfaceElevated bg, 8dp rounded corners, max height 280dp
 * - Selected city: teal accent + check icon
 * - Region group headers: GoldAccent, 11sp bold uppercase
 *
 * Behaviour:
 * - Tap the field → dropdown opens immediately (readOnly = true, no keyboard).
 * - User can type to filter; only cities from IndonesianCities can be selected.
 * - Free-text that doesn't match any city is cleared on dismiss.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityDropdownPicker(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf(value) }

    // Sync external value → internal query when value changes outside (e.g. reset)
    LaunchedEffect(value) {
        if (value != query) query = value
    }

    val keyboard = LocalSoftwareKeyboardController.current

    // Pre-compute filtered groups reactively from the search query.
    val filteredGroups by remember(query) {
        derivedStateOf {
            val q = query.trim()
            IndonesianCities.cityGroups.mapNotNull { group ->
                val matches = if (q.isBlank()) {
                    group.cities
                } else {
                    group.cities.filter { it.contains(q, ignoreCase = true) }
                }
                if (matches.isEmpty()) null else group.copy(cities = matches)
            }
        }
    }

        ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = { typed ->
                query = typed
                onValueChange(typed)
                if (!expanded) expanded = true
            },
            placeholder = { Text("Cari atau pilih kota...", color = TextMuted) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextLight,
                unfocusedTextColor = TextLight,
                focusedBorderColor = IslamicGreen,
                unfocusedBorderColor = DarkSurfaceVariant,
                focusedContainerColor = DarkBackground,
                unfocusedContainerColor = DarkBackground,
                cursorColor = IslamicGreen
            ),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
                // Validate: if the text doesn't match a city, clear it.
                val isValid = IndonesianCities.allCities.any {
                    it.equals(query.trim(), ignoreCase = true)
                }
                if (!isValid && query.isNotBlank()) {
                    val match = IndonesianCities.allCities.find {
                        it.equals(query.trim(), ignoreCase = true)
                    }
                    if (match != null) {
                        query = match
                        onValueChange(match)
                    } else {
                        query = ""
                        onValueChange("")
                    }
                } else if (isValid) {
                    // Sync canonical form
                    val match = IndonesianCities.allCities.find {
                        it.equals(query.trim(), ignoreCase = true)
                    }
                    if (match != null && match != query) {
                        query = match
                        onValueChange(match)
                    }
                }
            },
            modifier = Modifier
                .background(DarkSurfaceElevated, RoundedCornerShape(8.dp))
                .heightIn(max = 280.dp)
        ) {
            if (filteredGroups.isEmpty()) {
                Text(
                    text = "Kota tidak ditemukan",
                    color = TextMuted,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            } else {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 4.dp)
                ) {
                    filteredGroups.forEach { group ->
                        // Region header
                        Text(
                            text = group.region.uppercase(),
                            color = GoldAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(
                                start = 16.dp,
                                end = 16.dp,
                                top = 10.dp,
                                bottom = 4.dp
                            )
                        )
                        group.cities.forEach { city ->
                            val isSelected = city.equals(value, ignoreCase = true)
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = city,
                                            color = if (isSelected) IslamicGreen else TextLight,
                                            fontSize = 14.sp,
                                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                            modifier = Modifier.weight(1f)
                                        )
                                        if (isSelected) {
                                            Spacer(modifier = Modifier.width(8.dp))
                                            androidx.compose.material3.Icon(
                                                imageVector = Icons.Filled.Check,
                                                contentDescription = null,
                                                tint = IslamicGreen,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                },
                                onClick = {
                                    query = city
                                    onValueChange(city)
                                    expanded = false
                                    keyboard?.hide()
                                },
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
