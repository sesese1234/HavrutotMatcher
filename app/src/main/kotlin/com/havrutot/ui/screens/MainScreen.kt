package com.havrutot.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.havrutot.data.ExcelManager
import com.havrutot.data.ProfileRepository
import com.havrutot.models.Student
import com.havrutot.ui.components.PasswordDialog
import com.havrutot.ui.theme.AppTheme
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    var profile by remember { mutableStateOf(ProfileRepository.loadProfile()) }
    var unlocked by remember { mutableStateOf(profile.isListVisible) }
    var currentPairs by remember { mutableStateOf<List<Pair<Student, Student>>?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var resetDialogVisible by remember { mutableStateOf(false) }
    var menuExpanded by remember { mutableStateOf(false) }
    var changingPassword by remember { mutableStateOf(false) }

    val saveProfile = {
        ProfileRepository.saveProfile(profile)
    }

    AppTheme(accentColorHex = profile.accentColorHex) {
        if (!unlocked) {
            PasswordDialog(
                actualHash = profile.passwordHash,
                onUnlock = { 
                    unlocked = true 
                },
                onSetPassword = { hash ->
                    profile.passwordHash = hash
                    saveProfile()
                    unlocked = true
                }
            )
        } else if (changingPassword) {
            PasswordDialog(
                actualHash = profile.passwordHash,
                isChangeMode = true,
                onUnlock = { },
                onSetPassword = { hash ->
                    profile.passwordHash = hash
                    saveProfile()
                    changingPassword = false
                    errorMessage = "הסיסמה שונתה בהצלחה"
                },
                onCancel = { changingPassword = false }
            )
        } else if (currentPairs != null) {
            ResultsScreen(
                pairs = currentPairs!!,
                onBack = { currentPairs = null }
            )
        } else {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("מערכת הגרלת חברותות") },
                        actions = {
                            // Toggle Visibility Mode
                            IconButton(onClick = {
                                profile.isListVisible = !profile.isListVisible
                                saveProfile()
                                if (!profile.isListVisible) {
                                    unlocked = false // Lock it immediately
                                }
                            }) {
                                Icon(
                                    imageVector = if (profile.isListVisible) Icons.Default.Settings else Icons.Default.Lock,
                                    contentDescription = "שנה תצוגה"
                                )
                            }

                            // Menu
                            IconButton(onClick = { menuExpanded = true }) {
                                Icon(Icons.Default.MoreVert, contentDescription = "תפריט")
                            }
                            
                            DropdownMenu(
                                expanded = menuExpanded,
                                onDismissRequest = { menuExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("יצירת קובץ אקסל לדוגמה") },
                                    onClick = {
                                        menuExpanded = false
                                        val dialog = java.awt.FileDialog(null as java.awt.Frame?, "שמור קובץ לדוגמה", java.awt.FileDialog.SAVE)
                                        dialog.file = "sample.xlsx"
                                        dialog.isVisible = true
                                        if (dialog.directory != null && dialog.file != null) {
                                            val file = File(dialog.directory, dialog.file)
                                            ExcelManager.generateSampleExcel(file)
                                            errorMessage = "קובץ לדוגמה נוצר בשם '${file.name}'"
                                        }
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("ייצוא לאקסל") },
                                    onClick = {
                                        menuExpanded = false
                                        val dialog = java.awt.FileDialog(null as java.awt.Frame?, "ייצוא נתונים לאקסל", java.awt.FileDialog.SAVE)
                                        dialog.file = "export.xlsx"
                                        dialog.isVisible = true
                                        if (dialog.directory != null && dialog.file != null) {
                                            val file = File(dialog.directory, dialog.file)
                                            ExcelManager.exportToExcel(profile, file)
                                            errorMessage = "יוצא בהצלחה ל '${file.name}'"
                                        }
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("ייבוא מאקסל") },
                                    onClick = {
                                        menuExpanded = false
                                        val dialog = java.awt.FileDialog(null as java.awt.Frame?, "ייבוא מאקסל", java.awt.FileDialog.LOAD)
                                        dialog.file = "*.xlsx"
                                        dialog.isVisible = true
                                        if (dialog.directory != null && dialog.file != null) {
                                            val file = File(dialog.directory, dialog.file)
                                            try {
                                                val newStudents = ExcelManager.importFromExcel(file)
                                                profile = profile.copy(students = newStudents.toMutableList())
                                                saveProfile()
                                            } catch (e: Exception) {
                                                errorMessage = "נכשל בייבוא '${file.name}'."
                                            }
                                        }
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("איפוס היסטוריית הגרלות") },
                                    onClick = {
                                        menuExpanded = false
                                        resetDialogVisible = true
                                    }
                                )
                                Divider()
                                DropdownMenuItem(
                                    text = { Text("שינוי סיסמה") },
                                    onClick = {
                                        menuExpanded = false
                                        changingPassword = true
                                    }
                                )
                                // Simple Color Chooser Options
                                DropdownMenuItem(
                                    text = { Text("צבע עיקרי: סגול") },
                                    onClick = {
                                        menuExpanded = false
                                        profile = profile.copy(accentColorHex = "#BB86FC")
                                        saveProfile()
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("צבע עיקרי: כחול") },
                                    onClick = {
                                        menuExpanded = false
                                        profile = profile.copy(accentColorHex = "#64B5F6")
                                        saveProfile()
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("צבע עיקרי: ירוק") },
                                    onClick = {
                                        menuExpanded = false
                                        profile = profile.copy(accentColorHex = "#81C784")
                                        saveProfile()
                                    }
                                )
                            }
                        }
                    )
                }
            ) { padding ->
                Box(modifier = Modifier.padding(padding)) {
                    StudentsListScreen(
                        profile = profile,
                        onSaveProfile = saveProfile,
                        onGenerate = { currentPairs = it },
                        onShowError = { 
                            if (it.contains("No valid havrutot combinations")) {
                                resetDialogVisible = true
                            } else {
                                errorMessage = it 
                            }
                        }
                    )
                }
            }
        }

        if (resetDialogVisible) {
            AlertDialog(
                onDismissRequest = { resetDialogVisible = false },
                title = { Text("איפוס היסטוריה") },
                text = { Text("הגעת למצב בו מוצו כל אפשרויות ההגרלה ללא חזרה על העבר. האם תרצה לאפס את ההיסטוריה כדי להתחיל מחדש?") },
                confirmButton = {
                    Button(onClick = {
                        profile.history.clear()
                        saveProfile()
                        resetDialogVisible = false
                    }) { Text("אפס היסטוריה") }
                },
                dismissButton = {
                    TextButton(onClick = { resetDialogVisible = false }) { Text("ביטול") }
                }
            )
        }

        if (errorMessage != null) {
            AlertDialog(
                onDismissRequest = { errorMessage = null },
                title = { Text("הודעה") },
                text = { Text(errorMessage!!) },
                confirmButton = {
                    Button(onClick = { errorMessage = null }) { Text("אישור") }
                }
            )
        }
    }
}
