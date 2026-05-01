package com.havrutot.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.havrutot.models.Student
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import java.io.File
import com.havrutot.data.ExcelManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(
    pairs: List<Pair<Student, Student>>,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("תוצאות ההגרלה") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "חזור")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val dialog = java.awt.FileDialog(null as java.awt.Frame?, "ייצוא לאקסל", java.awt.FileDialog.SAVE)
                        dialog.file = "results.xlsx"
                        dialog.isVisible = true
                        if (dialog.directory != null && dialog.file != null) {
                            val file = File(dialog.directory, dialog.file)
                            ExcelManager.exportResultsToExcel(pairs, file)
                        }
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "ייצוא לאקסל")
                    }
                    IconButton(onClick = {
                        val htmlContent = """
                            <!DOCTYPE html>
                            <html dir="rtl" lang="he">
                            <head>
                                <meta charset="UTF-8">
                                <title>תוצאות הגרלה</title>
                                <style>
                                    body { font-family: Arial, sans-serif; padding: 20px; }
                                    table { width: 100%; border-collapse: collapse; margin-top: 20px; }
                                    th, td { border: 1px solid #ddd; padding: 12px; text-align: center; font-size: 18px; }
                                    th { background-color: #f2f2f2; }
                                    h1 { text-align: center; }
                                    @media print {
                                        button { display: none; }
                                    }
                                </style>
                            </head>
                            <body>
                                <h1>תוצאות הגרלת חברותות</h1>
                                <button onclick="window.print()" style="padding: 10px 20px; font-size: 16px; margin-bottom: 20px; cursor: pointer;">הדפס</button>
                                <table>
                                    <tr><th>חברותא א'</th><th>חברותא ב'</th></tr>
                                    ${pairs.joinToString("\n") { "<tr><td>${it.first.name}</td><td>${it.second.name}</td></tr>" }}
                                </table>
                                <script>window.print();</script>
                            </body>
                            </html>
                        """.trimIndent()
                        val tempFile = File.createTempFile("havrutot_results", ".html")
                        tempFile.writeText(htmlContent)
                        java.awt.Desktop.getDesktop().browse(tempFile.toURI())
                    }) {
                        Icon(Icons.Default.Send, contentDescription = "הדפס")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)
        ) {
            items(pairs) { pair ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(pair.first.name, style = MaterialTheme.typography.titleLarge)
                        Text("  -  ", style = MaterialTheme.typography.titleLarge)
                        Text(pair.second.name, style = MaterialTheme.typography.titleLarge)
                    }
                }
            }
        }
    }
}
