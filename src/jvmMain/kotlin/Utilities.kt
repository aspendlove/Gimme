import kotlinx.datetime.LocalDate

// Potential feature - custom date formatting
fun formatDate(date: LocalDate): String {
    return "${date.monthNumber}.${date.dayOfMonth}.${date.year}"
}
