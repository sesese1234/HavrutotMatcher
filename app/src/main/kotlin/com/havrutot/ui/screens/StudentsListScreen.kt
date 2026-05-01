package com.havrutot.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.havrutot.engine.Matchmaker
import com.havrutot.models.Profile
import com.havrutot.models.Student
import java.util.UUID

@Composable
fun StudentsListScreen(
    profile: Profile,
    onSaveProfile: () -> Unit,
    onGenerate: (List<Pair<Student, Student>>) -> Unit,
    onShowError: (String) -> Unit
) {
    var newStudentName by remember { mutableStateOf("") }
    var editingBlacklistFor by remember { mutableStateOf<Student?>(null) }
    var updateTrigger by remember { mutableStateOf(0) }
    
    // Ensure UI reacts to list changes
    val studentsList = remember(profile, updateTrigger) { profile.students.toList() }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = newStudentName,
                onValueChange = { newStudentName = it },
                label = { Text("שם הבחור") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (newStudentName.isNotBlank()) {
                        profile.students.add(Student(id = UUID.randomUUID().toString(), name = newStudentName.trim()))
                        updateTrigger++
                        onSaveProfile()
                        newStudentName = ""
                    }
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "הוסף")
                Spacer(modifier = Modifier.width(4.dp))
                Text("הוסף")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(studentsList, key = { it.id }) { student ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(student.name, style = MaterialTheme.typography.titleMedium)
                            Text(
                                "רשימה שחורה: ${if (student.blacklist.isEmpty()) "אין" else student.blacklist.joinToString()}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("נעול")
                            Checkbox(
                                checked = student.isLocked,
                                onCheckedChange = { locked ->
                                    val idx = profile.students.indexOfFirst { it.id == student.id }
                                    if (idx != -1) {
                                        profile.students[idx] = student.copy(isLocked = locked)
                                        updateTrigger++
                                        onSaveProfile()
                                    }
                                }
                            )
                        }

                        IconButton(onClick = { editingBlacklistFor = student }) {
                            Icon(Icons.Default.Settings, contentDescription = "ערוך רשימה שחורה")
                        }

                        IconButton(onClick = {
                            profile.students.removeAll { it.id == student.id }
                            updateTrigger++
                            onSaveProfile()
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "מחק")
                        }
                    }
                }
            }
        }

        if (editingBlacklistFor != null) {
            val currentStudent = editingBlacklistFor!!
            val otherStudents = studentsList.filter { it.id != currentStudent.id }
            var currentBlacklist by remember { mutableStateOf(currentStudent.blacklist.toSet()) }

            AlertDialog(
                onDismissRequest = { editingBlacklistFor = null },
                title = { Text("רשימה שחורה - ${currentStudent.name}") },
                text = {
                    LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 300.dp)) {
                        items(otherStudents) { other ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = currentBlacklist.contains(other.name),
                                    onCheckedChange = { checked ->
                                        if (checked) {
                                            currentBlacklist = currentBlacklist + other.name
                                        } else {
                                            currentBlacklist = currentBlacklist - other.name
                                        }
                                    }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(other.name)
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        val idx = profile.students.indexOfFirst { it.id == currentStudent.id }
                        if (idx != -1) {
                            profile.students[idx] = currentStudent.copy(blacklist = currentBlacklist.toList())
                            updateTrigger++
                            onSaveProfile()
                        }
                        editingBlacklistFor = null
                    }) { Text("שמור") }
                },
                dismissButton = {
                    TextButton(onClick = { editingBlacklistFor = null }) { Text("ביטול") }
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                try {
                    val pairs = Matchmaker.generatePairs(profile.students, profile.history)
                    if (pairs.isNotEmpty()) {
                        onGenerate(pairs)
                    } else {
                        onShowError("לא נמצאו אפשרויות הגרלה חוקיות או שאין מספיק תלמידים זמינים.")
                    }
                } catch (e: Exception) {
                    onShowError(e.message ?: "שגיאה לא ידועה בהגרלה")
                }
            },
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp)
        ) {
            Text("הגרל חברותות", style = MaterialTheme.typography.titleMedium)
        }
    }
}
