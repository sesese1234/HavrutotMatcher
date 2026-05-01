package com.havrutot.data

import com.havrutot.models.Profile
import com.havrutot.models.Student
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.UUID

object ExcelManager {
    fun generateSampleExcel(file: File) {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Students")
        val header = sheet.createRow(0)
        header.createCell(0).setCellValue("Name")
        header.createCell(1).setCellValue("Is Locked (Yes/No)")
        header.createCell(2).setCellValue("Blacklist Names (comma separated)")

        val sampleRow = sheet.createRow(1)
        sampleRow.createCell(0).setCellValue("John Doe")
        sampleRow.createCell(1).setCellValue("No")
        sampleRow.createCell(2).setCellValue("Jane Doe, Bob Smith")

        FileOutputStream(file).use { workbook.write(it) }
        workbook.close()
    }

    fun exportToExcel(profile: Profile, file: File) {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Students")
        val header = sheet.createRow(0)
        header.createCell(0).setCellValue("Name")
        header.createCell(1).setCellValue("Is Locked (Yes/No)")
        header.createCell(2).setCellValue("Blacklist Names")

        profile.students.forEachIndexed { idx, student ->
            val row = sheet.createRow(idx + 1)
            row.createCell(0).setCellValue(student.name)
            row.createCell(1).setCellValue(if (student.isLocked) "Yes" else "No")
            row.createCell(2).setCellValue(student.blacklist.joinToString(", "))
        }

        FileOutputStream(file).use { workbook.write(it) }
        workbook.close()
    }

    fun importFromExcel(file: File): List<Student> {
        val students = mutableListOf<Student>()
        val workbook = XSSFWorkbook(FileInputStream(file))
        val sheet = workbook.getSheetAt(0)
        
        for (i in 1..sheet.lastRowNum) {
            val row = sheet.getRow(i) ?: continue
            val nameCell = row.getCell(0)
            if (nameCell == null || nameCell.stringCellValue.isBlank()) continue

            val name = nameCell.stringCellValue.trim()
            val lockedStr = row.getCell(1)?.stringCellValue?.trim()?.lowercase() ?: "no"
            val isLocked = lockedStr == "yes" || lockedStr == "true"
            
            val blacklistStr = row.getCell(2)?.stringCellValue ?: ""
            val blacklist = if (blacklistStr.isNotBlank()) {
                blacklistStr.split(",").map { it.trim() }.filter { it.isNotBlank() }
            } else {
                emptyList()
            }

            students.add(Student(id = UUID.randomUUID().toString(), name = name, isLocked = isLocked, blacklist = blacklist))
        }
        workbook.close()
        return students
    }

    fun exportResultsToExcel(pairs: List<Pair<Student, Student>>, file: File) {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Results")
        sheet.isRightToLeft = true
        val header = sheet.createRow(0)
        header.createCell(0).setCellValue("חברותא א'")
        header.createCell(1).setCellValue("חברותא ב'")

        pairs.forEachIndexed { idx, pair ->
            val row = sheet.createRow(idx + 1)
            row.createCell(0).setCellValue(pair.first.name)
            row.createCell(1).setCellValue(pair.second.name)
        }

        FileOutputStream(file).use { workbook.write(it) }
        workbook.close()
    }
}
