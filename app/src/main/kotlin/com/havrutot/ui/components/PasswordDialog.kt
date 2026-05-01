package com.havrutot.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.havrutot.data.CryptoUtils

@Composable
fun PasswordDialog(
    actualHash: String?,
    isChangeMode: Boolean = false,
    onUnlock: () -> Unit,
    onSetPassword: (String) -> Unit,
    onCancel: (() -> Unit)? = null
) {
    var password by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }

    val isSettingMode = actualHash == null

    AlertDialog(
        onDismissRequest = { onCancel?.invoke() },
        title = { Text(if (isSettingMode) "הגדרת סיסמה ראשונית" else if (isChangeMode) "שינוי סיסמה" else "הכנס סיסמה") },
        text = {
            Column {
                Text(
                    text = if (isSettingMode) "צור סיסמה להגנה על הרשימה." else if (isChangeMode) "הכנס סיסמה נוכחית וסיסמה חדשה." else "הרשימה נעולה. הכנס סיסמה כדי לצפות.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { 
                        password = it
                        error = false 
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    isError = error,
                    label = { Text(if (isSettingMode || isChangeMode) "סיסמה נוכחית / חדשה (אם אין)" else "סיסמה") }
                )
                if (isChangeMode) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        label = { Text("סיסמה חדשה") }
                    )
                }
                
                if (error) {
                    Text(errorMsg, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (isSettingMode) {
                        if (password.isNotEmpty()) {
                            onSetPassword(CryptoUtils.hashString(password))
                        }
                    } else if (isChangeMode) {
                        if (CryptoUtils.hashString(password) == actualHash) {
                            if (newPassword.isNotEmpty()) {
                                onSetPassword(CryptoUtils.hashString(newPassword))
                            } else {
                                error = true
                                errorMsg = "סיסמה חדשה לא יכולה להיות ריקה"
                            }
                        } else {
                            error = true
                            errorMsg = "סיסמה נוכחית שגויה"
                        }
                    } else {
                        if (CryptoUtils.hashString(password) == actualHash) {
                            onUnlock()
                        } else {
                            error = true
                            errorMsg = "סיסמה שגויה"
                        }
                    }
                }
            ) {
                Text(if (isSettingMode) "הגדר" else if (isChangeMode) "שנה" else "פתח")
            }
        },
        dismissButton = {
            if (onCancel != null) {
                TextButton(onClick = onCancel) { Text("ביטול") }
            }
        }
    )
}
