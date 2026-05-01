package com.havrutot.engine

import com.havrutot.data.CryptoUtils
import com.havrutot.models.HavrutaHistory
import com.havrutot.models.Student

object Matchmaker {
    fun generatePairs(students: List<Student>, history: HavrutaHistory): List<Pair<Student, Student>> {
        val available = students.filter { !it.isLocked }.shuffled()
        if (available.size < 2) return emptyList()

        val pairs = mutableListOf<Pair<Student, Student>>()
        val used = mutableSetOf<String>()

        fun backtrack(index: Int): Boolean {
            if (used.size >= available.size - 1) return true
            if (index >= available.size) return true
            if (used.contains(available[index].id)) return backtrack(index + 1)

            val studentA = available[index]
            for (j in index + 1 until available.size) {
                val studentB = available[j]
                if (used.contains(studentB.id)) continue

                // Check constraints
                if (studentA.blacklist.contains(studentB.name) || studentB.blacklist.contains(studentA.name)) continue

                val pairHash1 = CryptoUtils.hashString("${studentA.id}-${studentB.id}")
                val pairHash2 = CryptoUtils.hashString("${studentB.id}-${studentA.id}")

                if (!history.pastHashes.contains(pairHash1) && !history.pastHashes.contains(pairHash2)) {
                    // Try pair
                    pairs.add(Pair(studentA, studentB))
                    used.add(studentA.id)
                    used.add(studentB.id)

                    if (backtrack(index + 1)) return true

                    // Undo
                    pairs.removeAt(pairs.size - 1)
                    used.remove(studentA.id)
                    used.remove(studentB.id)
                }
            }
            // Cannot pair this student with anyone remaining
            return false
        }

        if (backtrack(0)) {
            // Save to history
            for (pair in pairs) {
                history.addHash(CryptoUtils.hashString("${pair.first.id}-${pair.second.id}"))
            }
            return pairs
        }

        throw Exception("No valid havrutot combinations found without repeating past history.")
    }
}
